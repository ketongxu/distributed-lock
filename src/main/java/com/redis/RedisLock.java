package com.redis;

import com.AbstractDistributedLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;


/**
 * redisson实现 lura脚本实现
 *
 * =====源码
 * return this.commandExecutor.evalWriteAsync(this.getName(), LongCodec.INSTANCE, command,
 * "if (redis.call('exists', KEYS[1]) == 0) then redis.call('hset', KEYS[1], ARGV[2], 1); redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end;
 * //判断当前key是否存在，不存在hset设置 key(加锁的key) 线程id--ARGV[2]（这里是hash表操作）value就为1
 * if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then redis.call('hincrby', KEYS[1], ARGV[2], 1);redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end;
 * //如果KEYS[1], ARGV[2]存在，加锁key和线程id存在，就+1（redis.call('hincrby', KEYS[1], ARGV[2], 1)） 重入
 * return redis.call('pttl', KEYS[1]);"
 * //否则就认为这个锁已经存在且不是当前线程加的 返回锁的存活时间
 * , Collections.singletonList(this.getName()), new Object[]{this.internalLockLeaseTime, this.getLockName(threadId)});
 * ======
 * 线程id 解释：return this.id + ":" + threadId; uuid+threadId，保证不重复
 */

public class RedisLock extends AbstractDistributedLock {

    private RedissonClient client;
    private String key;

    public RedisLock(RedissonClient client,String key){
        this.client = client;
        this.key = key;
    }

    @Override
    public void lock() {
        client.getLock(key).lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        client.getLock(key).lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return client.getLock(key).tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return client.getLock(key).tryLock(time,unit);
    }

    @Override
    public void unlock() {
        client.getLock(key).unlock();
    }

    @Override
    public Condition newCondition() {
        return client.getLock(key).newCondition();
    }


}
