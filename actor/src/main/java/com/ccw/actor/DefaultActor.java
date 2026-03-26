package com.ccw.actor;

public class DefaultActor implements  Actor{

    private Long actorId;


    @Override
    public void onReceive(Object msg) {
        System.out.println("处理消息");
    }

    public DefaultActor(Long actorId) {
        this.actorId = actorId;
    }
}
