package com.bluedream.distributedlock.lock.impl;

import com.bluedream.distributedlock.engine.IDLMEngine;
import com.bluedream.distributedlock.lock.Lock;
import com.bluedream.distributedlock.lock.LockSwitcher;

import java.util.concurrent.TimeUnit;

/**
 * @author: draem0507
 * @date: 2020-01-03 20:52
 * @desc:
 */
public class DLMReentrantLock implements Lock, LockSwitcher {

    private Lock curLock;

    private String lockName;

    private int expireTime = -1;

    private int retry = -1;

    public DLMReentrantLock(IDLMEngine idlmEngine, String lockName) {

        this.curLock = idlmEngine.getReentrantLock(lockName);

        this.lockName = lockName;
    }


    public DLMReentrantLock(IDLMEngine idlmEngine, String lockName, int expireTime) {

        this.curLock = idlmEngine.getReentrantLock(lockName, expireTime);

        this.lockName = lockName;
        this.expireTime = expireTime;
    }

    public DLMReentrantLock(IDLMEngine idlmEngine, String lockName, int expireTime, int retry) {

        this.curLock = idlmEngine.getReentrantLock(lockName, expireTime, retry);

        this.lockName = lockName;
        this.expireTime = expireTime;

        this.retry = retry;
    }


    @Override
    public void lock() {
        curLock.lock();


    }

    @Override
    public boolean tryLock() {
        return curLock.tryLock();
    }

    @Override
    public boolean tryLock(long expire, TimeUnit unit) throws InterruptedException {
        return curLock.tryLock(expire, unit);
    }

    @Override
    public boolean lockInterruptibly() throws InterruptedException {
        return curLock.lockInterruptibly();
    }

    @Override
    public void unlock() {
        curLock.unlock();
    }

    @Override
    public String getName() {
        return curLock.getName();
    }

    @Override
    public void switchLock(IDLMEngine idlmEngine) {

        curLock=idlmEngine.getReentrantLock(lockName,expireTime,retry);

    }

    @Override
    public String toString() {
        return "DLMReentrantLock{" + "curLock=" + curLock + ", lockName='" + lockName + '\'' + ", expireTime=" + expireTime + ", retry=" + retry + '}';
    }
}
