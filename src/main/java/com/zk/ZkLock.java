package com.zk;

import com.AbstractDistributedLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ZkLock extends AbstractDistributedLock {

    private Logger logger = LoggerFactory.getLogger(ZkLock.class);

    private CuratorFramework client;

    private InterProcessLock lock ;


    public  ZkLock(String zkAddress,String lockPath) {
        // 1.Connect to zk
        client = CuratorFrameworkFactory.newClient(
                zkAddress,
                new RetryNTimes(5, 5000)
        );
        client.start();
        if(client.getState() == CuratorFrameworkState.STARTED){
            logger.info("zk client start successfully!");
            logger.info("zkAddress:{},lockPath:{}",zkAddress,lockPath);
        }else{
            throw new RuntimeException("客户端启动失败。。。");
        }
        this.lock = defaultLock(lockPath);
    }

    private InterProcessLock defaultLock(String lockPath ){
        return  new InterProcessMutex(client, lockPath);//由curator封装，虚拟队列节点实现，支持可以重入锁
    }
    @Override
    public void lock() {
        try {
            this.lock.acquire();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean tryLock() {
        boolean flag ;
        try {
            flag=this.lock.acquire(0, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return flag;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        boolean flag ;
        try {
            flag=this.lock.acquire(time,unit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return flag;
    }

    @Override
    public void unlock() {
        try {
            this.lock.release();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
