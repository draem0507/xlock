package com.bluedream.distributedlock.constants;

/**
 * @author: draem0507
 * @date: 2020-01-03 20:12
 * @desc:
 */
public class DLMConstants {


    private DLMConstants(){}


    public static final String LOG_ENGINE = "engineLog";
    public static final String LOG_TAIR = "tairLog";
    public static final String LOCAL_APPKEY = "com.sankuai.travel.dsg.cerberus";


    public interface ZKConstants{

        String ZK_ONLINE_CONF="/cerberus_zk/zk-online.properties";
        String ZK_OFFLINE_CONF="/cerberus_zk/zk-offline.properties";
    }


    public interface  TairConstants{

        String TAIR_ONLINE_CONF="/cerberus_tair/tair-online.conf";
        String TAIR_OFFLINE_CONF="/cerberus_tair/tair-offline.conf";

        /**
         * 超时时间600 seconds
         */
        int EXPIRE_TIME=600;

        /**
         * try count
         */
        int RETRY=3;

        /**
         * 自旋时间 50 ms
         */
        int SPIN_AWAIT_TIME=50;



    }




    public interface SquirrelConstants{

        String SQUIRREL_ONLINE_CONF="/cerberus_squirrel/squirrel-online.properties";

        String SQUIRREL_OFFLINE_CONF="/cerberus_squirrel/squirrel-offline.properties";

        /**
         * 超时时间600 seconds
         */
        int EXPIRE_TIME=600;

        /**
         * try count
         */
        int RETRY=3;

        /**
         * 自旋时间 50 ms
         */
        int SPIN_AWAIT_TIME=50;

    }

}
