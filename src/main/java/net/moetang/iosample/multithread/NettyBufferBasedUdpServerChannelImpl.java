package net.moetang.iosample.multithread;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Objects;

/**
 * Created by sunsun on 14-6-29.
 */
public class NettyBufferBasedUdpServerChannelImpl implements IOChannel {
    private final DatagramChannel channel;

    public NettyBufferBasedUdpServerChannelImpl(DatagramChannel channel) {
        this.channel = Objects.requireNonNull(channel);
    }

    @Override
    public void write(ByteBuf o, SocketAddress address) {
        ByteBuffer buffer = NettyBufferUnit.prepareByteBufferToSend(o);
        try {
            this.channel.send(buffer, address);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            NettyBufferUnit.releaseSendByteBuf(o);
        }
    }
}
