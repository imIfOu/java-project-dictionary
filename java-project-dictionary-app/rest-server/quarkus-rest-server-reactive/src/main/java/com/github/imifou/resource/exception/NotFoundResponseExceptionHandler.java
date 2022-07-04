package com.github.imifou.resource.exception;

import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.ErrorDto;
import com.github.imifou.exception.NotFoundResponseException;
import org.slf4j.MDC;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.UUID;

@Provider
public class NotFoundResponseExceptionHandler implements ExceptionMapper<NotFoundResponseException> {

    @Override
    public Response toResponse(NotFoundResponseException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorDto(
                        Response.Status.NOT_FOUND.name(),
                        Response.Status.NOT_FOUND.getReasonPhrase(),
                        UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID))
                ))
                .build();
    }
}
