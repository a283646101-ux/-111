package com.itheima.controller;

import com.itheima.entity.User;
import com.itheima.entity.UserDto;
import com.itheima.mapper.UserMapper;
import com.itheima.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping
    public User save(@RequestBody User user){
        return userService.insert(user);
    }

    @DeleteMapping
    public String deleteById(Long id){
        userService.deleteById(id);
        return "ok";
    }

	@DeleteMapping("/delAll")
    public String deleteAll(){
        userService.deleteAll();
        return "ok";
    }

    @GetMapping
    public User getById(Long id) {
        return userService.getById(id);
    }

    @PostMapping("/list")
    public List<User> getList(@RequestBody UserDto userDto){
        return userService.getList(userDto);
    }

    @GetMapping("/redis/check")
    public Map<String, Object> redisCheck() {
        String key = "redis:check:key";
        String value = "ok-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        stringRedisTemplate.opsForValue().set(key, value);
        String back = stringRedisTemplate.opsForValue().get(key);
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("written", value);
        result.put("readBack", back);
        result.put("connected", value.equals(back));
        return result;
    }
}
