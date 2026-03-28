package com.ccw;

public class Constant {

    //actor每次循环处理的消息条数最大上限
    public static final int ACTOR_LOOP_LIMIT = 20;

    //30s内活跃的actor不清理
    public static final long ACTOR_EXPIRE_TIME = 30_000;
}
