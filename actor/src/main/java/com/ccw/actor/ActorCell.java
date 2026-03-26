package com.ccw.actor;

import java.util.concurrent.atomic.AtomicBoolean;

public class ActorCell {
    private Long actorId;
    private Actor actor;
    private MailBox mailBox;
    private AtomicBoolean isActive = new AtomicBoolean(false);

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
        Object msg;
        while (count < Constant.ACTOR_LOOP_LIMIT && (msg = mailBox.poll()) != null) {
            actor.onReceive(msg);
            count++;
        }
    }

    public void addMsg(Object msg) {
        mailBox.offer(msg);
    }

    public ActorCell(Long actorId) {
        this.actorId = actorId;
        this.mailBox = new MailBox();
        this.actor = createActor(actorId);
    }

    private Actor createActor(Long actorId) {
        return new TestActor(actorId);
    }

    public boolean hasMsg() {
        return !mailBox.isEmpty();
    }
}
