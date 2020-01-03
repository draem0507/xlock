package com.bluedream.distributedlock.engine;

/**
 * @author: draem0507
 * @date: 2020-01-03 20:08
 * @desc:
 */
public abstract class AbstractDLMEngine implements IDLMEngine {

    protected String engineName;


    protected boolean isInitialized = false;

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    @Override
    public void setIsInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    @Override
    public String getEngineName() {
        return engineName;
    }

    @Override
    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }
}
