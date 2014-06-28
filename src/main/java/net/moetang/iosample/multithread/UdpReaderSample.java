package net.moetang.iosample.multithread;

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
public class UdpReaderSample {
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
                    ByteBuffer buffer = ByteBuffer.allocateDirect(2000);
                    buffer.clear();
                    while (true) {
                        try {
                            SocketAddress raddr = channel.receive(buffer);
                            if (raddr != null) {
                                buffer.flip();
                                // process and send
                                System.out.println("task " + finalI + " - " + raddr);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        } finally {
                            buffer.clear();
                        }
                    }
                }
            });
        }
    }

}
