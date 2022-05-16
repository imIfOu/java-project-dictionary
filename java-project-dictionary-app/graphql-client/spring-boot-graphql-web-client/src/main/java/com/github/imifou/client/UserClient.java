package com.github.imifou.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.imifou.client.config.CorrelationIdRequestInterceptor;
import com.github.imifou.client.config.UserClientConfig;
import com.github.imifou.data.User;
import graphql.kickstart.spring.webclient.boot.GraphQLWebClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Map;

@Slf4j
@Component
public class UserClient {

    private final GraphQLWebClient graphQLWebClient;

    @Autowired
    public UserClient(final UserClientConfig userClientConfig, final ObjectMapper objectMapper) {
        this.graphQLWebClient = buildGraphQLWebClient(userClientConfig, objectMapper);
    }

    private GraphQLWebClient buildGraphQLWebClient(final UserClientConfig userClientConfig, final ObjectMapper objectMapper) {
        var httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) userClientConfig.connectionTimeout())
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler((int) userClientConfig.readTimeout())));

        var strategies = ExchangeStrategies.builder()
                .codecs(clientDefaultCodecsConfigurer -> {
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                }).build();

        var builder = WebClient.builder()
                .exchangeStrategies(strategies)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(userClientConfig.url())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(new CorrelationIdRequestInterceptor());

        return GraphQLWebClient.newInstance(builder.build(), objectMapper);
    }

    public Flux<User> getUsers() {
        return graphQLWebClient.flux("graphql-queries/get-users.graphql", Map.of(), User.class);
    }

    public Mono<User> getUser(final Long id) {
        return graphQLWebClient.post("graphql-queries/get-user.graphql", Map.of("id", id), User.class);
    }

    public Mono<User> createUser(User user) {
        return graphQLWebClient.post("graphql-queries/create-user.graphql", Map.of("user", user), User.class);
    }

    public Mono<User> updateUser(final Long id, final User user) {
        return graphQLWebClient.post("graphql-queries/update-user.graphql", Map.of("id", id, "user", user), User.class);
    }
}
