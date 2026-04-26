package com.ccw.netty.handler;

import com.ccw.Envelope;
import com.ccw.netty.message.Message;
import com.ccw.netty.message.MessageMeta;
import com.ccw.netty.message.MessageCodecRegistry;
import com.ccw.session.Session;
import com.ccw.session.SessionManager;
import com.ccw.shard.ShardSystem;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageDispatcherHandler extends SimpleChannelInboundHandler {

    @Autowired
    private ShardSystem shardSystem;
    @Autowired
    private SessionManager sessionManager;

    //从netty消息转化为内部消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        Message message = (Message) msg;
        MessageMeta<?> meta = MessageCodecRegistry.getMetaByMsgId(message.getMsgId());
        if (meta == null) {
            throw new RuntimeException("类型不存在");
        }
        Session session = sessionManager.getSession(ctx.channel().id().asShortText());
        Envelope envelope = new Envelope(meta.deserialize(message), session, message.getMsgId());
        shardSystem.doDispatcher(envelope);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        sessionManager.onChannelActive(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        sessionManager.onChannelInActive(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        sessionManager.onChannelInActive(ctx.channel());
        super.exceptionCaught(ctx, cause);
    }
}
