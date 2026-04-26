package com.ccw.factory;

import com.ccw.actor.Actor;
import com.ccw.actor.TestActor;

@ActorType(ActorFactoryType.TEST)
public class TestActorFactory implements ActorFactory {

    @Override
    public Actor create(String actorId) {
        return new TestActor(actorId);
    }
}
