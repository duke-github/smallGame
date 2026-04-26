package com.ccw.session;

import com.ccw.business.message.HeartBeat;
import io.netty.channel.Channel;

public class Session {
    private Channel channel;
    private String sessionId;
    private String playerId;
    private volatile long lastHeartBeat;


    public Session(Channel channel, String sessionId, long lastHeartBeat) {
        this.channel = channel;
        this.sessionId = sessionId;
        this.lastHeartBeat = lastHeartBeat;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public long getLastHeartBeat() {
        return lastHeartBeat;
    }

    public void setLastHeartBeat(long lastHeartBeat) {
        this.lastHeartBeat = lastHeartBeat;
    }

    public String getActorId() {
        return playerId == null ? sessionId : playerId;
    }

    public void send(Object msg) {
        channel.writeAndFlush(msg);
    }
}
