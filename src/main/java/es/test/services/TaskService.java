package es.test.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.test.PersistenceService;
import es.test.daos.DaoException;
import es.test.daos.TaskDao;
import es.test.model.Task;
import es.test.model.TaskStatus;

public class TaskService extends AbsCrudService<Task, Integer, TaskDao> {

    private static final Logger LOG = LoggerFactory.getLogger(TaskService.class);
    private final ExecutorService executorService;

    @Inject
    public TaskService(TaskDao taskDAO, PersistenceService persistenceService, ExecutorService executorService) {
        super(taskDAO, persistenceService);
        this.executorService = executorService;
    }

    public void start(final int id) throws DaoException {
        Task task = read(id);
        if (task.getStatus() == TaskStatus.waiting) {
            executorService.submit(() -> {
                try {
                    execute(id);
                } catch (DaoException e) {
                    LOG.error("Error while start the task " + id, e);
                }
            }, true);
        }
    }

    private void execute(final int id) throws DaoException {
        try {
            {
                Task task = read(id);
                task.setCompletionFactor(0);
                task.setStatus(TaskStatus.running);
                update(task);
            }

            final int count = 10;
            for (int i = 0; i < count; i++) {
                Thread.sleep(TimeUnit.SECONDS.toMillis(15));

                {
                    Task task = read(id);
                    task.setCompletionFactor(i / (double) count);
                    update(task);
                }
            }

            {
                Task task = read(id);
                task.setCompletionFactor(1);
                task.setStatus(TaskStatus.completed);
                update(task);
            }
        } catch (InterruptedException e) {
            Task task = read(id);
            task.setStatus(TaskStatus.aborted);
            update(task);
        }
    }
}
