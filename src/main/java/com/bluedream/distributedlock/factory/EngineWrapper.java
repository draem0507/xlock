package com.bluedream.distributedlock.factory;

import com.bluedream.distributedlock.engine.IDLMEngine;
import com.bluedream.distributedlock.model.ConfigInfoModel;
import org.apache.commons.lang.StringUtils;
import org.omg.PortableInterceptor.INACTIVE;
import sun.java2d.jules.IdleTileCache;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: draem0507
 * @date: 2020-01-03 17:39
 * @desc:
 */
public class EngineWrapper {


    private ArrayList<IDLMEngine> engines = new ArrayList<>();

    private int curIndex;


    private ConfigInfoModel configInfoModel;

    private final Lock INDEX_LOCK = new ReentrantLock();


    public void load(IDLMEngine masterEngine) {
        engines.add(masterEngine);


    }

    public void load(IDLMEngine masterEngine, IDLMEngine SlaveEngine) {
        engines.add(masterEngine);
        engines.add(SlaveEngine);

    }

    public void load(IDLMEngine masterEngine, IDLMEngine SlaveEngine1, IDLMEngine SlaveEngine2) {
        engines.add(masterEngine);
        engines.add(SlaveEngine1);
        engines.add(SlaveEngine2);

    }

    public IDLMEngine switchEngine() throws InterruptedException {
        int size = engines.size();

        INDEX_LOCK.lockInterruptibly();

        try {
            int nextIndex = (curIndex + 1) % size;
            IDLMEngine nextEngine = engines.get(nextIndex);
            if (nextEngine.isInitialized()) {
                curIndex = nextIndex;
            } else {
                boolean init = nextEngine.init(configInfoModel);
                if (init) {
                    curIndex = nextIndex;

                }

            }

            return engines.get(nextIndex);

        } finally {
            INDEX_LOCK.unlock();
        }

    }


    public IDLMEngine switchEngine(String engineName) throws InterruptedException {

        INDEX_LOCK.lockInterruptibly();
        try {
            for (int i = 0; i < engines.size(); i++) {
                IDLMEngine idlmEngine = engines.get(i);
                if (StringUtils.equalsIgnoreCase(engineName, idlmEngine.getEngineName())) {
                    if (idlmEngine.isInitialized()) {
                        curIndex = i;
                    } else {

                        boolean init = idlmEngine.init(configInfoModel);
                        if (init) {
                            curIndex = 1;
                        }
                    }

                }


            }
            return engines.get(curIndex);

        } finally {
            INDEX_LOCK.unlock();
        }


    }


    public IDLMEngine init(ConfigInfoModel configInfoModel) {

        this.configInfoModel = configInfoModel;

        for (int i = 0; i < engines.size(); i++) {

            IDLMEngine idlmEngine = engines.get(i);
            if (idlmEngine.init(configInfoModel)) {
                curIndex = i;
                return idlmEngine;
            }

        }
        return null;
    }


    public IDLMEngine getEngine(String engineName) {

        for (int i = 0; i < engines.size(); i++) {
            IDLMEngine idlmEngine = engines.get(i);

            if (StringUtils.equalsIgnoreCase(engineName, idlmEngine.getEngineName()) && idlmEngine.init(configInfoModel)) {
                return idlmEngine;

            }

        }

        return null;

    }


    public void destroy() {

        for (IDLMEngine engine : engines) {
            engine.destroy();
        }
    }


}