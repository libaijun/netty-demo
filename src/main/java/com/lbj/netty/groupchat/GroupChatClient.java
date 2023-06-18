package com.lbj.netty.groupchat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @author lbj
 * @create 2023-06-18 11:13
 */
public class GroupChatClient {
    private int port;
    private String host;

    public GroupChatClient(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void start() {
        EventLoopGroup eventExecutors = new NioEventLoopGroup();    // 默认核心数x2
        Bootstrap bootstrap = new Bootstrap();

        try {


            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("encoder", new StringEncoder())
                                    .addLast("decoder", new StringDecoder())
                                    .addLast(new GroupChatClientHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();

            Channel channel = channelFuture.channel();
            System.out.println("client start=====>" + channel.localAddress());

            // 键盘输入
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                channel.writeAndFlush(msg + "\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        GroupChatClient client = new GroupChatClient(8888, "localhost");
        client.start();
    }

}
