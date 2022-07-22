package com.github.imifou.interceptor;

import com.github.imifou.data.CorrelationId;
import io.grpc.*;
import org.jboss.logging.MDC;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class CorrelationIdInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        var requestCorrelationId = metadata.get(Metadata.Key.of(CorrelationId.CORRELATION_ID_HEADER, Metadata.ASCII_STRING_MARSHALLER));
        if (Objects.isNull(requestCorrelationId) || requestCorrelationId.isEmpty()) {
            MDC.put(CorrelationId.CORRELATION_ID, UUID.randomUUID());
        } else {
            MDC.put(CorrelationId.CORRELATION_ID, requestCorrelationId);
        }

        var newServerCall = new ForwardingServerCall.SimpleForwardingServerCall<>(serverCall) {
            @Override
            public void sendHeaders(Metadata headers) {
                headers.put(Metadata.Key.of(CorrelationId.CORRELATION_ID_HEADER, Metadata.ASCII_STRING_MARSHALLER), MDC.get(CorrelationId.CORRELATION_ID).toString());
                super.sendHeaders(headers);
            }
        };

        return serverCallHandler.startCall(newServerCall, metadata);
    }
}
