package com.lbj.netty.rpc.client;

import com.lbj.netty.rpc.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lbj
 * @create 2023-07-01 11:34
 */
public class RpcClient {

    static RpcClientHandler rpcClientHandler;
    private static ExecutorService executorService = Executors.newFixedThreadPool(2);

    public RpcClient() {
        rpcClientHandler = new RpcClientHandler();
        initClient();
    }

    private void initClient() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                            .addLast(new StringEncoder())
                            .addLast(new StringDecoder())
                            .addLast(rpcClientHandler);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();
            System.out.println("连接到服务器");
        } catch (Exception e) {
            e.printStackTrace();
        } /*finally {       // 客户端不需要关闭，否则服务端接收不到消息
            workerGroup.shutdownGracefully();
        }*/
    }

    /**
     * 创建代理对象，该代理对象会通过netty去调用服务端的接口
     * 即模拟netty的异步编程（异步发请求->wait->结果到达->通知调用者获取->返回给客户端调用方）
     * @param clazz
     * @return
     */
    public <T> T getProxy(Class<T> clazz) {
        Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        rpcClientHandler.setParams((String) args[0]);

                        // 实际调用远程服务
                        Object result = executorService.submit(rpcClientHandler).get();

                        return result;
                    }
                });
        return (T)proxyInstance;
    }

    public static void main(String[] args) {
        // 创建消费者进程
        RpcClient rpcClient = new RpcClient();

        // 获得服务端本地代理接口
        HelloService helloService = rpcClient.getProxy(HelloService.class);

        for (int i=0;i<2;i++) {
            System.out.println("i="+i);
            String result = helloService.sayHello("rpc测试案例");

            System.out.println("result = " + result);
        }

    }
}
