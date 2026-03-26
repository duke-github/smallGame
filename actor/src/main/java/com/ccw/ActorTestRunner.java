package com.ccw;

import com.ccw.shard.Shard;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class ActorTestRunner {

    private final Shard shard;

    public ActorTestRunner(Shard shard) {
        this.shard = shard;
    }

    @PostConstruct
    public void test() throws InterruptedException {
        System.out.println("开始发送消息...");

        // 模拟多个 actor
        for (long i = 1; i <= 3; i++) {
            for (int j = 0; j < 5; j++) {
                shard.doDispatcher(0, i, "msg-" + j);
            }
        }

        // 再发一波延迟消息
        Thread.sleep(1000);

        shard.doDispatcher(0, 1L, "after-1s");

        System.out.println("消息发送完成");
    }
}