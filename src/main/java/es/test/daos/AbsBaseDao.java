package es.test.daos;

import java.text.MessageFormat;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.validation.constraints.NotNull;

import es.test.PersistenceService;

public abstract class AbsBaseDao<TEntity extends IdentifiableEntity<TId>, TId> {

    private final PersistenceService persistenceService;
    private final Class<TEntity> clazz;

    public AbsBaseDao(@NotNull PersistenceService persistenceService, @NotNull Class<TEntity> clazz) {
        this.persistenceService = persistenceService;
        this.clazz = clazz;
    }

    public void create(@NotNull TEntity entity) throws DaoException {
        persistenceService.getEntityManager().persist(entity);
        try {
            persistenceService.getEntityManager().flush();
        } catch (PersistenceException exception) {
            throw new DaoException(exception);
        }
    }

    public TEntity read(@NotNull TId id) throws DaoException {
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

    public void update(@NotNull TEntity entity) throws DaoException {
        try {
            persistenceService.getEntityManager().merge(entity);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public void delete(@NotNull TEntity entity) throws DaoException {
        try {
            if (!persistenceService.getEntityManager().contains(entity)) {
                entity = persistenceService.getEntityManager().merge(entity);
            }
            persistenceService.getEntityManager().remove(entity);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    public void deleteById(@NotNull TId id) throws DaoException {
        try {
            TEntity entity = persistenceService.getEntityManager().find(clazz, id);
            if (entity != null) {
                persistenceService.getEntityManager().remove(entity);
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    protected TEntity newInstance() throws DaoException {
        try {
            return (TEntity) clazz.newInstance();
        } catch (InstantiationException e) {
            throw new DaoException(e);
        } catch (IllegalAccessException e) {
            throw new DaoException(e);
        }
    }
}
