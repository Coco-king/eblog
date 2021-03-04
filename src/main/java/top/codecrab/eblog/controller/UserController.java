package top.codecrab.eblog.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.codecrab.eblog.common.annotation.AccessLimit;
import top.codecrab.eblog.common.response.Result;
import top.codecrab.eblog.entity.Comment;
import top.codecrab.eblog.entity.Post;
import top.codecrab.eblog.entity.User;
import top.codecrab.eblog.entity.UserMessage;
import top.codecrab.eblog.shiro.AccountProfile;
import top.codecrab.eblog.utils.ShiroUtil;
import top.codecrab.eblog.utils.UploadUtil;
import top.codecrab.eblog.vo.CommentVo;
import top.codecrab.eblog.vo.PostVo;
import top.codecrab.eblog.vo.UserMessageVo;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author codecrab
 * @since 2021-02-26
 */
@Controller
public class UserController extends BaseController {

    @Autowired
    private UploadUtil uploadUtil;

    @GetMapping("/user/home")
    public String home() {
        getUserHome(null);
        return "/user/home";
    }

    @GetMapping("/user/other/home/{username}")
    public String otherHome(@PathVariable("username") String username) {
        Assert.notBlank(username, "非法参数");
        getUserHome(username);
        return "/user/home";
    }

    @GetMapping("/jump")
    public String jump() {
        String username = ServletRequestUtils.getStringParameter(request, "username", "");
        Assert.notBlank(username, "非法参数");
        getUserHome(username);
        return "/user/home";
    }

