package net.moetang.iosample.multithread;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.nio.ByteBuffer;

/**
 * utils of using netty buffer as ByteBuffer
 */
public class NettyBufferUnit {
    private static final PooledByteBufAllocator allocator;

    static {
        allocator = new PooledByteBufAllocator(true);
    }

    private ByteBuf byteBuf;
    private ByteBuffer buffer;

    protected NettyBufferUnit(ByteBuf byteBuf, ByteBuffer buffer) {
        this.byteBuf = byteBuf;
        this.buffer = buffer;
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    // 1st step
    public static NettyBufferUnit allocate(int maxCap) {
        ByteBuf byteBuf = allocator.directBuffer(maxCap);
        byteBuf.writerIndex(maxCap);

        ByteBuffer buffer = byteBuf.nioBuffer();

        return new NettyBufferUnit(byteBuf, buffer);
    }

    // 2nd step - after read data to ByteBuffer
    public void prepareByteBufToProcess() {
        buffer.flip();
        byteBuf.readerIndex(0);
        byteBuf.writerIndex(buffer.limit());
        buffer.clear();
    }

    // optional 3rd step - to send data is in ByteBuf
    public static ByteBuffer prepareByteBufferToSend(ByteBuf buf) {
        return buf.nioBuffer();
    }

    // optional 4th step - release send buffer
    public static void releaseSendByteBuf(ByteBuf buf) {
        if (buf.refCnt() > 0) {
            buf.release();
        }
    }

    // 5th step - release buffer
    public void release() {
        ByteBuf buf = this.byteBuf;
        if (buf.refCnt() > 0) {
            buf.release();
        }
    }
}
