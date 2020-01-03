package com.bluedream.distributedlock.service.impl;

import com.bluedream.distributedlock.engine.IDLMEngine;
import com.bluedream.distributedlock.factory.EngineFactory;
import com.bluedream.distributedlock.factory.EngineWrapper;
import com.bluedream.distributedlock.lock.Lock;
import com.bluedream.distributedlock.lock.ReadWriteLock;
import com.bluedream.distributedlock.lock.impl.DLMReentrantLock;
import com.bluedream.distributedlock.lock.impl.DLMReentrantReadWriteLock;
import com.bluedream.distributedlock.model.ConfigInfoModel;
import com.bluedream.distributedlock.model.SquirrelConfigModel;
import com.bluedream.distributedlock.model.TairConfigModel;
import com.bluedream.distributedlock.model.ZKConfigModel;
import com.bluedream.distributedlock.service.IDistributedLockManager;
import com.bluedream.distributedlock.util.CryptUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author: draem0507
 * @date: 2020-01-03 17:20
 * @desc:
 */
public class DistributedLockManager implements IDistributedLockManager {


    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedLockManager.class);


    private String environment;

    private String appKey = "";

    private String secret = "";

    private TairConfigModel tairConfigModel;

    private ZKConfigModel zkConfigModel;

    private SquirrelConfigModel squirrelConfigModel;


    private ConfigInfoModel configInfoModel = new ConfigInfoModel();


    private final EngineWrapper engineWrapper;


    private IDLMEngine curEngine;


    public DistributedLockManager(String engineName) {
        this.engineWrapper = new EngineWrapper();

        engineWrapper.load(EngineFactory.createEngine(engineName));


    }

    public DistributedLockManager(String masterEngineName, String slaveEngineName) {
        this.engineWrapper = new EngineWrapper();

        engineWrapper.load(EngineFactory.createEngine(masterEngineName), EngineFactory.createEngine(slaveEngineName));


    }


    public DistributedLockManager(String masterEngineName, String slaveEngineName1, String slaveEngineName2) {
        this.engineWrapper = new EngineWrapper();

        engineWrapper.load(EngineFactory.createEngine(masterEngineName), EngineFactory.createEngine(slaveEngineName1), EngineFactory.createEngine(slaveEngineName2));


    }


    @Override
    public void init() {

        //参数校验&权限验证
        checkProperty();

        //装在configInfoModel
        loadConfigInfo();


        curEngine = engineWrapper.init(configInfoModel);


        if (null == curEngine) {
            String error = "Engine initialisation is failed";
            LOGGER.error(error);
            throw new RuntimeException(error);
        }
        LOGGER.info("Engine init successfully");


    }


    @Override
    public void destroy() {

        engineWrapper.destroy();

        LOGGER.info("Engines destruction is successful! ");


    }

    @Override
    public String switchEngine() {
        try {
            curEngine = engineWrapper.switchEngine();
            LOGGER.info("switchEngine successfully! current engine is {}", curEngine.getEngineName());
        } catch (InterruptedException e) {
            LOGGER.error("switchEngine has been interrupted!", e);
        }
        return curEngine.getEngineName();
    }

    @Override
    public String switchEngine(String engineName) {
        try {
            curEngine = engineWrapper.switchEngine(engineName);
            LOGGER.info("switchEngine successfully! current engine is {}", curEngine.getEngineName());
        } catch (InterruptedException e) {
            LOGGER.error("switchEngine has been interrupted! engineName={}", engineName, e);
        }
        return curEngine.getEngineName();
    }

    @Override
    public Lock getReentrantLock(String lockName) {
        if (null == curEngine) {
            return null;
        }

        return new DLMReentrantLock(curEngine, lockName);
    }

    @Override
    public Lock getReentrantLock(String lockName, int expireTime) {
        return new DLMReentrantLock(curEngine, lockName, expireTime);
    }

    @Override
    public Lock getReentrantLock(String lockName, int expireTime, int retry) {
        return new DLMReentrantLock(curEngine, lockName, expireTime, retry);
    }

    @Override
    public ReadWriteLock getReentrantReadWriteLock(String lockName) {
        return new DLMReentrantReadWriteLock(curEngine, lockName);
    }

    @Override
    public ReadWriteLock getReentrantReadWriterLock(String lockName, int expireTime) {
        return new DLMReentrantReadWriteLock(curEngine, lockName, expireTime);
    }

    @Override
    public ReadWriteLock getReentrantReadWriterLock(String lockName, int expireTime, int retry) {
        return new DLMReentrantReadWriteLock(curEngine, lockName, expireTime, retry);
    }


    private void loadConfigInfo() {

        configInfoModel.setAppKey(appKey);
        configInfoModel.setEnvironment(environment);
        configInfoModel.setSquirrelConfigModel(squirrelConfigModel);
        configInfoModel.setTairConfigModel(tairConfigModel);
        configInfoModel.setZkConfigModel(zkConfigModel);
    }

    private void checkProperty() {

        if (StringUtils.isBlank(environment) || !("offline".equalsIgnoreCase(environment) || "online".equalsIgnoreCase(environment))) {
            String error = "Incoming environment is invalid! Please check the environment property of DistributedLockManager.";
            LOGGER.error(error);
            throw new RuntimeException(error);
        }

        if (StringUtils.isBlank(appKey)) {
            String error = "Incoming appKey is invalid! Please check the appKey property of DistributedLockManager.";
            LOGGER.error(error);
            throw new RuntimeException(error);
        }

        if (!CryptUtil.validSecret(appKey, secret)) {
            String error = "Incoming secret is invalid! Please check the appKey property of DistributedLockManager or contact with the person in " +
                    "charge";
            LOGGER.error(error);
            throw new RuntimeException(error);
        }
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
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

    public ConfigInfoModel getConfigInfoModel() {
        return configInfoModel;
    }

    public void setConfigInfoModel(ConfigInfoModel configInfoModel) {
        this.configInfoModel = configInfoModel;
    }

    public EngineWrapper getEngineWrapper() {
        return engineWrapper;
    }

    public IDLMEngine getCurEngine() {
        return curEngine;
    }

    public void setCurEngine(IDLMEngine curEngine) {
        this.curEngine = curEngine;
    }
}
