package com.ccw.base.redis;

public class RedisRankUnit {

    private long userId;
    private long score;
    private long rank;

    public long getUserId() {
        return userId;
    }

    public Long getScore() {
        return score;
    }

    public long getRank() {
        return rank;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }
}
