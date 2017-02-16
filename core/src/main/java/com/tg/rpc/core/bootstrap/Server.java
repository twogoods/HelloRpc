package com.tg.rpc.core.bootstrap;

import com.tg.rpc.core.codec.ProtocolDecoder;
import com.tg.rpc.core.codec.ProtocolEncoder;
import com.tg.rpc.core.exception.ValidateException;
import com.tg.rpc.core.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by twogoods on 17/2/16.
 */
public class Server {
    private static Logger log = LogManager.getLogger(Server.class);

    private int port;
    private int maxCapacity;

    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Server(int port, int maxCapacity) {
        this.port = port;
        this.maxCapacity = maxCapacity;
    }

    public static class Builder {
        private int port;
        private int maxCapacity;

        public Server.Builder port(int port) throws ValidateException {
            if (port <= 0) {
                throw new ValidateException("port can't be negative");
            }
            this.port = port;
            return this;
        }

        public Server.Builder maxCapacity(int maxCapacity) throws ValidateException {
            if (maxCapacity <= 0) {
                throw new ValidateException("maxCapacity can't be negative");
            }
            this.maxCapacity = maxCapacity;
            return this;
        }

        public Server build() {
            return new Server(port, maxCapacity);
        }
    }

    public void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
                                    .addLast(new ProtocolDecoder(maxCapacity * 1024 * 1024))
                                    .addLast(new ProtocolEncoder())
                                    .addLast(new ServerHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(port).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("server start failed!", e);
        }
    }

    private void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
