package com.github.imifou.client;

import com.github.imifou.client.config.WireMockConfig;
import com.github.imifou.client.exception.GraphQLClientException;
import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.User;
import com.github.imifou.test.client.GraphQLClientTest;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@SpringBootTest
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = {WireMockConfig.class})
@DisplayName("Test user graphql client")
public class UserClientTest extends GraphQLClientTest<GraphQLClientException> {

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    UserClient userClient;

    @BeforeEach
    void initTest() {
        MDC.put(CorrelationId.CORRELATION_ID, UUID.randomUUID().toString());
    }

    @Override
    public List<User> getAllUser() {
        return userClient.getUsers().collectList().block();
    }

    @Override
    public User getUser(Long id) {
        return userClient.getUser(id).block();
    }

    @Override
    public User createUser(User user) {
        return userClient.createUser(user).block();
    }

    @Override
    public User updateUser(Long id, User user) {
        return userClient.updateUser(id, user).block();
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
        return (graphQLErrorsException) -> "NOT_FOUND";
    }

    @Override
    protected Function<GraphQLClientException, String> getMethodErrorMessage() {
        return (graphQLErrorsException) -> graphQLErrorsException.getErrors().get(0).getMessage();
    }

    @Override
    protected void testCreateUser_nominalCase() {
    }

    @Override
    protected void testCreateUser_whenResponseIsErrorWithLocationData() {
    }

    @Override
    protected void testUpdateUser_nominalCase() {
    }

    @Override
    protected void testUpdateUser_whenResponseIsErrorWithLocationData() {
    }
}

