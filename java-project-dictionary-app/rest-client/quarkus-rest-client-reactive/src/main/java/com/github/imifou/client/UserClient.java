package com.github.imifou.client;

import com.github.imifou.client.config.CorrelationIdRequestInterceptor;
import com.github.imifou.client.config.UserClientErrorHandler;
import com.github.imifou.data.User;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "user")
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
