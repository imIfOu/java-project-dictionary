package com.github.imifou.client.config;

import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.ErrorDto;
import com.github.imifou.exception.BadRequestResponseException;
import com.github.imifou.exception.ClientResponseException;
import com.github.imifou.exception.NotFoundResponseException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.slf4j.MDC;

import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class UserClientErrorHandler implements ResponseExceptionMapper<ClientResponseException> {

    @Override
    public ClientResponseException toThrowable(Response response) {
        var httpStatus = response.getStatusInfo().toEnum();
        var correlationId = UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID));

        var error = getErrorDto(response)
                .orElse(new ErrorDto(httpStatus.name(), httpStatus.getReasonPhrase(), correlationId));

        return switch (response.getStatus()) {
            case 400 -> new BadRequestResponseException(error);
            case 404 -> new NotFoundResponseException(error);
            default -> new ClientResponseException(error);
        };
    }

    private Optional<ErrorDto> getErrorDto(Response response) {
        try {
            return Optional.of(response.readEntity(ErrorDto.class));
        } catch (Throwable throwable) {
            log.debug("Invalid error response format !");
        }
        return Optional.empty();
    }
}
