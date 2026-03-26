package com.ccw.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultActorFactoryRegistry implements ActorFactoryRegistry {

    private final Map<ActorFactoryType, ActorFactory> factoryMap = new ConcurrentHashMap<>();

    public DefaultActorFactoryRegistry(ApplicationContext context) {
        Map<String, ActorFactory> beans = context.getBeansOfType(ActorFactory.class);

        for (ActorFactory factory : beans.values()) {
            ActorType annotation = factory.getClass().getAnnotation(ActorType.class);
            if (annotation == null) {
                continue;
            }

            factoryMap.put(annotation.value(), factory);
        }
    }

    @Override
    public ActorFactory get(ActorFactoryType type) {
        return factoryMap.get(type);
    }
}
