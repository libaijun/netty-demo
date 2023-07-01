package com.lbj.netty.rpc.server;

import com.lbj.netty.rpc.HelloService;
import com.lbj.netty.rpc.impl.HelloServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 每个客户端connect过来都会创建一套新的handler,所以有状态的成员变量不适合保存在这里
 * @author lbj
 * @create 2023-07-01 11:02
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private HelloService helloService = new HelloServiceImpl();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有客户端来了：" + ctx.channel().remoteAddress() + " RpcServerHandler = " + this);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("RpcServerHandler = "+ this + " helloService = " + helloService + " pipeline= " + ctx.pipeline());    // 每个连接都是相同的handler，不同的channel会创建一个handler实例
        String replay = helloService.sayHello((String) msg);
        System.out.println("replay = " + replay);
        ctx.writeAndFlush(replay);  // 返回请求结果给到客户端
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }
}
