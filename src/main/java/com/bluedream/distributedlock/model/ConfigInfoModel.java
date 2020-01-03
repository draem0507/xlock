package com.bluedream.distributedlock.model;

/**
 * @author: draem0507
 * @date: 2020-01-03 17:40
 * @desc:
 */
public class ConfigInfoModel {


    private String appKey;


    private String environment;


    private TairConfigModel tairConfigModel;

    private ZKConfigModel zkConfigModel;


    private SquirrelConfigModel squirrelConfigModel;


    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public TairConfigModel getTairConfigModel() {
        return tairConfigModel;
    }

    public void setTairConfigModel(TairConfigModel tairConfigModel) {
        this.tairConfigModel = tairConfigModel;
    }

    public ZKConfigModel getZkConfigModel() {
        return zkConfigModel;
    }

    public void setZkConfigModel(ZKConfigModel zkConfigModel) {
        this.zkConfigModel = zkConfigModel;
    }

    public SquirrelConfigModel getSquirrelConfigModel() {
        return squirrelConfigModel;
    }

    public void setSquirrelConfigModel(SquirrelConfigModel squirrelConfigModel) {
        this.squirrelConfigModel = squirrelConfigModel;
    }
}
