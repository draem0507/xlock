package com.bluedream.distributedlock.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author: draem0507
 * @date: 2020-01-03 16:34
 * @desc:
 */
public interface Lock {


    void lock();

    boolean tryLock();

    boolean tryLock(long expire, TimeUnit unit) throws InterruptedException;

    void lockInterruptibly() throws InterruptedException;

    void unlock();

    String getName();


}
