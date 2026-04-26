package com.ccw.actor;

import com.ccw.Envelope;

public class DefaultActor implements  Actor{

    private Long actorId;


    @Override
    public void onReceive(Envelope msg) {
        System.out.println("处理消息");
    }

    public DefaultActor(Long actorId) {
        this.actorId = actorId;
    }
}
