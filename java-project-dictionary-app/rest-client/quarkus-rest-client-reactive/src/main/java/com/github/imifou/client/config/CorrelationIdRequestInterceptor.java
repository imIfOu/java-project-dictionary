package com.github.imifou.client.config;

import com.github.imifou.data.CorrelationId;
import org.slf4j.MDC;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

public class CorrelationIdRequestInterceptor implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) {
        requestContext.getHeaders().add(CorrelationId.CORRELATION_ID_HEADER, MDC.get(CorrelationId.CORRELATION_ID));
    }
}
