package com;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public  abstract class AbstractDistributedLock implements Lock {



    public void lock() {
        throw new RuntimeException("不支持的操作");
    }

    public void lockInterruptibly() throws InterruptedException {
        throw new RuntimeException("不支持的操作");
    }

    public boolean tryLock() {
        throw new RuntimeException("不支持的操作");
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("不支持的操作");
    }

    public void unlock() {
        throw new RuntimeException("不支持的操作");
    }

    public Condition newCondition() {
        throw new RuntimeException("不支持的操作");
    }


}
