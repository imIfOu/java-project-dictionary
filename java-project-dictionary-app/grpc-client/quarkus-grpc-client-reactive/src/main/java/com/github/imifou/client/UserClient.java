package com.github.imifou.client;

import com.github.imifou.client.interceptor.CorrelationIdInterceptor;
import com.github.imifou.client.mapper.UserMapper;
import com.github.imifou.data.User;
import com.github.imifou.proto.IdRequest;
import com.github.imifou.proto.UserGrpc;
import com.google.protobuf.Empty;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.grpc.RegisterClientInterceptor;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class UserClient {

    @GrpcClient
    @RegisterClientInterceptor(CorrelationIdInterceptor.class)
    UserGrpc userGrpc;

    public Uni<List<User>> listAll(Empty request) {
        return userGrpc.listAll(Empty.getDefaultInstance())
                .map(UserMapper::toUserList);
    }

    public Uni<User> listById(Long id) {
        return userGrpc.listById(IdRequest.newBuilder().setId(id).build())
                .map(UserMapper::toUser);
    }

    public Uni<User> create(User user) {
        return userGrpc.create(UserMapper.toUserMessage(user))
                .map(UserMapper::toUser);
    }

    public Uni<User> updateById(Long id, User user) {
        return userGrpc.updateById(UserMapper.toUserMessage(user, id))
                .map(UserMapper::toUser);
    }

    public Uni<Boolean> deleteById(Long id) {
        return userGrpc.deleteById(IdRequest.newBuilder().setId(id).build())
                .map((empty) -> Boolean.TRUE);
    }

}
