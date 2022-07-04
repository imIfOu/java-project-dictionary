package com.github.imifou.filter;

import com.github.imifou.data.CorrelationId;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Order(1)
@Component
@NoArgsConstructor
public class CorrelationIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        var httpReq = (HttpServletRequest) servletRequest;
        var httpResp = (HttpServletResponse) servletResponse;

        var requestCorrelationId = httpReq.getHeader(CorrelationId.CORRELATION_ID_HEADER);
        if (Objects.isNull(requestCorrelationId)) {
            requestCorrelationId = UUID.randomUUID().toString();
        }

        MDC.put(CorrelationId.CORRELATION_ID, requestCorrelationId);
        httpResp.setHeader(CorrelationId.CORRELATION_ID_HEADER, requestCorrelationId);

        filterChain.doFilter(httpReq, httpResp);
    }
}
