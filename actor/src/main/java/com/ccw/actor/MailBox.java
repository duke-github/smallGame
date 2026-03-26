package com.ccw.actor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MailBox {
    Queue<Object> queue = new ConcurrentLinkedQueue<>();

    public void offer(Object msg) {
        queue.offer(msg);
    }

    public Object poll() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
