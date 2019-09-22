package es.test.daos;

import javax.inject.Inject;

import es.test.PersistenceService;
import es.test.model.Task;

public class TaskDao extends AbsBaseDao<Task, Integer> {

    @Inject
    public TaskDao(PersistenceService persistenceService) {
        super(persistenceService, Task.class);
    }

    public void deleteById(int id) throws DaoException {
        Task task = new Task();
        task.setId(id);
        delete(task);
    }
}
