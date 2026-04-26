package com.ccw.actor;

import com.ccw.Envelope;
import com.ccw.business.handler.MessageHandler;
import com.ccw.business.handler.MessageHandlerRegistry;

public class TestActor implements Actor {

    private final String actorId;

    public TestActor(String actorId) {
        this.actorId = actorId;
    }

    @Override
    public void onReceive(Envelope envelope) {
        MessageHandler<?> messageHandler = MessageHandlerRegistry.getHandlerByMsgId(envelope.getMsgId());
        if (messageHandler == null) {
            throw new RuntimeException("handler不存在, msgId=" + envelope.getMsgId());
        }
        dispatch(messageHandler, envelope);
    }

    @SuppressWarnings("unchecked")
    private static <T> void dispatch(MessageHandler<?> handler, Envelope envelope) {
        ((MessageHandler<T>) handler).handler(envelope.getSession(), (T) envelope.getMsg());
    }
}