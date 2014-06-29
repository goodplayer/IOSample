package net.moetang.iosample.multithread;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sunsun on 14-6-29.
 */
public abstract class AbstractNettyBufferBasedUdpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNettyBufferBasedUdpServer.class);

    private final ExecutorService executorService;
    private final DatagramChannel channel;
    private final IOChannel ioChannel;
    private final InetSocketAddress listenAddr;

    // for performance, not use volatile
    private boolean STOP = false;

    private final CountDownLatch stopLatch = new CountDownLatch(1);

    protected AbstractNettyBufferBasedUdpServer(
            int parallelLevel,
            InetSocketAddress listenAddr,
            IOTaskFactory taskFactory
    ) {
        this.executorService = Executors.newFixedThreadPool(parallelLevel);
        this.listenAddr = listenAddr;

        try {
            this.channel = DatagramChannel.open();
            this.channel.configureBlocking(true);
        } catch (IOException e) {
            LOGGER.error("open channel error", e);
            throw new RuntimeException("init channel error!");
        }

        this.ioChannel = new NettyBufferBasedUdpServerChannelImpl(this.channel);

        for (int i = 0; i < parallelLevel; i++) {
            final int finalI = i;
            IOTask task = taskFactory.getTask();
            this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (STOP) {
                            break;
                        }

                        NettyBufferUnit unit = NettyBufferUnit.allocate(2048);
                        try {
                            SocketAddress raddr = channel.receive(unit.getBuffer());
                            if (raddr != null) {
                                unit.prepareByteBufToProcess();

                                ByteBuf recvByteBuf = unit.getByteBuf();

                                // process and send
                                task.process(recvByteBuf, raddr, ioChannel);
                            } else {
                                LOGGER.warn("remote address is null.");
                            }
                        } catch (IOException e) {
                            LOGGER.error("io exception occurs. stop task #" + finalI + ".", e);
                            throw new RuntimeException("io exception occurs.");
                        } finally {
                            unit.release();
                        }
                    }
                }
            });
        }

    }

    public void start() {
        try {
            this.channel.bind(this.listenAddr);
        } catch (IOException e) {
            LOGGER.error("start listening error", e);
            throw new RuntimeException("start listening error");
        }
    }

    public void stop() {
        try {
            this.STOP = true;
            this.channel.close();
            this.executorService.shutdown();
            this.stopLatch.countDown();
        } catch (IOException e) {
            LOGGER.error("stop service error!", e);
        }
    }

    public void sync() throws InterruptedException {
        this.stopLatch.await();
    }

}
