package com.ccw.business.serialize;

import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class JsonMessageSerializer implements MessageSerializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(bytes, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialize error, clazz=" + clazz, e);
        }
    }

    @Override
    public byte[] serialize(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialize error, obj=" + obj, e);
        }
    }
}