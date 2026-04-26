package com.ccw.business.handler;

/**
 * 维护 msgId -> MessageHandler 的关系
 */
public class MessageHandlerRegistry {

    public static MessageHandler<?> getHandlerByMsgId(int msgId) {
        var d = com.ccw.netty.message.MessageDescriptorRegistry.getByMsgId(msgId);
        return d == null ? null : d.handler();
    }
}

