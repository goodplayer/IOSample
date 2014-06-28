package net.moetang.iosample.multithread;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.nio.ByteBuffer;

/**
 * Created by sunsun on 14-6-28.
 */
public class NettyBasedByteBufSample {
    public static void main(String[] args) {
        // 1. allocate ByteBuf
        // 2. convert to ByteBuffer
        // 3. io
        // 4. process in ByteBuf

        PooledByteBufAllocator allocator = new PooledByteBufAllocator();

        ByteBuf byteBuf = allocator.directBuffer(1000);
        byteBuf.writerIndex(1000);

        byteBuf.markReaderIndex();
        byteBuf.readByte();
        System.out.println(byteBuf.readableBytes());
        System.out.println(byteBuf.writableBytes());
        byteBuf.resetReaderIndex();
        System.out.println(byteBuf.readableBytes());
        System.out.println(byteBuf.writableBytes());
        System.out.println("--------");

        ByteBuffer buffer = byteBuf.nioBuffer();
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        buffer.putInt(1);
        print(buffer, byteBuf);

        buffer.flip();
        print(buffer, byteBuf);

        buffer.clear();
        print(buffer, byteBuf);

        System.out.println(byteBuf.refCnt());
        byteBuf.release();
        System.out.println(byteBuf.refCnt());
        byteBuf.release();
    }

    private static void print(ByteBuffer buffer, ByteBuf buf) {
        System.out.println("========");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());
        System.out.println("--------");
        System.out.println(buf.readableBytes());
        System.out.println(buf.writableBytes());
        System.out.println("========");
    }
}
