package es.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kong.unirest.GenericType;
import kong.unirest.HttpRequest;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class TaskResourceIT {

    private static final GenericType<List<Task>> listOfTasks = new GenericType<List<Task>>() {
    };

    private String baseUrl;
    private ObjectMapper objectMapper = new ObjectMapper();

    {
        Unirest.config().setObjectMapper(new JacksonObjectMapper(objectMapper));
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
    public void callIndexPage() throws Exception {
        // getAll
        List<Task> taks = Unirest.get(this.baseUrl)
                .asObject(listOfTasks)
                .getBody();
        assertEquals(0, taks.size());

        Task task = new Task();
        task.setName("name");
        task.setDescription("description");

        // create
        int status = Unirest.post(this.baseUrl)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(task))
                .asJson()
                .getStatus();
        assertEquals(200, status);

        // getAll
        List<Task> taks2 = Unirest.get(this.baseUrl)
                .asObject(listOfTasks)
                .getBody();
        assertEquals(1, taks2.size());

        int id = taks2.get(0).getId();

        // get
        Task task2 = Unirest.get(this.baseUrl + "/{id}")
                .routeParam("id", id + "")
                .asObject(Task.class)
                .getBody();
        assertNotNull(task2);

        Task task3 = new Task();
        task3.setName("name2");
        task3.setDescription("description2");

        // update
        int status2 = Unirest.put(this.baseUrl + "/{id}")
                .routeParam("id", id + "")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(task3))
                .asJson()
                .getStatus();
        assertEquals(200, status2);

        // delete
        int status3 = Unirest.delete(this.baseUrl + "/{id}")
                .routeParam("id", id + "")
                .asJson()
                .getStatus();
        assertEquals(200, status3);

        //getAll
        List<Task> taks3 = Unirest.get(this.baseUrl)
                .asObject(listOfTasks)
                .getBody();
        assertEquals(0, taks3.size());
    }

    private <T> T readValue(Class<T> clazz, JsonNode json) throws JsonParseException, JsonMappingException, IOException {
        return objectMapper.readValue(json.toString(), clazz);
    }

    private <T> T readValue(Class<T> clazz, HttpRequest<? extends HttpRequest<?>> req) throws JsonParseException, JsonMappingException, IOException {
        return readValue(clazz, req.asJson().getBody());
    }
}
