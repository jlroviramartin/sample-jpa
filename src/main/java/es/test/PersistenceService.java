package es.test;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

/**
 * Persistence services. A PersistenceService is unique per thread.
 */
public class PersistenceService implements AutoCloseable {

    public static final String NAME_OF_PU = "SAMPLE_JPA_PU";

    private static final Logger LOG = Logger.getLogger(PersistenceService.class.getName());

    private static EntityManagerFactory factory;

    private static final ThreadLocal<PersistenceService> INSTANCE = new ThreadLocal<PersistenceService>() {
        @Override
        protected PersistenceService initialValue() {
            return new PersistenceService();
        }
    };
    private static final AtomicInteger nInstances = new AtomicInteger();

    private EntityManager entityManager;
    private EntityTransaction transaction;

    public static EntityManagerFactory getEMF() {
        if (null == factory) {
            throw new IllegalStateException("EntityManagerFactory is null");
        }
        return factory;
    }

    public static void init(String nameOfPU) throws ClassNotFoundException {
        factory = Persistence.createEntityManagerFactory(nameOfPU);
        //emf = (EntityManagerFactory) new InitialContext().lookup("java:comp/env/persistence/" + nameOfPU);
    }

    /**
     * Destroys the entity manager factory.
     * 
     * @param desregisterDrivers
     *            Whether deregister the drivers.
     */
    public static void destroy(boolean desregisterDrivers) {
        if (null != factory) {
            factory.close();
        }

        if (desregisterDrivers) {
            desregisterDrivers();
        }
    }

    /**
     * Deregisters the drivers.
     */
    private static void desregisterDrivers() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();

        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();

            if (driver.getClass().getClassLoader() == cl) {
                try {
                    LOG.log(Level.INFO, "Deregistering JDBC driver {0}", driver);
                    DriverManager.deregisterDriver(driver);

                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, MessageFormat.format("Error while deregistering JDBC driver {0}", driver), ex);
                }
            } else {
                LOG.log(Level.INFO, "Not deregistered JDBC driver {0}: it does not belong to this webapp's ClassLoader", driver);
            }
        }
    }

    /**
     * Returns the instance (unique per thread) of PersistenceService.
     *
     * @return An instance of PersistenceService
     */
    public static PersistenceService getInstance() {
        return INSTANCE.get();
    }

    /**
     * Removes the instance (unique per thread) of PersistenceService.
     *
     * @return An instance of PersistenceService
     */
    private static void removeInstance() {
        INSTANCE.remove();
    }

    private PersistenceService() {
        LOG.log(Level.FINE, "Building PersistenceService {0}", nInstances.incrementAndGet());
    }

    /**
     * Returns an instance of EntityManager.
     *
     * @return an instance of EntityManager
     */
    public EntityManager getEntityManager() {
        if (null == entityManager) {
            LOG.log(Level.FINE, "Caching EntityManager");

            entityManager = getEMF().createEntityManager();
            entityManager.setFlushMode(FlushModeType.COMMIT); // En el commit va a la base de datos.
        }
        return entityManager;
    }

    /**
     * Returns the current transaction.
     * 
     * @return The current transaction.
     */
    private EntityTransaction getTransaction() {
        if (null == transaction) {
            transaction = getEntityManager().getTransaction();
        }
        return transaction;
    }

    /**
     * Begins a resource transaction.
     */
    public void beginTransaction() {
        try {
            getTransaction().begin();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Commits a resource transaction.
     */
    public void commitTransaction() {
        try {
            getTransaction().commit();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Rolls back a resource transaction.
     */
    public void rollbackTransaction() {
        try {
            getTransaction().rollback();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Executes {@code runnable} inside a transaction.
     * 
     * @param runnable
     *            A runnable code.
     */
    public void doInTransaction(Runnable runnable) {
        beginTransaction();
        try {
            runnable.run();
            commitTransaction();
        } catch (Throwable e) {
            rollbackTransaction();
            throw e;
        }
    }

    /**
     * Executes {@code runnable} inside a transaction.
     * 
     * @param runnable
     *            A runnable code.
     */
    @SuppressWarnings("unchecked")
    public <TException extends Exception> void doInTransaction(Class<? extends TException> clazz, RunnableWithException<TException> runnable) throws TException {
        beginTransaction();
        try {
            runnable.run();
            commitTransaction();
        } catch (Throwable e) {
            rollbackTransaction();
            if (clazz.isInstance(e)) {
                throw (TException) e;
            }
            throw new Error(e);
        }
    }

    /**
     * Closes this instance.
     */
    @Override
    public void close() {
        LOG.log(Level.FINE, "Closing PersistenceService {0}", nInstances.decrementAndGet());

        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
            entityManager = null;
        }
        removeInstance();
    }
}
