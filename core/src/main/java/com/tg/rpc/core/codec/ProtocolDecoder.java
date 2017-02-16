package com.tg.rpc.core.codec;

import com.tg.rpc.core.Serializer.KryoSerializer;
import com.tg.rpc.core.Serializer.Serializer;
import com.tg.rpc.core.transport.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by twogoods on 17/2/16.
 */
public class ProtocolDecoder extends LengthFieldBasedFrameDecoder {

    private Serializer serializer = new KryoSerializer();
    public ProtocolDecoder(int maxFrameLength) {
        super(maxFrameLength, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf decode = (ByteBuf) super.decode(ctx, in);
        if (decode != null) {
            int byteLength = decode.readableBytes();
            // TODO try to avoid data copy
            byte[] byteHolder = new byte[byteLength];
            decode.readBytes(byteHolder);
            Object deserialize = serializer.deserialize(byteHolder, Response.class);
            return deserialize;
        }
        return null;
    }
}