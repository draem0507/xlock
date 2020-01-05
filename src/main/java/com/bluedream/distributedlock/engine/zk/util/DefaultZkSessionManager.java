package com.bluedream.distributedlock.engine.zk.util;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: draem0507
 * @date: 2020-01-05 18:57
 * @desc:
 */
public class DefaultZkSessionManager implements ZkSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultZkSessionManager.class);
    // this could potentially be a very write-heavy list, so a synchronized list will perform better
    // than a more traditional CopyOnWriteArrayList would be
    private List<ConnectionListener> listeners = Collections.synchronizedList(new ArrayList<ConnectionListener>());


    private ZooKeeper zk;


    private final String connectionString;


    private final int timeout;


    private final ExecutorService executor;


    private volatile boolean shutdown;


    private ZkSessionPoller poller;


    private final int zkSessionPollInterval;


    public DefaultZkSessionManager(String connectionString, int timeout) {

        this(connectionString, timeout, Executors.newSingleThreadExecutor(), -1);


    }


    public DefaultZkSessionManager(String connectionString, int timeout, ExecutorService executor, int zkSessionPollInterval) {

        this.connectionString = connectionString;
        this.timeout = timeout;

        this.executor = executor;

        this.zkSessionPollInterval = zkSessionPollInterval;

    }

    @Override
    public ZooKeeper getZooKeeper() {

        if (shutdown) {
            throw new IllegalStateException("Cannot request a ZooKeeper after the session has been closed!");

        }

        if (zk == null || zk.getState() == ZooKeeper.States.CLOSED) {

            try {
                zk = new ZooKeeper(connectionString, timeout, new SessionWatcher(this));
            } catch (IOException e) {
                logger.error("Cerberus DefaultZkSessionManager ZooKeeper getZooKeeper got exception!", e);
                throw new RuntimeException(e);
            }
            if (zkSessionPollInterval > 0) {
                // stop any previous polling, if it hasn't been stopped already
                if (poller != null) {
                    poller.stopPolling();
                }
                // create a new poller for this ZooKeeper instance
                poller = new ZkSessionPoller(zk, zkSessionPollInterval, new SessionPollListener(zk, this));
                poller.startPolling();
            }

        } else {
            // make sure that your zookeeper instance is synced
            zk.sync("/", new AsyncCallback.VoidCallback() {
                @Override
                public void processResult(int rc, String path, Object ctx) {
                    // do nothing, we're good
                }
            }, this);

        }

        return zk;
    }

    @Override
    public void closeSession() {

        try {
            if (zk != null) {
                try {
                    zk.close();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            executor.shutdown();
            shutdown = true;
        }

    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {


        listeners.add(listener);

    }

    @Override
    public void removeConnectionListener(ConnectionListener listener) {

        listeners.remove(listener);
    }




    /*--------------------------------------------------------------------------------------------------------------------*/
    /* private helper methods */

    private void notifyListeners(WatchedEvent event) {
        notifyState(event.getState());
    }

    private void notifyState(final Watcher.Event.KeeperState state) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                if (state == Watcher.Event.KeeperState.Expired) {
                    // tell everyone that all their watchers and ephemeral nodes have been removed--suck
                    for (ConnectionListener listener : listeners) {
                        listener.expired();
                    }
                    zk = null;
                } else if (state == Watcher.Event.KeeperState.SyncConnected) {
                    // tell everyone that we've reconnected to the Server, and they should make sure that their watchers
                    // are in place
                    for (ConnectionListener listener : listeners) {
                        listener.syncConnected();
                    }
                } else if (state == Watcher.Event.KeeperState.Disconnected) {
                    for (ConnectionListener listener : listeners) {
                        listener.disconnected();
                    }
                }
            }
        });
    }


    private static class SessionPollListener extends ConnectionListenerSkeleton {

        private ZooKeeper zk;

        private DefaultZkSessionManager sessionManager;

        public SessionPollListener(ZooKeeper zk, DefaultZkSessionManager sessionManager) {

            this.zk = zk;

            this.sessionManager = sessionManager;


        }

        @Override
        public void expired() {
            logger.info("Session expiration has been detected. Notifying all connection listeners and cleaning up ZooKeeper State");
            // notify applications
            sessionManager.notifyState(Watcher.Event.KeeperState.Expired);
            // shut down this ZooKeeper instance
            try {
                zk.close();
            } catch (InterruptedException e) {
                logger.warn("An InterruptedException was detected while attempting to close a ZooKeeper instance; ignoring because we're shutting it down anyway");
            }
        }


    }

    private static class SessionWatcher implements Watcher {

        private DefaultZkSessionManager manager;

        public SessionWatcher(DefaultZkSessionManager manager) {
            this.manager = manager;

        }

        @Override
        public void process(WatchedEvent watchedEvent) {

            manager.notifyListeners(watchedEvent);

        }
    }
}
