package es.test.services;

import java.util.List;

import javax.validation.constraints.NotNull;

import es.test.PersistenceService;
import es.test.daos.AbsBaseDao;
import es.test.daos.DaoException;
import es.test.daos.IdentifiableEntity;

public abstract class AbsCrudService<TEntity extends IdentifiableEntity<TId>, TId, TDao extends AbsBaseDao<TEntity, TId>> {

    private final TDao dao;
    private final PersistenceService persistenceService;

    protected TDao getDao() {
        return dao;
    }

    protected PersistenceService getPersistenceService() {
        return persistenceService;
    }

    public AbsCrudService(@NotNull TDao dao, @NotNull PersistenceService persistenceService) {
        this.dao = dao;
        this.persistenceService = persistenceService;
    }

    public void create(@NotNull TEntity entity) throws DaoException {
        persistenceService.doInTransaction(DaoException.class, () -> dao.create(entity));
    }

    public TEntity read(@NotNull TId id) throws DaoException {
        return dao.read(id);
    }

    public List<TEntity> getAll() throws DaoException {
        return dao.getAll();
    }

    public void update(@NotNull TEntity entity) throws DaoException {
        persistenceService.doInTransaction(DaoException.class, () -> dao.update(entity));
    }

    public void delete(@NotNull TEntity entity) throws DaoException {
        persistenceService.doInTransaction(DaoException.class, () -> dao.delete(entity));
    }

    public void deleteById(@NotNull TId id) throws DaoException {
        persistenceService.doInTransaction(DaoException.class, () -> dao.deleteById(id));
    }
}
