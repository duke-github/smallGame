package com.ccw.factory;

public interface ActorFactoryRegistry {
    ActorFactory get(ActorFactoryType type);
}
