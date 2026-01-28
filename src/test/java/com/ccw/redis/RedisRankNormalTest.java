package com.ccw.redis;


import com.ccw.base.redis.RedisRankNormal;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisRankNormalTest {

    @Resource
    private RedisRankNormal redisRank;

    @Test
    public void testRank() {
        String key = "win:coin:20240807";
        for (int i = 0; i < 10000; i++) {
            redisRank.addScore(key, randomUserId(), randomScore());
        }
        List<RedisRankNormal.RedisRankUnit> rank = redisRank.getRank(key, 1, 100);
        for (RedisRankNormal.RedisRankUnit unit : rank) {
            System.out.println(unit.getRank() + "_" + unit.getUserId() + "_" + unit.getScore());
        }
        System.out.println(1);
    }


    public long randomUserId() {
        return new Random().nextInt(1000, 9999);
    }

    public long randomScore() {
        return new Random().nextInt(10000, 99999);
    }
}
