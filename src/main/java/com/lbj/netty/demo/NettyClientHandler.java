package com.lbj.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author: lbj
 * @create: 2019-12-19 21:16
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    // 当客户端连接服务器完成就会调用该方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf buf = Unpooled.copiedBuffer("Hello Server", CharsetUtil.UTF_8);
        ctx.writeAndFlush(buf);
    }

    // 当通道有读取事件发生时，会触发该方法。即服务器发送过来数据会触发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("收到服务器发来的消息： " + buf.toString(CharsetUtil.UTF_8));
        System.out.println("服务端地址： " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
