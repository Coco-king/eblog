package top.codecrab.eblog.utils;


import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 发邮件工具类
 */
@Slf4j
@Component
public final class EMailUtils {
    //邮件的发送者
    @Value("${spring.mail.username}")
    private String from;

    //注入MailSender
    @Autowired
    private JavaMailSender mailSender;

    //发送邮件的模板引擎
    //@Autowired //会起冲突
    //private FreeMarkerConfigurer configurer;

    /**
     * @param params       发送邮件的主题对象 object
     * @param title        邮件标题
     * @param templateName 模板名称
     */
    public boolean sendMessageMail(String to, Object params, String title, String templateName) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);//发送者
            helper.setTo(InternetAddress.parse(to));//接收者
            helper.setSubject("【" + title + "-" + LocalDate.now() + " " + LocalTime.now().withNano(0) + "】");//邮件标题
            //添加附件
            /*FileSystemResource file = new FileSystemResource(new File("E:\\Test\\linchanglan.jpg"));
            helper.addAttachment("附件.jpg", file);*/
            Map<String, Object> model = new HashMap<>();
            model.put("params", params);
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
            configuration.setClassForTemplateLoading(this.getClass(), "/templates");
            //注入 FreeMarkerConfigurer 会有冲突
            // Template template = configurer.getConfiguration().getTemplate(templateName);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(
                    configuration.getTemplate(templateName), model
            );
            helper.setText(text, true);
            mailSender.send(mimeMessage);
            log.info("发送邮件成功！");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
