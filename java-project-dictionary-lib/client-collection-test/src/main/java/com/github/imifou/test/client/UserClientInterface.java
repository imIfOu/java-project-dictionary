package com.github.imifou.test.client;

import com.github.imifou.data.User;

import java.util.List;

public interface UserClientInterface {

    List<User> getAllUser();

    User getUser(Long id);

    User createUser(User user);

    User updateUser(Long id, User user);
}
