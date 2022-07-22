package com.github.imifou.mapper;

import com.github.imifou.entity.Authority;
import com.github.imifou.entity.User;
import com.github.imifou.proto.UserListMessage;
import com.github.imifou.proto.UserMessage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public static UserListMessage toUserListMessage(final List<User> users) {
        return UserListMessage.newBuilder()
                .addAllUsers(users.stream().map(UserMapper::toUserMessage).collect(Collectors.toList()))
                .build();
    }

    public static UserMessage toUserMessage(final User user) {
        return UserMessage.newBuilder()
                .setId(user.id)
                .setName(user.name)
                .setAge(user.age)
                .setBirthDate(user.birthDate.format(DEFAULT_DATE_TIME_FORMAT))
                .addAllAuthorities(user.authorities.stream().map(UserMapper::toAuthorityMessage).collect(Collectors.toList()))
                .build();
    }

    public static UserMessage.AuthorityMessage toAuthorityMessage(Authority authority) {
        return UserMessage.AuthorityMessage.newBuilder()
                .setId(authority.id)
                .setName(authority.name)
                .build();
    }

    public static User toUserEntity(final UserMessage user) {
        return toUserEntity(new User(), user);
    }

    public static User toUserEntity(final User userEntity, final UserMessage user) {
        userEntity.name = user.getName();
        userEntity.age = user.getAge();
        userEntity.birthDate = LocalDate.parse(user.getBirthDate(), DEFAULT_DATE_TIME_FORMAT);
        userEntity.authorities = user.getAuthoritiesList().stream().map(UserMapper::toAuthorityEntity).collect(Collectors.toList());
        return userEntity;
    }

    public static Authority toAuthorityEntity(final UserMessage.AuthorityMessage authority) {
        var authorityEntity = new Authority();
        authorityEntity.name = authority.getName();
        return authorityEntity;
    }
}
