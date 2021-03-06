package top.codecrab.eblog.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.codecrab.eblog.common.response.Result;
import top.codecrab.eblog.entity.User;
import top.codecrab.eblog.entity.UserMessage;
import top.codecrab.eblog.utils.ValidationUtil;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Controller
public class VerifyController extends BaseController {

    @GetMapping("/captcha.jpg")
    public void captcha(HttpServletResponse response) throws IOException {
        //验证码文字
        String text = producer.createText();
        //放入shiro的session中
        SecurityUtils.getSubject().getSession().setAttribute(CAPTCHA_SESSION_KEY, text);
        //验证码图片
        BufferedImage image = producer.createImage(text);
        //设置页面不缓存
        response.setHeader("Cache-Control", "no-store, no-cache");
        //设置写入文件的类型
        response.setContentType("image/jpeg");
        //写入到页面
        ImageIO.write(image, "jpg", response.getOutputStream());
    }

    @GetMapping("/login")
    public String login() {
        return "verify/login";
    }

    @ResponseBody
    @PostMapping("/login")
    public Result doLogin(String username, String password) {
        if (StringUtils.isAnyBlank(username, password)) {
            return Result.fail("用户名或密码不能为空");
        }
        //生成token
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        //执行登录
        try {
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e) {
            if (e instanceof UnknownAccountException) {
                return Result.fail("用户不存在");
            } else if (e instanceof LockedAccountException) {
                return Result.fail("用户被禁用");
            } else if (e instanceof IncorrectCredentialsException) {
                return Result.fail("密码错误");
            } else {
                return Result.fail("用户认证失败");
            }
        }
        return Result.success().action("/");
    }

    @GetMapping("/register")
    public String register() {
        return "verify/reg";
    }

    @ResponseBody
    @PostMapping("/register")
    public Result doRegister(User user, String repass, String vercode) {
        //获取当前的session
        Session session = SecurityUtils.getSubject().getSession();
        //取出验证码
        String captcha = (String) session.getAttribute(CAPTCHA_SESSION_KEY);
        //删除验证码，防止暴力破解
        session.removeAttribute(CAPTCHA_SESSION_KEY);
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(user);
        if (validResult.hasErrors()) {
            return Result.fail(validResult.getErrors());
        }
        if (!user.getPassword().equals(repass)) {
            return Result.fail("两次密码输入不一致");
        }
        if (StringUtils.isBlank(captcha) || !captcha.equals(vercode)) {
            return Result.fail("图形验证码不匹配或已过期，请刷新后重试");
        }

        //验证通过，保存数据
        Result result = userService.register(user);
        if (result.getStatus() == -1) {
            //操作失败
            return result;
        }
        return result.action("/login");
    }

    @RequestMapping("/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:/login";
    }

    @GetMapping("/forgetPass")
    public String forgetPass() {
        return "/verify/forget";
    }

    @ResponseBody
    @GetMapping("/sendForgetCode")
    public Result sendForgetCode(String email) {
        if (StringUtils.isBlank(email)) return Result.fail("请输入已激活的邮箱");
        //验证码存入redis
        String code = producer.createText().toUpperCase();
        redisTemplate.opsForValue().set(FORGET_CAPTCHA_KEY + email, code, 300, TimeUnit.SECONDS);
        //发送邮件
        return userService.sendForgetEmail(email, code);
    }

    @ResponseBody
    @GetMapping("/checkForgetCode")
    public Result checkForgetCode(String code) {
        if (StringUtils.isBlank(code)) return Result.fail("请输入验证码");
        //获取当前的session
        Session session = SecurityUtils.getSubject().getSession();
        String captcha = (String) session.getAttribute(CAPTCHA_SESSION_KEY);
        session.removeAttribute(CAPTCHA_SESSION_KEY);
        if (!StringUtils.equals(code, captcha)) return Result.fail("图形验证码不匹配或已过期");
        return Result.success();
    }

    @ResponseBody
    @PostMapping("/forgetPass")
    public Result doForgetPass(String email, String pass, String repass, String vercode) {
        //取出验证码
        String captcha = redisTemplate.opsForValue().get(FORGET_CAPTCHA_KEY + email);
        //删除验证码，防止暴力破解
        //session.removeAttribute(FORGET_KAPTCHA_SESSION_KEY);
        if (!StringUtils.equals(vercode, captcha)) return Result.fail("验证码有误");

        if (StringUtils.isAnyBlank(pass, repass)) {
            return Result.fail("密码不能为空");
        }
        if (!StringUtils.equals(pass, repass)) {
            return Result.fail("两次输入的密码不一致");
        }
        User user = userService.getOne(new QueryWrapper<User>().eq("email", email));
        if (user == null) return Result.fail("未找到用户");

        if (StringUtils.equals(SecureUtil.md5(pass + user.getSalt()), user.getPassword())) {
            return Result.fail("修改失败，修改的密码不能和原密码相同");
        }

        //重新分配盐
        user.setSalt(UUID.randomUUID().toString(true));
        //加盐加密
        user.setPassword(SecureUtil.md5(pass + user.getSalt()));
        //更新
        boolean update = userService.updateById(user);
        return update ? Result.success().action("/login") : Result.fail("重置密码出现意外错误，请联系管理员");
    }
}
