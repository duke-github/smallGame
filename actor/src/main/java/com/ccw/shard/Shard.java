package com.ccw.shard;

import com.ccw.actor.ActorCell;
import com.ccw.factory.ActorFactoryRegistry;
import com.ccw.factory.ActorFactoryType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

//分片处理器
@Component
public class Shard {

    @Resource
    private final ActorFactoryRegistry factoryRegistry;

    public final Map<Long, ActorCell> actorCellMap = new ConcurrentHashMap<>();

    public static final ConcurrentLinkedQueue<ActorCell> activeActorList = new ConcurrentLinkedQueue<>();

    public Shard(ActorFactoryRegistry factoryRegistry) {
        this.factoryRegistry = factoryRegistry;
    }

    public void doDispatcher(int type, Long actorId, Object msg) {
        ActorCell actorCell = actorCellMap.computeIfAbsent(actorId, k -> createActorCell(ActorFactoryType.getByValue(type), actorId));
        actorCell.addMsg(msg);
        if (actorCell.getActive().compareAndSet(false, true)) {
            activeActorList.add(actorCell);
        }
    }

    @PostConstruct
    public void start() {
        Executors.newSingleThreadExecutor().submit(this::run);
    }

    private ActorCell createActorCell(ActorFactoryType factoryType, Long actorId) {
        return new ActorCell(actorId, factoryRegistry.get(factoryType).create(actorId));
    }


    public void run() {
        System.out.println("Shard running...");
        while (true) {
            ActorCell cell = activeActorList.poll();
            if (cell == null) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
            cell.loop();
            if (cell.hasMsg()) {
                activeActorList.offer(cell);
            } else {
                cell.setActive(false);

                //二次检查
                if (cell.hasMsg()) {
                    if (cell.getActive().compareAndSet(false, true)) {
                        activeActorList.offer(cell);
                    }
                }
            }
        }
    }


}
