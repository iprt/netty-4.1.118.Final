package org.iproute.eventloop;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

/**
 * ByteBufferTest
 *
 * @author tech@intellij.io
 * @since 2025-02-19
 */
public class ByteBufferTest {


    @Test
    public void testEmpty() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.flip();
        System.out.println(buffer.remaining());
    }


    @Test
    public void testWriteRead() {
        // 创建一个容量为 1024 字节的 ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        String data = "hello, world";
        // 写入数据到 ByteBuffer
        buffer.put(data.getBytes());

        // 切换到读模式
        buffer.flip();
        int remaining = buffer.remaining();

        byte[] bytes = new byte[remaining];
        // 读取数据到 byte 数组
        buffer.get(bytes);

        System.out.println(new String(bytes));

    }


}
