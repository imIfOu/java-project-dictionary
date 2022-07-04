package com.github.imifou.filter;

import com.github.imifou.data.CorrelationId;
import org.jboss.logging.MDC;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.util.Objects;
import java.util.UUID;

@Provider
public class CorrelationIdRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        var requestCorrelationId = requestContext.getHeaders().get(CorrelationId.CORRELATION_ID_HEADER);
        if (Objects.isNull(requestCorrelationId) || requestCorrelationId.isEmpty()) {
            MDC.put(CorrelationId.CORRELATION_ID, UUID.randomUUID());
        } else {
            MDC.put(CorrelationId.CORRELATION_ID, requestCorrelationId.get(0));
        }
    }
}
