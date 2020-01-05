package com.bluedream.distributedlock.engine.zk.util;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author: draem0507
 * @date: 2020-01-05 18:43
 * @desc: zk 投票检测
 */
public class ZkSessionPoller {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkSessionPoller.class);


    /**
     * poll interval in milliseconds
     */
    private final long pollIntervalMs;


    private final ZooKeeper zk;

    private final Object disConnectionTimeLock = "Lock";

    private Long startDisconnectTime;


    private final ConnectionListener pollListener;


    private final ScheduledExecutorService poller = Executors.newScheduledThreadPool(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {

            Thread t = new Thread(r);
            t.setName("menagerie-ZkConnectionPoller");
            return t;
        }


    });


    public void startPolling() {
        poller.scheduleAtFixedRate(new SessionPoller(), 0L, pollIntervalMs, TimeUnit.MILLISECONDS);

    }


    private class SessionPoller implements Runnable {

        private final int sessionTimeoutPeriod;

        public SessionPoller() {

            this.sessionTimeoutPeriod = zk.getSessionTimeout();
        }


        @Override
        public void run() {

            if (Thread.currentThread().isInterrupted())
                return; // we've been canceled, so return
            if (LOGGER.isTraceEnabled())
                LOGGER.trace("current state of ZooKeeper object: " + zk.getState());
            try {
                zk.exists("/", false);
                synchronized (disConnectionTimeLock) {
                    startDisconnectTime = null;
                }
            } catch (InterruptedException e) {
                LOGGER.error("Will be interrupted!", e);
            } catch (KeeperException e) {
                LOGGER.warn("Zk got keeper exception!", e);
                if (e.code() == KeeperException.Code.SESSIONEXPIRED) {
                    expire();
                } else if (e.code() == KeeperException.Code.CONNECTIONLOSS) {
                    LOGGER.debug("Received a ConnectionLoss Exception, determining if our session has expired");
                    long currentTime = System.currentTimeMillis();
                    boolean shouldExpire = false;
                    synchronized (disConnectionTimeLock) {
                        if (startDisconnectTime == null) {
                            startDisconnectTime = currentTime;
                        } else if ((currentTime - startDisconnectTime) > sessionTimeoutPeriod) {
                            shouldExpire = true;
                        }
                    }
                    if (shouldExpire)
                        expire();
                } else {
                    LOGGER.error("Zk got keeper exception without expected!", e);
                }
            }
        }


    }


    private void expire() {
        // session expired!
        LOGGER.info("Session has expired, notifying listenerand shutting down poller");
        ZkSessionPoller.this.stopPolling();
        pollListener.expired();

    }


    public  void stopPolling() {

        poller.shutdownNow();
    }


    public ZkSessionPoller(ZooKeeper zk, long pollIntervalMs, ConnectionListener pollListener) {

        this.zk = zk;

        this.pollIntervalMs = pollIntervalMs;

        this.pollListener = pollListener;
    }


}
