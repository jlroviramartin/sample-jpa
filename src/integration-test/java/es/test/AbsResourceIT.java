package es.test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.test.daos.IdentifiableEntity;
import kong.unirest.GenericType;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class AbsResourceIT<TEntity extends IdentifiableEntity<TId>, TId> {

    private ObjectMapper objectMapper = new ObjectMapper();
    {
        Unirest.config().setObjectMapper(new JacksonObjectMapper(objectMapper));
    }

    protected String baseUrl;
    protected final Class<TEntity> clazz;
    protected final Class<TId> idClazz;
    protected final GenericType<List<TEntity>> listOfEntities;

    protected AbsResourceIT(Class<TEntity> clazz, Class<TId> idClazz, GenericType<List<TEntity>> listOfEntities, String baseUrl) {
        this.clazz = clazz;
        this.idClazz = idClazz;
        this.listOfEntities = listOfEntities;
        this.baseUrl = baseUrl;
    }

    protected HttpResponse<TEntity> create(TEntity entity) throws JsonProcessingException {
        return Unirest.post(this.baseUrl)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(entity))
                .asObject(clazz);
    }

    protected HttpResponse<TEntity> get(TId id) {
        return Unirest.get(this.baseUrl + "/{id}")
                .routeParam("id", String.valueOf(id))
                .asObject(clazz);
    }

    protected HttpResponse<List<TEntity>> getAll() {
        return Unirest.get(this.baseUrl)
                .asObject(listOfEntities);
    }

    protected HttpResponse<?> update(TEntity entity) throws JsonProcessingException {
        TId id = entity.getId();
        return Unirest.put(this.baseUrl + "/{id}")
                .routeParam("id", id + "")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(entity))
                .asEmpty();
    }

    protected HttpResponse<?> delete(TId id) {
        return Unirest.delete(this.baseUrl + "/{id}")
                .routeParam("id", String.valueOf(id))
                .asEmpty();
    }

    protected <T> T readValue(Class<T> clazz, JsonNode json) throws JsonParseException, JsonMappingException, IOException {
        return objectMapper.readValue(json.toString(), clazz);
    }

    protected <T> T readValue(Class<T> clazz, HttpRequest<? extends HttpRequest<?>> req) throws JsonParseException, JsonMappingException, IOException {
        return readValue(clazz, req.asJson().getBody());
    }
}
