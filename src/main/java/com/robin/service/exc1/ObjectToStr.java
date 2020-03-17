package com.robin.service.exc1;

import com.alibaba.fastjson.JSON;
import com.robin.domain.User;
import redis.clients.jedis.Jedis;

public class ObjectToStr {

    public static void main(String[] args) {

        User user = new User();
        user.setName("Robin");
        user.setGender(1);
        user.setAge(23);
        String userJson = JSON.toJSONString(user);
        System.out.println("JsonString:" + userJson);

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.set("user", userJson);
        String result = jedis.get("user");
        System.out.println("RedisResult:" + result);

        User userObject = JSON.parseObject(result, User.class);
        System.out.println("userObject:" + userObject.getName());
    }
}
