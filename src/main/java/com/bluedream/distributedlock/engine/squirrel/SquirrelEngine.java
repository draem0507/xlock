package com.bluedream.distributedlock.engine.squirrel;

import com.bluedream.distributedlock.engine.AbstractDLMEngine;
import com.bluedream.distributedlock.lock.Lock;
import com.bluedream.distributedlock.lock.ReadWriteLock;
import com.bluedream.distributedlock.model.ConfigInfoModel;
import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.CacheRequest;

/**
 * @author: draem0507
 * @date: 2020-01-05 19:58
 * @desc:
 */
public class SquirrelEngine extends AbstractDLMEngine {


    private static final Logger LOGGER = LoggerFactory.getLogger(SquirrelEngine.class);


    private SquirrelProcessor processor;


    public SquirrelEngine() {

        engineName = "squirrel";
    }


    @Override
    public boolean init(ConfigInfoModel configInfoModel) {

        try {

            if (null == processor) {
                processor = new SquirrelProcessor(configInfoModel);

            }
        } catch (Exception e) {
            LOGGER.error("Something wrong with squirrel engine initialisation", e);
            return false;

        }

        setIsInitialized(true);


        return true;
    }

    @Override
    public void destroy() {



    }

    @Override
    public Lock getReentrantLock(String lockName) {
        return null;
    }

    @Override
    public Lock getReentrantLock(String lockName, int expireTime) {
        return null;
    }

    @Override
    public Lock getReentrantLock(String lockName, int expireTime, int retry) {
        return null;
    }

    @Override
    public ReadWriteLock getReadWriteLock(String lockName) {
        return null;
    }

    @Override
    public ReadWriteLock getReadWriteLock(String lockName, int expireTime) {
        return null;
    }

    @Override
    public ReadWriteLock getReadWriteLock(String lockName, int expireTime, int retry) {
        return null;
    }

    @Override
    public Object getProcessor() {
        return processor;
    }
}
