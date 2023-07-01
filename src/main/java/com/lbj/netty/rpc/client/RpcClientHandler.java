package com.lbj.netty.rpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @author lbj
 * @create 2023-07-01 11:24
 */
public class RpcClientHandler extends ChannelInboundHandlerAdapter implements Callable {
    /**
     * 用于保存上下文，以便call方法中使用
     */
    private ChannelHandlerContext context;
    private String params;
    private String result;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
        执行顺序为：
        1.channelActive
        2.setParams
        3.call
        4.channelRead
        5.call
     */

    /**
     * 该方法只会执行一次
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 建立连接，保存上下文
        this.context = ctx;
        System.out.println("保存上下文 context = " + ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 读取服务器返回数据msg,然后保存到成员变量result中，以便call方法调用
        this.result = (String) msg;
        System.out.println("channelRead接收服务器数据 result=" + msg);
        // 唤醒call方法
//        notify();
        countDownLatch.countDown();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 通过线程去调用rpc server的服务，相当于调用本地的接口一样
     * @return
     * @throws Exception
     */
    @Override
    public Object call() throws Exception {
        context.writeAndFlush(params);
//        wait(); // 等待至服务器返回结果，然后从channelRead方法中拿结果
        countDownLatch.await();
        return this.result;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
