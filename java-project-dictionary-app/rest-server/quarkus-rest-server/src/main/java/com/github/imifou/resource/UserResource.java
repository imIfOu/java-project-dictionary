package com.github.imifou.resource;

import com.github.imifou.entity.User;
import com.github.imifou.exception.BadRequestResponseException;
import com.github.imifou.exception.NotFoundResponseException;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @GET
    public Response getAllUsers() {
        return Response.ok(User.listAll())
                .build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        return User.findByIdOptional(id)
                .map(user -> Response.ok(user).build())
                .orElseThrow(() -> new NotFoundResponseException());
    }

    @POST
    @Transactional
    public Response create(User user) {
        user.persist();
        return Response.created(URI.create("/api/v1/users/" + user.id))
                .entity(user)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, User user) {
        return User.<User>findByIdOptional(id)
                .map(userToUpdate -> {
                    userToUpdate.name = user.name;
                    userToUpdate.age = user.age;
                    userToUpdate.birthDate = user.birthDate;
                    userToUpdate.authorities = user.authorities;
                    return Response.ok(userToUpdate).build();
                })
                .orElseThrow(() -> new BadRequestResponseException());
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        if (!User.deleteById(id)) throw new BadRequestResponseException();
        return Response.noContent().build();
    }
}
