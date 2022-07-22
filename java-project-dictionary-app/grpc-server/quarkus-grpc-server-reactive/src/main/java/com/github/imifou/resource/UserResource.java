package com.github.imifou.resource;

import com.github.imifou.entity.User;
import com.github.imifou.interceptor.CorrelationIdInterceptor;
import com.github.imifou.mapper.UserMapper;
import com.github.imifou.proto.IdRequest;
import com.github.imifou.proto.UserGrpc;
import com.github.imifou.proto.UserListMessage;
import com.github.imifou.proto.UserMessage;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.GrpcService;
import io.quarkus.grpc.RegisterInterceptor;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;

@GrpcService
@RegisterInterceptor(CorrelationIdInterceptor.class)
public class UserResource implements UserGrpc {

    @Override
    public Uni<UserListMessage> listAll(Empty request) {
        return User.<User>listAll()
                .map(UserMapper::toUserListMessage);
    }

    @Override
    public Uni<UserMessage> listById(IdRequest request) {
        return User.<User>findById(request.getId())
                .onItem().ifNull().failWith(new StatusRuntimeException(Status.NOT_FOUND))
                .map(UserMapper::toUserMessage);
    }

    @Override
    @ReactiveTransactional
    public Uni<UserMessage> create(UserMessage request) {
        return Uni.createFrom().item(request)
                .map(UserMapper::toUserEntity)
                .flatMap(user -> user.<User>persist())
                .map(UserMapper::toUserMessage);
    }

    @Override
    @ReactiveTransactional
    public Uni<UserMessage> updateById(UserMessage request) {
        return User.<User>findById(request.getId())
                .onItem().ifNull().failWith(new StatusRuntimeException(Status.INVALID_ARGUMENT))
                .invoke(userToUpdate -> UserMapper.toUserEntity(userToUpdate, request).persistAndFlush())
                .map(UserMapper::toUserMessage);
    }

    @Override
    @ReactiveTransactional
    public Uni<Empty> deleteById(IdRequest request) {
        return User.deleteById(request.getId())
                .map(isDeleted -> {
                    if (!isDeleted) throw new StatusRuntimeException(Status.INVALID_ARGUMENT);
                    return Empty.newBuilder().build();
                });
    }
}
