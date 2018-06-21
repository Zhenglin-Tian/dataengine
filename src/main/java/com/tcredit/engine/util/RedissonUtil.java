package com.tcredit.engine.util;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-23 10:52
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-23 10:52
 * @updatedRemark:
 * @version:
 */
public class RedissonUtil {

    /**
     * 上下文在redis中的存放时间
     */
    public static int JEDIS_KEY_EXPIRE_IN_SEC = 0;

    /**
     *
     */
    private static RedissonClient client;
    static {
        initSentinelModel();
    }


    private static void initSentinelModel(){
        String pwd = PropertiesUtil.getString("REDIS_AUTH");
        String clusterName = PropertiesUtil.getString("REDIS_SENTINEL_CLUSTER_NAME");
        Config config = new Config();
//        config.useSentinelServers().setMasterConnectionMinimumIdleSize(8);
//        config.useSentinelServers().setMasterConnectionPoolSize(10);
//        config.useSentinelServers().setSlaveConnectionMinimumIdleSize(8);
//        config.useSentinelServers().setSlaveConnectionPoolSize(10);
//        config.useSentinelServers().addSentinelAddress("172.19.160.126:26379", "172.19.160.127:26379", "172.19.160.128:26379");
        for (String redisUrl : getRedisHostAndPort()) {
            config.useSentinelServers().addSentinelAddress(redisUrl);
        }
        config.useSentinelServers().setMasterName(clusterName);
        if (StringUtils.isNotBlank(pwd)) {
            config.useSentinelServers().setPassword(pwd);
        }
        client = Redisson.create(config);

        String expire = PropertiesUtil.getString("REDIS_KEY_EXPIRE_IN_SECS");
        if (StringUtils.isNotBlank(expire)) {
            JEDIS_KEY_EXPIRE_IN_SEC = Integer.parseInt(expire);
        } else {
            JEDIS_KEY_EXPIRE_IN_SEC = 10 * 60;
        }
    }


    /**
     * 获取所有的redis地址
     */
    private static Set<String> getRedisHostAndPort() {
        Set<String> hostAndPorts = Sets.newHashSet();
        int defaultPort = 26379;
        String[] sa = PropertiesUtil.getString("REDIS_IPS").split(",");
        for (String s : sa) {
            String[] ssa = s.split(":");
            if (ssa.length == 2) {
                hostAndPorts.add(s.trim());
            } else if (ssa.length == 1) {
                String hostAndPort = s.trim() + ":" + defaultPort;
                hostAndPorts.add(hostAndPort);
            } else {
                throw new RuntimeException("redis地址解析错误");
            }
        }
        return hostAndPorts;
    }

    /**
     * 获取RedissonClient
     * @return
     */
    private static RedissonClient getClient() {
        return client;
    }

    /**
     * 关闭客户端
     */
    public static void close() {
        if (client != null) {
            client.shutdown();
        }
    }

    public static String getString(String key) {
        String val = "";
        RBucket<Object> bucket = getClient().getBucket(key);
        Object o = bucket.get();
        if (o != null) {
            val = o.toString();
        }
        return val;
    }

    public static void delete(String key){
        if (getClient() == null) {
            throw new RuntimeException("获取jedis实例异常");
        }
        RBucket<Object> bucket = getClient().getBucket(key);
        bucket.delete();
    }


    /**
     * @param key
     * @param value
     * @param expireInSec
     */
    public static void set(String key, String value, int expireInSec) {
        if (getClient() == null) {
            throw new RuntimeException("获取jedis实例异常");
        }
        RBucket<Object> bucket = getClient().getBucket(key);
        bucket.set(value, expireInSec, TimeUnit.SECONDS);
    }

    /**
     * @param key
     * @param value
     */
    public static void set(String key, String value) {
        if (getClient() == null) {
            throw new RuntimeException("获取jedis实例异常");
        }
        RBucket<Object> bucket = getClient().getBucket(key);
        bucket.set(value);
    }

    /**
     * 获取key值
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        if (getClient() == null) {
            throw new RuntimeException("获取jedis实例异常");
        }
        RBucket<Object> bucket = getClient().getBucket(key);
        Object o = bucket.get();
        if (o != null) {
            return String.valueOf(o);
        }
        return null;
    }


    /**
     * 增加操作
     *
     * @param key
     */
    public static long incrBy(String key) {
        if (getClient() == null) {
            throw new RuntimeException("获取jedis实例异常");
        }
        RAtomicLong atomicLong = getClient().getAtomicLong(key);
        long incr = atomicLong.incrementAndGet();
        atomicLong.expire(300, TimeUnit.SECONDS);
        return incr;

    }

    /**
     * 自增操作，先将存在的key删除，及重新开始计数
     *
     * @param key
     * @return
     */
    public static long incrByFromZero(String key) {
        if (getClient() == null) {
            throw new RuntimeException("无法获取jedis实例");
        }
        RAtomicLong atomicLong = getClient().getAtomicLong(key);

        if (atomicLong.isExists()) {
            atomicLong.delete();
        }
        long incr = atomicLong.incrementAndGet();
        atomicLong.expire(300, TimeUnit.SECONDS);
        return incr;
    }

    /**
     * 获取锁
     *
     * @param lockName
     * @return
     */
    public static RLock getRLock(String lockName) {
        if (getClient() == null) {
            throw new RuntimeException("无法获取jedis实例");
        }
        RLock rLock = getClient().getLock(lockName);
        while (rLock == null || rLock.isLocked()) {
            if (!rLock.isLocked()) {
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        rLock.lock(10, TimeUnit.SECONDS);
        return rLock;

    }


    public static void main(String[] args) {
        String key = "xxxxx_xxxx2";

//        Config config = new Config();
//        config.useSentinelServers().setMasterConnectionMinimumIdleSize(8);
//        config.useSentinelServers().setMasterConnectionPoolSize(10);
//        config.useSentinelServers().setSlaveConnectionMinimumIdleSize(8);
//        config.useSentinelServers().setSlaveConnectionPoolSize(10);
//        config.useSentinelServers().addSentinelAddress("172.19.160.126:26379","172.19.160.127:26379","172.19.160.128:26379");
//        config.useSentinelServers().setMasterName("mymaster");
//        //config.useMasterSlaveServers().setMasterAddress("172.19.160.127:6379");
//        RedissonClient client = Redisson.create(config);
//
//        RBucket<Object> bucket = client.getBucket(key);
//        Object s = bucket.get();
//        System.out.println(s);
//        //client.shutdown();
//        client.shutdown();

        /**
         * 测试锁
         */
        String lockName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        RLock rLock = getRLock(lockName);
        if (rLock != null) {
            System.out.println(rLock.getName() + "--------" + rLock.isLocked());
            if (!rLock.isLocked()) {
                rLock.lock(60, TimeUnit.SECONDS);
            }
        }



    }


}
