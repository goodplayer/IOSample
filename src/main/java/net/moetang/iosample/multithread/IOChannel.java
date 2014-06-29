package net.moetang.iosample.multithread;

import io.netty.buffer.ByteBuf;

import java.net.SocketAddress;

/**
 * Created by sunsun on 14-6-29.
 */
public interface IOChannel {
    public void write(ByteBuf o, SocketAddress address);
}
