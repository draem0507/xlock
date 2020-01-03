package com.bluedream.distributedlock.lock;

import com.bluedream.distributedlock.lock.Lock;

/**
 * @author: draem0507
 * @date: 2020-01-03 16:37
 * @desc:
 */
public interface ReadWriteLock {


    Lock readLock();

    Lock writeLock();


}
