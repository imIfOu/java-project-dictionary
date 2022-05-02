package com.github.imifou;

import com.github.imifou.client.UserClient;
import com.github.imifou.config.WireMockConfig;
import com.github.imifou.data.Authority;
import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.User;
import com.github.imifou.exception.BadRequestResponseException;
import com.github.imifou.exception.NotFoundResponseException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.nio.charset.Charset.defaultCharset;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.util.StreamUtils.copyToString;

@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { WireMockConfig.class })
@DisplayName("Test user http client")
public class UserClientTest {

    private static final Long USER_ID = 1L;

    @Autowired
    WireMockServer mockUserApi;

    @Autowired
    UserClient userClient;

    @BeforeEach
    void initTest() {
        MDC.put(CorrelationId.CORRELATION_ID, UUID.randomUUID().toString());
    }

    void configureMockServer(String path, int status, String resource) throws IOException {
        mockUserApi.stubFor(WireMock.get(WireMock.urlEqualTo(path))
                .willReturn(WireMock.aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(copyToString(
                                UserClientTest.class.getClassLoader().getResourceAsStream(resource),
                                defaultCharset()
                        ))));
    }

    @Test
    @DisplayName("Get users method with not empty array response")
    void testGetUsers_whenResponseIsNotEmpty() throws IOException {
        configureMockServer("/users", HttpStatus.OK.value() ,"payload/get-users-list-response.json");

        assertTrue(userClient.getUsers().containsAll(
                asList(
                        new User("Toto", 15, LocalDate.of(2002,10,05),List.of(
                                new Authority("ADMIN"),
                                new Authority("SIMPLE_USER"))
                        ),
                        new User("Titi", 10, LocalDate.of(2012,10,05), null)
                )));
    }

    @Test
    @DisplayName("Get users method with a empty array response")
    void testGetUsers_whenResponseIsEmpty() throws IOException {
        configureMockServer("/users", HttpStatus.OK.value() ,"payload/get-users-empty-response.json");

        assertTrue(userClient.getUsers().isEmpty());
    }

    @Test
    @DisplayName("Get user by id method with not found response with invalid error dto format")
    void testGetUserById_whenResponseIsNotFoundInvalidErrorFormat() throws IOException {
        configureMockServer("/users/" + USER_ID, HttpStatus.NOT_FOUND.value() ,"payload/empty-response.json");

        NotFoundResponseException exception = assertThrows(NotFoundResponseException.class, ()-> userClient.getUser(USER_ID));
        assertEquals(exception.getError().code(), HttpStatus.NOT_FOUND.name());
        assertEquals(exception.getError().message(), HttpStatus.NOT_FOUND.getReasonPhrase());
        assertEquals(exception.getError().correlationId(), UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID)));
    }

    @Test
    @DisplayName("Get user by id method with not found response with valid error dto format")
    void testGetUserById_whenResponseIsNotFoundValidErrorFormat() throws IOException {
        configureMockServer("/users/" + USER_ID, HttpStatus.NOT_FOUND.value() ,"payload/error-dto-response.json");

        NotFoundResponseException exception = assertThrows(NotFoundResponseException.class, ()-> userClient.getUser(USER_ID));
        assertEquals(exception.getError().code(), "INVALID_USER");
        assertEquals(exception.getError().message(), "Invalid user format !");
        assertEquals(exception.getError().correlationId(), UUID.fromString("c9495a10-4c28-4e9d-a912-63f293db5c06"));
    }

    @Test
    @DisplayName("Get user by id method with bad request response with invalid error dto format")
    void testGetUserById_whenResponseIsBadRequestInvalidErrorFormat() throws IOException {
        configureMockServer("/users/" + USER_ID, HttpStatus.BAD_REQUEST.value() ,"payload/empty-response.json");

        BadRequestResponseException exception = assertThrows(BadRequestResponseException.class, ()-> userClient.getUser(USER_ID));
        assertEquals(exception.getError().code(), HttpStatus.BAD_REQUEST.name());
        assertEquals(exception.getError().message(), HttpStatus.BAD_REQUEST.getReasonPhrase());
        assertEquals(exception.getError().correlationId(), UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID)));
    }

    @Test
    @DisplayName("Get user by id method with bad request response with valid error dto format")
    void testGetUserById_whenResponseIsBadRequestValidErrorFormat() throws IOException {
        configureMockServer("/users/" + USER_ID, HttpStatus.BAD_REQUEST.value() ,"payload/error-dto-response.json");

        BadRequestResponseException exception = assertThrows(BadRequestResponseException.class, ()-> userClient.getUser(USER_ID));
        assertEquals(exception.getError().code(), "INVALID_USER");
        assertEquals(exception.getError().message(), "Invalid user format !");
        assertEquals(exception.getError().correlationId(), UUID.fromString("c9495a10-4c28-4e9d-a912-63f293db5c06"));
    }
}