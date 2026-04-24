package com.ccw.netty.message;

import com.ccw.business.message.HeartBeat;

public enum RequestType {
    //REQUEST(0, "被封装的请求消息"),
    //RESPONSE(1, "被封装的返回消息"),
    HEART_BEAT(2, "心跳消息", HeartBeat.class),
    //LOGIN(1000, "登录")
    ;

    private int requestType;
    private String requestName;
    private final Class<?> clazz;

    RequestType(int requestType, String requestName, Class<?> clazz) {
        this.requestType = requestType;
        this.requestName = requestName;
        this.clazz = clazz;
    }

    public int getRequestType() {
        return requestType;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    ;
}
