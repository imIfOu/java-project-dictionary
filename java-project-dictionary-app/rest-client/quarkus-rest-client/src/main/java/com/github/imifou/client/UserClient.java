package com.github.imifou.client;

import com.github.imifou.client.config.CorrelationIdRequestInterceptor;
import com.github.imifou.client.config.UserClientErrorHandler;
import com.github.imifou.data.User;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import java.util.List;

@Path("/users")
@Produces("application/json")
@Consumes("application/json")
@RegisterRestClient(configKey = "user")
@RegisterProvider(CorrelationIdRequestInterceptor.class)
@RegisterProvider(UserClientErrorHandler.class)
public interface UserClient {

    @GET
    List<User> getUsers();

    @GET
    @Path("/{id}")
    User getUser(@PathParam("id") Long id);

    @POST
    User createUser(User user);

    @PUT
    @Path("/{id}")
    User updateUser(@PathParam("id") Long id, User user);
}
