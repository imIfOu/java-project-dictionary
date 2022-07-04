package com.github.imifou.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class Authority extends PanacheEntity {
    public String name;
}
