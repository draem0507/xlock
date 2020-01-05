package com.bluedream.distributedlock.engine.squirrel;

import com.bluedream.distributedlock.common.LockHolder;
import com.bluedream.distributedlock.constants.DLMConstants;
import com.bluedream.distributedlock.lock.Lock;
import com.dianping.squirrel.client.StoreKey;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvm.hotspot.debugger.cdbg.TemplateType;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author: draem0507
 * @date: 2020-01-05 20:16
 * @desc:
 */
public class ReentrantSquirrelLock implements Lock {

    private static final Logger LOG = LoggerFactory.getLogger(ReentrantSquirrelLock.class);

    private SquirrelProcessor squirrelProcessor;


    /**
     * lock name
     */
    private String lockName;

    /**
     * squirrel key
     */
    private StoreKey key;


    private int expireTime;

    private String uuid;

    private int retry;

    private ThreadLocal<LockHolder> locks = new ThreadLocal<>();


    public ReentrantSquirrelLock(String lockName, SquirrelProcessor squirrelProcessor, int expireTime, int retry) {

        this.lockName = lockName;

        this.squirrelProcessor = squirrelProcessor;

        this.expireTime = expireTime;

        this.retry = retry;

        this.uuid = UUID.randomUUID().toString();

        this.key = new StoreKey(squirrelProcessor.getCategory(), lockName);
    }


    @Override
    public void lock() {

        if (reentrant()) {
            return;
        }
        //自旋
        while (true) {
            try {
                if (squirrelProcessor.add(key, uuid, expireTime, retry)) {
                    locks.set(new LockHolder(key.toString()));
                    LOG.info("Try acquire lock success. key: {}, uuid: {}", key, uuid);
                    return;

                } else {
                    LOG.debug("Try acquire lock failed. key: {}, uuid: {}", key, uuid);

                }
            } catch (Exception e) {
                LOG.error("Redis try acquire lock failed, key: {}", key, e);
                throw new RuntimeException(e);
            }
            LockSupport.parkNanos(this, TimeUnit.MILLISECONDS.toNanos(DLMConstants.SquirrelConstants.SPIN_AWAIT_TIME));


        }
    }

    @Override
    public boolean tryLock() {

        // 锁重入检查
        if (reentrant()) {
            return true;
        }


        try {
            if (squirrelProcessor.add(key, uuid, expireTime, retry)) {
                locks.set(new LockHolder(key.toString()));
                LOG.info("Try acquire lock success. key: {}, uuid: {}", key, uuid);
                return true;
            } else {
                LOG.debug("Try acquire lock failed. key: {}, uuid: {}", key, uuid);
                return false;
            }
        } catch (Exception e) {
            LOG.error("Redis try acquire lock failed, key: {}", key, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean tryLock(long expire, TimeUnit unit) throws InterruptedException {
        Preconditions.checkNotNull(unit, "时间单位不能为空");

        // 锁重入检查
        if (reentrant()) {
            return true;
        }

        long timeout = System.nanoTime() + unit.toNanos(expire);
        if (timeout < 0) {
            timeout = Long.MAX_VALUE;
        }
        while (true) {
            try {
                if (squirrelProcessor.add(key, uuid, expireTime, retry)) {
                    locks.set(new LockHolder(key.toString()));
                    LOG.info("Try acquire lock success. key: {}, uuid: {}", key, uuid);
                    return true;
                }
            } catch (InterruptedException e) {
                throw new InterruptedException();
            } catch (Exception e) {
                LOG.error("Redis try acquire lock failed, key: {}", key, e);
                throw new RuntimeException(e);
            }

            if (System.nanoTime() >= timeout) {
                LOG.debug("Try acquire lock timeout. key: {}, uuid: {}", key, uuid);
                return false;
            } else {
                LOG.debug("Try acquire lock failed, will try again. key: {}, uuid: {}", key, uuid);
            }

            LockSupport.parkNanos(this, TimeUnit.MILLISECONDS.toNanos(DLMConstants.SquirrelConstants.SPIN_AWAIT_TIME));

            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        tryLock(Long.MAX_VALUE, TimeUnit.DAYS);

    }

    @Override
    public void unlock() {

        LockHolder lockHolder = this.locks.get();
        if (lockHolder == null) {
            throw new IllegalMonitorStateException("Attempting to unlock without first obtaining that lock on this thread");
        }

        int lockCounts = lockHolder.decrementLock();
        try {
            if (lockCounts == 0) {
                locks.remove();
                if (squirrelProcessor.compareAndDelete(key, uuid, retry)) {
                    LOG.info("Release lock success. key: {}, uuid: {}", key, uuid);
                } else {
                    LOG.debug("Release lock failed. key: {}, uuid: {}", key, uuid);
                }
            }
        } catch (Exception e) {
            LOG.error("Redis try release lock failed. key: {}", key, e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getName() {
        return lockName;
    }


    /**
     * 锁重入检查
     *
     * @return 重入则返回 true; 否则返回 false
     */
    private boolean reentrant() {
        try {
            if (LockHolder.checkReentrancy(locks)) {
                // reentrant, refresh lease time
                return squirrelProcessor.compareAndSet(key, this.uuid, this.uuid, expireTime, retry);
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Redis refresh lease time failed, key: " + key, e);
        }
    }

    @Override
    public String toString() {
        return "ReentrantSquirrelLock{" + "squirrelProcessor=" + squirrelProcessor + ", lockName='" + lockName + '\'' + ", key=" + key + ", expireTime=" + expireTime + ", uuid='" + uuid + '\'' + ", retry=" + retry + ", locks=" + locks + '}';
    }
}
