package com.tg.rpc.core.codec;

import com.tg.rpc.core.Serializer.ProtostuffSerializer;
import com.tg.rpc.core.Serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by twogoods on 17/2/16.
 */
public class ProtocolEncoder extends MessageToByteEncoder<Object> {
    private Serializer serializer = new ProtostuffSerializer();

    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] bytes = serializer.serialize(msg);
        int length = bytes.length;
        out.writeInt(length);
        out.writeBytes(bytes);
    }
}