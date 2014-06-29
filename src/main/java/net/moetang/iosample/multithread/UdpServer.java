package net.moetang.iosample.multithread;

import java.net.InetSocketAddress;

/**
 * Created by sunsun on 14-6-29.
 */
public class UdpServer extends AbstractNettyBufferBasedUdpServer {
    public UdpServer(int parallelLevel, InetSocketAddress listenAddr, IOTaskFactory taskFactory) {
        super(parallelLevel, listenAddr, taskFactory);
    }
}
