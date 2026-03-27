package com.ccw.actor;

import java.util.concurrent.atomic.AtomicInteger;

public class TestActor implements Actor {

    private final Long actorId;

    public TestActor(Long actorId) {
        this.actorId = actorId;
    }


    AtomicInteger count = new AtomicInteger(0);

    @Override
    public void onReceive(Object msg) {
        int current = count.addAndGet(1);
        System.out.println(Thread.currentThread().getName() + " Actor[" + actorId + "] 一共收到" + current + "条消息: " + msg);
    }
}