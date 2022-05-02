package com.github.imifou.client.config;

import com.github.imifou.data.CorrelationId;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class CorrelationIdRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(CorrelationId.CORRELATION_ID_HEADER, MDC.get(CorrelationId.CORRELATION_ID));
    }
}
