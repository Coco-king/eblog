package top.codecrab.eblog.common.interceptor;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.codecrab.eblog.common.annotation.AccessLimit;
import top.codecrab.eblog.common.response.Result;
import top.codecrab.eblog.utils.CommonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 解决方案博主：https://onestar.newstar.net.cn/blog/73
 */
@Slf4j
@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            response.setStatus(org.springframework.http.HttpStatus.OK.value());
            return true;
        }

        // 判断请求是否属于方法的请求
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            // 获取方法中的注解,看是否有该注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (null == accessLimit) {
                return true;
            }

            // 指定时间内
            int seconds = accessLimit.seconds();
            // 允许请求次数
            int maxCount = accessLimit.maxCount();

            String ip = CommonUtils.getRemoteHost(request);
            String servletPath = request.getServletPath();
            String key = "visitCount:" + servletPath + ":" + ip;

            // 从redis中获取用户访问的次数
            String s = redisTemplate.opsForValue().get(key);
            if (s == null) s = "0";
            Integer count = Integer.valueOf(s);

            if (count <= 0) {
                // 第一次访问
                redisTemplate.opsForValue().set(key, "1", seconds, TimeUnit.SECONDS);
                log.info("{} 访问次数 =======> {}", servletPath, 1);
                return true;
            }

            if (count < maxCount) {
                // count加1
                count++;
                log.info("{} 访问次数 =======> {}", servletPath, count);
                redisTemplate.opsForValue().set(key, String.valueOf(count), seconds, TimeUnit.SECONDS);
                return true;
            }

            // 超出访问次数
            if (count >= maxCount) {
                //继续记录
                count++;
                redisTemplate.opsForValue().set(key, String.valueOf(count), seconds, TimeUnit.SECONDS);
                log.info("{} 访问次数 =======> {}", servletPath, count);
                //返回 json 请求过于频繁请稍后再试
                String jsonStr = JSONUtil.toJsonStr(new Result(-1, "请求过于频繁请稍后再试", null));
                try {
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("application/json; charset=utf-8");
                    response.getWriter().print(jsonStr);
                } catch (IOException e) {
                    log.error("{} 返回“请求过于频繁请稍后再试时”出错 ====> {}", servletPath, e.getMessage());
                }
                return false;
            }
        }
        return true;
    }
}
