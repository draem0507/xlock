package com.bluedream.distributedlock.engine.zk.util;

/**
 * @author: draem0507
 * @date: 2020-01-05 17:45
 * @desc:
 */
public interface ConnectionListener {


    void syncConnected();


    void expired();


    void disconnected();


}
