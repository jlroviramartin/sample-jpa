package es.test.services;

import java.util.List;

import es.test.PersistenceService;
import es.test.daos.AbsBaseDao;
import es.test.daos.DaoException;

public abstract class AbsCrudService<TEntity, TDao extends AbsBaseDao<TEntity>> {

    private final TDao dao;
    private final PersistenceService persistenceService;

    public AbsCrudService(TDao dao, PersistenceService persistenceService) {
        this.dao = dao;
        this.persistenceService = persistenceService;
    }

    public void create(TEntity entity) throws DaoException {
        persistenceService.doInTransaction(DaoException.class, () -> dao.create(entity));
    }

    public TEntity read(int id) throws DaoException {
        return dao.read(id);
    }

    public List<TEntity> getAll() throws DaoException {
        return dao.getAll();
    }

    public void update(TEntity entity) throws DaoException {
        persistenceService.doInTransaction(DaoException.class, () -> dao.update(entity));
    }

    public void delete(TEntity entity) throws DaoException {
        persistenceService.doInTransaction(DaoException.class, () -> dao.delete(entity));
    }
}
