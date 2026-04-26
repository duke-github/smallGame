package com.ccw.netty.message;

import com.ccw.business.serialize.MessageSerializer;

public class MessageMeta<T> {
    private final Class<T> requestDataClassType;
    private final MessageSerializer serializer;

    public MessageMeta(Class<T> requestDataClassType,  MessageSerializer serializer) {
        this.requestDataClassType = requestDataClassType;
        this.serializer = serializer;
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

    public MessageSerializer getSerializer() {
        return serializer;
    }

    public T deserialize(Message message) {
        return this.getSerializer().deserialize(message.getBody(),requestDataClassType);
    }
}
