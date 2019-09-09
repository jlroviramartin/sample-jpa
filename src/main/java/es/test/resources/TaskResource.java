package es.test.resources;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import es.test.Task;
import es.test.daos.DaoException;
import es.test.services.TaskService;

@Path("/task")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    private TaskService taskService;

    @Inject
    public TaskResource(TaskService taskService) {
        this.taskService = taskService;
    }

    @GET
    @Path("/hello")
    public String getHello() {
        return "Hello world!";
    }

    @POST
    public Response create(Task task) throws DaoException {
        taskService.create(task);
        return Response.ok().build();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") int id) throws DaoException {
        Task task = taskService.read(id);
        return Response.ok(task).build();
    }

    @GET
    public Response getAll() throws DaoException {
        List<Task> tasks = taskService.getAll();
        return Response.ok(tasks).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") int id, Task task) throws DaoException {
        Task updateTask = taskService.read(id);
        updateTask.setName(task.getName());
        updateTask.setDescription(task.getDescription());
        taskService.update(updateTask);
        return Response.ok().build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) throws DaoException {
        taskService.deleteById(id);
        return Response.ok().build();
    }
}
