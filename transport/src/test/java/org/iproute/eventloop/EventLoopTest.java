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

    @Test
    public void testNIO() {
        Selector selector = null;
        ServerSocketChannel serverSocketChannel = null;
        try {
            // 创建 Selector
            selector = Selector.open();

            // 打开 ServerSocketChannel 并绑定端口
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(12345));
            // 设置为非阻塞模式
            serverSocketChannel.configureBlocking(false);

            // 将 ServerSocketChannel 注册到 Selector，关注连接请求
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Echo Server 已启动，监听端口 12345...");

            // 事件循环
            while (true) {
                // 阻塞等待事件，当至少一个事件就绪时返回
                int num = selector.select();
                if (num == 0) {
                    continue;
                }
                // 获取所有就绪事件的 SelectionKey
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    // 移除当前 key，防止重复处理
                    iterator.remove();

                    if (key.isAcceptable()) {
                        // 处理连接事件
                        SocketChannel socketChannel;
                        try (ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel()) {
                            socketChannel = ssChannel.accept();
                        }
                        if (socketChannel != null) {
                            socketChannel.configureBlocking(false);
                            // 注册读事件
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            System.out.println("新客户端连接：" + socketChannel.getRemoteAddress());
                        }
                    } else if (key.isReadable()) {
                        // 处理读事件
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int bytesRead = socketChannel.read(buffer);

                        if (bytesRead > 0) {
                            buffer.flip();
                            // Echo 将数据原样返回给客户端
                            socketChannel.write(buffer);
                            buffer.clear();
                        } else if (bytesRead < 0) {
                            // 当客户端关闭连接时
                            System.out.println("客户端断开：" + socketChannel.getRemoteAddress());
                            socketChannel.close();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭 Selector 和 ServerSocketChannel
            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (serverSocketChannel != null) {
                try {
                    serverSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
