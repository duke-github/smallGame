package com.ccw.message;

public class Message {
    private int length;
    private int msgId;
    private byte[] body;

    public Message(int msgId,byte[] bytes) {
        this.msgId = msgId;
        this.body =bytes;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
