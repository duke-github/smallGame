package com.ccw.netty.handler;

import com.ccw.netty.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class SocketMessageDecoder extends ByteToMessageDecoder {

    public static final int LENGTH_OF_MESSAGE = 4;
    public static final int LENGTH_OF_HEAD = 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) {
        while (true) {
            //这里是预读一个int 代表接下来的body的长度
            int length = in.getInt(in.readerIndex());
            if (in.readableBytes() < LENGTH_OF_MESSAGE + length || length < LENGTH_OF_HEAD || length > 10 * 1024) {
                //半包检测  非法包丢弃
                return;
            }
            //跳过消息长度
            in.skipBytes(LENGTH_OF_MESSAGE);
            //读取消息体内容
            int msgId = in.readInt();
            int bodyLength = length - LENGTH_OF_HEAD;

            byte[] bytes = new byte[bodyLength];
            //将数据写入byte数组
            in.readBytes(bytes);

            Message message = new Message(msgId, bytes);
            out.add(message);
        }
    }
}
