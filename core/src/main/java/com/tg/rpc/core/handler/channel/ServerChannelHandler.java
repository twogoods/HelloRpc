package com.tg.rpc.core.handler.channel;

import com.tg.rpc.core.entity.Request;
import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.handler.response.ResponseHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by twogoods on 17/2/16.
 */
public class ServerChannelHandler extends SimpleChannelInboundHandler<Request> {
    private static Logger log = LogManager.getLogger(ServerChannelHandler.class);

    private ResponseHandler responseHandler;

    public ServerChannelHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
        log.info("get request {}",request);
        Response response = responseHandler.handle(request);
        channelHandlerContext.pipeline().writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //TOOD
//        ctx.channel().close();
    }
}
