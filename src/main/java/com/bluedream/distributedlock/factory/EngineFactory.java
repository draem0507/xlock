package com.bluedream.distributedlock.factory;

import com.bluedream.distributedlock.engine.IDLMEngine;
import com.bluedream.distributedlock.engine.zk.ZKEngine;
import com.sun.org.apache.regexp.internal.RE;

/**
 * @author: draem0507
 * @date: 2020-01-03 17:39
 * @desc:
 */
public class EngineFactory {


    private final static String ZK = "zk";
    private final static String TAIR = "tair";
    private final static String SQUIRREL = "squirrel";

    private EngineFactory() {
    }


    public static IDLMEngine createEngine(String engineName) {
        switch (engineName) {

            case ZK:
                return new ZKEngine();
            default:
                return null;

        }

    }


}
