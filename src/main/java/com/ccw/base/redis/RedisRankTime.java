package com.ccw.base.redis;

import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

//带有时间戳的排行榜   zset中的积分只用于排序 不用与展示 积分需要单独记录
//zset的score 使用前27位作为积分(分数超过1亿) 后26位作为时间  记录从排行榜开始时间例2026-1-1  再按照5s压缩 可以记录十年内的榜单
// 如果判断分数超过1亿   可以再对时间位数进行压缩 因为分数超级大 且每次变动非常小的情况下  碰撞的概率会比较低
//如果分数不是每次增加1  而是增加100  10000 这样的  对分数进行除最小的比例以后再记录
public class RedisRankTime implements RedisRank {

    private static final int TIME_BITS = 26;
    private static final int SCORE_BITS = 27;

    private static final long START_TIME_SECONDS = LocalDate.of(2026, 1, 1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();

    private final ZSetOperations<String, Object> zSetOperations;
    private final String rankKey;

    public RedisRankTime(RedisUtil redisUtil, String rankKey) {
        this.rankKey = rankKey;
        this.zSetOperations = redisUtil.getZSetOpe();
    }

    /**
     * 根据当前的时间戳获取实际积分对应的zset积分
     */
    private long fillScore(long realScore) {
        if (realScore < 0 || realScore > 1 << SCORE_BITS) {
            throw new IllegalStateException("time rank error score:" + realScore);
        }

        long nowSeconds = System.currentTimeMillis() / 1000;
        long timePart = (nowSeconds - START_TIME_SECONDS) >> 2;

        return (realScore << TIME_BITS) | timePart;
    }

    /**
     * 获取实际积分
     */
    private long realScore(long rankScore) {
        return rankScore >> TIME_BITS;
    }


    @Override
    public void incrementScore(long userId, long addScore) {

        Double old = zSetOperations.score(rankKey, userId);
        long oldRealScore = 0;

        if (old != null) {
            oldRealScore = realScore(old.longValue());
        }

        long newScore = oldRealScore + addScore;
        long rankScore = fillScore(newScore);

        zSetOperations.add(rankKey, userId, (double) rankScore);
    }


    @Override
    public List<RedisRankUnit> getRankRange(int start, int end) {

        Set<ZSetOperations.TypedTuple<Object>> members =
                zSetOperations.reverseRangeWithScores(rankKey, start - 1, end - 1);

        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }

        List<RedisRankUnit> result = new ArrayList<>();
        int rank = start;

        for (ZSetOperations.TypedTuple<Object> tuple : members) {
            long rankScore = tuple.getScore().longValue();
            RedisRankUnit unit = new RedisRankUnit();
            unit.setUserId((Long) tuple.getValue());
            unit.setRank(rank++);
            unit.setScore(realScore(rankScore));
            result.add(unit);
        }

        return result;
    }

    @Override
    public List<RedisRankUnit> getSameScoreRankRange(int start, int end) {
        Set<ZSetOperations.TypedTuple<Object>> members = zSetOperations.reverseRangeWithScores(rankKey, 0, end - 1);

        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }

        List<RedisRankUnit> result = new ArrayList<>();

        long lastScore = Long.MIN_VALUE;
        int denseRank = 0;
        int index = 0;

        for (ZSetOperations.TypedTuple<Object> tuple : members) {
            long score = tuple.getScore().longValue();
            long realScore = realScore(score);
            if (realScore != lastScore) {
                denseRank++;
                lastScore = realScore;
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
            unit.setScore(realScore);
            result.add(unit);
        }

        return result;
    }

    @Override
    public RedisRankUnit getPlayerRank(long userId) {
        Long rank = zSetOperations.reverseRank(rankKey, userId);
        Double score = zSetOperations.score(rankKey, userId);

        RedisRankUnit unit = new RedisRankUnit();
        unit.setUserId(userId);

        if (rank == null || score == null) {
            return unit;
        }
        long raw = score.longValue();
        unit.setRank(rank + 1);
        unit.setScore(realScore(raw));
        return unit;
    }

    public RedisRankUnit getSameScorePlayerRank(long userId) {
        RedisRankUnit unit = new RedisRankUnit();
        unit.setUserId(userId);

        Double raw = zSetOperations.score(rankKey, userId);
        if (raw == null) {
            return unit;
        }

        long myRealScore = realScore(raw.longValue());

        Set<ZSetOperations.TypedTuple<Object>> all = zSetOperations.reverseRangeWithScores(rankKey, 0, -1);

        Set<Long> higherDistinct = new HashSet<>();

        for (ZSetOperations.TypedTuple<Object> tuple : all) {
            long rs = realScore(tuple.getScore().longValue());
            if (rs > myRealScore) {
                higherDistinct.add(rs);
            }
        }
        unit.setRank(higherDistinct.size() + 1);
        unit.setScore(myRealScore);
        return unit;
    }



    @Override
    public void remove(long userId) {
        zSetOperations.remove(rankKey, userId);
    }
}
