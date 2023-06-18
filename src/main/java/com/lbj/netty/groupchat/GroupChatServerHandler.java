package com.lbj.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author lbj
 * @create 2023-06-18 10:50
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler {
    /**
     * netty自带的，用来保存所有客户端的channel 
     */
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 读取客户端发来的消息，然后要实现转发给其他客户端
     * @param channelHandlerContext
     * @param o
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        Channel channel = channelHandlerContext.channel();

        for (Channel channel1 : channels) {
            if (channel != channel1) {
                channel1.writeAndFlush("[客户]" + channel1.remoteAddress() + " 发送了消息: " + o + " \n");
            } else {
                channel.writeAndFlush("[自己]发送了消息: " + o  + "\n");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();    // 关闭通道
    }

    /**
     * 客户端连接建立，第一个被执行的方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 1.通知其他客户端，有新用户上线了
        // channels.writeAndFlush方法会遍历集合中所有的channel并发送消息
        channels.writeAndFlush("[客户端]" + channel.remoteAddress() + " 加入聊天\n");
        // 2.保存到全局变量里
        channels.add(channel);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 上线了\n");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 离线了\n");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channels.writeAndFlush("[客户端]" + ctx.channel().remoteAddress() + " 离开了\n");
        System.out.println("groupChannel size=" + channels.size());
    }
}
