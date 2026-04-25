package com.ccw.netty.message;

import com.ccw.business.handler.MessageHandler;
import com.ccw.business.serialize.MessageSerializer;
import io.netty.channel.ChannelHandlerContext;

public class MessageMeta<T> {
    private final Class<T> requestDataClassType;
    private final MessageHandler<T> handler;
    private final MessageSerializer serializer;

    public MessageMeta(Class<T> requestDataClassType, MessageHandler<T> handler, MessageSerializer serializer) {
        this.requestDataClassType = requestDataClassType;
        this.handler = handler;
        this.serializer = serializer;
    }

    public void handle(ChannelHandlerContext ctx, byte[] bytes) {
        T obj = serializer.deserialize(bytes, requestDataClassType);
        handler.handler(ctx, obj);
    }

    public byte[] serialize(T obj) {
        return serializer.serialize(obj);
    }

    @SuppressWarnings("unchecked")
    public static byte[] serializeAny(MessageMeta<?> meta, Object obj) {
        return ((MessageMeta<Object>) meta).serialize(obj);
    }

    public Class<T> getRequestDataClassType() {
        return requestDataClassType;
    }

    public MessageHandler<T> getHandler() {
        return handler;
    }

    public MessageSerializer getSerializer() {
        return serializer;
    }
}
