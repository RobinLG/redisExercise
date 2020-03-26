package com.robin.service.exc7;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

public class TransactionDemo {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("host", 6379);
        String userId = "abc";
        String key = keyFor(userId);
        jedis.setnx(key, String.valueOf(5));    // initializes
        System.out.println(doubleAccount(jedis, userId));
        jedis.close();
    }

    public static int doubleAccount(Jedis jedis, String userId) {
        String key = keyFor(userId);
        while (true) {
            jedis.watch(key);
            int value = Integer.parseInt(jedis.get(key));
            value *= 2; // double
            Transaction tx = jedis.multi();
            tx.set(key, String.valueOf(value));
            List<Object> res = tx.exec();
            if (res != null) {
                break;  // success
            }
        }
        return Integer.parseInt(jedis.get(key));    // again to require balance
    }

    public static String keyFor(String userId) {
        return String.format("accoount_%s", userId);
    }
}
