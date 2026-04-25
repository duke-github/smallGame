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

@Component
public class MessageRegistry {

    public static final Map<Integer, MessageMeta<?>> REQUEST_TYP_MAP = new HashMap<>();
    public static final Map<Class<?>, Integer> CLASS_TO_MSG_ID = new HashMap<>();
    public static final Map<Class<?>, MessageMeta<?>> CLASS_TO_META = new HashMap<>();

    public MessageRegistry(ApplicationContext applicationContext) {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(HandlerType.class);
        for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
            //获取bean并转化
            MessageHandler<?> value = (MessageHandler<?>) entry.getValue();
            //获取bean的class
            Class<?> clazz = AopUtils.getTargetClass(value);
            //获取这个bean绑定的泛型class
            Class<?> messageClass = resolveMessageType(clazz);
            //获取对应的消息类型
            HandlerType handlerType = clazz.getAnnotation(HandlerType.class);
            //获取对应的请求类型
            RequestType requestType = handlerType.value();

            //获取序列化工具
            Class<? extends MessageSerializer> serializerClass =
                    handlerType.serializer();
            MessageSerializer serializer =
                    applicationContext.getBean(serializerClass);

            MessageMeta<?> meta = new MessageMeta<>(messageClass, (MessageHandler) value, serializer);
            REQUEST_TYP_MAP.put(requestType.getRequestType(), meta);
            CLASS_TO_MSG_ID.put(messageClass, requestType.getRequestType());
            CLASS_TO_META.put(messageClass, meta);
        }
    }

    public static int resolveMsgIdByClass(Class<?> clazz) {
        Integer id = CLASS_TO_MSG_ID.get(clazz);
        if (id != null) return id;
        for (Map.Entry<Class<?>, Integer> e : CLASS_TO_MSG_ID.entrySet()) {
            if (e.getKey().isAssignableFrom(clazz)) return e.getValue();
        }
        throw new RuntimeException("Cannot resolve msgId for class: " + clazz.getName());
    }

    public static MessageMeta<?> resolveMetaByClass(Class<?> clazz) {
        MessageMeta<?> meta = CLASS_TO_META.get(clazz);
        if (meta != null) return meta;
        for (Map.Entry<Class<?>, MessageMeta<?>> e : CLASS_TO_META.entrySet()) {
            if (e.getKey().isAssignableFrom(clazz)) return e.getValue();
        }
        throw new RuntimeException("Cannot resolve meta for class: " + clazz.getName());
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
