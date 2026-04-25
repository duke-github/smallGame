package com.ccw.business.message;

public class HeartBeat {

    // 客户端时间（请求用）
    private long clientTime;

    // 服务端时间（响应用）
    private long serverTime;

    public long getClientTime() {
        return clientTime;
    }

    public void setClientTime(long clientTime) {
        this.clientTime = clientTime;
    }

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    @Override
    public String toString() {
        return "HeartBeat{" +
                "clientTime=" + clientTime +
                ", serverTime=" + serverTime +
                '}';
    }
}