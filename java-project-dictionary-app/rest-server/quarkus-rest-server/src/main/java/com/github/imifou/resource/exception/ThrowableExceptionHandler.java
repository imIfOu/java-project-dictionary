package com.github.imifou.resource.exception;

import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.ErrorDto;
import org.slf4j.MDC;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.UUID;

@Provider
public class ThrowableExceptionHandler implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorDto(
                        Response.Status.INTERNAL_SERVER_ERROR.name(),
                        Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID))
                ))
                .build();
    }
}