package com.github.imifou.client;

import com.github.imifou.client.config.InjectWireMock;
import com.github.imifou.client.config.WireMockConfig;
import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.User;
import com.github.imifou.test.client.GraphQLClientTest;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.graphql.client.GraphQLClientException;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.MDC;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@QuarkusTest
@QuarkusTestResource(WireMockConfig.class)
public class UserClientTest extends GraphQLClientTest<GraphQLClientException> {
    
    @InjectWireMock
    WireMockServer wireMockServer;

    @Inject
    UserClient userClient;

    @BeforeAll
    static void initTest() {
        MDC.put(CorrelationId.CORRELATION_ID, UUID.randomUUID().toString());
    }

    @Override
    public List<User> getAllUser() {
        return userClient.getUsers()
                .await()
                .indefinitely();
    }

    @Override
    public User getUser(Long id) {
        return userClient.getUser(id)
                .await()
                .indefinitely();
    }

    @Override
    public User createUser(User user) {
        return userClient.createUser(user)
                .await()
                .indefinitely();
    }

    @Override
    public User updateUser(Long id, User user) {
        return userClient.updateUser(USER_ID, user)
                .await()
                .indefinitely();
    }

    @Override
    public WireMockServer getWireMockServer() {
        return wireMockServer;
    }

    @Override
    protected Class<GraphQLClientException> getGraphQLResponseException() {
        return GraphQLClientException.class;
    }

    @Override
    protected Function<GraphQLClientException, String> getMethodErrorCode() {
        return (graphQLClientException) -> graphQLClientException.getErrors().get(0).getCode();
    }

    @Override
    protected Function<GraphQLClientException, String> getMethodErrorMessage() {
        return (graphQLClientException) -> graphQLClientException.getErrors().get(0).getMessage();
    }
}
