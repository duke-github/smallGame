package com.ccw.netty.handler;

import com.ccw.netty.message.Message;
import com.ccw.netty.message.MessageMeta;
import com.ccw.netty.message.MessageRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageDispatcherHandler extends SimpleChannelInboundHandler {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        Message message = (Message) msg;
        MessageMeta<?> meta = MessageRegistry.REQUEST_TYP_MAP.get(message.getMsgId());
        if (meta == null) {
            throw new RuntimeException("类型不存在");
        }
        //todo IO线程同步处理玩家业务消息  拆分处理
        meta.handle(ctx, message.getBody());
    }
}
