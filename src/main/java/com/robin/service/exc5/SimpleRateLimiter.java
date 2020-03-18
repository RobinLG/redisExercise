package com.robin.service.exc5;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class SimpleRateLimiter {

    private Jedis jedis;

    public SimpleRateLimiter(Jedis jedis) {
        this.jedis = jedis;
    }

    public boolean isActionAllowed(String userId, String actionKey, int period, int maxCount) {
        String key = String.format("hist:%s:%s", userId, actionKey);
        long nowTs = System.currentTimeMillis();
        Pipeline pipe = jedis.pipelined();
        pipe.multi();
        // 记录行为
        // value 和 score都使用时间戳
        pipe.zadd(key, nowTs, ""+nowTs);
        // 移除时间窗口之前的行为记录，剩下的都是时间窗口内的
        pipe.zremrangeByScore(key, 0, nowTs - period * 1000);
        // 获取窗口内的行为数量
        Response<Long> count = pipe.zcard(key);
        // 设置zset过期时间，避免冷用户持续占用内存
        // 过期时间应该等于时间窗口的长度，再多宽限1s
        pipe.expire(key, period + 1);
        // 批量执行
        pipe.exec();
        pipe.close();
        // 比较数量是否超标
        return count.get() <= maxCount;
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("106.54.93.137", 6379);
        SimpleRateLimiter limiter = new SimpleRateLimiter(jedis);
        for (int i = 0; i < 20; i++) {
            System.out.println(limiter.isActionAllowed("robin", "reply", 60, 5));
        }
    }
}
