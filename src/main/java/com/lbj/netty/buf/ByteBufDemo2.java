package com.lbj.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

/**
 * @author lbj
 * @create 2023-06-08 21:49
 */
public class ByteBufDemo2 {
    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.copiedBuffer("你好，hello!", Charset.forName("utf-8"));
        if (byteBuf.hasArray()) {
            byte[] array = byteBuf.array();

            int i = byteBuf.readableBytes();
            int i1 = byteBuf.arrayOffset();
            int i2 = byteBuf.readerIndex();
            int i3 = byteBuf.writerIndex();
            CharSequence charSequence = byteBuf.getCharSequence(1, 5, Charset.forName("utf-8"));
        }
    }
}
