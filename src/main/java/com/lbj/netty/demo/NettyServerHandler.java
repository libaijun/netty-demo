package com.lbj.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author: lbj
 * @create: 2019-12-19 20:37
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    // 读取的方法
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务器读线程： " + Thread.currentThread().getName());
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端发送消息是： " + buf.toString(CharsetUtil.UTF_8));
    }

    // 读取完毕的方法
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ByteBuf byteBuf = Unpooled.copiedBuffer("Hello Client...", CharsetUtil.UTF_8);
        ctx.writeAndFlush(byteBuf);
    }

    // 处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
