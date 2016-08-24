package com.blade.kit.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blade.kit.IOKit;

/**
 * 非同步的ByteArrayOutputStream替换方案, 执行toByteArray() 方法时返回的是只读的内部字节数组, 避免了没有必要的字节复制. 本代码移植自IBM
 * developer works文章：
 * <ul>
 * <li><a href="http://www.ibm.com/developerworks/cn/java/j-io1/index.shtml">彻底转变流，第 1 部分：从输出流中读取</a>
 * <li><a href="http://www.ibm.com/developerworks/cn/java/j-io2/index.shtml">彻底转变流，第 2 部分：优化 Java 内部 I/O</a>
 * </ul>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class ByteArrayOutputStream extends OutputStream {

    // internal buffer
    private byte[] buffer;
    private int index;
    private int capacity;

    // is the stream closed?
    private boolean closed;

    // is the buffer shared?
    private boolean shared;

    public ByteArrayOutputStream() {
        this(IOKit.DEFAULT_BUFFER_SIZE);
    }

    public ByteArrayOutputStream(int initialBufferSize) {
        capacity = initialBufferSize;
        buffer = new byte[capacity];
    }

    @Override
    public void write(int datum) throws IOException {
        if (closed) {
            throw new IOException("Stream closed");
        }

        if (index >= capacity) {
            // expand the internal buffer
            capacity = capacity * 2 + 1;

            byte[] tmp = new byte[capacity];

            System.arraycopy(buffer, 0, tmp, 0, index);
            buffer = tmp;

            // the new buffer is not shared
            shared = false;
        }

        // store the byte
        buffer[index++] = (byte) datum;

    }

    @Override
    public void write(byte[] data, int offset, int length) throws IOException {
        if (data == null) {
            throw new NullPointerException();
        }
        if (offset < 0 || offset + length > data.length || length < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (closed) {
            throw new IOException("Stream closed");
        }

        if (index + length > capacity) {
            // expand the internal buffer
            capacity = capacity * 2 + length;

            byte[] tmp = new byte[capacity];

            System.arraycopy(buffer, 0, tmp, 0, index);
            buffer = tmp;

            // the new buffer is not shared
            shared = false;
        }

        // copy in the subarray
        System.arraycopy(data, offset, buffer, index, length);
        index += length;

    }

    @Override
    public void close() {
        closed = true;
    }

    public void writeTo(OutputStream out) throws IOException {
        // write the internal buffer directly
        out.write(buffer, 0, index);
    }

    public ByteArray toByteArray() {
        shared = true;
        return new ByteArray(buffer, 0, index);
    }

    public InputStream toInputStream() {
        // return a stream reading from the shared internal buffer
        shared = true;
        return new ByteArrayInputStream(buffer, 0, index);
    }

    public void reset() throws IOException {
        if (closed) {
            throw new IOException("Stream closed");
        }

        if (shared) {
            // create a new buffer if it is shared
            buffer = new byte[capacity];
            shared = false;
        }

        // reset index
        index = 0;

    }
}