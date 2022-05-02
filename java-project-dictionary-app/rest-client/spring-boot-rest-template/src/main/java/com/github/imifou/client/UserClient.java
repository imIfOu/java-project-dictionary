package com.github.imifou.client;

import com.github.imifou.client.config.UserClientErrorHandler;
import com.github.imifou.client.config.CorrelationIdRequestInterceptor;
import com.github.imifou.client.config.UserClientConfig;
import com.github.imifou.data.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class UserClient {

    private final RestOperations restClient;

    @Autowired
    public UserClient(final UserClientConfig userClientConfig) {
        this.restClient = buildRestTemplate(userClientConfig);
    }

    private RestOperations buildRestTemplate(final UserClientConfig userClientConfig){
        var restTemplateBuilder = new RestTemplateBuilder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .setConnectTimeout(Duration.ofMillis(userClientConfig.connectionTimeout()))
                .setReadTimeout(Duration.ofMillis(userClientConfig.readTimeout()))
                .rootUri(userClientConfig.url())
                .additionalInterceptors(new CorrelationIdRequestInterceptor())
                .errorHandler(new UserClientErrorHandler());
        return restTemplateBuilder.build();
    }

    public List<User> getUsers() {
        return restClient.exchange("/users", HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {})
                .getBody();
    }

    public User getUser(final Long id) {
        return restClient.exchange("/users/" + id, HttpMethod.GET, null, User.class)
                .getBody();
    }

    public User createUser(User user) {
        HttpEntity<User> entity = new HttpEntity<>(user);
        return restClient.exchange("/users", HttpMethod.POST, entity, User.class)
                .getBody();
    }

    public User updateUser(final Long id, final User user) {
        HttpEntity<User> entity = new HttpEntity<>(user);
        return restClient.exchange("/users/" + id, HttpMethod.PUT, entity, User.class)
                .getBody();
    }
}
