package com.ccw.session;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    public void onChannelActive(Channel channel) {
        Session session = new Session(channel, channel.id().asShortText(), System.currentTimeMillis());
        sessionMap.put(session.getSessionId(), session);
    }


    public void onChannelInActive(Channel channel) {
        sessionMap.remove(channel.id().asShortText());
    }

    public Session getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }
}
