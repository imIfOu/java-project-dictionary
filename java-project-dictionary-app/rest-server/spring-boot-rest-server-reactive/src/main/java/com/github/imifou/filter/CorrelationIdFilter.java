package com.github.imifou.filter;

import com.github.imifou.data.CorrelationId;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Objects;
import java.util.UUID;

@Order(1)
@Component
@NoArgsConstructor
public class CorrelationIdFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String correlationId;
        var requestCorrelationIdList = exchange.getRequest().getHeaders().get(CorrelationId.CORRELATION_ID_HEADER);
        if (Objects.isNull(requestCorrelationIdList) || requestCorrelationIdList.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        } else {
            correlationId = requestCorrelationIdList.get(0);
        }

        MDC.put(CorrelationId.CORRELATION_ID, correlationId);
        exchange.getResponse().getHeaders().add(CorrelationId.CORRELATION_ID_HEADER, correlationId);

        return chain.filter(exchange)
                .contextWrite(Context.of(CorrelationId.CORRELATION_ID, correlationId));
    }
}
