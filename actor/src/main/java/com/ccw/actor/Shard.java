package com.ccw.actor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Shard {

    public final Map<Long, ActorCell> actorCellMap = new ConcurrentHashMap<>();

    public static final ConcurrentLinkedQueue<ActorCell> activeActorList = new ConcurrentLinkedQueue<>();


    public void doDispatcher(Long userId, Object msg) {
        ActorCell actorCell = actorCellMap.computeIfAbsent(userId, this::createActorCell);
        actorCell.addMsg(msg);
        if (actorCell.getActive().compareAndSet(false, true)) {
            activeActorList.add(actorCell);
        }
    }

    private ActorCell createActorCell(Long actorId) {
        return new ActorCell(actorId);
    }


    public void run() {
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
