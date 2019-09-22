package es.test.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import es.test.daos.DaoException;
import es.test.daos.TaskDao;
import es.test.model.Task;
import es.test.services.TaskService;

@Path("/task")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource extends AbsRestResource<TaskService, Task, Integer, TaskDao> {

    @Inject
    public TaskResource(TaskService taskService) {
        super(taskService);
    }

    @GET
    @Path("/hello")
    public String getHello() {
        return "Hello world!";
    }

    @GET
    @Path("/start/{id}")
    public Response start(@PathParam("id") int id) throws DaoException {
        getService().start(id);
        return Response.ok().build();
    }
}
