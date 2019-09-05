package es.test;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;

public class PersistenceService implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(PersistenceService.class.getName());
    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEMF() {
        if (null == emf) {
            throw new IllegalStateException("EntityManagerFactory is null");
        }
        return emf;
    }

    public static void init(String nameOfPU) {
        try {
            emf = (EntityManagerFactory) new InitialContext().lookup("java:comp/env/persistence/" + nameOfPU);
        } catch (NamingException ex) {
            //init(nameOfPU, Collections.EMPTY_MAP);
        }
    }

    public static void destroy(boolean desregisterDriver) {
        if (null != emf) {
            emf.close();
        }

        if (desregisterDriver) {
            desregisterDriver();
        }
    }

    private static void desregisterDriver() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();

        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();

            if (driver.getClass().getClassLoader() == cl) {
                try {
                    LOG.log(Level.INFO, "Deregistering JDBC driver {0}", driver);
                    DriverManager.deregisterDriver(driver);

                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, String.format("Error deregistering JDBC driver %s", driver), ex);
                }

            } else {
                LOG.log(Level.INFO, "Not deregistering JDBC driver {0} as it does not belong to this webapp's ClassLoader", driver);
            }
        }
    }

    private static final ThreadLocal<PersistenceService> INSTANCE = new ThreadLocal<PersistenceService>() {
        @Override
        protected PersistenceService initialValue() {
            return new PersistenceService();
        }
    };

    private EntityManager em;
    private EntityTransaction utx;

    private static final AtomicInteger nInstances = new AtomicInteger();

    private PersistenceService() {
        LOG.log(Level.FINE, "Creando PersistenceService {0}", nInstances.incrementAndGet());
    }

    /**
     * Returns an instance of PersistenceService.
     *
     * @return an instance of PersistenceService
     */
    public static PersistenceService getInstance() {
        return INSTANCE.get();
    }

    private static void removeInstance() {
        INSTANCE.remove();
    }

    /**
     * Returns an instance of EntityManager.
     *
     * @return an instance of EntityManager
     */
    public EntityManager getEntityManager() {
        if (null == em) {
            em = getEMF().createEntityManager();
            em.setFlushMode(FlushModeType.COMMIT); // En el commit va a la base de datos.

            //Map<String, Object> properties = getEMF().getProperties();
            //tryToSetProperty("javax.persistence.cache.storeMode", properties, em);
            //tryToSetProperty("javax.persistence.cache.retrieveMode", properties, em);
            //printProperties(em);
        }

        return em;
    }

    private EntityTransaction getTransaction() {
        if (null == utx) {
            utx = getEntityManager().getTransaction();
        }
        return utx;
    }

    /**
     * Begins a resource transaction.
     */
    public void beginTx() {
        try {
            getTransaction().begin();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Commits a resource transaction.
     */
    public void commitTx() {
        try {
            getTransaction().commit();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Rolls back a resource transaction.
     */
    public void rollbackTx() {
        try {
            getTransaction().rollback();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Closes this instance.
     */
    @Override
    public void close() {
        LOG.log(Level.FINE, "Cerrando PersistenceService {0}", nInstances.decrementAndGet());

        if (em != null && em.isOpen()) {
            em.close();
            em = null;
        }

        removeInstance();
    }
}
