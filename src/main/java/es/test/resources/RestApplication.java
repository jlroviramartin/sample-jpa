package es.test.resources;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import es.test.MyBinder;
import es.test.daos.TaskDao;
import es.test.json.ObjectMapperProvider;
import es.test.services.TaskService;

@ApplicationPath("/rest")
public class RestApplication extends ResourceConfig {

    public RestApplication() {
        configResources();
        registerServices();
    }

    private void configResources() {
        System.out.println("***** RestApplication: configResources");

        // json mapping
        register(ObjectMapperProvider.class);
        register(JacksonFeature.class);

        // Resources REST
        register(TaskResource.class);

        // Providers
        register(CorsFilter.class);

        // Listeners
        //register(GuiceBridgeLifecycleListener.class);
    }

    private void registerServices() {
        register(new MyBinder());
    }
}
