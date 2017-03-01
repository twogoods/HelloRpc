package com.tg.rpc.core.handler.channel;

import com.tg.rpc.core.entity.QueueHolder;
import com.tg.rpc.core.entity.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;


/**
 * Created by twogoods on 17/2/16.
 */
public class ClientChannelHandler extends SimpleChannelInboundHandler<Response> {
    private static Logger log = LoggerFactory.getLogger(ClientChannelHandler.class);

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {
        BlockingQueue<Response> blockingQueue = QueueHolder.get(response.getRequestId());
        if (blockingQueue != null) {
            blockingQueue.put(response);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("error in channel:{} {}",ctx.channel(),cause);
    }
}
