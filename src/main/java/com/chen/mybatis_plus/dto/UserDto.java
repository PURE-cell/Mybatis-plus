package com.chen.mybatis_plus.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserDto {
    private String name;
    private int age;
    private String email;
    private String address;

}
