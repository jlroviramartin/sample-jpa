package es.test.resources;

import java.util.List;

import javax.validation.constraints.NotNull;
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

import es.test.daos.AbsBaseDao;
import es.test.daos.DaoException;
import es.test.daos.IdentifiableEntity;
import es.test.services.AbsCrudService;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class AbsRestResource<TService extends AbsCrudService<TEntity, TId, TDao>, TEntity extends IdentifiableEntity<TId>, TId, TDao extends AbsBaseDao<TEntity, TId>> {

    private TService service;

    protected TService getService() {
        return service;
    }

    public AbsRestResource(@NotNull TService taskService) {
        this.service = taskService;
    }

    @POST
    public Response create(@NotNull TEntity entity) throws DaoException {
        service.create(entity);
        return Response.ok(entity).build();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") @NotNull TId id) throws DaoException {
        TEntity entity = service.read(id);
        return Response.ok(entity).build();
    }

    @GET
    public Response getAll() throws DaoException {
        List<TEntity> entities = service.getAll();
        return Response.ok(entities).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") @NotNull TId id, @NotNull TEntity entity) throws DaoException {
        service.update(entity);
        return Response.ok().build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") @NotNull TId id) throws DaoException {
        service.deleteById(id);
        return Response.ok().build();
    }
}
