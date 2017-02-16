package com.tg.rpc.core.handler;

import com.tg.rpc.core.transport.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * Created by twogoods on 17/2/16.
 */
public class ClientHandler extends SimpleChannelInboundHandler<Response> {


    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //TOOD
        ctx.channel().close();
    }
}
