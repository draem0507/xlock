/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.bluedream.distributedlock.common;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holder for information about a specific lock
 *
 * @author jiangxu
 * @version 1.0
 * @created 2015-09-24
 */
public class LockHolder {
    private final String lockNode;
    private final AtomicInteger numLocks = new AtomicInteger(1);
    private final int lockTime;

    public LockHolder(String lockNode) {
        this.lockNode = lockNode;
        this.lockTime = -1;
    }

    public LockHolder(int lockTime) {
        this.lockTime = lockTime;
        this.lockNode = "";
    }

    public void incrementLock() {
        numLocks.incrementAndGet();
    }

    public int decrementLock() {
        return numLocks.decrementAndGet();
    }

    public String getLockNode() {
        return lockNode;
    }

    public int getLockTime() {
        return lockTime;
    }

    public static boolean checkReentrancy(ThreadLocal<LockHolder> locks) {
        LockHolder local = locks.get();
        if(local!=null){
            local.incrementLock();
            return true;
        }
        return false;
    }

}
