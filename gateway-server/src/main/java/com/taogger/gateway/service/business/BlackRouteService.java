package com.taogger.gateway.service.business;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.taogger.common.utils.ServerJSONResult;
import com.taogger.gateway.config.nacos.KJNcConfigManager;
import com.taogger.gateway.model.BlackRouteEntity;
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
 * 路由黑名单Service
 * @author taogger
 * @date 2022/8/16 10:33
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BlackRouteService {

    private final ConfigService configService;

    @Value("${spring.cloud.nacos.config.group}")
    private String group;

    public ServerJSONResult list(int page, int limit) {
        PageRequest pageable = PageRequest.of(page - 1, limit);
        //分页
        List<BlackRouteEntity> entities = KJNcConfigManager.getBlackRoutes().stream()
                .skip((page - 1) * limit)
                .limit(limit).collect(Collectors.toList());
        PageImpl blackIpEntities = new PageImpl<>(entities, pageable, KJNcConfigManager.getBlackRoutes().size());
        return ServerJSONResult.ok(blackIpEntities);
    }

    public List<BlackRouteEntity> getNotExpire() {
        return KJNcConfigManager.getBlackRoutes();
    }

    public ServerJSONResult add(BlackRouteEntity blackRouteEntity) {
        LocalDateTime now = LocalDateTime.now();
        long time = now.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        if (blackRouteEntity.getExpireTime().longValue() != -1l) {
            //毫秒相加不符合前端业务，需 *1000
            blackRouteEntity.setExpireTime(time + blackRouteEntity.getExpireTime() * 1000);
        }
        Long id = new Snowflake().nextId();
        blackRouteEntity.setId(id.toString());
        KJNcConfigManager.saveBlackRoute(blackRouteEntity);
        return ServerJSONResult.ok();
    }

    public ServerJSONResult del(String id) {
        KJNcConfigManager.delBlackRoute(id);
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
                List<BlackRouteEntity> filterBlackRoutes = new ArrayList<>();
                LocalDateTime now = LocalDateTime.now();
                for (BlackRouteEntity blackRouteEntity : KJNcConfigManager.getBlackRoutes()) {
                    if (blackRouteEntity.getExpireTime() != -1l) {
                        long time = now.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                        if (time > blackRouteEntity.getExpireTime()) {
                            filterBlackRoutes.add(blackRouteEntity);
                        }
                    }
                }
                //移除->更新
                if (!filterBlackRoutes.isEmpty()) {
                    KJNcConfigManager.getBlackRoutes().removeAll(filterBlackRoutes);
                    log.info("【进入黑名单路由定时任务清除数据,filterBlackIps:{}】", JSONUtil.toJsonStr(filterBlackRoutes));
                    try {
                        configService.publishConfig(KJNcConfigManager.blackRouteDataId, group, JSONUtil.toJsonStr(KJNcConfigManager.getBlackRoutes()), ConfigType.JSON.getType());
                    } catch (NacosException e) {
                        log.error("【黑名单路由定时任务清除数据异常,异常信息为:{}】",e.getErrMsg());
                    }
                }
            }
        };
        //每隔两秒执行一次
        timer.schedule(timerTask, 2000, 2000);
    }
}
