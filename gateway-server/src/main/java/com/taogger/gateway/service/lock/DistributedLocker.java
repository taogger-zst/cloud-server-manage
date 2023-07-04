package com.taogger.gateway.service.lock;

import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁redis加工
 * @author chennz
 * @date 2021/9/8 9:49
 */
public interface DistributedLocker {

    RLock get(String lockKey);

    RLock lock(String lockKey);

    RLock lock(String lockKey, int timeout);

    RLock lock(String lockKey, TimeUnit unit, int timeout);

    boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime);

    void unlock(String lockKey);

    void unlock(RLock lock);

    boolean tryFairLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime);

    boolean existKey(String key);
}
