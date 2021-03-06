package top.codecrab.eblog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.codecrab.eblog.utils.CommonUtils;
import top.codecrab.eblog.utils.EMailUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class EblogApplicationTests {

    @Autowired
    private EMailUtils eMailUtils;

    @Test
    void name() {
        System.out.println(CommonUtils.getSign());
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
