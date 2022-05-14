package com.github.imifou.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.imifou.client.config.CorrelationIdRequestInterceptor;
import com.github.imifou.client.config.UserClientConfig;
import com.github.imifou.client.config.UserClientErrorHandler;
import com.github.imifou.data.User;
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
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Component
public class UserClient {

    private final WebClient webClient;

    @Autowired
    public UserClient(final UserClientConfig userClientConfig, final ObjectMapper objectMapper) {
        this.webClient = buildWebClient(userClientConfig, objectMapper);
    }

    private WebClient buildWebClient(final UserClientConfig userClientConfig, final ObjectMapper objectMapper) {
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
                .filter(ExchangeFilterFunction.ofResponseProcessor(new UserClientErrorHandler()))
                .filter(new CorrelationIdRequestInterceptor());

        return builder.build();
    }

    public Flux<User> getUsers() {
        return webClient.get()
                .uri("/users")
                .retrieve()
                .bodyToFlux(User.class);
    }

    public Mono<User> getUser(final Long id) {
        return webClient.get()
                .uri("/users/" + id)
                .retrieve()
                .bodyToMono(User.class);
    }

    public Mono<User> createUser(User user) {
        return webClient.post()
                .uri("/users")
                .body(BodyInserters.fromValue(user))
                .retrieve()
                .bodyToMono(User.class);
    }

    public Mono<User> updateUser(final Long id, final User user) {
        return webClient.put()
                .uri("/users/" + id)
                .body(BodyInserters.fromValue(user))
                .retrieve()
                .bodyToMono(User.class);
    }
}
