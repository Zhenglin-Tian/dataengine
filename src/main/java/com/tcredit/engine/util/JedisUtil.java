//package com.tcredit.engine.util;

//
//import com.google.common.collect.Sets;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
//import redis.clients.jedis.*;
//
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
///**
// * @description: 哨兵模式请求redis
// * @author: zl.T
// * @since: 2017-12-06 11:26
// * @updatedUser: zl.T
// * @updatedDate: 2017-12-06 11:26
// * @updatedRemark:
// * @version:
// */
//public class JedisUtil {
//    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
//            .getLogger(JedisUtil.class);
//
//    public static int JEDIS_KEY_EXPIRE_IN_SEC = 0;
//
//    /**
//     * JedisPool, pwd  sentinel模式
//     */
//    private static JedisSentinelPool jedisSentinelPoolPool = null;
//
//
//    static {
//        createSentinelPool();
//        String expire = PropertiesUtil.getString("REDIS_KEY_EXPIRE_IN_SECS");
//        if (StringUtils.isNotBlank(expire)) {
//            JEDIS_KEY_EXPIRE_IN_SEC = Integer.parseInt(expire);
//        } else {
//            JEDIS_KEY_EXPIRE_IN_SEC = 30 * 60;
//        }
//    }
//
//    /**
//     * 获取所有的redis地址
//     */
//    private static Set<String> getRedisHostAndPort() {
//        Set<String> hostAndPorts = Sets.newHashSet();
//        int defaultPort = 26379;
//        String[] sa = PropertiesUtil.getString("REDIS_IPS").split(",");
//        for (String s : sa) {
//            String[] ssa = s.split(":");
//            if (ssa.length == 2) {
//                hostAndPorts.add(s.trim());
//            } else if (ssa.length == 1) {
//                String hostAndPort = s.trim() + ":" + defaultPort;
//                hostAndPorts.add(hostAndPort);
//            } else {
//                throw new RuntimeException("redis地址解析错误");
//            }
//        }
//        return hostAndPorts;
//    }
//
//    /**
//     * 创建jedisPool
//     *
//     * @return
//     */
//    private static void createSentinelPool() {
//        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
//        genericObjectPoolConfig.setMaxTotal(10);
//        genericObjectPoolConfig.setMaxIdle(8);
//        genericObjectPoolConfig.setMinIdle(8);
//        genericObjectPoolConfig.setTestOnBorrow(true);
//        genericObjectPoolConfig.setTestOnReturn(true);
//
//        Set<String> redisHostAndPort = getRedisHostAndPort();
//        String jedisPwd = PropertiesUtil.getString("REDIS_AUTH");
//        String clusterName = PropertiesUtil.getString("REDIS_SENTINEL_CLUSTER_NAME");
//        if (StringUtils.isNotBlank(jedisPwd)) {
//            jedisSentinelPoolPool = new JedisSentinelPool(clusterName, redisHostAndPort, genericObjectPoolConfig, jedisPwd);
//        } else {
//            jedisSentinelPoolPool = new JedisSentinelPool(clusterName, redisHostAndPort, genericObjectPoolConfig);
//        }
//    }
//
//    /**
//     * 获取jedis客户端
//     *
//     * @return
//     */
//    public static Jedis borrow() {
//        Jedis jedis = jedisSentinelPoolPool.getResource();
//        return jedis;
//    }
//
//    /**
//     * 关闭客户端，返回到pool
//     *
//     * @param jedis
//     */
//    public static void close(Jedis jedis) {
//        if (jedis != null) {
//            jedis.close();
//        }
//    }
//
//    /**
//     * @param key
//     * @param value
//     * @param expireInSec
//     */
//    public static void set(String key, String value, int expireInSec) {
//        Jedis jedis = borrow();
//        if (jedis == null) {
//            throw new RuntimeException("获取jedis实例异常");
//        }
//        jedis.set(key, value);
//        jedis.expire(key, expireInSec);
//
//        close(jedis);
//    }
//
//    /**
//     * @param key
//     * @param value
//     */
//    public static void set(String key, String value) {
//        Jedis jedis = borrow();
//        if (jedis == null) {
//            throw new RuntimeException("获取jedis实例异常");
//        }
//        jedis.set(key, value);
//
//        close(jedis);
//    }
//
//    /**
//     * 获取key值
//     *
//     * @param key
//     * @return
//     */
//    public static String get(String key) {
//        Jedis jedis = borrow();
//        String s = jedis.get(key);
//        close(jedis);
//        return s;
//    }
//
//
//
//    /**
//     * 增加操作
//     *
//     * @param key
//     */
//    public static long incrBy(String key) {
//        Jedis jedis = borrow();
//        try {
//            if (jedis == null) {
//                throw new RuntimeException("获取jedis实例异常");
//            }
//            Long incr = jedis.incr(key);
//            jedis.expire(key, 600);
//            return incr;
//        } finally {
//            close(jedis);
//
//        }
//    }
//
//    /**
//     * 自增操作，先将存在的key删除，及重新开始计数
//     *
//     * @param key
//     * @return
//     */
//    public static long incrByFromZero(String key) {
//        Jedis jedis = borrow();
//        try {
//
//
//            if (jedis == null) {
//                throw new RuntimeException("无法获取jedis实例");
//            }
//            if (jedis.exists(key)) {
//                jedis.del(key);
//            }
//            Long incr = jedis.incr(key);
//            jedis.expire(key, 600);
//            return incr;
//        } finally {
//            close(jedis);
//        }
//    }
//
//    public static void main(String[] args) throws InterruptedException {
//        String key = "ttt_key_key_";
////        String val = "ttt_key_key";
////        set(key,val,120);
////
////        Thread.sleep(300);
////
////        String s = get(key);
////        System.out.println(s);
//        // long l = incrBy(key);
//        //System.out.println(borrow().get("2"));
//
//
//
//        /*String key2 = "xxxxxxxxxxxxxxxxxx";
//        String s = JedisUtil.borrow().get(key2);
//        System.out.println(s);*/
//
//
//    }
//
//
//}
//




