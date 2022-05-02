package com.github.imifou.data;

import java.time.LocalDate;
import java.util.List;

public record User(String name, Integer age, LocalDate birthDate, List<Authority> authorities) {

    public User {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Invalid Name, cannot be blank");
        }

        if (age < 0) {
            throw new IllegalArgumentException("Invalid Age, cannot be negative");
        }

        if (birthDate == null) {
            throw new IllegalArgumentException("Invalid birthDate, cannot be blank");
        }
    }
}
