package es.test;

import javax.persistence.EntityManager;

public class TaskDAO {

    private final EntityManager entityManager;

    public TaskDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public TaskDAO() {
        this(PersistenceService.getInstance().getEntityManager());
    }

    public void create(Task task) {
        entityManager.persist(task);
    }

    public Task read(int id) {
        return entityManager.find(Task.class, id);
    }

    public void update(Task task) {
        entityManager.merge(task);
    }

    public void delete(Task task) {
        entityManager.remove(task);
    }
}
