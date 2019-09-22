package es.test.background;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Hands out threads from the wrapped threadfactory with setDeamon(true), so the threads won't keep the JVM alive when it should otherwise exit.
 */
public class DaemonThreadFactory implements ThreadFactory {

    private final ThreadFactory factory;

    public DaemonThreadFactory() {
        this(Executors.defaultThreadFactory());
    }

    public DaemonThreadFactory(ThreadFactory factory) {
        if (factory == null) {
            throw new NullPointerException("factory cannot be null");
        }
        this.factory = factory;
    }

    public Thread newThread(Runnable r) {
        final Thread t = factory.newThread(r);
        t.setDaemon(true);
        return t;
    }
}