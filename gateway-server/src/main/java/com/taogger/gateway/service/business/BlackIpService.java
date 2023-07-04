package com.taogger.gateway.service.business;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.config.nacos.KJNcConfigManager;
import com.taogger.gateway.model.BlackIpEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * ip黑名单Service
 * @author taogger
 * @date 2022/8/15 18:03
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BlackIpService {

    private final ConfigService configService;

    @Value("${spring.cloud.nacos.config.group}")
    private String group;

    public ServerJSONResult list(int page, int limit) {
        PageRequest pageable = PageRequest.of(page - 1, limit);
        //分页
        List<BlackIpEntity> entities = KJNcConfigManager.getBlackIps().stream()
                .skip((page - 1) * limit)
                .limit(limit).collect(Collectors.toList());
        PageImpl<BlackIpEntity> blackIpEntities = new PageImpl<>(entities, pageable, KJNcConfigManager.getBlackIps().size());
        return ServerJSONResult.ok(blackIpEntities);
    }

    public List<BlackIpEntity> getNotExpire() {
        return KJNcConfigManager.getBlackIps();
    }

    public ServerJSONResult add(BlackIpEntity blackIpEntity) {
        LocalDateTime now = LocalDateTime.now();
        long time = now.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        if (blackIpEntity.getExpireTime().longValue() != -1l) {
            //毫秒相加不符合前端业务，需 *1000
            blackIpEntity.setExpireTime(time + blackIpEntity.getExpireTime() * 1000);
        }
        Long id = new Snowflake().nextId();
        blackIpEntity.setId(id.toString());
        KJNcConfigManager.saveBlackIp(blackIpEntity);
        return ServerJSONResult.ok();
    }

    public ServerJSONResult del(String id) {
        KJNcConfigManager.delBlackIp(id);
        return ServerJSONResult.ok();
    }

    @PostConstruct
    public void init() {
        //初始化调用-增加定时器定时过滤掉过期的数据
        //创建timer对象
        Timer timer = new Timer();
        //创建timerTask对象
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //过滤掉过期的数据
                List<BlackIpEntity> filterBlackIps = new ArrayList<>();
                LocalDateTime now = LocalDateTime.now();
                for (BlackIpEntity blackIpEntity : KJNcConfigManager.getBlackIps()) {
                    if (blackIpEntity.getExpireTime() != -1l) {
                        long time = now.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                        if (time > blackIpEntity.getExpireTime()) {
                            filterBlackIps.add(blackIpEntity);
                        }
                    }
                }
                //移除->更新
                if (!filterBlackIps.isEmpty()) {
                    KJNcConfigManager.getBlackIps().removeAll(filterBlackIps);
                    log.info("【进入黑名单ip定时任务清除数据,filterBlackIps:{}】", JSONUtil.toJsonStr(filterBlackIps));
                    try {
                        configService.publishConfig(KJNcConfigManager.blackIpDataId, group, JSONUtil.toJsonStr(KJNcConfigManager.getBlackIps()), ConfigType.JSON.getType());
                    } catch (NacosException e) {
                        log.error("【黑名单ip定时任务清除数据异常,异常信息为:{}】",e.getErrMsg());
                    }
                }
            }
        };
        //每隔两秒执行一次
        timer.schedule(timerTask, 2000, 2000);
    }
}
