package com.lbj.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author lbj
 * @create 2023-06-08 21:49
 */
public class ByteBufDemo {
    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.buffer(10);// UnpooledByteBufAllocator$InstrumenteUnpooledUnsafeHeapByteBuf

        for (int i = 0; i < byteBuf.capacity(); i++) {
            byteBuf.writeByte(i);
        }
        /*
        readerIndex
        writerIndex
         */
        for (int i = 0; i < byteBuf.capacity(); i++) {
            System.out.println(byteBuf.readByte());
        }
    }
}
