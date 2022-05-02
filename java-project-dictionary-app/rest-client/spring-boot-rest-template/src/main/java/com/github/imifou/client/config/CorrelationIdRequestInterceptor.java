package com.github.imifou.client.config;

import com.github.imifou.data.CorrelationId;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class CorrelationIdRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().set(CorrelationId.CORRELATION_ID_HEADER, MDC.get(CorrelationId.CORRELATION_ID));
        return execution.execute(request,body);
    }
}
