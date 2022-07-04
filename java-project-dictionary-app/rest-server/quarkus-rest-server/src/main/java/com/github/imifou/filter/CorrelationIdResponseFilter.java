package com.github.imifou.filter;

import com.github.imifou.data.CorrelationId;
import org.slf4j.MDC;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CorrelationIdResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        responseContext.getHeaders().putSingle(CorrelationId.CORRELATION_ID_HEADER, MDC.get(CorrelationId.CORRELATION_ID));
    }
}
