package com.ccw;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
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
            System.out.println(path);
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(boss, worker)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(new HttpServerCodec())
                                        .addLast(new HttpObjectAggregator(65536))
                                        .addLast(new WebSocketServerProtocolHandler(path));
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
