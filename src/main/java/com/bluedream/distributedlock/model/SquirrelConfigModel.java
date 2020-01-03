package com.bluedream.distributedlock.model;

import com.dianping.squirrel.client.impl.redis.RedisStoreClient;

/**
 * @author: draem0507
 * @date: 2020-01-03 17:52
 * @desc:
 */
public class SquirrelConfigModel {


    private RedisStoreClient redisStoreClient;


    /**
     * mt squirrel feature, aim at 保证同一个key，在集群中相互不受影响
     */
    private String category;


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public RedisStoreClient getRedisStoreClient() {
        return redisStoreClient;
    }

    public void setRedisStoreClient(RedisStoreClient redisStoreClient) {
        this.redisStoreClient = redisStoreClient;
    }
}
