package com.github.imifou.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.ErrorDto;
import com.github.imifou.exception.BadRequestResponseException;
import com.github.imifou.exception.ClientResponseException;
import com.github.imifou.exception.NotFoundResponseException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class UserClientErrorHandler implements ResponseErrorHandler {

    private final DefaultResponseErrorHandler defaultResponseErrorHandler;
    private final ObjectMapper objectMapper;

    public UserClientErrorHandler(){
        this.objectMapper = new ObjectMapper();
        this.defaultResponseErrorHandler = new DefaultResponseErrorHandler();
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return defaultResponseErrorHandler.hasError(response);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        var httpStatus = response.getStatusCode();
        var correlationId = UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID));

        var error = getErrorDto(response.getBody())
                .orElse(new ErrorDto(httpStatus.name(), httpStatus.getReasonPhrase(), correlationId));

        throw switch (httpStatus.value()){
            case 400 -> new BadRequestResponseException(error);
            case 404 -> new NotFoundResponseException(error);
            default -> new ClientResponseException(error);
        };
    }

    private Optional<ErrorDto> getErrorDto(InputStream responseBody) {
        if(responseBody != null) {
            try {
                return Optional.of(objectMapper.readValue(responseBody, ErrorDto.class));
            } catch (Throwable throwable) {
                log.debug("Invalid error response format !");
            }
        }
        return Optional.empty();
    }
}
