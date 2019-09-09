package es.test.daos;

import java.text.MessageFormat;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import es.test.PersistenceService;

public abstract class AbsBaseDao<TEntity> {

    private final PersistenceService persistenceService;
    private final Class<TEntity> clazz;

    @Inject
    public AbsBaseDao(PersistenceService persistenceService, Class<TEntity> clazz) {
        this.persistenceService = persistenceService;
        this.clazz = clazz;
    }

    public void create(TEntity entity) throws DaoException {
        persistenceService.getEntityManager().persist(entity);
        try {
            persistenceService.getEntityManager().flush();
        } catch (PersistenceException exception) {
            throw new DaoException(exception);
        }
    }

    public TEntity read(int id) throws DaoException {
        try {
            return persistenceService.getEntityManager().find(clazz, id);
        } catch (PersistenceException exception) {
            throw new DaoException(exception);
        }
    }

    public List<TEntity> getAll() throws DaoException {
        //CriteriaBuilder cb = persistenceService.getEntityManager().getCriteriaBuilder();
        //CriteriaQuery<TEntity> consulta = cb.createQuery(TEntity.class);
        //Root<TEntity> tasks = consulta.from(TEntity.class);
        //return persistenceService.getEntityManager().createQuery(consulta).getResultList();

        String jpql = MessageFormat.format("SELECT entity FROM {0} entity", clazz.getSimpleName());
        try {
            return persistenceService.getEntityManager().createQuery(jpql, clazz).getResultList();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public void update(TEntity entity) throws DaoException {
        try {
            persistenceService.getEntityManager().merge(entity);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public void delete(TEntity entity) throws DaoException {
        try {
            if (!persistenceService.getEntityManager().contains(entity)) {
                entity = persistenceService.getEntityManager().merge(entity);
            }
            persistenceService.getEntityManager().remove(entity);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }
}
