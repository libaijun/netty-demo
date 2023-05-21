package com.lbj.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author lbj
 * @create 2023-05-21 13:33
 */
public class GroupNioServer {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public GroupNioServer() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(9999));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() {
        while (true) {

            int select = 0;
            try {
                select = selector.select(2000);

                if (select > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey next = iterator.next();

                        // 用完事件就要删除掉
                        iterator.remove();

                        if (next.isAcceptable()) {
                            SocketChannel accept = serverSocketChannel.accept();
                            System.out.println(accept.getRemoteAddress() + " 上线了");
                            accept.configureBlocking(false);    // 设置非阻塞
                            accept.register(selector, SelectionKey.OP_READ);
                        }

                        if (next.isReadable()) {
                            readData(next);
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void readData(SelectionKey key) {

        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(20);

        try {
            int read = channel.read(buffer);
            if (read > 0) {
                String s = new String(buffer.array());
                System.out.println("来自客户端消息 " + s);

                // 转发消息
                sendMsgOtherClient(s, channel);
            }
        } catch (IOException e) {
            try {
                System.out.println(channel.getRemoteAddress() + " 离线了");
                key.cancel();
                channel.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private void sendMsgOtherClient(String msg, SocketChannel self) {
        for (SelectionKey key : selector.keys()) {
            Channel channel = key.channel();
            if (channel instanceof SocketChannel && channel != self) {
                SocketChannel target = (SocketChannel) channel;
                ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
                try {
                    target.write(byteBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("转发消息完毕："  + msg);
    }


    public static void main(String[] args) {
        GroupNioServer groupNioServer = new GroupNioServer();
        groupNioServer.listen();
    }


}
