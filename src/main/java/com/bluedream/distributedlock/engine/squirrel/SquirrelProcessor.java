package com.bluedream.distributedlock.engine.squirrel;

import com.bluedream.distributedlock.constants.DLMConstants;
import com.bluedream.distributedlock.model.ConfigInfoModel;
import com.bluedream.distributedlock.model.SquirrelConfigModel;
import com.dianping.squirrel.client.StoreKey;
import com.dianping.squirrel.client.impl.redis.RedisClientBuilder;
import com.dianping.squirrel.client.impl.redis.RedisStoreClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author: draem0507
 * @date: 2020-01-05 20:03
 * @desc:
 */
public class SquirrelProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SquirrelProcessor.class);


    /**
     * mt redis client
     */
    private RedisStoreClient redisStoreClient;

    /**
     * mt squirrel feature, aim at 保证同一个key，在集群中相互不受影响
     */
    private String category;


    public SquirrelProcessor(ConfigInfoModel configInfoModel) throws Exception {

        String env = configInfoModel.getEnvironment();

        SquirrelConfigModel squirrelConfigModel = configInfoModel.getSquirrelConfigModel();

        if (isValidConfig(squirrelConfigModel)) {
            init(configInfoModel.getAppKey(), squirrelConfigModel);
        } else {
            // 使用默认配置 根据线上或者线下环境进行不同的初始化
            if ("online".equalsIgnoreCase(env)) {
                init(configInfoModel.getAppKey(), DLMConstants.SquirrelConstants.SQUIRREL_ONLINE_CONF);
            } else {
                init(configInfoModel.getAppKey(), DLMConstants.SquirrelConstants.SQUIRREL_OFFLINE_CONF);
            }
        }


    }


    private void init(String appKey, SquirrelConfigModel squirrelConfigModel) {
        if (null != redisStoreClient) {
            this.destroy();
        }
        redisStoreClient = squirrelConfigModel.getRedisStoreClient();
        category = squirrelConfigModel.getCategory();
    }


    private void init(String appKey, String path) throws Exception {
        if (null != redisStoreClient) {
            this.destroy();
        }

        InputStream areaInputStream = null;
        try {
            LOGGER.info("SquirrelProcessor is initializing with default squirrel config!");
            LOGGER.warn("目前Cerberus使用的是公用Squirrel集群，请确认已获知使用公用Squirrel集群的风险且确定使用，否则请配置私有的Squirrel集群。");

            // 读取配置文件中的属性
            areaInputStream = SquirrelProcessor.class.getResourceAsStream(path);
            Properties properties = new Properties();
            properties.load(areaInputStream);

            category = properties.getProperty("category");
            String clusterName = properties.getProperty("clusterName");
            int readTimeout = Integer.parseInt(properties.getProperty("readTimeout"));
            String routerType = properties.getProperty("routerType");
            redisStoreClient = new RedisClientBuilder(clusterName).readTimeout(readTimeout).routerType(routerType).build();
        } catch (IOException ioException) {
            LOGGER.error("Loading the conf file is failed", ioException);
            throw ioException;
        } catch (Exception e) {
            LOGGER.error("Something wrong with SquirrelConstants initialisation", e);
            throw e;
        } finally {
            if (areaInputStream != null) {
                areaInputStream.close();
            }
        }
    }

    public void destroy() {
        redisStoreClient = null;
        LOGGER.info("SquirrelProcessor destruction success!");
    }


    public boolean add(StoreKey key, String value, int leaseTime, int retry) throws Exception {
        Boolean result;
        try {
            result = redisStoreClient.add(key, value, leaseTime);
        } catch (Exception e) {
            result = exceptionHandler(SquirrelMethodEnum.ADD, e, key, value, null, leaseTime, retry);
            if (false == result) {
                // 有可能客户端超时，但服务端已成功添加（key，value）
                if (redisStoreClient.exists(key)) {
                    // 若key存在，获取value1，并判断value1是否与value相等，相等则返回true
                    String getValue = redisStoreClient.get(key);
                    return StringUtils.equals(value, getValue);
                }
            }
        }

        return result;
    }

    public boolean compareAndDelete(StoreKey key, String value, int retry) throws Exception {
        Boolean result;
        try {
            result = redisStoreClient.compareAndDelete(key, value);
        } catch (Exception e) {
            result = exceptionHandler(SquirrelMethodEnum.COMPARE_AND_DELETE, e, key, value, null, -1, retry);
        }
        return result;
    }

    public boolean compareAndSet(StoreKey key, String oldValue, String newValue, int expireTime, int retry) throws Exception {
        Boolean result;
        try {
            result = redisStoreClient.compareAndSet(key, oldValue, newValue, expireTime);
        } catch (Exception e) {
            result = exceptionHandler(SquirrelMethodEnum.COMPARE_AND_DELETE, e, key, oldValue, newValue, expireTime, retry);
        }
        return result;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 处理squirrel的操作抛出的异常，根据重试次数进行回调
     *
     * @param type
     * @param squirrelException
     * @param key
     * @param oldValue
     * @param newValue
     * @param expireTime
     * @param retry
     * @return
     * @throws Exception
     */
    private boolean exceptionHandler(SquirrelMethodEnum type, Exception squirrelException, StoreKey key, String oldValue, String newValue,
                                     int expireTime, int retry) throws Exception {
        if (Thread.interrupted())
            throw new InterruptedException();

        boolean result = false;
        if (retry > 0) {
            retry--;
            Thread.sleep(100);
            LOGGER.info(type.getField() + " error; Retry:" + (retry + 1) + "; Key:" + key + "; exception:" + squirrelException.toString());
            switch (type) {
                case ADD:
                    result = this.add(key, oldValue, expireTime, retry);
                    break;
                case COMPARE_AND_DELETE:
                    result = this.compareAndDelete(key, oldValue, retry);
                    break;
                case COMPARE_AND_SET:
                    result = this.compareAndSet(key, oldValue, newValue, expireTime, retry);
                    break;
                default:
                    break;
            }
        } else {
            LOGGER.error(type.getField() + " error; Key:" + key, squirrelException);
            throw squirrelException;
        }

        return result;
    }


    private boolean isValidConfig(SquirrelConfigModel squirrelConfigModel) {
        if (null == squirrelConfigModel) {
            return false;
        }

        if (null != squirrelConfigModel.getRedisStoreClient() && StringUtils.isNotBlank(squirrelConfigModel.getCategory())) {
            return true;
        }

        return false;
    }
}
