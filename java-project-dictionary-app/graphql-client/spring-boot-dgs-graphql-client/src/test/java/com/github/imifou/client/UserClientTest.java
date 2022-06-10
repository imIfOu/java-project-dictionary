package com.github.imifou.client;

import com.github.imifou.MockServer;
import com.github.imifou.client.config.WireMockConfig;
import com.github.imifou.client.exception.GraphQLClientException;
import com.github.imifou.data.Authority;
import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.User;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = {WireMockConfig.class})
@DisplayName("Test user graphql client")
public class UserClientTest {

    private static final Long USER_ID = 1L;

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
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
        MockServer.mockGraphqlServer(wireMockServer, "1.0");

        assertTrue(userClient.getUsers().collectList().block().containsAll(
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
        MockServer.mockGraphqlServer(wireMockServer, "1.1");

        assertTrue(userClient.getUsers().collectList().block().isEmpty());
    }

    @Test
    @DisplayName("2.0 - Get user by id method, nominal case")
    void testGetUserById_nominalCase() {
        MockServer.mockGraphqlServer(wireMockServer, "2.0");

        var user = userClient.getUser(USER_ID).block();
        assertEquals("Toto", user.name());
        assertEquals(15, user.age());
        assertEquals(LocalDate.of(2002, 10, 5), user.birthDate());
        assertEquals(List.of(new Authority("ADMIN"), new Authority("SIMPLE_USER")), user.authorities());
    }

    @Test
    @DisplayName("2.1 - Get user by id method with error with location path")
    void testGetUserById_whenResponseIsErrorWithLocationData() {
        MockServer.mockGraphqlServer(wireMockServer, "2.1");

        GraphQLClientException exception = assertThrows(GraphQLClientException.class, () -> userClient.getUser(USER_ID).block());
        var error = exception.getErrors().get(0);
        assertEquals(error.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    @Test
    @DisplayName("2.2 - Get user by id method with error with no location path")
    void testGetUserById_whenResponseIsErrorWithoutLocationData() {
        MockServer.mockGraphqlServer(wireMockServer, "2.2");

        GraphQLClientException exception = assertThrows(GraphQLClientException.class, () -> userClient.getUser(USER_ID).block());
        var error = exception.getErrors().get(0);
        assertEquals(error.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    @Disabled("Dgs client uses its own instance of object mapper without Java 8 date/time module jsr310")
    @Test
    @DisplayName("3.0 - Create user, nominal case")
    void testCreateUser_nominalCase() {
        MockServer.mockGraphqlServer(wireMockServer, "3.0");

        var user = buildDefaultUser();
        var createUser = userClient.createUser(user).block();
        assertEquals(user, createUser);
    }

    @Disabled("Dgs client uses its own instance of object mapper without Java 8 date/time module jsr310")
    @Test
    @DisplayName("3.1 - Create user method with error with location path")
    void testCreateUser_whenResponseIsErrorWithLocationData() {
        MockServer.mockGraphqlServer(wireMockServer, "3.1");

        var user = buildDefaultUser();
        GraphQLClientException exception = assertThrows(GraphQLClientException.class, () -> userClient.createUser(user).block());
        var error = exception.getErrors().get(0);
        assertEquals(error.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    @Disabled("Dgs client uses its own instance of object mapper without Java 8 date/time module jsr310")
    @Test
    @DisplayName("3.2 - Create user method with error with no location path")
    void testCreateUser_whenResponseIsErrorWithoutLocationData() {
        MockServer.mockGraphqlServer(wireMockServer, "3.2");

        var user = buildDefaultUser();
        GraphQLClientException exception = assertThrows(GraphQLClientException.class, () -> userClient.createUser(user).block());
        var error = exception.getErrors().get(0);
        assertEquals(error.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    @Disabled("Dgs client uses its own instance of object mapper without Java 8 date/time module jsr310")
    @Test
    @DisplayName("4.0 - Update user, nominal case")
    void testUpdateUser_nominalCase() {
        MockServer.mockGraphqlServer(wireMockServer, "4.0");

        var user = buildDefaultUser();
        var updateUser = userClient.updateUser(USER_ID, user).block();
        assertEquals(user, updateUser);
    }

    @Disabled("Dgs client uses its own instance of object mapper without Java 8 date/time module jsr310")
    @Test
    @DisplayName("4.1 - Update user by id method with error with location path")
    void testUpdateUser_whenResponseIsErrorWithLocationData() {
        MockServer.mockGraphqlServer(wireMockServer, "4.1");

        var user = buildDefaultUser();
        GraphQLClientException exception = assertThrows(GraphQLClientException.class, () -> userClient.updateUser(USER_ID, user).block());
        var error = exception.getErrors().get(0);
        assertEquals(error.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    @Disabled("Dgs client uses its own instance of object mapper without Java 8 date/time module jsr310")
    @Test
    @DisplayName("4.2 - Update user by id method with error with no location path")
    void testUpdateUser_whenResponseIsErrorWithoutLocationData() {
        MockServer.mockGraphqlServer(wireMockServer, "4.2");

        var user = buildDefaultUser();
        GraphQLClientException exception = assertThrows(GraphQLClientException.class, () -> userClient.updateUser(USER_ID, user).block());
        var error = exception.getErrors().get(0);
        assertEquals(error.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase());
    }
}

