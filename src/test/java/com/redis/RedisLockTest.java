package com.redis;

import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedisLockTest {

    private RedissonClient getClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://xxxx:xxxx").setPassword("xxxxxx");//.setConnectionMinimumIdleSize(10).setConnectionPoolSize(10);//.setConnectionPoolSize();//172.16.10.164
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Test
    public void test() throws Exception {
        int[] count = {0};
        for (int i = 0; i < 10; i++) {
            RedissonClient client = getClient();
            final RedisLock redisLock = new RedisLock(client,"lock_key");
            executorService.submit(() -> {
                try {
                    redisLock.lock();
                    count[0]++;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        redisLock.unlock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
        System.out.println(count[0]);
    }
}
