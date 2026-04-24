package com.ccw.business.handler;

import com.ccw.business.message.HeartBeat;
import io.netty.channel.ChannelHandlerContext;

public class HeartBeatHandler implements MessageHandler<HeartBeat> {
    @Override
    public void handler(ChannelHandlerContext ctx, HeartBeat heartBeat) {
        ctx.writeAndFlush("heartBeat成功");
    }
}
