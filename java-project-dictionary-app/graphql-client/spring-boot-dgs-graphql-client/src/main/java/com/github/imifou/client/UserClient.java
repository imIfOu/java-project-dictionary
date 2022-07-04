package com.github.imifou.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.imifou.client.config.CorrelationIdRequestInterceptor;
import com.github.imifou.client.config.UserClientConfig;
import com.github.imifou.client.exception.GraphQLClientException;
import com.github.imifou.data.User;
import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.client.GraphQLResponse;
import com.netflix.graphql.dgs.client.MonoGraphQLClient;
import com.netflix.graphql.dgs.client.WebClientGraphQLClient;
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

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UserClient {

    private final WebClientGraphQLClient graphQLClient;
    private static final TypeRef<List<User>> LIST_USER_TYPE_REF = new TypeRef<List<User>>() {
    };
    private static final TypeRef<User> USER_TYPE_REF = new TypeRef<User>() {
    };

    @Autowired
    public UserClient(final UserClientConfig userClientConfig, final ObjectMapper objectMapper) {
        this.graphQLClient = buildGraphQLClient(userClientConfig, objectMapper);
    }

    private WebClientGraphQLClient buildGraphQLClient(final UserClientConfig userClientConfig, final ObjectMapper objectMapper) {
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

        return MonoGraphQLClient.createWithWebClient(builder.build());
    }


    private <T> T extractData(final GraphQLResponse graphQLResponse, final String path, final TypeRef<T> typeRef) {
        if (graphQLResponse.hasErrors()) {
            throw new GraphQLClientException(graphQLResponse.getErrors());
        }
        return graphQLResponse.extractValueAsObject(path, typeRef);
    }

    public Flux<User> getUsers() {
        return graphQLClient.reactiveExecuteQuery("query users { users {name age birthDate authorities {name}} }")
                .map(r -> extractData(r, "users", LIST_USER_TYPE_REF))
                .flatMapIterable(user -> user);
    }

    public Mono<User> getUser(final Long id) {
        return graphQLClient.reactiveExecuteQuery("query user($id: BigInteger) { user {name age birthDate authorities {name}} }", Map.of("id", id))
                .map(r -> extractData(r, "user", USER_TYPE_REF));
    }

    public Mono<User> createUser(User user) {
        return graphQLClient.reactiveExecuteQuery("mutation createUser($user: UserInput) { createUser(user: $user) {name age birthDate authorities {name}} }", Map.of("user", user))
                .map(r -> extractData(r, "user", USER_TYPE_REF));
    }

    public Mono<User> updateUser(final Long id, final User user) {
        return graphQLClient.reactiveExecuteQuery("mutation updateUser($id: BigInteger, $user: UserInput) { updateUser(user: $user) {name age birthDate authorities {name}} }", Map.of("id", id, "user", user))
                .map(r -> extractData(r, "user", USER_TYPE_REF));
    }
}
