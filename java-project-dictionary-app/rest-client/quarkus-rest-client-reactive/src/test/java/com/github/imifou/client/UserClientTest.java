package com.github.imifou.client;

import com.github.imifou.client.config.InjectWireMock;
import com.github.imifou.client.config.WireMockConfig;
import com.github.imifou.data.User;
import com.github.imifou.test.client.RestClientTest;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Disabled;

import javax.inject.Inject;
import java.util.List;

@Disabled("Regression on MDC management on quarkus 2.10.*")
@QuarkusTest
@QuarkusTestResource(WireMockConfig.class)
public class UserClientTest extends RestClientTest {

    @InjectWireMock
    WireMockServer wireMockServer;

    @Inject
    @RestClient
    UserClient userClient;

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
}
