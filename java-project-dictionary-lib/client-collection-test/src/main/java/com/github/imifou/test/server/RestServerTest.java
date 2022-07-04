package com.github.imifou.test.server;


import com.github.imifou.data.Authority;
import com.github.imifou.data.CorrelationId;
import com.github.imifou.test.AbstractTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Test user rest server")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class RestServerTest extends AbstractTest {

    private String correlationId;

    public abstract Integer getPort();

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/api/v1/users";
        RestAssured.port = getPort();
        this.correlationId = UUID.randomUUID().toString();
    }

    @Test
    @Order(1)
    @DisplayName("1.0 - Get all users, when array is empty")
    void testGetAllUsers_whenResponseIsEmpty() {
        given()
                .contentType(ContentType.JSON)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .when().get()
                .then()
                .statusCode(200)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .body("$", empty());

    }

    @Test
    @Order(1)
    @DisplayName("1.0 - Get user by ID, when user is not found")
    void testGetUserById_WhenUserIsNotFound() {
        given()
                .contentType(ContentType.JSON)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .when().get("/1")
                .then()
                .statusCode(404)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .body("code", is("NOT_FOUND"))
                .body("message", is("Not Found"))
                .body("correlationId", is(correlationId));
    }

    @Test
    @Order(1)
    @DisplayName("1.0 - Delete user by ID, when user is not found")
    void testDeleteUser_WhenUserIsNotFound() {
        given()
                .contentType(ContentType.JSON)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .when().delete("/1")
                .then()
                .statusCode(400)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .body("code", is("BAD_REQUEST"))
                .body("message", is("Bad Request"))
                .body("correlationId", is(correlationId));
    }

    @Test
    @Order(1)
    @DisplayName("1.0 - Update user by ID, when user is not found ")
    void testUpdateUser_WhenUserIsNotFound() {
        var user = buildDefaultUser();

        given().body(user)
                .contentType(ContentType.JSON)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .when().put("/1")
                .then()
                .statusCode(400)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .body("code", is("BAD_REQUEST"))
                .body("message", is("Bad Request"))
                .body("correlationId", is(correlationId));
    }

    @Test
    @Order(2)
    @DisplayName("2.0 - Create user, nominal case")
    void testCreateUser_nominalCase() {
        var user = buildDefaultUser();

        given().body(user)
                .contentType(ContentType.JSON)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .when().post()
                .then()
                .statusCode(201)
                .header("location", matchesRegex("http://localhost:" + getPort() + "/api/v1/users/[0-9]+"))
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .body("name", is(user.name()))
                .body("age", is(user.age()))
                .body("birthDate", is(user.birthDate().toString()))
                .body("authorities[0].name", is(user.authorities().get(0).name()))
                .body("authorities[1].name", is(user.authorities().get(1).name()));
    }

    @Test
    @Order(3)
    @DisplayName("3.0 - Get user by ID, nominal case")
    void testGetUserById_nominalCase() {
        var user = buildDefaultUser();

        given()
                .contentType(ContentType.JSON)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .when().get("/1")
                .then()
                .statusCode(200)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .body("name", is(user.name()))
                .body("age", is(user.age()))
                .body("birthDate", is(user.birthDate().toString()))
                .body("authorities[0].name", is(user.authorities().get(0).name()))
                .body("authorities[1].name", is(user.authorities().get(1).name()));
    }

    @Test
    @Order(4)
    @DisplayName("4.0 - Get all users, nominal case")
    void testGetAllUsers_nominalCase() {
        var user = buildDefaultUser();

        given()
                .contentType(ContentType.JSON)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .when().get()
                .then()
                .statusCode(200)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .body("[0].name", is(user.name()))
                .body("[0].age", is(user.age()))
                .body("[0].birthDate", is(user.birthDate().toString()))
                .body("[0].authorities[0].name", is(user.authorities().get(0).name()))
                .body("[0].authorities[1].name", is(user.authorities().get(1).name()));
    }

    @Test
    @Order(5)
    @DisplayName("5.0 - Update user by ID, nominal case")
    void testUpdateUser_nominalCase() {
        var user = buildDefaultUser();
        user.authorities().add(new Authority(UUID.randomUUID().toString()));

        given().body(user)
                .contentType(ContentType.JSON)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .when().put("/1")
                .then()
                .statusCode(200)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .body("name", is(user.name()))
                .body("age", is(user.age()))
                .body("birthDate", is(user.birthDate().toString()))
                .body("authorities[0].name", is(user.authorities().get(0).name()))
                .body("authorities[1].name", is(user.authorities().get(1).name()))
                .body("authorities[2].name", is(user.authorities().get(2).name()));
    }

    @Test
    @Order(6)
    @DisplayName("6.0 - Delete user by ID, nominal case")
    void testDeleteUser_nominalCase() {
        given()
                .contentType(ContentType.JSON)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId)
                .when().delete("/1")
                .then()
                .statusCode(204)
                .header(CorrelationId.CORRELATION_ID_HEADER, correlationId);
    }
}
