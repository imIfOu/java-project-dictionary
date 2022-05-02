package com.github.imifou.client.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.ErrorDto;
import com.github.imifou.exception.BadRequestResponseException;
import com.github.imifou.exception.ClientResponseException;
import com.github.imifou.exception.NotFoundResponseException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

@Slf4j
public class UserClientErrorHandler implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    public UserClientErrorHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        var httpStatus = HttpStatus.valueOf(response.status());
        var correlationId = UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID));

        var error = getErrorDto(response.body())
                .orElse(new ErrorDto(httpStatus.name(), httpStatus.getReasonPhrase(), correlationId));

        return switch (response.status()){
            case 400 -> new BadRequestResponseException(error);
            case 404 -> new NotFoundResponseException(error);
            default -> new ClientResponseException(error);
        };
    }

    private Optional<ErrorDto> getErrorDto(Response.Body responseBody) {
        if(responseBody != null) {
            try {
                return Optional.of(objectMapper.readValue(responseBody.asInputStream(), ErrorDto.class));
            } catch (Throwable throwable) {
                log.debug("Invalid error response format !");
            }
        }
        return Optional.empty();
    }
}