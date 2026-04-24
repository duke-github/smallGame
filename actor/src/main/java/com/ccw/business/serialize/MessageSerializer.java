package com.ccw.business.serialize;

public interface MessageSerializer {

    <T>T deserialize(byte[] bytes, Class<T> clazz);
}
