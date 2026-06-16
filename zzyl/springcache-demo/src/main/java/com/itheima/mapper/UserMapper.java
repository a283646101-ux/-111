package com.itheima.mapper;

import com.itheima.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper{

    @Insert("insert into user(name,age) values (#{name},#{age})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(User user);

    @Delete("delete from user where id = #{id}")
    void deleteById(Long id);

    @Delete("delete from user")
    void deleteAll();

    @Select("select * from user where id = #{id}")
    User getById(Long id);

    @Select("select * from user where name like #{name} and age >= #{age}")
    List<User> getList(@Param("name") String name,@Param("age") int age);
}
