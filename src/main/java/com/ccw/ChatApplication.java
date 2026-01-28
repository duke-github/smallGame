package com.ccw;

import com.iohao.game.external.core.micro.PipelineContext;
import com.iohao.game.external.core.netty.DefaultExternalServer;
import com.iohao.game.external.core.netty.DefaultExternalServerBuilder;
import com.iohao.game.external.core.netty.micro.WebSocketMicroBootstrapFlow;
import com.iohao.game.external.core.netty.simple.NettyRunOne;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatApplication {
    public static void main(String[] args) {
        DefaultExternalServerBuilder builder = DefaultExternalServer.newBuilder(8002);
        builder.setting().setMicroBootstrapFlow(new WebSocketMicroBootstrapFlow() {
            public void pipelineFlow(PipelineContext context) {
                super.httpHandler(context);
                super.websocketHandler(context);
                context.addFirst(new SimpleChannelInboundHandler<TextWebSocketFrame>() {
                    static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        super.channelActive(ctx);
                        channelGroup.add(ctx.channel());
                    }

                    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
                        var message = String.format("[user:%s]:[%s]", ctx.channel().id().asShortText(), msg.text());
                        channelGroup.writeAndFlush(new TextWebSocketFrame(message));
                    }
                });
            }
        });

        new NettyRunOne()
                .setExternalServer(builder.build())
                .startup();
    }
}
