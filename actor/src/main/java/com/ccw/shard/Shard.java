package com.ccw.shard;

import com.ccw.Constant;
import com.ccw.actor.ActorCell;
import com.ccw.factory.ActorFactoryRegistry;
import com.ccw.factory.ActorFactoryType;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Iterator;
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
    private long lastClearTime;

    public final Map<Long, ActorCell> actorCellMap = new ConcurrentHashMap<>();
    private Iterator<Map.Entry<Long, ActorCell>> cleanIterator;


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
        lastClearTime = System.currentTimeMillis();
        workerThread = Thread.currentThread();

        int idleCount = 0;

        while (true) {
            //清理的时间和actor的活跃时间使用同一个时间 确保每轮清理的过程中 不会清理掉这一轮存活的actor
            if (System.currentTimeMillis() - lastClearTime > Constant.ACTOR_EXPIRE_TIME) {
                clearActor();
            }
            ActorCell cell = activeActorList.poll();
            if (cell == null) {
                //空闲清理 降低时间循环清理的压力 无停顿
                if (System.currentTimeMillis() - lastClearTime > 1000) {
                    clearActor();
                }
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
            cell.resetLastActiveTime();
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


    /**
     * 1.按照时间判断是否清理
     * 2.只清理一部分
     * 3.检测活跃
     */
    private void clearActor() {
        int count = 0;
        int limit = 100;
        lastClearTime = System.currentTimeMillis();
        if (cleanIterator == null || !cleanIterator.hasNext()) {
            cleanIterator = actorCellMap.entrySet().iterator();
        }
        while (cleanIterator.hasNext() && count < limit) {
            ActorCell actorCell = cleanIterator.next().getValue();
            if (actorCell.canClear()) {
                cleanIterator.remove();
                count++;
            }
        }
    }


}
