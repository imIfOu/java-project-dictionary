package com.github.imifou.client.interceptor;

import com.github.imifou.data.CorrelationId;
import io.grpc.*;
import org.jboss.logging.MDC;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class CorrelationIdInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {

        var call = channel.newCall(methodDescriptor, callOptions);

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(call) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata metadata) {

                var requestCorrelationId = metadata.get(Metadata.Key.of(CorrelationId.CORRELATION_ID_HEADER, Metadata.ASCII_STRING_MARSHALLER));
                if (Objects.isNull(requestCorrelationId) || requestCorrelationId.isEmpty()) {
                    MDC.put(CorrelationId.CORRELATION_ID, UUID.randomUUID());
                } else {
                    MDC.put(CorrelationId.CORRELATION_ID, requestCorrelationId);
                }

                super.start(responseListener, metadata);
            }
        };

    }
}
