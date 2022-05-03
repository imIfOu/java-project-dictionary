package com.github.imifou.client;

import com.github.imifou.client.config.CorrelationIdRequestInterceptor;
import com.github.imifou.client.config.UserClientErrorHandler;
import com.github.imifou.data.User;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import java.util.List;

@Path("/users")
@Produces("application/json")
@Consumes("application/json")
@RegisterRestClient(configKey="user")
@RegisterProvider(CorrelationIdRequestInterceptor.class)
@RegisterProvider(UserClientErrorHandler.class)
public interface UserClient {

    @GET
    Uni<List<User>> getUsers();

    @GET
    @Path("/{id}")
    Uni<User> getUser(@PathParam("id") Long id);

    @POST
    Uni<User> createUser(User user);
    @PUT
    @Path("/{id}")
    Uni<User> updateUser(@PathParam("id") Long id, User user);
}
