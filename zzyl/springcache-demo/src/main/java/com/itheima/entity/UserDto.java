package com.itheima.entity;

import lombok.Data;

import java.util.Objects;

/**
 * @author sjqn
 */
@Data
public class UserDto {

    private String name;
    private int age;

    @Override
    public int hashCode() {
        int result = Objects.hash(getName(),getAge());
        return result;
    }
}
