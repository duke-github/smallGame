package com.ccw.actor;

public class TestActor implements Actor {

    private final Long actorId;

    public TestActor(Long actorId) {
        this.actorId = actorId;
    }

    @Override
    public void onReceive(Object msg) {
        System.out.println("Actor[" + actorId + "] 收到消息: " + msg);
    }
}