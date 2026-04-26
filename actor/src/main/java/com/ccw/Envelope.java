package com.ccw;

import com.ccw.session.Session;

public class Envelope {
    private Object msg;
    private Session session;
    private int msgId;


    public Envelope(Object msg, Session session, int msgId) {
        this.msg = msg;
        this.session = session;
        this.msgId = msgId;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getActorId(){
        return session.getActorId();
    }
}
