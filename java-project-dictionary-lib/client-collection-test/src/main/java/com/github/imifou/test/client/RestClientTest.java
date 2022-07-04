package com.github.imifou.test.client;

import com.github.imifou.MockServer;
import com.github.imifou.data.Authority;
import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.User;
import com.github.imifou.exception.BadRequestResponseException;
import com.github.imifou.exception.NotFoundResponseException;
import com.github.imifou.test.AbstractTest;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public abstract class RestClientTest extends AbstractTest implements UserClientInterface, WireMockInterface {

    @BeforeEach
    void initTest() {
        MDC.put(CorrelationId.CORRELATION_ID, UUID.randomUUID().toString());
    }

    @Test
    @DisplayName("1.0 - Get users method, nominal case")
    protected void testGetUsers_nominalCase() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.GET, "/users", 200, "1.0");

        assertTrue(getAllUser().containsAll(
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
    protected void testGetUsers_whenResponseIsEmpty() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.GET, "/users", 200, "1.1");

        assertTrue(getAllUser().isEmpty());
    }

    @Test
    @DisplayName("2.0 - Get user by id method, nominal case")
    protected void testGetUserById_nominalCase() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.GET, "/users/" + USER_ID, 200, "2.0");

        var user = getUser(USER_ID);
        assertEquals("Toto", user.name());
        assertEquals(15, user.age());
        assertEquals(LocalDate.of(2002, 10, 5), user.birthDate());
        assertEquals(List.of(new Authority("ADMIN"), new Authority("SIMPLE_USER")), user.authorities());
    }

    @Test
    @DisplayName("2.1 - Get user by id method with not found response with invalid error dto format")
    protected void testGetUserById_whenResponseIsNotFoundInvalidErrorFormat() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.GET, "/users/" + USER_ID, 404, "2.1");

        NotFoundResponseException exception = assertThrows(NotFoundResponseException.class, () -> getUser(USER_ID));
        assertEquals(exception.getError().code(), "NOT_FOUND");
        assertEquals(exception.getError().message(), "Not Found");
        assertEquals(exception.getError().correlationId(), UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID)));
    }


    @Test
    @DisplayName("2.2 - Get user by id method with not found response with valid error dto format")
    protected void testGetUserById_whenResponseIsNotFoundValidErrorFormat() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.GET, "/users/" + USER_ID, 404, "2.2");

        NotFoundResponseException exception = assertThrows(NotFoundResponseException.class, () -> getUser(USER_ID));
        assertEquals(exception.getError().code(), "INVALID_USER");
        assertEquals(exception.getError().message(), "Invalid user format !");
        assertEquals(exception.getError().correlationId(), UUID.fromString("c9495a10-4c28-4e9d-a912-63f293db5c06"));
    }

    @Test
    @DisplayName("2.3 - Get user by id method with bad request response with invalid error dto format")
    protected void testGetUserById_whenResponseIsBadRequestInvalidErrorFormat() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.GET, "/users/" + USER_ID, 400, "2.3");

        BadRequestResponseException exception = assertThrows(BadRequestResponseException.class, () -> getUser(USER_ID));
        assertEquals(exception.getError().code(), "BAD_REQUEST");
        assertEquals(exception.getError().message(), "Bad Request");
        assertEquals(exception.getError().correlationId(), UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID)));
    }

    @Test
    @DisplayName("2.4 - Get user by id method with bad request response with valid error dto format")
    protected void testGetUserById_whenResponseIsBadRequestValidErrorFormat() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.GET, "/users/" + USER_ID, 400, "2.4");

        BadRequestResponseException exception = assertThrows(BadRequestResponseException.class, () -> getUser(USER_ID));
        assertEquals(exception.getError().code(), "INVALID_USER");
        assertEquals(exception.getError().message(), "Invalid user format !");
        assertEquals(exception.getError().correlationId(), UUID.fromString("c9495a10-4c28-4e9d-a912-63f293db5c06"));
    }

    @Test
    @DisplayName("3.0 - Create user, nominal case")
    protected void testCreateUser_nominalCase() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.POST, "/users", 201, "3.0");

        var user = buildDefaultUser();
        var createUser = createUser(user);
        assertEquals(user, createUser);
    }

    @Test
    @DisplayName("3.1 - Create user method with bad request response with invalid error dto format")
    protected void testCreateUser_whenResponseIsBadRequestInvalidErrorFormat() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.POST, "/users", 400, "3.1");

        var user = buildDefaultUser();
        BadRequestResponseException exception = assertThrows(BadRequestResponseException.class, () -> createUser(user));
        assertEquals(exception.getError().code(), "BAD_REQUEST");
        assertEquals(exception.getError().message(), "Bad Request");
        assertEquals(exception.getError().correlationId(), UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID)));
    }

    @Test
    @DisplayName("3.2 - Create user method with bad request response with valid error dto format")
    protected void testCreateUser_whenResponseIsBadRequestValidErrorFormat() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.POST, "/users", 400, "3.2");

        var user = buildDefaultUser();
        BadRequestResponseException exception = assertThrows(BadRequestResponseException.class, () -> createUser(user));
        assertEquals(exception.getError().code(), "INVALID_USER");
        assertEquals(exception.getError().message(), "Invalid user format !");
        assertEquals(exception.getError().correlationId(), UUID.fromString("c9495a10-4c28-4e9d-a912-63f293db5c06"));
    }

    @Test
    @DisplayName("4.0 - Update user, nominal case")
    protected void testUpdateUser_nominalCase() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.PUT, "/users/" + USER_ID, 200, "4.0");

        var user = buildDefaultUser();
        var updateUser = updateUser(USER_ID, user);
        assertEquals(user, updateUser);
    }

    @Test
    @DisplayName("4.1 - Update user method with not found response with invalid error dto format")
    protected void testUpdateUser_whenResponseIsNotFoundInvalidErrorFormat() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.PUT, "/users/" + USER_ID, 404, "4.1");

        var user = buildDefaultUser();
        NotFoundResponseException exception = assertThrows(NotFoundResponseException.class, () -> updateUser(USER_ID, user));
        assertEquals(exception.getError().code(), "NOT_FOUND");
        assertEquals(exception.getError().message(), "Not Found");
        assertEquals(exception.getError().correlationId(), UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID)));
    }


    @Test
    @DisplayName("4.2 - Update user method with not found response with valid error dto format")
    protected void testUpdateUser_whenResponseIsNotFoundValidErrorFormat() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.PUT, "/users/" + USER_ID, 404, "4.2");

        var user = buildDefaultUser();
        NotFoundResponseException exception = assertThrows(NotFoundResponseException.class, () -> updateUser(USER_ID, user));
        assertEquals(exception.getError().code(), "INVALID_USER");
        assertEquals(exception.getError().message(), "Invalid user format !");
        assertEquals(exception.getError().correlationId(), UUID.fromString("c9495a10-4c28-4e9d-a912-63f293db5c06"));
    }

    @Test
    @DisplayName("4.3 - Update user method with bad request response with invalid error dto format")
    protected void testUpdateUser_whenResponseIsBadRequestInvalidErrorFormat() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.PUT, "/users/" + USER_ID, 400, "4.3");

        var user = buildDefaultUser();
        BadRequestResponseException exception = assertThrows(BadRequestResponseException.class, () -> updateUser(USER_ID, user));
        assertEquals(exception.getError().code(), "BAD_REQUEST");
        assertEquals(exception.getError().message(), "Bad Request");
        assertEquals(exception.getError().correlationId(), UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID)));
    }

    @Test
    @DisplayName("4.4 - Update user method with bad request response with valid error dto format")
    protected void testUpdateUser_whenResponseIsBadRequestValidError() {
        MockServer.mockRestServer(getWireMockServer(), RequestMethod.PUT, "/users/" + USER_ID, 400, "4.4");

        var user = buildDefaultUser();
        BadRequestResponseException exception = assertThrows(BadRequestResponseException.class, () -> updateUser(USER_ID, user));
        assertEquals(exception.getError().code(), "INVALID_USER");
        assertEquals(exception.getError().message(), "Invalid user format !");
        assertEquals(exception.getError().correlationId(), UUID.fromString("c9495a10-4c28-4e9d-a912-63f293db5c06"));
    }
}
