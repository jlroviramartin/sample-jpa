package es.test.services;

import javax.inject.Inject;

import es.test.PersistenceService;
import es.test.Task;
import es.test.daos.DaoException;
import es.test.daos.TaskDao;

public class TaskService extends AbsCrudService<Task, TaskDao> {

    @Inject
    public TaskService(TaskDao taskDAO, PersistenceService persistenceService) {
        super(taskDAO, persistenceService);
    }

    public void deleteById(int id) throws DaoException {
        Task task = new Task();
        task.setId(id);
        delete(task);
    }
}
