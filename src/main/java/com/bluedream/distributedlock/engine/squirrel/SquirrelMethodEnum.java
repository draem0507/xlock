/*
 * Copyright (c) 2010-2016 meituan.com
 * All rights reserved.
 * 
 */
package com.bluedream.distributedlock.engine.squirrel;

/**
 * TODO 在这里编写类的功能描述
 *
 * @author jiangxu
 * @version 1.0
 * @created 2017-01-09
 */
public enum SquirrelMethodEnum {
    ADD(0, "setNx"),
    COMPARE_AND_DELETE(1, "compareAndDelete"),
    COMPARE_AND_SET(2,"compareAndSet"),
    ;

    private int code;
    private String field;

    SquirrelMethodEnum(int code, String field) {
        this.code = code;
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public int getCode() {
        return code;
    }
}
