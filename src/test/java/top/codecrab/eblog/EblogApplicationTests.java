package top.codecrab.eblog;

import cn.hutool.core.lang.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import top.codecrab.eblog.service.PostService;
import top.codecrab.eblog.utils.EMailUtils;
import top.codecrab.eblog.utils.RedisUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class EblogApplicationTests {

    @Autowired
    private PostService postService;

    @Autowired
    private EMailUtils eMailUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Test
    void name() {
        System.out.println(UUID.randomUUID().toString(true));
    }

    @Test
    void testEmail() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "测试");
        params.put("email", "测试");
        params.put("created", new Date());
        eMailUtils.sendMessageMail("3060550682@qq.com", params, "测试邮件", "/email/activeEmail.ftl");
    }

    /**
     * 测试更新score时，会不会刷新过期时间
     * <br>
     * 结论：可以
     */
    @Test
    void testZIncrementScore() {
    }
}
