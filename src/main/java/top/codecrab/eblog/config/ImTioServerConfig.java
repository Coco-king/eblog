package top.codecrab.eblog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.codecrab.eblog.im.handler.MsgHandlerFactory;
import top.codecrab.eblog.im.server.ImServerStarter;

import java.io.IOException;

@Slf4j
@Configuration
public class ImTioServerConfig {

    @Value("${im.server.port}")
    private int imPort;

    @Bean
    public ImServerStarter imServiceStarter() {
        try {
            //启动tio服务
            ImServerStarter serviceStarter = new ImServerStarter(imPort);
            serviceStarter.start();

            //设置处理器类别
            MsgHandlerFactory.init();

            return serviceStarter;
        } catch (IOException e) {
            log.error("启动tio服务失败", e);
        }
        return null;
    }
}
