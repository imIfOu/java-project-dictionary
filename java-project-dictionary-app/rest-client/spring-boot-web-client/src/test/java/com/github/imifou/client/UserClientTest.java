package com.github.imifou.client;

import com.github.imifou.client.config.WireMockConfig;
import com.github.imifou.data.User;
import com.github.imifou.test.client.RestClientTest;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@SpringBootTest
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = {WireMockConfig.class})
@DisplayName("Test user http client")
public class UserClientTest extends RestClientTest {

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    UserClient userClient;
    
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
}