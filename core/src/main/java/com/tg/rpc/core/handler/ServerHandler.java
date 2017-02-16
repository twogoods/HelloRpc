package com.tg.rpc.core.handler;

import com.tg.rpc.core.transport.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by twogoods on 17/2/16.
 */
public class ServerHandler extends SimpleChannelInboundHandler<Request> {
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //TOOD
        ctx.channel().close();
    }
}
