package com.ccw.netty.message;


/**
维护msgid和对应的类class和序列化关系
 */
public class MessageCodecRegistry {

    public static MessageMeta<?> getMetaByMsgId(int msgId) {
        MessageDescriptor d = MessageDescriptorRegistry.getByMsgId(msgId);
        return d == null ? null : d.toMeta();
    }

    public static int resolveMsgIdByClass(Class<?> clazz) {
        MessageDescriptor d = MessageDescriptorRegistry.getByClass(clazz);
        if (d != null) return d.msgId();
        throw new RuntimeException("Cannot resolve msgId for class: " + clazz.getName());
    }

    public static MessageMeta<?> resolveMetaByClass(Class<?> clazz) {
        MessageDescriptor d = MessageDescriptorRegistry.getByClass(clazz);
        if (d != null) return d.toMeta();
        throw new RuntimeException("Cannot resolve meta for class: " + clazz.getName());
    }
}

