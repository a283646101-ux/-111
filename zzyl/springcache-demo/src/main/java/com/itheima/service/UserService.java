package com.itheima.service;

import com.itheima.entity.User;
import com.itheima.entity.UserDto;
import com.itheima.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sjqn
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User insert(User user){
        userMapper.insert(user);
        return user;
    }

    public User getById(Long id){
        User user = userMapper.getById(id);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    public void deleteById(Long id){
        userMapper.deleteById(id);
    }

    public void deleteAll(){
        userMapper.deleteAll();
    }

    public List<User> getList(UserDto userDto){
        List<User> list = userMapper.getList("%" + userDto.getName() + "%", userDto.getAge());
        return list;
    }

}
