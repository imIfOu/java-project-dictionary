package com.github.imifou.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "users")
public class User {
    @Id
    public Long id;
    public String name;
    public Integer age;
    public LocalDate birthDate;
    @Transient
    public List<Authority> authorities = new ArrayList<>();
}

