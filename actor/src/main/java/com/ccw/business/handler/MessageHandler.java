package com.ccw.business.handler;

import com.ccw.session.Session;

public interface MessageHandler<T> {

    void handler(Session session, T message);
}
