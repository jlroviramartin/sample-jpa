package es.test.background;

import java.util.concurrent.ExecutorService;

import javax.servlet.Servlet;

public class BackgroundUtils {

    public static final int NUMBER_OF_EXECUTORS = 1;
    public static final String EXECUTOR_NAME = "MY_EXECUTOR";

    public static ExecutorService getExecutorService(Servlet servlet) {
        ExecutorService executor = (ExecutorService) servlet.getServletConfig().getServletContext().getAttribute(EXECUTOR_NAME);
        return executor;
    }
}
