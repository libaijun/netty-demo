package com.lbj.netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author lbj
 * @create 2023-06-18 15:57
 */
public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 发生了事件时，会调用该方法
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 判断是否为
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                System.out.println("指定时间范围内无读事件");
            } else if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                System.out.println("指定时间范围内无写事件");
            } else {
                System.out.println("指定时间范围内无读、写事件");
            }
        }
    }


}
