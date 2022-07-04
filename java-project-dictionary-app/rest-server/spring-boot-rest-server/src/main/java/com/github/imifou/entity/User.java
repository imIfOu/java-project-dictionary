package com.github.imifou.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    public Long id;
    public String name;
    public Integer age;
    public LocalDate birthDate;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<Authority> authorities = new ArrayList<>();
}

