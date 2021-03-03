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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.codecrab.eblog.common.response.Result;
import top.codecrab.eblog.entity.Category;
import top.codecrab.eblog.entity.Post;
import top.codecrab.eblog.entity.UserCollection;
import top.codecrab.eblog.utils.ShiroUtil;
import top.codecrab.eblog.utils.ValidationUtil;
import top.codecrab.eblog.vo.CommentVo;
import top.codecrab.eblog.vo.PostVo;

import java.util.Date;

@Controller
public class PostController extends BaseController {

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
        Session session = SecurityUtils.getSubject().getSession();
        String captcha = (String) session.getAttribute(KAPTCHA_SESSION_KEY);
        session.removeAttribute(KAPTCHA_SESSION_KEY);
        if (!StringUtils.equals(vercode, captcha))
            return Result.fail("图形验证码不匹配或已过期，请刷新后重试");
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(post);
        //校检表单信息
        if (validResult.hasErrors()) return Result.fail(validResult.getErrors());

        boolean res;
        Long id;
        Post one = postService.getById(post.getId());
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
            post.setUserId(ShiroUtil.getProfileId());
            post.setStatus(0); //0表示未审核，1表示审核 -1删除
            res = postService.save(post);
            id = post.getId();
        } else {
            //编辑
            Assert.isTrue(one.getUserId().equals(ShiroUtil.getProfileId()), "您没有权限修改不属于您的文章");
            one.setModified(new Date());
            one.setTitle(post.getTitle());
            one.setCategoryId(post.getCategoryId());
            one.setContent(post.getContent());
            one.setStatus(0);
            res = postService.updateById(one);
            id = one.getId();
        }
        return res ? Result.success().action("/post/" + id) : Result.fail("未知原因，发布失败");
    }

    @ResponseBody
    @PostMapping("/post/delete")
    public Result postDelete(Long id) {
        postService.delete(id);
        return Result.success().action("/user/index");
    }
}
