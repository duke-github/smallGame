package com.ccw;

import com.ccw.netty.handler.MessageDispatcherHandler;
import com.ccw.netty.handler.SocketMessageDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NettyServer {

    private EventLoopGroup boss;
    private EventLoopGroup worker;

    @Value("${netty.port}")
    private int nettyPort;
    @Value("${netty.path}")
    private String path;

    @PostConstruct
    public void start() {
        new Thread(() -> {
            boss = new NioEventLoopGroup(1);
            worker = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(boss, worker)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(new SocketMessageDecoder()).addLast(new MessageDispatcherHandler());
                            }
                        });

                ChannelFuture future = bootstrap.bind(nettyPort).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @PreDestroy
    public void stop() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}
