package com.github.imifou.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Authority {
    @Id
    @GeneratedValue
    public Long id;
    public String name;
}
