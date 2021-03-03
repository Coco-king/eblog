package top.codecrab.eblog.config;

import cn.hutool.core.map.MapUtil;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.codecrab.eblog.shiro.AccountRealm;
import top.codecrab.eblog.shiro.AuthFilter;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public SessionManager sessionManager(RedisSessionDAO redisSessionDAO) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    @Bean
    public SessionsSecurityManager securityManager(AccountRealm realm, SessionManager sessionManager, RedisCacheManager redisCacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setSessionManager(sessionManager);
        securityManager.setCacheManager(redisCacheManager);
        return securityManager;
    }

    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(
            SecurityManager securityManager,
            AuthFilter authFilter
    ) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        //配置登录的URL和登陆成功的url
        shiroFilter.setLoginUrl("/login");
        shiroFilter.setSuccessUrl("/user/center");
        //设置未授权跳转页面
        shiroFilter.setUnauthorizedUrl("/error/403");

        //设置自定义过滤器
        shiroFilter.setFilters(MapUtil.of("auth", authFilter));

        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/user/home", "auth");
        filterMap.put("/user/set", "auth");
        filterMap.put("/user/upload", "auth");
        filterMap.put("/user/activate", "auth");
        filterMap.put("/user/message", "auth");
        filterMap.put("/user/index", "auth");

        filterMap.put("/collection/add", "auth");
        filterMap.put("/collection/remove", "auth");

        filterMap.put("/post/edit", "auth");

        filterMap.put("/**", "anon");
        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;
    }
}
