package com.github.imifou.client.mapper;

import com.github.imifou.data.Authority;
import com.github.imifou.data.User;
import com.github.imifou.proto.UserListMessage;
import com.github.imifou.proto.UserMessage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static UserMessage toUserMessage(final User user) {
        return UserMessage.newBuilder()
                .setName(user.name())
                .setAge(user.age())
                .setBirthDate(user.birthDate().format(DEFAULT_DATE_TIME_FORMAT))
                .addAllAuthorities(user.authorities().stream().map(UserMapper::toAuthorityMessage).collect(Collectors.toList()))
                .build();
    }

    public static UserMessage toUserMessage(final User user, final Long id) {
        return UserMessage.newBuilder()
                .setId(id)
                .setName(user.name())
                .setAge(user.age())
                .setBirthDate(user.birthDate().format(DEFAULT_DATE_TIME_FORMAT))
                .addAllAuthorities(user.authorities().stream().map(UserMapper::toAuthorityMessage).collect(Collectors.toList()))
                .build();
    }

    public static UserMessage.AuthorityMessage toAuthorityMessage(Authority authority) {
        return UserMessage.AuthorityMessage.newBuilder()
                .setName(authority.name())
                .build();
    }

    public static List<User> toUserList(final UserListMessage users) {
        return users.getUsersList().stream()
                .map(UserMapper::toUser)
                .collect(Collectors.toList());
    }

    public static User toUser(final UserMessage user) {
        var name = user.getName();
        var age = user.getAge();
        var birthDate = LocalDate.parse(user.getBirthDate(), DEFAULT_DATE_TIME_FORMAT);
        var authorities = user.getAuthoritiesList().stream().map(UserMapper::toAuthority).collect(Collectors.toList());
        return new User(name, age, birthDate, authorities);
    }

    public static Authority toAuthority(final UserMessage.AuthorityMessage authority) {
        return new Authority(authority.getName());
    }
}
