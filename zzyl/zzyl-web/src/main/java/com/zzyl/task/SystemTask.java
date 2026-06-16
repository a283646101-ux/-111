package com.zzyl.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class SystemTask {

    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyStatistics() {
        log.info("执行每日统计任务，当前时间：{}", LocalDateTime.now());
    }

    @Scheduled(fixedRate = 300000)
    public void healthCheck() {
        log.debug("系统健康检查，当前时间：{}", LocalDateTime.now());
    }
}
