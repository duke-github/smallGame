package com.ccw.business.handler;

import io.netty.channel.ChannelHandlerContext;

public interface MessageHandler<T> {

    void handler(ChannelHandlerContext ctx, T message);
}
