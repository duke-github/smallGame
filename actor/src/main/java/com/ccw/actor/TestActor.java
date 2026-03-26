package com.ccw.actor;

import java.util.concurrent.atomic.AtomicInteger;

public class TestActor implements Actor {

    private final Long actorId;

    // 总处理数
    public final AtomicInteger counter = new AtomicInteger(0);

    // 并发检测
    private final AtomicInteger concurrent = new AtomicInteger(0);

    public TestActor(Long actorId) {
        this.actorId = actorId;
    }

    @Override
    public void onReceive(Object msg) {
        int c = concurrent.incrementAndGet();

        // ❗如果 >1，说明出现并发执行（错误）
        if (c > 1) {
            System.err.println("❌ 并发执行了 actorId=" + actorId);
        }

        // 模拟业务耗时
        // Thread.yield();

        counter.incrementAndGet();

        concurrent.decrementAndGet();
    }
}