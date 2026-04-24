package com.ccw.netty.message;

import com.ccw.business.handler.HeartBeatHandler;
import com.ccw.business.message.HeartBeat;
import com.ccw.business.serialize.JsonMessageSerializer;

import java.util.HashMap;
import java.util.Map;

public class MessageRegistry {

    public static final Map<Integer, MessageMeta<?>>  REQUEST_TYP_MAP = new HashMap<>();


    static {
        REQUEST_TYP_MAP.put(RequestType.HEART_BEAT.getRequestType(), new MessageMeta<>(HeartBeat.class, new HeartBeatHandler(), new JsonMessageSerializer()));
    }
}
