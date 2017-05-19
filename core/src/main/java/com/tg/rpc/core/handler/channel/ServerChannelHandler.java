package com.tg.rpc.core.handler.channel;

import com.tg.rpc.core.entity.Request;
import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.handler.response.ResponseHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by twogoods on 17/2/16.
 */
public class ServerChannelHandler extends SimpleChannelInboundHandler<Request> {

    private static Logger log = LoggerFactory.getLogger(ServerChannelHandler.class);
    private ResponseHandler responseHandler;

    public ServerChannelHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("{} in channel:{}", cause, ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
        Response response = responseHandler.handle(request);
        log.debug("server get request:{}, return response:{}", request, response);
        channelHandlerContext.pipeline().writeAndFlush(response);
    }
}
