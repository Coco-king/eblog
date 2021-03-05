package top.codecrab.eblog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.code.kaptcha.Producer;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.ServletRequestUtils;
import top.codecrab.eblog.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseController {

    protected static final String CAPTCHA_SESSION_KEY = "CAPTCHA_SESSION_KEY";
    protected static final String POST_CAPTCHA_KEY = "captcha:post:";
    protected static final String FORGET_CAPTCHA_KEY = "captcha:user:";

    @Autowired
    protected Producer producer;

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    @Autowired
    protected PostService postService;

    @Autowired
    protected CommentService commentService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserMessageService messageService;

    @Autowired
    protected UserCollectionService collectionService;

    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected AdminService adminService;

    @Autowired
    protected SearchService searchService;

    @Autowired
    protected AmqpTemplate amqpTemplate;

    @Autowired
    protected StringRedisTemplate redisTemplate;

    /**
     * 抽取获取分页bean
     *
     * @param <T> 分页的泛型
     */
    protected <T> Page<T> getPage() {
        //当前页
        int cp = ServletRequestUtils.getIntParameter(request, "cp", 1);
        //一页显示多少条
        int ps = ServletRequestUtils.getIntParameter(request, "ps", 10);
        //包装分页
        return new Page<>(cp, ps);
    }

    /**
     * 发送消息到rabbitmq
     */
    protected void sendMsg(String routingKey, Long id) {
        try {
            //捕获异常，消息发送成功与否不能影响方法本身的事务
            amqpTemplate.convertAndSend(routingKey, id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }
}
