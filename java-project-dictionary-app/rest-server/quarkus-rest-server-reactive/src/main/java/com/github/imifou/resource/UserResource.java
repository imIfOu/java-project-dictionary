package com.github.imifou.resource;

import com.github.imifou.entity.User;
import com.github.imifou.exception.BadRequestResponseException;
import com.github.imifou.exception.NotFoundResponseException;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @GET
    public Uni<Response> getAllUsers() {
        return User.<User>listAll()
                .map(users -> Response.ok(users).build());
    }

    @GET
    @Path("/{id}")
    public Uni<Response> get(@PathParam("id") Long id) {
        return User.<User>findById(id)
                .onItem().ifNull().failWith(new NotFoundResponseException())
                .map(user -> Response.ok(user).build());
    }

    @POST
    @ReactiveTransactional
    public Uni<Response> create(User user, UriInfo uriInfo) {
        return user.<User>persist()
                .map(persistUser -> Response.created(uriInfo.getRequestUri().resolve("/api/v1/users/" + persistUser.id))
                        .entity(persistUser)
                        .build()
                );
    }

    @PUT
    @Path("/{id}")
    @ReactiveTransactional
    public Uni<Response> update(@PathParam("id") Long id, User user) {
        return User.<User>findById(id)
                .onItem().ifNull().failWith(new NotFoundResponseException())
                .invoke(userToUpdate -> {
                    userToUpdate.name = user.name;
                    userToUpdate.age = user.age;
                    userToUpdate.birthDate = user.birthDate;
                    userToUpdate.authorities = user.authorities;
                })
                .map(userToUpdate -> Response.ok(userToUpdate).build());
    }

    @DELETE
    @Path("/{id}")
    @ReactiveTransactional
    public Uni<Response> delete(@PathParam("id") Long id) {
        return User.deleteById(id)
                .map(isDeleted -> {
                    if (!isDeleted) throw new BadRequestResponseException();
                    return Response.noContent().build();
                });
    }
}
