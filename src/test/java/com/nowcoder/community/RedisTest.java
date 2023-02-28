package com.nowcoder.community;

import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey, 1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
    }

    @Test
    public void testHash() {
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "xiaomin");
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
    }

    @Test
    public void testSortedSet() {
        String key = "test:fruit";

        redisTemplate.opsForZSet().add(key, "banana", 7);
        redisTemplate.opsForZSet().add(key, "apple", 3);
        redisTemplate.opsForZSet().add(key, "cherry", 8);
        redisTemplate.opsForZSet().add(key, "lemon", 4);

        System.out.println(redisTemplate.opsForZSet().zCard(key));
        System.out.println(redisTemplate.opsForZSet().score(key, "apple"));
    }

    @Test
    public void testKeys() {
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.expire("test:fruit", 10, TimeUnit.SECONDS);
    }

    // 多次访问同一个key
    @Test
    public void testBound() {
        String redisKey = "test:count";
        BoundValueOperations boundValue = redisTemplate.boundValueOps(redisKey);
        boundValue.increment();
        System.out.println(boundValue.get());
    }

    // 编程式事务
    @Test
    public void testTransaction() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";
                operations.multi();
                operations.opsForSet().add(redisKey, "zhangsan");
                operations.opsForSet().add(redisKey, "lisi");
                operations.opsForSet().add(redisKey, "wangwu");

                return operations.exec();
            }
        });
        System.out.println(obj);
    }

    // 统计20晚重复数据的独立总数
    @Test
    public void testHyperLongLong() {
        String redisKey = "test:hll:01";
        Random random = new Random();
        for (int i=0; i<100000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey, i);
        }
        for (int i=0; i<100000; i++) {
            int r = random.nextInt(100000);
            redisTemplate.opsForHyperLogLog().add(redisKey, r);
        }
        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));
    }

    // 将3组数据进行合并，在统计合并后的重复数据的独立总数
    @Test
    public void testHyperLongLongUnit() {
        String redisKey1 = "test:hll:02";
        for (int i=0; i<10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey1, i);
        }
        String redisKey2 = "test:hll:03";
        for (int i=5000; i<15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2, i);
        }
        String redisKey3 = "test:hll:04";
        for (int i=10000; i<20000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3, i);
        }
        String unionKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey, redisKey1, redisKey2, redisKey3);
        System.out.println(redisTemplate.opsForHyperLogLog().size(unionKey));
    }

    // 统计一组数据的布尔值
    @Test
    public void testBitMap() {
        String redisKey = "test:bm:1";
        // 记录
        redisTemplate.opsForValue().setBit(redisKey, 1, true);
        redisTemplate.opsForValue().setBit(redisKey, 4, true);
        redisTemplate.opsForValue().setBit(redisKey, 7, true);
        // 查询
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 2));

        // 统计
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println((Long) obj);
    }

    // 统计三组数据的Boolean值，并对这三组数据做布尔运算
    @Test
    public void testBitMapOps() {
        String redisKey2 = "test:bm:2";
        // 记录
        redisTemplate.opsForValue().setBit(redisKey2, 0, true);
        redisTemplate.opsForValue().setBit(redisKey2, 1, true);
        redisTemplate.opsForValue().setBit(redisKey2, 2, true);

        String redisKey3 = "test:bm:3";
        // 记录
        redisTemplate.opsForValue().setBit(redisKey3, 2, true);
        redisTemplate.opsForValue().setBit(redisKey3, 3, true);
        redisTemplate.opsForValue().setBit(redisKey3, 4, true);

        String redisKey4 = "test:bm:4";
        // 记录
        redisTemplate.opsForValue().setBit(redisKey4, 4, true);
        redisTemplate.opsForValue().setBit(redisKey4, 5, true);
        redisTemplate.opsForValue().setBit(redisKey4, 6, true);
        String redisKey = "test:bm:or";

        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR, redisKey.getBytes(),
                        redisKey2.getBytes(), redisKey3.getBytes(), redisKey4.getBytes());
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println((Long) obj);
    }

}
