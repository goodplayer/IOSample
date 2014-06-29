package net.moetang.iosample.multithread;

import io.netty.buffer.ByteBuf;
import io.netty.util.ResourceLeakDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by sunsun on 14-6-29.
 */
public class ServerSample {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSample.class);

    public static void main(String[] args) throws InterruptedException {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

        UdpServer udpServer = new UdpServer(
                Runtime.getRuntime().availableProcessors(),
                new InetSocketAddress(12345),
                new IOTaskFactory() {
                    @Override
                    public IOTask getTask() {
                        return new IOTask() {
                            @Override
                            public void process(ByteBuf buffer, SocketAddress remoteAddr, IOChannel channel) {
//                                System.out.println("recv data. readable_bytes=[" + buffer + "], addr=[" + remoteAddr + "], thread=[" + Thread.currentThread() + "]");
                            }
                        };
                    }
                }
        );

        udpServer.start();

        udpServer.sync();

        System.out.println("stopped");
    }
}
