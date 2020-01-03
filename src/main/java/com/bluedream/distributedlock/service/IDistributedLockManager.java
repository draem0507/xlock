package com.bluedream.distributedlock.service;

import com.bluedream.distributedlock.lock.Lock;
import com.bluedream.distributedlock.lock.ReadWriteLock;

/**
 * @author: draem0507
 * @date: 2020-01-03 16:33
 * @desc:
 */
public interface IDistributedLockManager {

    /**
     * init config
     */
    void init();


    /**
     * destroy init resources ,like release zk connection etc..
     */
    void destroy();


    /**
     * switch engine
     *
     * @return
     */

    String switchEngine();

    /**
     * switch engine by name
     *
     * @param engineName
     * @return
     */
    String switchEngine(String engineName);


    /**
     * get lock
     *
     * @param lockName
     * @return
     */
    Lock getReentrantLock(String lockName);


    /**
     * get lock with  expireTime
     *
     * @param lockName
     * @param expireTime
     * @return
     */
    Lock getReentrantLock(String lockName, int expireTime);


    /**
     * get lock with expireTime and retry
     *
     * @param lockName
     * @param expireTime
     * @param retry
     * @return
     */
    Lock getReentrantLock(String lockName, int expireTime, int retry);


    /**
     * get readWriter lock
     *
     * @param lockName
     * @return
     */
    ReadWriteLock getReentrantReadWriteLock(String lockName);

    /**
     * get readWriter lock with expireTime
     *
     * @param lockName
     * @param expireTime
     * @return
     */

    ReadWriteLock getReentrantReadWriterLock(String lockName, int expireTime);


    /**
     * get readWriter lock with expireTime and retry
     *
     * @param lockName
     * @param expireTime
     * @param retry
     * @return
     */
    ReadWriteLock getReentrantReadWriterLock(String lockName, int expireTime, int retry);


}
