package com.github.imifou.test;

import com.github.imifou.data.Authority;
import com.github.imifou.data.User;

import java.time.LocalDate;
import java.util.ArrayList;

public abstract class AbstractTest {

    protected static final Long USER_ID = 1L;

    protected User buildDefaultUser() {
        var authorities = new ArrayList<Authority>();
        authorities.add(new Authority("ADMIN"));
        authorities.add(new Authority("SIMPLE_USER"));

        return new User("Toto", 15, LocalDate.of(2002, 10, 5), authorities);
    }
}
