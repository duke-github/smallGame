package com.ccw.actor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class ActorTest {

    public static void main(String[] args) throws Exception {

        Shard shard = new Shard();

        // 启动调度线程
        Thread t = new Thread(shard::run);
        t.setDaemon(true);
        t.start();

        int actorCount = 1000;
        int threadCount = 8;
        int msgPerThread = 100_000;

        ExecutorService pool = Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch = new CountDownLatch(threadCount);

        // ⭐ 多线程疯狂发消息
        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                for (int j = 0; j < msgPerThread; j++) {
                    long actorId = ThreadLocalRandom.current().nextInt(actorCount);
                    shard.doDispatcher(actorId, j);
                }
                latch.countDown();
            });
        }

        latch.await();
        System.out.println("✅ 所有消息发送完");

        // 等待消费
        Thread.sleep(3000);

        // ⭐ 校验结果
        int total = 0;

        for (ActorCell cell : shard.actorCellMap.values()) {
            TestActor actor = (TestActor) cell.getActor();
            total += actor.counter.get();
        }

        int expected = threadCount * msgPerThread;

        System.out.println("期望处理: " + expected);
        System.out.println("实际处理: " + total);

        if (expected != total) {
            System.err.println("❌ 丢消息了！！！");
        } else {
            System.out.println("✅ 没丢消息");
        }

        pool.shutdown();
    }
}