package com.ccw;

import com.ccw.shard.ShardSystem;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ActorTestRunner implements CommandLineRunner {

    private final ShardSystem shardSystem;

    public ActorTestRunner(ShardSystem shardSystem) {
        this.shardSystem = shardSystem;
    }

    @Override
    public void run(String... args) {
        System.out.println("开始发送消息...");

        // 模拟多个 actor
        for (long i = 1; i < 100; i++) {
            shardSystem.doDispatcher(0, i, "msg-" + i);
        }

        System.out.println("消息发送完成");
    }
}