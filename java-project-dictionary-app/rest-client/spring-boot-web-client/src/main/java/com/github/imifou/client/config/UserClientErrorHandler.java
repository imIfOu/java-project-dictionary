package com.github.imifou.client.config;

import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.ErrorDto;
import com.github.imifou.exception.BadRequestResponseException;
import com.github.imifou.exception.ClientResponseException;
import com.github.imifou.exception.NotFoundResponseException;
import org.slf4j.MDC;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

public class UserClientErrorHandler implements Function<ClientResponse, Mono<ClientResponse>> {

    @Override
    public Mono<ClientResponse> apply(ClientResponse clientResponse) {
        var httpStatus = clientResponse.statusCode();
        var correlationId = UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID));

        if(httpStatus.isError()) {
           return clientResponse.bodyToMono(ErrorDto.class)
                    .onErrorReturn(new ErrorDto(httpStatus.name(), httpStatus.getReasonPhrase(), correlationId))
                    .defaultIfEmpty(new ErrorDto(httpStatus.name(), httpStatus.getReasonPhrase(), correlationId))
                    .flatMap( error -> switch (httpStatus.value()){
                            case 400 ->  Mono.error(new BadRequestResponseException(error));
                            case 404 ->  Mono.error(new NotFoundResponseException(error));
                            default ->  Mono.error(new ClientResponseException(error));
                        }
                    );
        }

        return Mono.just(clientResponse);
    }
}