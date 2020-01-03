package com.bluedream.distributedlock.engine;

import com.bluedream.distributedlock.lock.Lock;
import com.bluedream.distributedlock.lock.ReadWriteLock;
import com.bluedream.distributedlock.model.ConfigInfoModel;

/**
 * @author: draem0507
 * @date: 2020-01-03 16:39
 * @desc:
 */
public interface IDLMEngine {


    boolean init(ConfigInfoModel configInfoModel);


    void destroy();


    Lock getReentrantLock(String lockName);

    Lock getReentrantLock(String lockName, int expireTime);

    Lock getReentrantLock(String lockName, int expireTime, int retry);

    ReadWriteLock getReadWriteLock(String lockName);

    ReadWriteLock getReadWriteLock(String lockName, int expireTime);

    ReadWriteLock getReadWriteLock(String lockName, int expireTime, int retry);

    boolean isInitialized();

    void setIsInitialized(boolean isInitialized);

    String getEngineName();

    void setEngineName(String engineName);

    Object getProcessor();
}