package com.bluedream.distributedlock.lock;

import com.bluedream.distributedlock.engine.IDLMEngine;

/**
 * @author: draem0507
 * @date: 2020-01-03 16:39
 * @desc:
 */
public interface LockSwitcher {


    void switchLock(IDLMEngine idlmEngine);
}
