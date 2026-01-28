package com.ccw.base.redis;

import java.util.List;
import java.util.Set;

public interface RedisRank {
    List<RedisRankUnit> getRankRange(int start, int end);

    List<RedisRankUnit> getSameScoreRankRange(int start, int end);

    void incrementScore(long userId, long score);

    //获取某个人的排行和分数
    RedisRankUnit getPlayerRank(long userId);


    RedisRankUnit getSameScorePlayerRank(long userId);



    void remove(long userId);
}
