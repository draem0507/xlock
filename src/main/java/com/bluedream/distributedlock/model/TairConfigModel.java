package com.bluedream.distributedlock.model;

import com.taobao.tair3.client.config.impl.TairConfig;

/**
 * @author: draem0507
 * @date: 2020-01-03 17:42
 * @desc:
 */
public class TairConfigModel {


    private String path;


    private String area;


    private TairConfig tairConfig;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public TairConfig getTairConfig() {
        return tairConfig;
    }

    public void setTairConfig(TairConfig tairConfig) {
        this.tairConfig = tairConfig;
    }
}
