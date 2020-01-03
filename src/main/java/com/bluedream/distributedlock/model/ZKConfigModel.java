package com.bluedream.distributedlock.model;

/**
 * @author: draem0507
 * @date: 2020-01-03 17:42
 * @desc:
 */
public class ZKConfigModel {


    private String zkHost;


    private int zkTimeout;


    public String getZkHost() {
        return zkHost;
    }

    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }

    public int getZkTimeout() {
        return zkTimeout;
    }

    public void setZkTimeout(int zkTimeout) {
        this.zkTimeout = zkTimeout;
    }
}
