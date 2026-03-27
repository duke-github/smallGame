package com.ccw.shard;

import com.ccw.actor.ActorCell;
import com.ccw.factory.ActorFactoryRegistry;
import com.ccw.factory.ActorFactoryType;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * 分片处理器
 * 每个shard绑定一个线程
 */
@Component
@Scope("prototype")
public class Shard {

    @Resource
    private ActorFactoryRegistry factoryRegistry;

    private volatile Thread workerThread;

    public final Map<Long, ActorCell> actorCellMap = new ConcurrentHashMap<>();

    public final ConcurrentLinkedQueue<ActorCell> activeActorList = new ConcurrentLinkedQueue<>();

    public Shard() {
    }

    public void doDispatcher(int type, Long actorId, Object msg) {
        ActorCell actorCell = actorCellMap.computeIfAbsent(actorId, k -> createActorCell(ActorFactoryType.getByValue(type), actorId));
        actorCell.addMsg(msg);
        if (actorCell.getActive().compareAndSet(false, true)) {
            activeActorList.add(actorCell);
            LockSupport.unpark(workerThread);
        }
    }

    private ActorCell createActorCell(ActorFactoryType factoryType, Long actorId) {
        return new ActorCell(actorId, factoryRegistry.get(factoryType).create(actorId));
    }


    public void run() {
        System.out.println("Shard running...");
        workerThread = Thread.currentThread();

        int idleCount = 0;

        while (true) {
            ActorCell cell = activeActorList.poll();
            if (cell == null) {
                if (idleCount < 100) {
                    Thread.onSpinWait(); //自旋
                    idleCount++;
                    continue;
                }
                //判断 - 再次确认队列为空 缩小判断和休眠的间歇
                if (activeActorList.isEmpty()) {
                    LockSupport.park();
                }
                idleCount = 0;
                continue;
            }
            idleCount = 0;
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
