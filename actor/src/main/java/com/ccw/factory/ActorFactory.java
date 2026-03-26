package com.ccw.factory;

import com.ccw.actor.Actor;

public interface ActorFactory {

    Actor create(Long actorId);
}
