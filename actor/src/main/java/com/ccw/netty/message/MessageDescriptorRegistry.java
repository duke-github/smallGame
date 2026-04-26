package com.ccw.netty.message;

import com.ccw.business.handler.MessageHandler;
import com.ccw.business.message.HandlerType;
import com.ccw.business.serialize.MessageSerializer;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 扫描 HandlerType注解 一次，构建 msgId 和handler还有序列化器映射
 */
@Component
public class MessageDescriptorRegistry {

    public static final Map<Integer, MessageDescriptor> BY_MSG_ID = new HashMap<>();
    public static final Map<Class<?>, MessageDescriptor> BY_CLASS = new HashMap<>();

    public MessageDescriptorRegistry(ApplicationContext applicationContext) {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(HandlerType.class);
        for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
            MessageHandler<?> handlerBean = (MessageHandler<?>) entry.getValue();
            Class<?> handlerClass = AopUtils.getTargetClass(handlerBean);

            Class<?> messageClass = resolveMessageType(handlerClass);
            HandlerType handlerType = handlerClass.getAnnotation(HandlerType.class);
            RequestType requestType = handlerType.value();

            Class<? extends MessageSerializer> serializerClass = handlerType.serializer();
            MessageSerializer serializer = applicationContext.getBean(serializerClass);

            int msgId = requestType.getRequestType();
            MessageDescriptor descriptor = new MessageDescriptor(msgId, messageClass, serializer, handlerBean);

            BY_MSG_ID.put(msgId, descriptor);
            BY_CLASS.put(messageClass, descriptor);
        }
    }

    public static MessageDescriptor getByMsgId(int msgId) {
        return BY_MSG_ID.get(msgId);
    }

    public static MessageDescriptor getByClass(Class<?> clazz) {
        MessageDescriptor d = BY_CLASS.get(clazz);
        if (d != null) return d;
        for (Map.Entry<Class<?>, MessageDescriptor> e : BY_CLASS.entrySet()) {
            if (e.getKey().isAssignableFrom(clazz)) return e.getValue();
        }
        return null;
    }

    private Class<?> resolveMessageType(Class<?> handlerClass) {
        while (handlerClass != null) {
            Type[] interfaces = handlerClass.getGenericInterfaces();
            for (Type type : interfaces) {
                if (type instanceof ParameterizedType pt) {
                    if (pt.getRawType() == MessageHandler.class) {
                        return (Class<?>) pt.getActualTypeArguments()[0];
                    }
                }
            }
            handlerClass = handlerClass.getSuperclass();
        }
        throw new RuntimeException("Cannot resolve message type");
    }
}

