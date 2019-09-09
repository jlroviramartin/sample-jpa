package es.test;

import javax.persistence.EntityManager;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

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
    }
}
