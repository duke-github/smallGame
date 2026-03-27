package com.ccw.factory;

import com.ccw.actor.Actor;

/**
 *  actor工厂接口
 *  所有的actor应该通过继承工厂接口创建对应的actor
 */

public interface ActorFactory {

    Actor create(Long actorId);
}
