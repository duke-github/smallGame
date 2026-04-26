package com.ccw.actor;

import com.ccw.Constant;
import com.ccw.Envelope;

import java.util.concurrent.atomic.AtomicBoolean;

public class ActorCell {
    private String actorId;
    private Actor actor;
    private MailBox mailBox;
    private AtomicBoolean isActive = new AtomicBoolean(false);
    public long lastActiveTime = System.currentTimeMillis();

    public Actor getActor() {
        return actor;
    }

    public AtomicBoolean getActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive.set(active);
    }

    public void loop() {
        int count = 0;
        Envelope msg;
        while (count < Constant.ACTOR_LOOP_LIMIT && (msg = mailBox.poll()) != null) {
            actor.onReceive(msg);
            count++;
        }
    }

    public void addMsg(Object msg) {
        mailBox.offer((Envelope) msg);
    }

    public ActorCell(String actorId, Actor actor) {
        this.actorId = actorId;
        this.mailBox = new MailBox();
        this.actor = actor;
    }

    private Actor createActor(String actorId) {
        return new TestActor(actorId);
    }

    public boolean hasMsg() {
        return !mailBox.isEmpty();
    }

    public boolean canClear() {
        return hasMsg() && isActive.get() && lastActiveTime - System.currentTimeMillis() > Constant.ACTOR_EXPIRE_TIME;
    }

    public void resetLastActiveTime() {
        lastActiveTime = System.currentTimeMillis();
    }
}
