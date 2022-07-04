package com.github.imifou.client;

import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.User;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import io.smallrye.graphql.client.typesafe.api.Header;
import io.smallrye.graphql.client.typesafe.api.NestedParameter;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;
import org.slf4j.MDC;

import java.util.List;

@GraphQLClientApi(configKey = "user")
@Header(name = CorrelationId.CORRELATION_ID_HEADER, method = "setHeaderCorrelationId")
public interface UserClient {

    @Query(value = "users")
    Uni<List<User>> getUsers();

    @Query(value = "user")
    Uni<User> getUser(@NestedParameter("user") Long id);

    @Mutation
    Uni<User> createUser(User user);

    @Mutation
    Uni<User> updateUser(@NestedParameter("id") Long id, User user);

    static String setHeaderCorrelationId() {
        return MDC.get(CorrelationId.CORRELATION_ID);
    }
}
