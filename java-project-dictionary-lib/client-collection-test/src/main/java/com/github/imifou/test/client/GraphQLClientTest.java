package com.github.imifou.test.client;

import com.github.imifou.MockServer;
import com.github.imifou.data.Authority;
import com.github.imifou.data.User;
import com.github.imifou.test.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public abstract class GraphQLClientTest<E extends Throwable> extends AbstractTest implements UserClientInterface, WireMockInterface {

    protected abstract Class<E> getGraphQLResponseException();

    protected abstract Function<E, String> getMethodErrorCode();

    protected abstract Function<E, String> getMethodErrorMessage();


    @Test
    @DisplayName("1.0 - Get users method, nominal case")
    protected void testGetUsers_nominalCase() {
        MockServer.mockGraphqlServer(getWireMockServer(), "1.0");

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
        MockServer.mockGraphqlServer(getWireMockServer(), "1.1");

        assertTrue(getAllUser().isEmpty());
    }

    @Test
    @DisplayName("2.0 - Get user by id method, nominal case")
    protected void testGetUserById_nominalCase() {
        MockServer.mockGraphqlServer(getWireMockServer(), "2.0");

        var user = getUser(USER_ID);
        assertEquals("Toto", user.name());
        assertEquals(15, user.age());
        assertEquals(LocalDate.of(2002, 10, 5), user.birthDate());
        assertEquals(List.of(new Authority("ADMIN"), new Authority("SIMPLE_USER")), user.authorities());
    }


    @Test
    @DisplayName("2.1 - Get user by id method with error")
    protected void testGetUserById_whenResponseIsErrorWithLocationData() {
        MockServer.mockGraphqlServer(getWireMockServer(), "2.1");

        E exception = assertThrows(getGraphQLResponseException(), () -> getUser(USER_ID));
        assertEquals(getMethodErrorCode().apply(exception), "NOT_FOUND");
        assertEquals(getMethodErrorMessage().apply(exception), "Not Found");
    }

    @Test
    @DisplayName("3.0 - Create user, nominal case")
    protected void testCreateUser_nominalCase() {
        MockServer.mockGraphqlServer(getWireMockServer(), "3.0");

        var user = buildDefaultUser();
        var createUser = createUser(user);
        assertEquals(user, createUser);
    }

    @Test
    @DisplayName("3.1 - Create user method with error")
    protected void testCreateUser_whenResponseIsErrorWithLocationData() {
        MockServer.mockGraphqlServer(getWireMockServer(), "3.1");

        E exception = assertThrows(getGraphQLResponseException(), () -> createUser(buildDefaultUser()));
        assertEquals(getMethodErrorCode().apply(exception), "NOT_FOUND");
        assertEquals(getMethodErrorMessage().apply(exception), "Not Found");
    }

    @Test
    @DisplayName("4.0 - Update user, nominal case")
    protected void testUpdateUser_nominalCase() {
        MockServer.mockGraphqlServer(getWireMockServer(), "4.0");

        var user = buildDefaultUser();
        var updateUser = updateUser(USER_ID, user);
        assertEquals(user, updateUser);
    }


    @Test
    @DisplayName("4.1 - Update user by id method with error")
    protected void testUpdateUser_whenResponseIsErrorWithLocationData() {
        MockServer.mockGraphqlServer(getWireMockServer(), "4.1");

        E exception = assertThrows(getGraphQLResponseException(), () -> updateUser(USER_ID, buildDefaultUser()));
        assertEquals(getMethodErrorCode().apply(exception), "NOT_FOUND");
        assertEquals(getMethodErrorMessage().apply(exception), "Not Found");
    }
}
