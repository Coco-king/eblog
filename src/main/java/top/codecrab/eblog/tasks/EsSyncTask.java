package top.codecrab.eblog.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.codecrab.eblog.service.SearchService;

@Slf4j
@Component
public class EsSyncTask {

    @Autowired
    private SearchService searchService;

    /**
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void ScheduledInitEsData() {
        log.info("ES初始化成功，共有 {} 条数据", searchService.initES());
    }

}
