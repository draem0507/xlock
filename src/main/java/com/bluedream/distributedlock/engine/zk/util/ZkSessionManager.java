package com.bluedream.distributedlock.engine.zk.util;

import org.apache.zookeeper.ZooKeeper;

/**
 * @author: draem0507
 * @date: 2020-01-05 17:44
 * @desc:
 */
public interface ZkSessionManager {


    ZooKeeper getZooKeeper();



    void closeSession();



    void addConnectionListener(ConnectionListener listener);


    void removeConnectionListener(ConnectionListener listener);

}
