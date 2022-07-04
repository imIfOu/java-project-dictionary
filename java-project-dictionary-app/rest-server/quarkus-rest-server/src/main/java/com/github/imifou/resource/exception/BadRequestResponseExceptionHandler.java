package com.github.imifou.resource.exception;

import com.github.imifou.data.CorrelationId;
import com.github.imifou.data.ErrorDto;
import com.github.imifou.exception.BadRequestResponseException;
import org.slf4j.MDC;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.UUID;

@Provider
public class BadRequestResponseExceptionHandler implements ExceptionMapper<BadRequestResponseException> {

    @Override
    public Response toResponse(BadRequestResponseException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorDto(
                        Response.Status.BAD_REQUEST.name(),
                        Response.Status.BAD_REQUEST.getReasonPhrase(),
                        UUID.fromString(MDC.get(CorrelationId.CORRELATION_ID))
                ))
                .build();
    }
}
