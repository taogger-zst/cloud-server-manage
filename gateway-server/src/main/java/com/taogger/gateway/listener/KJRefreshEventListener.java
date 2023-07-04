package com.taogger.gateway.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.cloud.endpoint.event.RefreshEventListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

/**
 * @author taogger
 * @date 2022/11/29 10:31
 */
@Component
@Slf4j
public class KJRefreshEventListener extends RefreshEventListener {
    public KJRefreshEventListener(ContextRefresher refresh) {
        super(refresh);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            handle((ApplicationReadyEvent) event);
        }
        else if (event instanceof RefreshEvent) {
            log.info("【触发刷新事件:desc:{},time:{}】",((RefreshEvent) event).getEventDesc(),event.getTimestamp());
        }
    }
}