    private void getUserHome(String username) {
        User user;
        String name;
        if (StringUtils.isBlank(username)) {
            user = userService.getById(ShiroUtil.getProfileId());
            name = "我";
        } else {
            user = userService.getOne(new QueryWrapper<User>()
                    //负一的状态为未激活邮箱，但是也需要查询到
                    .eq("username", username).ge("status", -1));
            name = username;
        }
        Assert.notNull(user, "该用户已不存在或已被封禁");
        //查出近一年的所有文章和评论
        List<Post> posts = postService.list(new QueryWrapper<Post>()
                .eq("user_id", user.getId()).ge("status", 0)
                .gt("created", DateUtil.offsetMonth(new Date(), -12))
                .orderByDesc("created"));
        List<CommentVo> comments = commentService.commentsAndPostTitle(new QueryWrapper<Comment>()
                .eq("user_id", user.getId()).ge("status", 0)
                .gt("created", DateUtil.offsetMonth(new Date(), -12))
                .orderByDesc("created"));
        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user, profile);
        request.setAttribute("name", name);
        request.setAttribute("user", profile);
        request.setAttribute("posts", posts);
        request.setAttribute("comments", comments);
    }

    @GetMapping("/user/set")
    public String set() {
        User user = userService.getById(ShiroUtil.getProfileId());
        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user, profile);
        request.setAttribute("user", profile);
        return "/user/set";
    }

    @ResponseBody
    @PostMapping("/user/set")
    public Result doSet(AccountProfile user) {
        return userService.doSet(user);
    }

    @GetMapping("/user/index")
    public String index() {
        return "/user/index";
    }

    /**
     * 查询当前用户发表的帖子
     */
    @ResponseBody
    @GetMapping("/user/public")
    public Result userPublic() {
        IPage<Post> page = postService.page(getPage(), new QueryWrapper<Post>()
                .eq("user_id", ShiroUtil.getProfileId())
                .ge("status", 0).orderByDesc("created"));
        return Result.success(page);
    }

    /**
     * 查询当前用户收藏的帖子
     */
    @ResponseBody
    @GetMapping("/user/collection")
    public Result userCollection() {
        QueryWrapper<Post> wrapper = new QueryWrapper<Post>()
                .eq("p.user_id", ShiroUtil.getProfileId())
                .ge("p.status", 1);
        IPage<PostVo> page = postService.selectPageCollection(getPage(), wrapper);
        return Result.success(page);
    }

    @GetMapping("/user/message")
    public String message() {
        //status >= 0则为未删除
        IPage<UserMessageVo> page = messageService.paging(getPage(), new QueryWrapper<UserMessage>()
                .eq("to_user_id", ShiroUtil.getProfileId())
                .ge("status", 0).orderByDesc("created"));
        //把vo类型的信息集合转为普通类型的
        List<UserMessage> messages = page.getRecords().stream().map(vo -> {
            UserMessage userMessage = new UserMessage();
            BeanUtil.copyProperties(vo, userMessage);
            //设置为消息已读
            userMessage.setStatus(1);
            return userMessage;
        }).collect(Collectors.toList());
        //执行更新
        messageService.updateBatchById(messages);
        request.setAttribute("pageData", page);
        return "/user/message";
    }

    @ResponseBody
    @PostMapping("/message/remove")
    public Result messageRemove(Long id, Boolean all) {
        if (all == null) all = false;
        List<UserMessage> messages = messageService.list(new QueryWrapper<UserMessage>()
                .eq("to_user_id", ShiroUtil.getProfileId()).eq(!all, "id", id));
        //将他们的状态改为-1，表示已删除
        messages.forEach(msg -> msg.setStatus(-1));
        boolean update = messageService.updateBatchById(messages);
        return update ? Result.success() : Result.fail("由于未知原因，删除失败");
    }

    /**
     * 上传头像
     */
    @ResponseBody
    @PostMapping("/user/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws IOException {
        return uploadUtil.upload(UploadUtil.TYPE_AVATAR, file);
    }

    /**
     * 修改密码
     *
     * @param nowPass 现在的密码
     * @param pass    新密码
     * @param repass  重复新密码
     */
    @ResponseBody
    @PostMapping("/user/repass")
    public Result repass(String nowPass, String pass, String repass) {
        if (StringUtils.isAnyBlank(nowPass, pass, repass)) {
            return Result.fail("密码不能为空");
        }
        if (!StringUtils.equals(pass, repass)) {
            return Result.fail("两次输入的密码不一致");
        }
        User user = userService.getById(ShiroUtil.getProfileId());
        String md5Pass = SecureUtil.md5(nowPass + user.getSalt());
        if (!StringUtils.equals(md5Pass, user.getPassword())) {
            return Result.fail("输入的密码不正确");
        }
        //重新分配盐
        user.setSalt(UUID.randomUUID().toString(true));
        user.setPassword(SecureUtil.md5(pass + user.getSalt()));
        user.setModified(new Date());
        userService.updateById(user);
        return Result.success().action("/user/set#pass");
    }

    @GetMapping("/user/activate")
    public String activate() {
        User user = userService.getById(ShiroUtil.getProfileId());
        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user, profile);
        request.setAttribute("user", profile);
        return "/verify/activate";
    }

    /**
     * 发送激活邮件
     */
    @AccessLimit(maxCount = 5, seconds = 60 * 60)
    @ResponseBody
    @PostMapping("/user/activate")
    public Result sendActivateEmail(String email) {
        return userService.sendActivateEmail(email);
    }

    @Value("${server.dns-addr}")
    private String dnsAddr;

    /**
     * 用户点击链接后激活邮箱
     */
    @AccessLimit(maxCount = 5, seconds = 60 * 60)
    @RequestMapping("/user/activation/{code}")
    public String activateEmail(@PathVariable("code") String code) {
        User user = userService.getOne(new QueryWrapper<User>().eq("code", code));
        if (user == null) {
            request.setAttribute("msg", "验证邮箱失败");
            request.setAttribute("index", dnsAddr);
            return "/email/fail";
        }
        user.setStatus(0);
        userService.updateById(user);
        request.setAttribute("msg", "");
        request.setAttribute("index", dnsAddr);
        return "/email/success";
    }

    /**
     * 查出所有未读消息
     */
    @ResponseBody
    @PostMapping("/message/nums")
    public Map<String, Integer> nums() {
        int count = messageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", ShiroUtil.getProfileId()).eq("status", 0));
        return MapUtil.builder("status", 0).put("count", count).build();
    }

    @ResponseBody
    @PostMapping("/message/read")
    public Result read() {
        return Result.success();
    }
}
