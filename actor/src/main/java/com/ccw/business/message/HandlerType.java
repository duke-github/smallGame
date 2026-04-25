package com.ccw.business.message;

import com.ccw.business.serialize.JsonMessageSerializer;
import com.ccw.business.serialize.MessageSerializer;
import com.ccw.netty.message.RequestType;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface HandlerType {
    RequestType value();
    Class<? extends MessageSerializer> serializer()
            default JsonMessageSerializer.class;
}
