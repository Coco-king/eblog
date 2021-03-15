package top.codecrab.eblog.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.codecrab.eblog.common.response.Result;
import top.codecrab.eblog.entity.Category;
import top.codecrab.eblog.entity.Comment;
import top.codecrab.eblog.entity.Post;
import top.codecrab.eblog.entity.UserCollection;
import top.codecrab.eblog.search.mq.MqTypes;
import top.codecrab.eblog.utils.CommonUtils;
import top.codecrab.eblog.utils.ShiroUtil;
import top.codecrab.eblog.utils.UploadUtil;
import top.codecrab.eblog.utils.ValidationUtil;
import top.codecrab.eblog.vo.CommentVo;
import top.codecrab.eblog.vo.PostVo;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class PostController extends BaseController {

    @GetMapping("/postCaptcha.jpg")
    public void postCaptcha(HttpServletResponse response) throws IOException {
        //验证码文字
        String text = producer.createText();
        //放入redis
        redisTemplate.opsForValue().set(POST_CAPTCHA_KEY + CommonUtils.getRemoteHost(request), text, 300, TimeUnit.SECONDS);
        //验证码图片
        BufferedImage image = producer.createImage(text);
        //设置页面不缓存
        response.setHeader("Cache-Control", "no-store, no-cache");
        //设置写入文件的类型
        response.setContentType("image/jpeg");
        //写入到页面
        ImageIO.write(image, "jpg", response.getOutputStream());
    }

    @GetMapping("/category/{id:\\d*}")
    public String category(@PathVariable("id") Long id, Boolean recommend) {
        int cp = ServletRequestUtils.getIntParameter(request, "cp", 1);
        request.setAttribute("currentCategoryId", id);
        request.setAttribute("cp", cp);
        request.setAttribute("isRecommend", recommend);
        request.setAttribute("isOrder", "created");
        return "post/category";
    }

    @GetMapping("/category/recommend")
    public String recommend(Long id, Boolean recommend, String order) {
        int cp = ServletRequestUtils.getIntParameter(request, "cp", 1);
        request.setAttribute("currentCategoryId", id);
        request.setAttribute("cp", cp);
        request.setAttribute("isRecommend", recommend);
        request.setAttribute("isOrder", order);
        return "post/category";
    }

    @GetMapping("/post/{id:\\d*}")
    public String detail(@PathVariable("id") Long id) {
        //查询文章
        PostVo postVo = postService.detail(id);
        Assert.notNull(postVo, "文章不存在或已被删除");
        //查询评论 1、分页 2、文章id 3、用户id 4、排序
        IPage<CommentVo> page = commentService.paging(getPage(), id, null, "created");

        //增加阅读量到缓存
        postService.cacheViewCount(postVo, request);

        //页面markdown显示有问题，在前面换一行就没事了
        postVo.setContent("\n" + postVo.getContent());
        request.setAttribute("post", postVo);
        request.setAttribute("pageData", page);
        request.setAttribute("currentCategoryId", postVo.getCategoryId());
        return "/post/detail";
    }

    @ResponseBody
    @PostMapping("/collection/find")
    public Result collectionFind(Long pid) {
        int count = collectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", ShiroUtil.getProfileId()).eq("post_id", pid));
        return Result.success(MapUtil.of("collection", count > 0));
    }

    @ResponseBody
    @PostMapping("/collection/remove")
    public Result collectionRemove(Long pid) {
        boolean remove = collectionService.remove(new QueryWrapper<UserCollection>()
                .eq("user_id", ShiroUtil.getProfileId()).eq("post_id", pid));
        return remove ? Result.success() : Result.fail("取消收藏失败，文章状态异常");
    }

    @ResponseBody
    @PostMapping("/collection/add")
    public Result collectionAdd(Long pid) {
        Post post = postService.getOne(new QueryWrapper<Post>()
                .eq("id", pid).ge("status", 1));
        Assert.notNull(post, "文章已被删除或被封禁");

        UserCollection collection = new UserCollection();
        collection.setPostId(pid);
        collection.setPostUserId(post.getUserId());
        collection.setUserId(ShiroUtil.getProfileId());
        collection.setCreated(new Date());
        collection.setModified(collection.getCreated());
        //执行保存
        boolean save = collectionService.save(collection);
        return save ? Result.success() : Result.fail("收藏失败，文章状态异常");
    }

    @GetMapping("/post/edit")
    public String postEdit() {
        String id = request.getParameter("id");
        if (!StringUtils.isBlank(id)) {
            //发布，设置回显
            Post post = postService.getOne(new QueryWrapper<Post>().eq("id", id));
            Assert.notNull(post, "帖子不存在或已删除");
            Long profileId = ShiroUtil.getProfileId();
            if (profileId == null || profileId != 1) {
                Assert.isTrue(post.getUserId().equals(profileId), "您没有权限修改不属于您的文章");
            }
            request.setAttribute("post", post);
            request.setAttribute("type", "发布");
        }
        //编辑
        request.setAttribute("categories", categoryService.list(new QueryWrapper<Category>().eq("status", 0)));
        request.setAttribute("type", "编辑");
        return "/post/edit";
    }

    /**
     * 新增或编辑文章
     */
    @ResponseBody
    @PostMapping("/post/submit")
    public Result postSubmit(Post post, String vercode) {
        String captcha = redisTemplate.opsForValue().get(POST_CAPTCHA_KEY + CommonUtils.getRemoteHost(request));
        redisTemplate.delete(POST_CAPTCHA_KEY + CommonUtils.getRemoteHost(request));
        if (!StringUtils.equals(vercode, captcha))
            return Result.fail("图形验证码不匹配或已过期，请刷新后重试");
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(post);
        //校检表单信息
        if (validResult.hasErrors()) return Result.fail(validResult.getErrors());

        boolean res;
        Long id;
        Post one = postService.getById(post.getId());
        Long profileId = ShiroUtil.getProfileId();
        if (profileId == null)
            return Result.fail("请先登录再尝试编辑").action("/login");
        if (one == null) {
            //新增
            post.setViewCount(0);
            post.setEditMode("0");
            post.setCommentCount(0);
            post.setLevel(0);
            post.setRecommend(false);
            post.setCreated(new Date());
            post.setModified(post.getCreated());
            post.setVoteUp(0);
            post.setVoteDown(0);
            post.setUserId(profileId);
            if (profileId == 1) {
                post.setStatus(1); //0表示未审核，1表示审核 -1删除
            } else {
                post.setStatus(0); //0表示未审核，1表示审核 -1删除
            }
            res = postService.save(post);
            id = post.getId();

            //该分类下文章个数加1
            Category category = categoryService.getById(post.getCategoryId());
            Assert.notNull(category, "找不到分类");
            category.setPostCount(category.getPostCount() + 1);
            categoryService.updateById(category);
        } else {
            //编辑
            Assert.isTrue(one.getUserId().equals(profileId), "您没有权限修改不属于您的文章");
            one.setModified(new Date());
            one.setTitle(post.getTitle());
            one.setCategoryId(post.getCategoryId());
            one.setContent(post.getContent());
            if (profileId != 1) {
                one.setStatus(0);
            }
            res = postService.updateById(one);
            id = one.getId();
        }

        //发送消息到队列
        this.sendMsg(MqTypes.POST_INSERT_ROUTING_KEY, id);

        return res ? Result.success((one == null ? "发布" : "编辑") + "成功，请等待审核")
                .action("/post/" + id) : Result.fail("未知原因，发布失败");
    }

    /**
     * 删除文章
     */
    @ResponseBody
    @PostMapping("/post/delete")
    public Result postDelete(Long id) {
        postService.delete(id);

        //发送消息到队列
        this.sendMsg(MqTypes.POST_REMOVE_ROUTING_KEY, id);
        return Result.success().action("/user/index");
    }

    @GetMapping("/post/delete")
    public String delete(Long id) {
        postService.delete(id);

        //发送消息到队列
        this.sendMsg(MqTypes.POST_REMOVE_ROUTING_KEY, id);
        return "/user/index";
    }

    /**
     * 评论
     */
    @ResponseBody
    @PostMapping("/post/reply")
    public Result postReply(Long postId, String content) {
        return postService.postReply(postId, content);
    }

    /**
     * 删除评论
     */
    @ResponseBody
    @PostMapping("/post/jieda-delete")
    public Result jiedaDelete(Long id) {
        return postService.jiedaDelete(id);
    }

    /**
     * 采纳评论
     */
    @ResponseBody
    @PostMapping("/post/jieda-accept")
    public Result jiedaAccept(Long id) {
        Comment comment = commentService.getById(id);
        Assert.notNull(comment, "找不到该评论或已被删除");
        comment.setStatus(66);
        commentService.updateById(comment);
        return Result.success();
    }

    /**
     * 评论赞
     */
    @ResponseBody
    @PostMapping("/post/jieda-zan")
    public Result jiedaZan(Long id, boolean ok) {
        Comment comment = commentService.getById(id);
        Assert.notNull(comment, "找不到该评论或已被删除");
        comment.setVoteUp(comment.getVoteUp() + (ok ? -1 : 1));
        commentService.updateById(comment);
        return Result.success();
    }

    /**
     * 上传博客图片
     */
    @ResponseBody
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) {
        try {
            Result upload = uploadUtil.upload(UploadUtil.TYPE_POST, file);
            if (upload.getStatus() == 0) {
                return MapUtil.builder("message", (Object) "上传成功")
                        .put("success", 1).put("status", 0)
                        .put("url", upload.getData().toString()).build();
            }
            return MapUtil.builder("message", (Object) upload.getMsg())
                    .put("success", 0).put("status", -1).build();
        } catch (IOException e) {
            return MapUtil.builder("message", (Object) "上传成功")
                    .put("success", 0).put("status", -1).build();
        }
    }
}
