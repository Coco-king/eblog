package top.codecrab.eblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codecrab.eblog.common.response.Result;
import top.codecrab.eblog.entity.User;
import top.codecrab.eblog.entity.UserMessage;
import top.codecrab.eblog.mapper.UserMapper;
import top.codecrab.eblog.service.UserMessageService;
import top.codecrab.eblog.service.UserService;
import top.codecrab.eblog.shiro.AccountProfile;
import top.codecrab.eblog.utils.CommonUtils;
import top.codecrab.eblog.utils.EMailUtils;
import top.codecrab.eblog.utils.ShiroUtil;
import top.codecrab.eblog.utils.ValidationUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-02-26
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Value("${server.dns-addr}")
    private String dnsAddr;

    @Autowired
    private EMailUtils eMailUtils;

    @Autowired
    private UserMessageService messageService;

    @Override
    @Transactional
    public Result register(User user) {
        //判断邮箱是否被占用
        int count = this.count(new QueryWrapper<User>().eq("email", user.getEmail()));
        if (count > 0) return Result.fail("邮箱已被占用");

        //判断用户名是否被占用
        count = this.count(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (count > 0) return Result.fail("用户名已被占用");

        //打包对象,使用新的对象接收，防止被恶意注入
        user = packageUser(user);
        //执行保存
        boolean save = this.save(user);
        if (save) {
            //向用户发送系统消息
            UserMessage message = new UserMessage();
            message.setFromUserId(0L);
            message.setToUserId(user.getId());
            message.setPostId(0L);
            message.setCommentId(0L);
            message.setContent("欢迎加入竹隐寒烟！请到个人中心激活邮箱吧！");
            message.setType(0);
            message.setCreated(new Date());
            message.setModified(message.getCreated());
            message.setStatus(0);
            messageService.save(message);
        }
        return save ? Result.success() : Result.fail("注册用户出现意外错误");
    }

    @Override
    public AccountProfile login(String username, String password) {
        User user = this.getOne(new QueryWrapper<User>()
                .eq("username", username).or().eq("email", username));
        if (user == null) {
            throw new UnknownAccountException();
        }
        //加密用户输入的密码
        String saltPassword = SecureUtil.md5(password + user.getSalt());
        //与数据库中的做对比
        if (!saltPassword.equals(user.getPassword())) {
            throw new IncorrectCredentialsException();
        }

        //登陆成功
        user.setLasted(new Date());
        this.updateById(user);

        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user, profile);
        return profile;
    }

    @Override
    public Result doSet(AccountProfile user) {
        AccountProfile profile = ShiroUtil.getProfile();
        if (profile == null) return Result.fail("找不到当前用户，请重新登陆");

        //是上传头像的
        if (StringUtils.isNotBlank(user.getAvatar())) {
            User one = this.getById(profile.getId());
            if (one == null) return Result.fail("找不到当前用户，请重新登陆");
            //更新数据库的
            one.setAvatar(user.getAvatar());
            this.updateById(one);
            //刷新缓存中的
            profile.setAvatar(user.getAvatar());
            ShiroUtil.setUser(profile);
            return Result.success().action("/user/set#avatar");
        }

        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(user);
        if (validResult.hasErrors()) {
            return Result.fail(validResult.getErrors());
        }
        User temp = this.getById(profile.getId());
        if (temp == null) return Result.fail("找不到当前用户，请重新登陆");

        //查询用户名是否有重复的，不算自己的
        int count = this.count(new QueryWrapper<User>()
                .eq("username", user.getUsername()).ne("id", profile.getId()));
        if (count > 0) return Result.fail("该昵称已被占用");
        count = this.count(new QueryWrapper<User>()
                .eq("email", user.getEmail()).ne("id", profile.getId()));
        if (count > 0) return Result.fail("该邮箱已被注册");

        //更换邮箱了，设置激活状态为-1
        if (!StringUtils.equals(user.getEmail(), temp.getEmail())) {
            temp.setStatus(-1);
        }

        temp.setCity(user.getCity());
        temp.setGender(user.getGender());
        temp.setEmail(user.getEmail());
        temp.setUsername(user.getUsername());
        temp.setSign(user.getSign());
        this.updateById(temp);

        //更新缓存的值
        BeanUtil.copyProperties(temp, profile);
        ShiroUtil.setUser(profile);
        return Result.success().action("/user/set#info");
    }

    @Override
    public Result sendActivateEmail(String email) {
        User user = this.getById(ShiroUtil.getProfileId());
        if (!StringUtils.equals(user.getEmail(), email)) {
            return Result.fail("当前用户邮箱与所请求的邮箱不一致");
        }
        String username = user.getUsername();
        String title = "亲爱的【" + username + "】！请验证您的邮箱";
        String href = dnsAddr + "/user/activation/" + user.getCode();
        Map<String, Object> params = new HashMap<>();
        params.put("href", href);
        params.put("username", username);
        params.put("email", email);
        params.put("created", new Date());
        params.put("type", 0);//表示这是激活邮件
        boolean sendEMail = eMailUtils.sendMessageMail(email, params, title, "/email/activeEmail.ftl");
        return sendEMail ? Result.success() : Result.fail("由于未知原因，邮件发送失败，请重试");
    }

    @Override
    public Result sendForgetEmail(String email, String code) {
        User user = this.getOne(new QueryWrapper<User>().eq("email", email));
        if (user == null) return Result.fail("未找到用户");
        if (user.getStatus() == -1) return Result.fail("该邮箱未激活，无法找回密码");

        String username = user.getUsername();
        String title = "亲爱的【" + username + "】！这是一封验证码邮件";
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("username", username);
        params.put("email", email);
        params.put("created", new Date());
        params.put("type", 1);//表示这是重置密码邮件
        boolean sendEMail = eMailUtils.sendMessageMail(email, params, title, "/email/activeEmail.ftl");
        return sendEMail ? Result.success() : Result.fail("由于未知原因，邮件发送失败，请重试");
    }

    /**
     * 把页面传入的用户对象重新打包赋值
     */
    private User packageUser(User user) {
        Date now = new Date();
        User result = new User();
        result.setUsername(user.getUsername());
        result.setEmail(user.getEmail());
        //获取盐，生成简单模式的uuid，不带 "-"
        String salt = UUID.randomUUID().toString(true);
        result.setSalt(salt);
        //给密码加盐加密
        String password = SecureUtil.md5(user.getPassword() + salt);
        result.setPassword(password);
        //记录一个坑，此处最前边不加 "/" 会导致访问资源时加上上级路径
        result.setAvatar("/res/images/avatar/default.png");
        result.setStatus(-1);
        result.setModified(now);
        result.setCreated(now);
        result.setWechat(null);
        result.setVipLevel(0);
        result.setSign(CommonUtils.getSign());
        result.setPostCount(0);
        result.setPoint(0);
        result.setMobile(null);
        result.setLasted(null);
        result.setGender("-1");
        result.setCommentCount(0);
        result.setBirthday(null);
        result.setCity("地球");
        result.setCode(UUID.randomUUID().toString(true));
        return result;
    }
}
