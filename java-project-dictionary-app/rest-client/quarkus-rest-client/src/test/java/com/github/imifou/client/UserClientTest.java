package com.github.imifou.client;

import com.github.imifou.MockServer;
import com.github.imifou.client.config.InjectWireMock;
import com.github.imifou.client.config.WireMockConfig;
import com.github.imifou.data.Authority;
import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.User;
import com.github.imifou.exception.BadRequestResponseException;
import com.github.imifou.exception.NotFoundResponseException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
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
    @RestClient
    UserClient userClient;

    @BeforeEach
    void initTest() {
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
        MockServer.mockRestServer(wireMockServer, RequestMethod.GET, "/users", Response.Status.OK.getStatusCode(), "1.0");

        assertTrue(userClient.getUsers().containsAll(
                asList(
                        new User("Toto", 15, LocalDate.of(2002, 10, 5), List.of(
                                new Authority("ADMIN"),
                                new Authority("SIMPLE_USER"))
                        ),
                        new User("Titi", 10, LocalDate.of(2012, 10, 5), null)
                )));
    }


    @Test
    @DisplayName("1.1 - Get users method with a empty array response")
    void testGetUsers_whenResponseIsEmpty() {
        MockServer.mockRestServer(wireMockServer, RequestMethod.GET, "/users", Response.Status.OK.getStatusCode(), "1.1");

        assertTrue(userClient.getUsers().isEmpty());
    }

    @Test
    @DisplayName("2.0 - Get user by id method, nominal case")
    void testGetUserById_nominalCase() {
        MockServer.mockRestServer(wireMockServer, RequestMethod.GET, "/users/" + USER_ID, Response.Status.OK.getStatusCode(), "2.0");

        var user = userClient.getUser(USER_ID);
        assertEquals("Toto", user.name());
        assertEquals(15, user.age());
        assertEquals(LocalDate.of(2002, 10, 5), user.birthDate());
        assertEquals(List.of(new Authority("ADMIN"), new Authority("SIMPLE_USER")), user.authorities());
    }

    @Test
    @DisplayName("2.1 - Get user by id method with not found response with invalid error dto format")
    void testGetUserById_whenResponseIsNotFoundInvalidErrorFormat() {
        MockServer.mockRestServer(wireMockServer, RequestMethod.GET, "/users/" + USER_ID, Response.Status.NOT_FOUND.getStatusCode(), "2.1");

        NotFoundResponseException exception = assertThrows(NotFoundResponseException.class, () -> userClient.getUser(USER_ID));
        assertEquals(exception.getError().code(), Response.Status.NOT_FOUND.name());
        assertEquals(exception.getError().message(), Response.Status.NOT_FOUND.getReasonPhrase());
        assertEquals(exception.getError().correlationId(), UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID)));
    }


    @Test
    @DisplayName("2.2 - Get user by id method with not found response with valid error dto format")
    void testGetUserById_whenResponseIsNotFoundValidErrorFormat() {
        MockServer.mockRestServer(wireMockServer, RequestMethod.GET, "/users/" + USER_ID, Response.Status.NOT_FOUND.getStatusCode(), "2.2");

        NotFoundResponseException exception = assertThrows(NotFoundResponseException.class, () -> userClient.getUser(USER_ID));
        assertEquals(exception.getError().code(), "INVALID_USER");
        assertEquals(exception.getError().message(), "Invalid user format !");
        assertEquals(exception.getError().correlationId(), UUID.fromString("c9495a10-4c28-4e9d-a912-63f293db5c06"));
    }

    @Test
    @DisplayName("2.3 - Get user by id method with bad request response with invalid error dto format")
    void testGetUserById_whenResponseIsBadRequestInvalidErrorFormat() {
        MockServer.mockRestServer(wireMockServer, RequestMethod.GET, "/users/" + USER_ID, Response.Status.BAD_REQUEST.getStatusCode(), "2.3");

        BadRequestResponseException exception = assertThrows(BadRequestResponseException.class, () -> userClient.getUser(USER_ID));
        assertEquals(exception.getError().code(), Response.Status.BAD_REQUEST.name());
        assertEquals(exception.getError().message(), Response.Status.BAD_REQUEST.getReasonPhrase());
        assertEquals(exception.getError().correlationId(), UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID)));
    }

    @Test
    @DisplayName("2.4 - Get user by id method with bad request response with valid error dto format")
    void testGetUserById_whenResponseIsBadRequestValidErrorFormat() {
        MockServer.mockRestServer(wireMockServer, RequestMethod.GET, "/users/" + USER_ID, Response.Status.BAD_REQUEST.getStatusCode(), "2.4");

        BadRequestResponseException exception = assertThrows(BadRequestResponseException.class, () -> userClient.getUser(USER_ID));
        assertEquals(exception.getError().code(), "INVALID_USER");
        assertEquals(exception.getError().message(), "Invalid user format !");
        assertEquals(exception.getError().correlationId(), UUID.fromString("c9495a10-4c28-4e9d-a912-63f293db5c06"));
    }

    @Test
    @DisplayName("3.0 - Create user, nominal case")
    void testCreateUser_nominalCase() {
        MockServer.mockRestServer(wireMockServer, RequestMethod.POST, "/users", Response.Status.CREATED.getStatusCode(), "3.0");

        var user = buildDefaultUser();
        var createUser = userClient.createUser(user);
        assertEquals(user, createUser);
    }

    @Test
    @DisplayName("3.1 - Create user method with bad request response with invalid error dto format")
    void testCreateUser_whenResponseIsBadRequestInvalidErrorFormat() {
        MockServer.mockRestServer(wireMockServer, RequestMethod.POST, "/users", Response.Status.BAD_REQUEST.getStatusCode(), "3.1");

        var user = buildDefaultUser();
        BadRequestResponseException exception = assertThrows(BadRequestResponseException.class, () -> userClient.createUser(user));
        assertEquals(exception.getError().code(), Response.Status.BAD_REQUEST.name());
        assertEquals(exception.getError().message(), Response.Status.BAD_REQUEST.getReasonPhrase());
        assertEquals(exception.getError().correlationId(), UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID)));
    }

    @Test
    @DisplayName("3.2 - Create user method with bad request response with valid error dto format")
    void testCreateUser_whenResponseIsBadRequestValidErrorFormat() {
        MockServer.mockRestServer(wireMockServer, RequestMethod.POST, "/users", Response.Status.BAD_REQUEST.getStatusCode(), "3.2");

        var user = buildDefaultUser();
        BadRequestResponseException exception = assertThrows(BadRequestResponseException.class, () -> userClient.createUser(user));
        assertEquals(exception.getError().code(), "INVALID_USER");
        assertEquals(exception.getError().message(), "Invalid user format !");
        assertEquals(exception.getError().correlationId(), UUID.fromString("c9495a10-4c28-4e9d-a912-63f293db5c06"));
    }

    @Test
    @DisplayName("4.0 - Update user, nominal case")
    void testUpdateUser_nominalCase() {
        MockServer.mockRestServer(wireMockServer, RequestMethod.PUT, "/users/" + USER_ID, Response.Status.OK.getStatusCode(), "4.0");

        var user = buildDefaultUser();
        var updateUser = userClient.updateUser(USER_ID, user);
        assertEquals(user, updateUser);
    }

    @Test
    @DisplayName("4.1 - Update user method with not found response with invalid error dto format")
    void testUpdateUser_whenResponseIsNotFoundInvalidErrorFormat() {
        MockServer.mockRestServer(wireMockServer, RequestMethod.PUT, "/users/" + USER_ID, Response.Status.NOT_FOUND.getStatusCode(), "4.1");

        var user = buildDefaultUser();
        NotFoundResponseException exception = assertThrows(NotFoundResponseException.class, () -> userClient.updateUser(USER_ID, user));
        assertEquals(exception.getError().code(), Response.Status.NOT_FOUND.name());
        assertEquals(exception.getError().message(), Response.Status.NOT_FOUND.getReasonPhrase());
        assertEquals(exception.getError().correlationId(), UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID)));
    }


    @Test
    @DisplayName("4.2 - Update user method with not found response with valid error dto format")
    void testUpdateUser_whenResponseIsNotFoundValidErrorFormat() {
        MockServer.mockRestServer(wireMockServer, RequestMethod.PUT, "/users/" + USER_ID, Response.Status.NOT_FOUND.getStatusCode(), "4.2");

        var user = buildDefaultUser();
        NotFoundResponseException exception = assertThrows(NotFoundResponseException.class, () -> userClient.updateUser(USER_ID, user));
        assertEquals(exception.getError().code(), "INVALID_USER");
        assertEquals(exception.getError().message(), "Invalid user format !");
        assertEquals(exception.getError().correlationId(), UUID.fromString("c9495a10-4c28-4e9d-a912-63f293db5c06"));
    }

    @Test
    @DisplayName("4.3 - Update user method with bad request response with invalid error dto format")
    void testUpdateUser_whenResponseIsBadRequestInvalidErrorFormat() {
        MockServer.mockRestServer(wireMockServer, RequestMethod.PUT, "/users/" + USER_ID, Response.Status.BAD_REQUEST.getStatusCode(), "4.3");

        var user = buildDefaultUser();
        BadRequestResponseException exception = assertThrows(BadRequestResponseException.class, () -> userClient.updateUser(USER_ID, user));
        assertEquals(exception.getError().code(), Response.Status.BAD_REQUEST.name());
        assertEquals(exception.getError().message(), Response.Status.BAD_REQUEST.getReasonPhrase());
        assertEquals(exception.getError().correlationId(), UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID)));
    }

    @Test
    @DisplayName("4.4 - Update user method with bad request response with valid error dto format")
    void testUpdateUser_whenResponseIsBadRequestValidErrorFormat() {
        MockServer.mockRestServer(wireMockServer, RequestMethod.PUT, "/users/" + USER_ID, Response.Status.BAD_REQUEST.getStatusCode(), "4.4");

        var user = buildDefaultUser();
        BadRequestResponseException exception = assertThrows(BadRequestResponseException.class, () -> userClient.updateUser(USER_ID, user));
        assertEquals(exception.getError().code(), "INVALID_USER");
        assertEquals(exception.getError().message(), "Invalid user format !");
        assertEquals(exception.getError().correlationId(), UUID.fromString("c9495a10-4c28-4e9d-a912-63f293db5c06"));
    }
}
