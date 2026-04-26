package com.ccw.netty.message;

import com.ccw.business.handler.MessageHandler;
import com.ccw.business.serialize.MessageSerializer;

/**
 * 封装一层 以msgId为唯一键 用于解析handler注解携带的信息
 */
public record MessageDescriptor(int msgId, Class<?> messageClass, MessageSerializer serializer,
                                MessageHandler<?> handler) {
    public MessageMeta<?> toMeta() {
        return new MessageMeta<>(messageClass, serializer);
    }
}

