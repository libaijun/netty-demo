package com.lbj.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author lbj
 * @create 2023-05-21 14:03
 */
public class GroupNioClient {
    private Selector selector;
    private SocketChannel socketChannel;
    private String clientName;
    private Integer PORT = 9999;
    private String HOST = "127.0.0.1";

    public GroupNioClient() {
        try {
            selector = Selector.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(HOST, PORT);
//            socketChannel = socketChannel.open(inetSocketAddress);
            socketChannel = SocketChannel.open(inetSocketAddress);
//            SocketChannel socketChannel = SocketChannel.open();

            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            clientName = socketChannel.getLocalAddress().toString();
            System.out.println(clientName + " is ok ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendInfo(String info) {
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readInfo() {
        int select = 0;
        try {
            select = selector.select(2000);
            if (select > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(20);
                        channel.read(buffer);
                        String msg = new String(buffer.array());
                        System.out.println("收到消息: " + msg);

                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        GroupNioClient groupNioClient = new GroupNioClient();
        // 读消息
        new Thread(()->{
            while (true) {
                groupNioClient.readInfo();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        // 发消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            groupNioClient.sendInfo(s);
        }
    }


}
