package com.learnandtry;

import com.AbstractDistributedLock;
import com.zk.ZkLock;
import org.apache.zookeeper.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * zk 分布式锁 持久节点，虚拟节点，虚拟队列节点
 *
 */
public class ZkDistributedLock  {


    private ThreadLocal<Integer> reentrantCount = new ThreadLocal<Integer>();//锁重入


    private  ThreadLocal<String> concurentContextPath=new ThreadLocal<String>();

    private CountDownLatch countDownLatch=new CountDownLatch(1);

    private  String lockPath;

    private  ZooKeeper zooKeeper;


    public ZkDistributedLock(String lockPath,String url ){


    }



    private boolean tryLock(){
        if (this.reentrantCount.get() != null) {
            int count = this.reentrantCount.get();
            if (count > 0) {
                this.reentrantCount.set(++count);
                return true;
            }
        }
        //不是重入锁，先创建锁节点
        try {
            if(this.concurentContextPath.get()==null){
                this.concurentContextPath.set(zooKeeper.create("/" + this.lockPath, Thread.currentThread().getName().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL));
            }
            List<String> children=zooKeeper.getChildren("/",false);
            //如果是第一
            Collections.sort(children);
            if(concurentContextPath.get().equals(children.get(0))){
                this.reentrantCount.set(1);
                return true;
            }else{//不是第一个就 监控前面的节点删除动作
                int curIndex = children.indexOf(concurentContextPath.get().substring(lockPath.length() + 1));//获取当前请求节点数
                //监控前面一个 节点的删除动作
                zooKeeper.exists(this.lockPath + children.get(curIndex - 1)
                        , new Watcher() {
                            public void process(WatchedEvent watchedEvent) {
                                switch (watchedEvent.getType()) {
                                    case None:
                                        break;
                                    case NodeCreated:
                                        break;
                                    case NodeDeleted:
                                        tryLock();
                                        break;
                                    case NodeDataChanged:
                                        break;
                                    case NodeChildrenChanged:
                                        break;
                                }
                            }
                        });
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       return false;
    }



    public boolean unlock() {
        if (this.reentrantCount.get() != null) {
            int count = this.reentrantCount.get();
            if (count > 1) {
                this.reentrantCount.set(--count);
            } else {
                this.reentrantCount.remove();
            }
        }
        try {
           zooKeeper.delete(lockPath,-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return true;
    }


}
