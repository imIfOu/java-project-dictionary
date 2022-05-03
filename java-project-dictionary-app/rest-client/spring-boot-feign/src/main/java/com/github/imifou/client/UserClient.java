package com.github.imifou.client;

import com.github.imifou.data.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "user", url="${feign.client.config.user.url}")
public interface UserClient {

    
    @RequestMapping(method = RequestMethod.GET, value = "/users")
    List<User> getUsers();

    @RequestMapping(method = RequestMethod.GET, value = "/users/{userId}")
    User getUser(@PathVariable("userId") Long id);

    @RequestMapping(method = RequestMethod.POST, value = "/users", consumes = "application/json")
    User createUser(User user);

    @RequestMapping(method = RequestMethod.PUT, value = "/users/{userId}", consumes = "application/json")
    User updateUser(@PathVariable("userId") Long id, User user);
}
