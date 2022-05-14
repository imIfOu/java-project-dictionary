package com.github.imifou.client;

import com.github.imifou.MockServer;
import com.github.imifou.client.config.InjectWireMock;
import com.github.imifou.client.config.WireMockConfig;
import com.github.imifou.data.Authority;
import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.User;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.graphql.client.GraphQLClientException;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(WireMockConfig.class)
public class UserClientTest {

    private static final Long USER_ID = 1L;

    @InjectWireMock
    WireMockServer wireMockServer;

    @Inject
    UserClient userClient;

    @BeforeAll
    static void initTest() {
        MDC.put(CorrelationId.CORRELATION_ID, UUID.randomUUID().toString());
    }

    private User buildDefaultUser() {
        return new User("Toto", 15, LocalDate.of(2002, 10, 5), List.of(
                new Authority("ADMIN"),
                new Authority("SIMPLE_USER"))
        );
    }

    @Test
    @DisplayName("1.0 - Get users method, nominal case")
    void testGetUsers_nominalCase() {
        MockServer.mockGraphqlServer(wireMockServer, "1.0");

        userClient.getUsers()
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted()
                .assertItem(
                        asList(
                                new User("Toto", 15, LocalDate.of(2002, 10, 05), List.of(
                                        new Authority("ADMIN"),
                                        new Authority("SIMPLE_USER"))
                                ),
                                new User("Titi", 10, LocalDate.of(2012, 10, 05), null)
                        )
                );
    }

    @Test
    @DisplayName("1.1 - Get users method with a empty array response")
    void testGetUsers_whenResponseIsEmpty() {
        MockServer.mockGraphqlServer(wireMockServer, "1.1");

        var users = userClient.getUsers()
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted()
                .getItem();

        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("2.0 - Get user by id method, nominal case")
    void testGetUserById_nominalCase() {
        MockServer.mockGraphqlServer(wireMockServer, "2.0");

        var userOrError = userClient.getUser(USER_ID)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted()
                .getItem();

        assertTrue(userOrError.isPresent());
        assertFalse(userOrError.hasErrors());

        var user = userOrError.get();
        assertEquals("Toto", user.name());
        assertEquals(15, user.age());
        assertEquals(LocalDate.of(2002, 10, 5), user.birthDate());
        assertEquals(List.of(new Authority("ADMIN"), new Authority("SIMPLE_USER")), user.authorities());
    }

    @Test
    @DisplayName("2.1 - Get user by id method with error with location path")
    void testGetUserById_whenResponseIsErrorWithLocationData() {
        MockServer.mockGraphqlServer(wireMockServer, "2.1");

        var userOrError = userClient.getUser(USER_ID)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted()
                .getItem();

        assertFalse(userOrError.isPresent());
        assertTrue(userOrError.hasErrors());

        var error = userOrError.getErrors().get(0);
        assertEquals(error.getCode(), Response.Status.NOT_FOUND.name());
        assertEquals(error.getMessage(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    @Test
    @DisplayName("2.2 - Get user by id method with error with no location path")
    void testGetUserById_whenResponseIsErrorWithoutLocationData() {
        MockServer.mockGraphqlServer(wireMockServer, "2.2");

        GraphQLClientException exception = (GraphQLClientException) userClient.getUser(USER_ID)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitFailure()
                .assertFailedWith(GraphQLClientException.class)
                .getFailure();

        var error = exception.getErrors().get(0);
        assertEquals(error.getCode(), Response.Status.NOT_FOUND.name());
        assertEquals(error.getMessage(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    @Test
    @DisplayName("3.0 - Create user, nominal case")
    void testCreateUser_nominalCase() {
        MockServer.mockGraphqlServer(wireMockServer, "3.0");

        var user = buildDefaultUser();
        var userOrError = userClient.createUser(user)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted()
                .getItem();

        assertTrue(userOrError.isPresent());
        assertFalse(userOrError.hasErrors());

        var createUser = userOrError.get();
        assertEquals(user, createUser);
    }

    @Test
    @DisplayName("3.1 - Create user method with error with location path")
    void testCreateUser_whenResponseIsErrorWithLocationData() {
        MockServer.mockGraphqlServer(wireMockServer, "3.1");

        var user = buildDefaultUser();
        var userOrError = userClient.createUser(user)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted()
                .getItem();

        assertFalse(userOrError.isPresent());
        assertTrue(userOrError.hasErrors());

        var error = userOrError.getErrors().get(0);
        assertEquals(error.getCode(), Response.Status.NOT_FOUND.name());
        assertEquals(error.getMessage(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    @Test
    @DisplayName("3.2 - Create user method with error with no location path")
    void testCreateUser_whenResponseIsErrorWithoutLocationData() {
        MockServer.mockGraphqlServer(wireMockServer, "3.2");

        var user = buildDefaultUser();
        GraphQLClientException exception = (GraphQLClientException) userClient.createUser(user)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitFailure()
                .assertFailedWith(GraphQLClientException.class)
                .getFailure();

        var error = exception.getErrors().get(0);
        assertEquals(error.getCode(), Response.Status.NOT_FOUND.name());
        assertEquals(error.getMessage(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    @Test
    @DisplayName("4.0 - Update user, nominal case")
    void testUpdateUser_nominalCase() {
        MockServer.mockGraphqlServer(wireMockServer, "4.0");

        var user = buildDefaultUser();
        var userOrError = userClient.updateUser(USER_ID, user)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted()
                .getItem();

        assertTrue(userOrError.isPresent());
        assertFalse(userOrError.hasErrors());

        var updateUser = userOrError.get();
        assertEquals(user, updateUser);
    }

    @Test
    @DisplayName("4.1 - Update user by id method with error with location path")
    void testUpdateUser_whenResponseIsErrorWithLocationData() {
        MockServer.mockGraphqlServer(wireMockServer, "4.1");

        var user = buildDefaultUser();
        var userOrError = userClient.updateUser(USER_ID, user)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted()
                .getItem();

        assertFalse(userOrError.isPresent());
        assertTrue(userOrError.hasErrors());

        var error = userOrError.getErrors().get(0);
        assertEquals(error.getCode(), Response.Status.NOT_FOUND.name());
        assertEquals(error.getMessage(), Response.Status.NOT_FOUND.getReasonPhrase());
    }

    @Test
    @DisplayName("4.2 - Update user by id method with error with no location path")
    void testUpdateUser_whenResponseIsErrorWithoutLocationData() {
        MockServer.mockGraphqlServer(wireMockServer, "4.2");

        var user = buildDefaultUser();
        GraphQLClientException exception = (GraphQLClientException) userClient.updateUser(USER_ID, user)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitFailure()
                .assertFailedWith(GraphQLClientException.class)
                .getFailure();

        var error = exception.getErrors().get(0);
        assertEquals(error.getCode(), Response.Status.NOT_FOUND.name());
        assertEquals(error.getMessage(), Response.Status.NOT_FOUND.getReasonPhrase());
    }
}
