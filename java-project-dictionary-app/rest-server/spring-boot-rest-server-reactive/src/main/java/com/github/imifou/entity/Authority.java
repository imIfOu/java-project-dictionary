package com.github.imifou.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;


@Data
public class Authority {
    @Id
    public Long id;
    public String name;
    @JsonIgnore
    public Long usersId;
}
