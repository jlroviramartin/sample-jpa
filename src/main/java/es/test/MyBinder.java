package es.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

import es.test.background.BackgroundUtils;
import es.test.background.DaemonThreadFactory;
import es.test.daos.TaskDao;
import es.test.services.TaskService;

public class MyBinder extends AbstractBinder {

    @Override
    protected void configure() {

        System.out.println("***** MyBinder.configure");

        bind(TaskDao.class).to(TaskDao.class).in(RequestScoped.class);
        bind(TaskService.class).to(TaskService.class).in(RequestScoped.class);

        bindFactory(new Factory<EntityManager>() {
            @Override
            public EntityManager provide() {
                return PersistenceService.getInstance().getEntityManager();
            }

            @Override
            public void dispose(EntityManager instance) {
                //PersistenceService.getInstance().close();
            }
        }).to(EntityManager.class).in(RequestScoped.class);

        bindFactory(new Factory<PersistenceService>() {
            @Override
            public PersistenceService provide() {
                return PersistenceService.getInstance();
            }

            @Override
            public void dispose(PersistenceService instance) {
            }
        }).to(PersistenceService.class).in(RequestScoped.class);

        // Registering a ExecutorService in a singleton scope
        bindFactory(new Factory<ExecutorService>() {
            @Override
            public ExecutorService provide() {
                ThreadFactory daemonFactory = new DaemonThreadFactory();

                ExecutorService executor;
                final int c = BackgroundUtils.NUMBER_OF_EXECUTORS;
                if (c <= 1) {
                    executor = Executors.newSingleThreadExecutor(daemonFactory);
                } else {
                    executor = Executors.newFixedThreadPool(c, daemonFactory);
                }
                return executor;
            }

            @Override
            public void dispose(ExecutorService instance) {
                instance.shutdownNow(); // process/wait until all pending jobs are done
            }
        }).to(ExecutorService.class).in(Singleton.class);
    }
}
