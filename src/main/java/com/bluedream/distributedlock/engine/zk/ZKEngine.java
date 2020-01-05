package com.bluedream.distributedlock.engine.zk;

import com.bluedream.distributedlock.constants.DLMConstants;
import com.bluedream.distributedlock.engine.AbstractDLMEngine;
import com.bluedream.distributedlock.engine.zk.lock.ReentrantZkLock;
import com.bluedream.distributedlock.engine.zk.lock.ReentrantZkReadWriteLock;
import com.bluedream.distributedlock.engine.zk.util.DefaultZkSessionManager;
import com.bluedream.distributedlock.engine.zk.util.ZkSessionManager;
import com.bluedream.distributedlock.lock.Lock;
import com.bluedream.distributedlock.lock.ReadWriteLock;
import com.bluedream.distributedlock.model.ConfigInfoModel;
import com.bluedream.distributedlock.model.ZKConfigModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author: draem0507
 * @date: 2020-01-03 20:10
 * @desc:
 */
public class ZKEngine extends AbstractDLMEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(DLMConstants.LOG_ENGINE);


    private String host;

    private int timeout;

    private String baseLockPath;


    private ZkSessionManager zkSessionManager;

    public ZKEngine() {

        engineName = "zk";


    }


    @Override
    public boolean init(ConfigInfoModel configInfoModel) {
        try {
            loadConfig(configInfoModel);
        } catch (Exception e) {
            LOGGER.error("Something wrong with zk engine initialisation", e);
            return false;

        }
        zkSessionManager = new DefaultZkSessionManager(host, timeout);
        setIsInitialized(true);


        return true;
    }

    @Override
    public void destroy() {

        if (null != zkSessionManager) {

            zkSessionManager.closeSession();
        }

    }

    @Override
    public Lock getReentrantLock(String lockName) {

        if (StringUtils.isBlank(lockName)) {
            return new ReentrantZkLock(baseLockPath, zkSessionManager);

        }
        return new ReentrantZkLock(baseLockPath + "/" + lockName, zkSessionManager);
    }

    @Override
    public Lock getReentrantLock(String lockName, int expireTime) {
        return getReentrantLock(lockName);
    }

    @Override
    public Lock getReentrantLock(String lockName, int expireTime, int retry) {
        return getReentrantLock(lockName);
    }

    @Override
    public ReadWriteLock getReadWriteLock(String lockName) {
        if (StringUtils.isBlank(lockName)) {

            return new ReentrantZkReadWriteLock(baseLockPath, zkSessionManager);
        }

        return new ReentrantZkReadWriteLock(baseLockPath + "/" + lockName, zkSessionManager);
    }

    @Override
    public ReadWriteLock getReadWriteLock(String lockName, int expireTime) {
        return this.getReadWriteLock(lockName);
    }

    @Override
    public ReadWriteLock getReadWriteLock(String lockName, int expireTime, int retry) {
        return this.getReadWriteLock(lockName);
    }

    @Override
    public Object getProcessor() {
        return zkSessionManager;
    }


    private void loadConfig(ConfigInfoModel configInfoModel) throws IOException {
        String environment = configInfoModel.getEnvironment();
        ZKConfigModel zkConfigModel = configInfoModel.getZkConfigModel();
        String zkHost = "";
        int zkTimeout = 0;
        if (null == zkConfigModel) {
            LOGGER.info("ZK client is initializing with default tair config!");
            LOGGER.warn("目前Cerberus使用的是公用ZK集群，请确认已获知使用公用ZK集群的风险且确定使用，否则请配置私有的ZK集群。");
        } else {
            zkHost = zkConfigModel.getZkHost();
            zkTimeout = zkConfigModel.getZkTimeout();
            LOGGER.info("ZK client is initializing with specified config!");
        }

        String configPath;
        if ("online".equals(environment)) {
            configPath = DLMConstants.ZKConstants.ZK_ONLINE_CONF;
        } else {
            configPath = DLMConstants.ZKConstants.ZK_OFFLINE_CONF;
        }

        InputStream inputStream = null;
        Properties properties = new Properties();

        try {
            // 读取jar包中的文件流
            inputStream = ZKEngine.class.getResourceAsStream(configPath);
            properties.load(inputStream);

            if (StringUtils.isBlank(zkHost)) {
                host = properties.getProperty("host");
            } else {
                host = zkHost;
            }
            if (0 < zkTimeout) {
                timeout = zkTimeout;
            } else {
                timeout = Integer.valueOf(properties.getProperty("timeout"));
            }
            baseLockPath = properties.getProperty("baseLockPath");
        } catch (Exception e) {
            LOGGER.error("Something wrong with loading config files", e);
        } finally {
            if (null != inputStream) {
                inputStream.close();
            }
        }
    }
}
