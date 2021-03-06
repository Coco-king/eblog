package top.codecrab.eblog.shiro;

import cn.hutool.json.JSONUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.stereotype.Component;
import top.codecrab.eblog.common.response.Result;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 判断是否是ajax请求，指定返回信息
 */
@Component
public class AuthFilter extends UserFilter {

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        //X-Requested-With: XMLHttpRequest
        HttpServletRequest req = (HttpServletRequest) request;
        String header = req.getHeader("X-Requested-With");
        //表示这是一个Ajax请求
        if ("XMLHttpRequest".equals(header)) {
            //判断是否登录
            boolean authenticated = SecurityUtils.getSubject().isAuthenticated();
            if (!authenticated) {
                //返回json数据
                response.setContentType("application/json;charset=UTF-8");
                response.getOutputStream().print(JSONUtil.toJsonStr(Result.fail("登录授权信息已过期或未登录，请刷新页面或登陆")));
            }
        } else {
            //如果是url请求，走父类的方法
            super.redirectToLogin(request, response);
        }
    }
}
