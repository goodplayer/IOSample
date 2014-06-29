package net.moetang.iosample.multithread;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sunsun on 14-6-28.
 */
public class NettyBasedUdpReaderSample {
    public static void main(String[] args) throws IOException {

        DatagramChannel channel = DatagramChannel.open();
        channel.bind(new InetSocketAddress("0.0.0.0", 12345));
        channel.configureBlocking(true);

        int PARALLEL_LEVEL = Runtime.getRuntime().availableProcessors();
        System.out.println("# of core: " + PARALLEL_LEVEL);
        ExecutorService pool = Executors.newFixedThreadPool(PARALLEL_LEVEL);

        for (int i = 0; i < PARALLEL_LEVEL; i++) {
            final int finalI = i;
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        NettyBufferUnit unit = NettyBufferUnit.allocate(2048);
                        try {
                            SocketAddress raddr = channel.receive(unit.getBuffer());
                            if (raddr != null) {
                                unit.prepareByteBufToProcess();

                                // process and send
                                System.out.println("task " + finalI + ":" + Thread.currentThread() + " - " + raddr + " - " + unit.getByteBuf().readableBytes());

                                if (unit.getByteBuf().readByte() != 1 || unit.getByteBuf().readByte() != 2) {
                                    System.out.println("data error for task " + finalI);
                                }

                                ByteBuf sendBuf = unit.getByteBuf();
                                //get ByteBuffer then send
                                ByteBuffer sendBuffer = NettyBufferUnit.prepareByteBufferToSend(sendBuf);
                                NettyBufferUnit.releaseSendByteBuf(sendBuf);
                            } else {
                                System.out.println("addr is null for taks " + finalI);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        } finally {
                            unit.release();
                        }
                    }
                }
            });
        }
    }
}
