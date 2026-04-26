package com.ccw.business.handler;

import com.ccw.business.message.HandlerType;
import com.ccw.business.message.HeartBeat;
import com.ccw.netty.message.RequestType;
import com.ccw.session.Session;
import io.netty.util.AttributeKey;

@HandlerType(value = RequestType.HEART_BEAT)
public class HeartBeatHandler implements MessageHandler<HeartBeat> {
    @Override
    public void handler(Session session, HeartBeat heartBeat) {

        long now = System.currentTimeMillis();
        // 更新时间
        session.getChannel().attr(AttributeKey.valueOf("lastHeartBeat")).set(now);
        // 回填 serverTime
        heartBeat.setServerTime(now);
        // 直接回写
        session.send(heartBeat);
    }
}
