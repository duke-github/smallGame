package com.ccw.shard;

import com.ccw.netty.message.Message;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
//分片管理 每个分片对应一个线程
public class ShardSystem {

    @Resource
    private ApplicationContext context;

    private volatile List<Shard> shards;


    Map<Long, Shard> shardMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws InterruptedException {
        int n = Runtime.getRuntime().availableProcessors();
        CountDownLatch latch = new CountDownLatch(n);
        List<Shard> temp = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executor = Executors.newFixedThreadPool(n);

        for (int i = 0; i < n; i++) {
            executor.execute(() -> {
                Shard shard = context.getBean(Shard.class);
                temp.add(shard);
                latch.countDown();
                shard.run();
            });
        }
        // 等所有 shard 初始化完成
        latch.await();

        this.shards = Collections.unmodifiableList(temp);
    }


    //提取一个dispatcher 将不同的actor分配给不同的shard 但是相同的actor要分配给相同的shard
    //不保存actor - shard对应关系 只根据算法判断
    public void doDispatcher(int type, Long actorId, Message msg) {
        Shard shard = shardMap.computeIfAbsent(actorId, k -> {
            //这里是检索
            int i = ThreadLocalRandom.current().nextInt(shards.size());
            return shards.get(i);
        });

        shard.doDispatcher(type, actorId, msg);
    }

}
