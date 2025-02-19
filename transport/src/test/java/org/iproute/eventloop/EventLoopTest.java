package org.iproute.eventloop;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * EventLoopTest
 *
 * @author tech@intellij.io
 * @since 2025-02-18
 */
public class EventLoopTest {

    @Test
    public void testExample() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            group.next().schedule(() -> System.out.println("定时任务执行"), 1, TimeUnit.SECONDS);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        } finally {
            group.shutdownGracefully();
        }

    }

}
