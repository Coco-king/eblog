package top.codecrab.eblog.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.codecrab.eblog.service.UserService;
import top.codecrab.eblog.utils.ShiroUtil;

@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        AccountProfile profile = (AccountProfile) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Long id = profile.getId();
        //用户状态为99表示是管理员，给他添加角色
        if (id != null && id == 1L) {
            info.addRole("admin");
        }
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //controller的login方法进来到这里
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        //取出用户名/邮箱和密码
        String username = usernamePasswordToken.getUsername();
        String password = String.valueOf(usernamePasswordToken.getPassword());
        //调用service方法执行登录
        AccountProfile profile = userService.login(username, password);
        //存入缓存，让前台调用
        SecurityUtils.getSubject().getSession().setAttribute("userInfo", profile);
        return new SimpleAuthenticationInfo(profile, token.getCredentials(), getName());
    }
}
