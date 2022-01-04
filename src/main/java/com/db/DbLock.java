package com.db;

import com.AbstractDistributedLock;

/**
 * 1.基于数据库实现分布式锁，主键冲突来作
 * 2.new DbLock（lock_name,expire_time,threadId）;   成功插入一条记录:加锁的key，截止失效时间，当前线程id（Thread.currentThread().getId() + "-" + UUID.randomUUID().toString();）
 * 3.同时起个死循环线程watchDog 每隔一段时间观察数据库的记录
 * 4.判断数据库的线程id是否等于当前线程id  ==的话，续期
 * 5.不是的话在判断当前时间是否大于数据截止失效时间， 大于就del当前时间 防止死锁的情况
 */
/**
 * 不可重入,目前引擎最好用myisam，innodb 由于gap和意向锁会造成死锁，rollback 性能下降，且机器down掉 造成死锁
 * create table id_distribute_lock(
 * id int unsigned auto_increment primary key,
 * lock_name varchar(100) not null,
 * expire_time bigint not null,
 * thread_id varchar(100) not null,
 * unique(lock_name)
 * ) engine=myisam;
 */

public class DbLock extends AbstractDistributedLock {


}
