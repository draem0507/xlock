package com.bluedream.distributedlock.engine.zk;

import com.bluedream.distributedlock.engine.AbstractDLMEngine;
import com.bluedream.distributedlock.lock.Lock;
import com.bluedream.distributedlock.lock.ReadWriteLock;
import com.bluedream.distributedlock.model.ConfigInfoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: draem0507
 * @date: 2020-01-03 20:10
 * @desc:
 */
public class ZKEngine extends AbstractDLMEngine {
    private static final Logger LOGGER= LoggerFactory.getLogger(ZKEngine.class);


    private String host;

    private int timeout;

    private String baseLockPath;

    @Override
    public boolean init(ConfigInfoModel configInfoModel) {
        return false;
    }

    @Override
    public void destroy() {

    }

    @Override
    public Lock getReentrantLock(String lockName) {
        return null;
    }

    @Override
    public Lock getReentrantLock(String lockName, int expireTime) {
        return null;
    }

    @Override
    public Lock getReentrantLock(String lockName, int expireTime, int retry) {
        return null;
    }

    @Override
    public ReadWriteLock getReadWriteLock(String lockName) {
        return null;
    }

    @Override
    public ReadWriteLock getReadWriteLock(String lockName, int expireTime) {
        return null;
    }

    @Override
    public ReadWriteLock getReadWriteLock(String lockName, int expireTime, int retry) {
        return null;
    }

    @Override
    public Object getProcessor() {
        return null;
    }
}
