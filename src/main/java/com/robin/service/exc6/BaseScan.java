package com.robin.service.exc6;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class BaseScan {

    public static void main(String[] arg) {


        Jedis jedis = new Jedis("host", 6379);

        Pipeline pipe = jedis.pipelined();
        pipe.multi();

        for (int i = 0; i < 10000; i++) {
            pipe.set("key"+i, i+"");
        }
        // 批量执行
        pipe.exec();
        pipe.close();
        System.out.println(jedis.get("key99"));
    }
}
