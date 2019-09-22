package es.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import es.test.model.Task;
import es.test.model.TaskStatus;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class TaskResourceIT extends AbsResourceIT<Task, Integer> {

    public TaskResourceIT() {
        super(Task.class, Integer.class, new GenericType<List<Task>>() {
        }, "");
    }

    @Before
    public void initializeTest() throws Exception {
        String port = System.getProperty("servlet.port");
        if (port == null) {
            port = "8080";
        }
        baseUrl = "http://localhost:" + port + "/sample-jpa/rest/task";

        System.out.println("Base URL " + baseUrl);
    }

   @Test
    public void fullCrudTest() throws Exception {
        // getAll
        List<Task> taks = getAll().getBody();
        assertEquals(0, taks.size());

        Task task = new Task();
        task.setName("name");
        task.setDescription("description");

        // create
        HttpResponse<Task> response = create(task);
        assertEquals(200, response.getStatus());

        // getAll
        List<Task> taks2 = getAll().getBody();
        assertEquals(1, taks2.size());

        int id = taks2.get(0).getId();

        // get
        Task task2 = get(id).getBody();
        assertNotNull(task2);
        assertEquals((Integer) id, task2.getId());
        assertEquals("name", task2.getName());
        assertEquals("description", task2.getDescription());

        Task task3 = new Task();
        task3.setId(id);
        task3.setName("name2");
        task3.setDescription("description2");

        // update
        int status2 = update(task3).getStatus();
        assertEquals(200, status2);

        // delete
        int status3 = delete(id).getStatus();
        assertEquals(200, status3);

        //getAll
        List<Task> taks3 = getAll().getBody();
        assertEquals(0, taks3.size());
    }

    //@Test
    public void run() throws Exception {
        Task task = new Task();
        task.setName("name");
        task.setDescription("description");
        int id = create(task).getBody().getId();

        start(id);

        Task runningTask = get(id).getBody();
        while (!runningTask.getStatus().equals(TaskStatus.completed)) {
            System.out.println(runningTask.getStatus());
            System.out.println(runningTask.getCompletionFactor());
            Thread.sleep(TimeUnit.SECONDS.toMillis(2));

            runningTask = get(id).getBody();
            if (runningTask.getStatus().equals(TaskStatus.waiting)) {
                System.out.println("-");
            }
        }
    }

    protected HttpResponse<?> start(int id) {
        return Unirest.get(this.baseUrl + "/start/{id}")
                .routeParam("id", String.valueOf(id))
                .asEmpty();
    }
}
