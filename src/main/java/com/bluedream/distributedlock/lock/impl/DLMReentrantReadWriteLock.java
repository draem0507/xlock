package com.bluedream.distributedlock.lock.impl;

import com.bluedream.distributedlock.engine.IDLMEngine;
import com.bluedream.distributedlock.lock.Lock;
import com.bluedream.distributedlock.lock.LockSwitcher;
import com.bluedream.distributedlock.lock.ReadWriteLock;

import java.util.concurrent.TimeUnit;

/**
 * @author: draem0507
 * @date: 2020-01-03 21:09
 * @desc:
 */
public class DLMReentrantReadWriteLock implements ReadWriteLock, LockSwitcher {

    private ReadWriteLock curLock;
    private String lockName;
    private int expireTime = -1;
    private int retry = -1;


    public DLMReentrantReadWriteLock(IDLMEngine idlmEngine, String lockName) {

        curLock = idlmEngine.getReadWriteLock(lockName);
        this.lockName = lockName;

    }

    public DLMReentrantReadWriteLock(IDLMEngine idlmEngine, String lockName, int expireTime) {

        curLock = idlmEngine.getReadWriteLock(lockName, expireTime);
        this.lockName = lockName;
        this.expireTime = expireTime;


    }


    public DLMReentrantReadWriteLock(IDLMEngine idlmEngine, String lockName, int expireTime, int retry) {
        curLock = idlmEngine.getReadWriteLock(lockName, expireTime, retry);
        this.lockName = lockName;
        this.expireTime = expireTime;

        this.retry = retry;


    }

    @Override
    public Lock readLock() {
        return curLock.readLock();
    }

    @Override
    public Lock writeLock() {
        return curLock.writeLock();
    }

    @Override
    public void switchLock(IDLMEngine idlmEngine) {

        this.curLock=idlmEngine.getReadWriteLock(lockName,expireTime,retry);

    }
}
