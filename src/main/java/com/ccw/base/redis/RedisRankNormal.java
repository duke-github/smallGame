package com.ccw.base.redis;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;

//这个是正常的积分排行榜
public class RedisRankNormal implements RedisRank {

    private final RedisUtil redisUtil;
    private final String key;

    public RedisRankNormal(RedisUtil redisUtil, String key) {
        this.redisUtil = redisUtil;
        this.key = key;
    }

    //获取排行榜的名次
    public List<RedisRankUnit> getRankRange(int start, int end) {
        ZSetOperations<String, Object> redisRankOpe = redisUtil.getZSetOpe();
        List<RedisRankUnit> unitList = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisRankOpe.reverseRangeWithScores(key, start - 1, end - 1);
        if (typedTuples == null || typedTuples.isEmpty()) {
            return Collections.emptyList();
        }
        int rank = start;
        for (ZSetOperations.TypedTuple<Object> tuple : typedTuples) {
            RedisRankUnit unit = new RedisRankUnit();
            unit.setUserId((Long) tuple.getValue());
            unit.setRank(rank);
            unit.setScore(Objects.requireNonNull(tuple.getScore()).longValue());
            unitList.add(unit);
            rank++;
        }
        return unitList;
    }

    public List<RedisRankUnit> getSameScoreRankRange(int start, int end) {
        ZSetOperations<String, Object> redisRankOpe = redisUtil.getZSetOpe();

        Set<ZSetOperations.TypedTuple<Object>> members =
                redisRankOpe.reverseRangeWithScores(key, 0, end - 1);

        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }

        List<RedisRankUnit> result = new ArrayList<>();

        long lastScore = Long.MIN_VALUE;
        int denseRank = 0;
        int index = 0;

        for (ZSetOperations.TypedTuple<Object> tuple : members) {

            long score = tuple.getScore().longValue();

            if (score != lastScore) {
                denseRank++;
                lastScore = score;
            }

            index++;
            if (index < start) {
                continue;
            }
            if (index > end) {
                break;
            }

            RedisRankUnit unit = new RedisRankUnit();
            unit.setUserId((Long) tuple.getValue());
            unit.setRank(denseRank);
            unit.setScore(score);
            result.add(unit);
        }

        return result;
    }

    public RedisRankUnit getSameScorePlayerRank(long userId) {

        ZSetOperations<String, Object> redisRankOpe = redisUtil.getZSetOpe();
        RedisRankUnit unit = new RedisRankUnit();
        unit.setUserId(userId);

        Double myScore = redisRankOpe.score(key, userId);
        if (myScore == null) {
            return unit;
        }

        Set<ZSetOperations.TypedTuple<Object>> higher = redisRankOpe.reverseRangeByScoreWithScores(key, myScore + 1, Double.MAX_VALUE);


        Set<Long> higherDistinctScores = new HashSet<>();

        for (ZSetOperations.TypedTuple<Object> tuple : higher) {
            long score = tuple.getScore().longValue();
            if (score > myScore.longValue()) {
                higherDistinctScores.add(score);
            }
        }

        unit.setRank(higherDistinctScores.size() + 1);
        unit.setScore(myScore.longValue());
        return unit;
    }


    //增加分数
    public void incrementScore(long userId, long score) {
        ZSetOperations<String, Object> redisRankOpe = redisUtil.getZSetOpe();
        redisRankOpe.incrementScore(key, userId, score);
    }

    //获取某个人的排行和分数
    public RedisRankUnit getPlayerRank(long userId) {
        ZSetOperations<String, Object> redisRankOpe = redisUtil.getZSetOpe();
        RedisRankUnit unit = new RedisRankUnit();
        unit.setUserId(userId);
        Long rank = redisRankOpe.reverseRank(key, userId);
        if (rank == null) {
            return unit;
        }
        unit.setRank(rank + 1);
        unit.setScore(redisRankOpe.score(key, userId).longValue());
        return unit;
    }

    //删除某个人
    public void remove(long userId) {
        ZSetOperations<String, Object> redisRankOpe = redisUtil.getZSetOpe();
        redisRankOpe.remove(key, userId);
    }
}
