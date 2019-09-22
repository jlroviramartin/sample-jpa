package es.test.background;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

// https://stackoverflow.com/questions/4907502/running-a-background-java-program-in-tomcat
//@WebListener
public class ExecutorListener implements ServletContextListener {

    private ExecutorService executor;

    public void contextInitialized(ServletContextEvent arg) {
        ServletContext context = arg.getServletContext();

        ThreadFactory daemonFactory = new DaemonThreadFactory();

        final int c = BackgroundUtils.NUMBER_OF_EXECUTORS;
        if (c <= 1) {
            executor = Executors.newSingleThreadExecutor(daemonFactory);
        } else {
            executor = Executors.newFixedThreadPool(c, daemonFactory);
        }

        context.setAttribute(BackgroundUtils.EXECUTOR_NAME, executor);
    }

    public void contextDestroyed(ServletContextEvent arg) {
        executor.shutdownNow(); // process/wait until all pending jobs are done
    }
}
