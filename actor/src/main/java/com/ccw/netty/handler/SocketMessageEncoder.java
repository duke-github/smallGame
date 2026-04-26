package com.ccw.netty.handler;

import com.ccw.netty.message.Message;
import com.ccw.netty.message.MessageMeta;
import com.ccw.netty.message.MessageCodecRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class SocketMessageEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        int msgId;
        byte[] bodyBytes;

        // 允许业务层直接写 Message（已经是 msgId + body 的形式）
        if (msg instanceof Message m) {
            msgId = m.getMsgId();
            bodyBytes = m.getBody() == null ? new byte[0] : m.getBody();
        } else {
            // 业务对象 -> (registry 找到 msgId + 对应 serializer) -> body bytes
            msgId = MessageCodecRegistry.resolveMsgIdByClass(msg.getClass());
            MessageMeta<?> meta = MessageCodecRegistry.resolveMetaByClass(msg.getClass());
            bodyBytes = MessageMeta.serializeAny(meta, msg);
        }

        // 协议：length(int32) + msgId(int32) + body
        int length = SocketMessageDecoder.LENGTH_OF_HEAD + bodyBytes.length;
        out.writeInt(length);
        out.writeInt(msgId);
        out.writeBytes(bodyBytes);
    }
}
