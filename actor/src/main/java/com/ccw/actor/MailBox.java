package com.ccw.actor;

import com.ccw.Envelope;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MailBox {
    Queue<Envelope> queue = new ConcurrentLinkedQueue<>();

    public void offer(Envelope msg) {
        queue.offer(msg);
    }

    public Envelope poll() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
