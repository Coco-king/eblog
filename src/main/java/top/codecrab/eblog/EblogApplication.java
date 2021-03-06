package top.codecrab.eblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling //开启定时任务
public class EblogApplication {

    public static void main(String[] args) {
        SpringApplication.run(EblogApplication.class, args);
        System.out.println("http://localhost:9090");
    }

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
    }
}
