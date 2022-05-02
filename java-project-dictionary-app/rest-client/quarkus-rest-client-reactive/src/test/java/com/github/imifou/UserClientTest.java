package com.github.imifou;

import com.github.imifou.client.UserClient;
import com.github.imifou.config.InjectWireMock;
import com.github.imifou.config.WireMockConfig;
import com.github.imifou.data.Authority;
import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.User;
import com.github.imifou.exception.BadRequestResponseException;
import com.github.imifou.exception.NotFoundResponseException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.nio.charset.Charset.defaultCharset;
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

    @BeforeAll
    static void initTest() {
        MDC.put(CorrelationId.CORRELATION_ID, UUID.randomUUID().toString());
    }

    void configureMockServer(String path, int status, String resource) throws IOException {
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(path))
                .willReturn(WireMock.aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBody(
                                new String(
                                        UserClientTest.class.getClassLoader().getResourceAsStream(resource).readAllBytes(),
                                        defaultCharset()
                                )
                        )));
    }

    @Test
    @DisplayName("Get users method with not empty array response")
    void testGetUsers_whenResponseIsNotEmpty() throws IOException {
        configureMockServer("/users", Response.Status.OK.getStatusCode() ,"payload/get-users-list-response.json");

        userClient.getUsers()
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted()
                .assertItem(
                    asList(
                        new User("Toto", 15, LocalDate.of(2002,10,05), List.of(
                            new Authority("ADMIN"),
                            new Authority("SIMPLE_USER"))
                        ),
                        new User("Titi", 10, LocalDate.of(2012,10,05), null)
                    )
                );
    }

    @Test
    @DisplayName("Get users method with a empty array response")
    void testGetUsers_whenResponseIsEmpty() throws IOException {
        configureMockServer("/users", Response.Status.OK.getStatusCode() ,"payload/get-users-empty-response.json");

        List<User> users = userClient.getUsers()
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitItem()
                .assertCompleted()
                .getItem();

        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("Get user by id method with not found response with invalid error dto format")
    void testGetUserById_whenResponseIsNotFoundInvalidErrorFormat() throws IOException {
        configureMockServer("/users/" + USER_ID, Response.Status.NOT_FOUND.getStatusCode() ,"payload/empty-response.json");

        NotFoundResponseException exception = (NotFoundResponseException) userClient.getUser(USER_ID)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitFailure()
                .assertFailedWith(NotFoundResponseException.class)
                .getFailure();

        assertEquals(exception.getError().code(), Response.Status.NOT_FOUND.name());
        assertEquals(exception.getError().message(), Response.Status.NOT_FOUND.getReasonPhrase());
        assertEquals(exception.getError().correlationId(), UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID)));
    }

    @Test
    @DisplayName("Get user by id method with not found response with valid error dto format")
    void testGetUserById_whenResponseIsNotFoundValidErrorFormat() throws IOException {
        configureMockServer("/users/" + USER_ID, Response.Status.NOT_FOUND.getStatusCode() ,"payload/error-dto-response.json");

        NotFoundResponseException exception = (NotFoundResponseException) userClient.getUser(USER_ID)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitFailure()
                .assertFailedWith(NotFoundResponseException.class)
                .getFailure();

        assertEquals(exception.getError().code(), "INVALID_USER");
        assertEquals(exception.getError().message(), "Invalid user format !");
        assertEquals(exception.getError().correlationId(), UUID.fromString("c9495a10-4c28-4e9d-a912-63f293db5c06"));
    }

    @Test
    @DisplayName("Get user by id method with bad request response with invalid error dto format")
    void testGetUserById_whenResponseIsBadRequestInvalidErrorFormat() throws IOException {
        configureMockServer("/users/" + USER_ID, Response.Status.BAD_REQUEST.getStatusCode() ,"payload/empty-response.json");

        BadRequestResponseException exception = (BadRequestResponseException) userClient.getUser(USER_ID)
                .invoke(e -> {
                    MDC.put(CorrelationId.CORRELATION_ID, UUID.randomUUID().toString());
                })
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitFailure()
                .assertFailedWith(BadRequestResponseException.class)
                .getFailure();

        assertEquals(exception.getError().code(), Response.Status.BAD_REQUEST.name());
        assertEquals(exception.getError().message(), Response.Status.BAD_REQUEST.getReasonPhrase());
        assertEquals(exception.getError().correlationId(), UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID)));
    }

    @Test
    @DisplayName("Get user by id method with bad request response with valid error dto format")
    void testGetUserById_whenResponseIsBadRequestValidErrorFormat() throws IOException {
        configureMockServer("/users/" + USER_ID, Response.Status.BAD_REQUEST.getStatusCode() ,"payload/error-dto-response.json");

        BadRequestResponseException exception = (BadRequestResponseException) userClient.getUser(USER_ID)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .awaitFailure()
                .assertFailedWith(BadRequestResponseException.class)
                .getFailure();

        assertEquals(exception.getError().code(), "INVALID_USER");
        assertEquals(exception.getError().message(), "Invalid user format !");
        assertEquals(exception.getError().correlationId(), UUID.fromString("c9495a10-4c28-4e9d-a912-63f293db5c06"));
    }

}
