package com.ccw.actor;

import com.ccw.message.Message;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MailBox {
    Queue<Message> queue = new ConcurrentLinkedQueue<>();

    public void offer(Message msg) {
        queue.offer(msg);
    }

    public Message poll() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
