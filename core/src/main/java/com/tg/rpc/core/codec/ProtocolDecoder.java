package com.tg.rpc.core.codec;

import com.tg.rpc.core.Serializer.ProtostuffSerializer;
import com.tg.rpc.core.Serializer.Serializer;
import com.tg.rpc.core.entity.Request;
import com.tg.rpc.core.entity.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by twogoods on 17/2/16.
 */
public class ProtocolDecoder extends LengthFieldBasedFrameDecoder {
    private Serializer serializer = new ProtostuffSerializer();
    private Class clazz;

    public ProtocolDecoder(int maxFrameLength,Class clazz) {
        super(maxFrameLength, 0, 4, 0, 4);
        this.clazz=clazz;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf decode = (ByteBuf) super.decode(ctx, in);
        if (decode != null) {
            int byteLength = decode.readableBytes();
            // TODO try to avoid data copy
            byte[] byteHolder = new byte[byteLength];
            decode.readBytes(byteHolder);
            Object deserialize = serializer.deserialize(byteHolder, clazz);
            return deserialize;
        }
        return null;
    }

    public static ProtocolDecoder serverDecoder(int maxFrameLength) {
        return new ProtocolDecoder(maxFrameLength,Request.class);
    }

    public static ProtocolDecoder clientDecoder(int maxFrameLength) {
        return new ProtocolDecoder(maxFrameLength,Response.class);
    }
}