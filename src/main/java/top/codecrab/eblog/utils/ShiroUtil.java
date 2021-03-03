package top.codecrab.eblog.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import top.codecrab.eblog.shiro.AccountProfile;

public class ShiroUtil {

    /**
     * 获取当前用户
     */
    public static AccountProfile getProfile() {
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 获取当前用户id
     */
    public static Long getProfileId() {
        AccountProfile profile = getProfile();
        if (profile == null) return null;
        return profile.getId();
    }

    /**
     * 切换身份，登录后，动态更改subject的用户属性
     */
    public static void setUser(AccountProfile profile) {
        Subject subject = SecurityUtils.getSubject();
        subject.getSession().setAttribute("userInfo", profile);
        PrincipalCollection principalCollection = subject.getPrincipals();
        String realmName = principalCollection.getRealmNames().iterator().next();
        PrincipalCollection newPrincipalCollection =
                new SimplePrincipalCollection(profile, realmName);
        subject.runAs(newPrincipalCollection);
    }
}
