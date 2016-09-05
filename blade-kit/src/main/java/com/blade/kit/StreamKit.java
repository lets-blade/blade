/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.kit;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import com.blade.kit.io.ByteArray;
import com.blade.kit.io.ByteArrayOutputStream;
import com.blade.kit.io.FastByteArrayOutputStream;

/**
 * 基于流的工具类
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public abstract class StreamKit {

    /**
     * 从输入流读取内容, 写入到输出流中. 此方法使用大小为8192字节的默认的缓冲区.
     * 
     * @param in 输入流
     * @param out 输出流
     * 
     * @throws IOException 输入输出异常
     */
    public static void io(InputStream in, OutputStream out) throws IOException {
        io(in, out, -1);
    }

    /**
     * 从输入流读取内容, 写入到输出流中. 使用指定大小的缓冲区.
     * 
     * @param in 输入流
     * @param out 输出流
     * @param bufferSize 缓冲区大小(字节数)
     * 
     * @throws IOException 输入输出异常
     */
    public static void io(InputStream in, OutputStream out, int bufferSize) throws IOException {
        if (bufferSize == -1) {
            bufferSize = IOKit.DEFAULT_BUFFER_SIZE;
        }

        byte[] buffer = new byte[bufferSize];
        int amount;
        
        while ((amount = in.read(buffer)) >= 0) {
            out.write(buffer, 0, amount);
        }

        out.flush();
    }

    /**
     * 从输入流读取内容, 写入到输出流中. 此方法使用大小为8192字符的默认的缓冲区.
     * 
     * @param in 输入流
     * @param out 输出流
     * 
     * @throws IOException 输入输出异常
     */
    public static void io(Reader in, Writer out) throws IOException {
        io(in, out, -1);
    }

    /**
     * 从输入流读取内容, 写入到输出流中. 使用指定大小的缓冲区.
     * 
     * @param in 输入流
     * @param out 输出流
     * @param bufferSize 缓冲区大小(字符数)
     * 
     * @throws IOException 输入输出异常
     */
    public static void io(Reader in, Writer out, int bufferSize) throws IOException {
        if (bufferSize == -1) {
            bufferSize = IOKit.DEFAULT_BUFFER_SIZE >> 1;
        }

        char[] buffer = new char[bufferSize];
        int amount;

        while ((amount = in.read(buffer)) >= 0) {
            out.write(buffer, 0, amount);
        }

        out.flush();
    }

    /**
     * 从输入流读取内容, 写入到输出流中. 此方法使用大小为8192字节的默认的缓冲区.
     * 
     * @param in 输入流
     * @param out 输出流
     * @param closeIn 是否关闭输入流
     * @param closeOut 是否关闭输出流
     * @throws IOException 输入输出异常
     */
    public static void io(InputStream in, OutputStream out, boolean closeIn, boolean closeOut) throws IOException {

        try {
            io(in, out);
        } finally {
            if (closeIn) {
                close(in);
            }

            if (closeOut) {
                close(out);
            }
        }
    }

    /**
     * 从输入流读取内容, 写入到输出流中. 此方法使用大小为8192字节的默认的缓冲区.
     * 
     * @param in 输入流
     * @param out 输出流
     * @param closeIn 是否关闭输入流
     * @param closeOut 是否关闭输出流
     * @throws IOException 输入输出异常
     */
    public static void io(Reader in, Writer out, boolean closeIn, boolean closeOut) throws IOException {
        try {
            io(in, out);
        } finally {
            if (closeIn) {
                close(in);
            }

            if (closeOut) {
                close(out);
            }
        }
    }

    /** 从输入流读取内容, 写入到目标文件 */
    public static void io(InputStream in, File dest) throws IOException {
        OutputStream out = new FileOutputStream(dest);
        io(in, out);
    }

    /** 从输入流读取内容, 写入到目标文件 */
    public static void io(InputStream in, File dest, boolean closeIn, boolean closeOut) throws IOException {
        OutputStream out = new FileOutputStream(dest);
        try {
            io(in, out);
        } finally {
            if (closeIn) {
                close(in);
            }

            if (closeOut) {
                close(out);
            }
        }
    }

    /** 从输入流读取内容, 写入到目标文件 */
    public static void io(InputStream in, String dest) throws IOException {
        OutputStream out = new FileOutputStream(dest);
        io(in, out);
    }

    /** 从输入流读取内容, 写入到目标文件 */
    public static void io(InputStream in, String dest, boolean closeIn, boolean closeOut) throws IOException {
        OutputStream out = new FileOutputStream(dest);
        try {
            io(in, out);
        } finally {
            if (closeIn) {
                close(in);
            }

            if (closeOut) {
                close(out);
            }
        }
    }

    /** 从输入流读取内容, 写入到目标文件 */
    public static void io(Reader in, File dest) throws IOException {
        Writer out = new FileWriter(dest);
        io(in, out);
    }

    /** 从输入流读取内容, 写入到目标文件 */
    public static void io(Reader in, File dest, boolean closeIn, boolean closeOut) throws IOException {
        Writer out = new FileWriter(dest);
        try {
            io(in, out);
        } finally {
            if (closeIn) {
                close(in);
            }

            if (closeOut) {
                close(out);
            }
        }
    }

    /** 从输入流读取内容, 写入到目标文件 */
    public static void io(Reader in, String dest) throws IOException {
        Writer out = new FileWriter(dest);
        io(in, out);
    }

    /** 从输入流读取内容, 写入到目标文件 */
    public static void io(Reader in, String dest, boolean closeIn, boolean closeOut) throws IOException {
        Writer out = new FileWriter(dest);
        try {
            io(in, out);
        } finally {
            if (closeIn) {
                close(in);
            }

            if (closeOut) {
                close(out);
            }
        }
    }

    /**
     * 取得同步化的输出流.
     * 
     * @param out 要包裹的输出流
     * 
     * @return 线程安全的同步化输出流
     */
    public static OutputStream synchronizedOutputStream(OutputStream out) {
        return new SynchronizedOutputStream(out);
    }

    /**
     * 取得同步化的输出流.
     * 
     * @param out 要包裹的输出流
     * @param lock 同步锁
     * 
     * @return 线程安全的同步化输出流
     */
    public static OutputStream synchronizedOutputStream(OutputStream out, Object lock) {
        return new SynchronizedOutputStream(out, lock);
    }

    /**
     * 将指定输入流的所有文本全部读出到一个字符串中.
     * 
     * @param in 要读取的输入流
     * 
     * @return 从输入流中取得的文本
     * 
     * @throws IOException 输入输出异常
     */
    public static String readText(InputStream in) throws IOException {
        return readText(in, null, -1);
    }

    /**
     * 将指定输入流的所有文本全部读出到一个字符串中.
     * 
     * @param in 要读取的输入流
     * @param encoding 文本编码方式
     * 
     * @return 从输入流中取得的文本
     * 
     * @throws IOException 输入输出异常
     */
    public static String readText(InputStream in, String encoding) throws IOException {
        return readText(in, encoding, -1);
    }

    /**
     * 将指定输入流的所有文本全部读出到一个字符串中.
     * 
     * @param in 要读取的输入流
     * @param charset 文本编码方式
     * @param bufferSize 缓冲区大小(字符数)
     * 
     * @return 从输入流中取得的文本
     * 
     * @throws IOException 输入输出异常
     */
    public static String readText(InputStream in, String charset, int bufferSize) throws IOException {
        Reader reader = (charset == null) ? new InputStreamReader(in) : new InputStreamReader(in, charset);

        return readText(reader, bufferSize);
    }

    /**
     * 将指定输入流的所有文本全部读出到一个字符串中.
     * 
     * @param in 要读取的输入流
     * @param charset 文本编码方式
     * @param closeIn 是否关闭输入流
     * @return 从输入流中取得的文本
     * @throws IOException 输入输出异常
     */
    public static String readText(InputStream in, String charset, boolean closeIn) throws IOException {
        Reader reader = charset == null ? new InputStreamReader(in) : new InputStreamReader(in, charset);

        return readText(reader, closeIn);
    }

    /**
     * 将指定输入流的所有文本全部读出到一个字符串中.
     * 
     * @param in 要读取的输入流
     * @param closeIn 是否关闭输入流
     * @return 从输入流中取得的文本
     * @throws IOException 输入输出异常
     */
    public static String readText(Reader in, boolean closeIn) throws IOException {
        StringWriter out = new StringWriter();

        io(in, out, closeIn, true);

        return out.toString();
    }

    /**
     * 将指定Reader的所有文本全部读出到一个字符串中.
     * 
     * @param reader 要读取的Reader
     * 
     * @return 从Reader中取得的文本
     * 
     * @throws IOException 输入输出异常
     */
    public static String readText(Reader reader) throws IOException {
        return readText(reader, -1);
    }

    /**
     * 将指定Reader的所有文本全部读出到一个字符串中.
     * 
     * @param reader 要读取的Reader
     * @param bufferSize 缓冲区的大小(字符数)
     * 
     * @return 从Reader中取得的文本
     * 
     * @throws IOException 输入输出异常
     */
    public static String readText(Reader reader, int bufferSize) throws IOException {
        StringWriter writer = new StringWriter();

        io(reader, writer, bufferSize);

        return writer.toString();
    }

    /**
     * 将指定InputStream的所有内容全部读出到一个byte数组中。
     * 
     * @param in 要读取的InputStream
     * @return ByteArray # @see ByteArray
     * @throws IOException
     */
    public static ByteArray readBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        io(in, out);

        return out.toByteArray();
    }

    /** 将指定InputStream的所有内容全部读出到一个byte数组中。 */
    public static ByteArray readBytes(InputStream in, boolean closeIn) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        io(in, out, closeIn, true);

        return out.toByteArray();
    }

    /**
     * 将指定File的所有内容全部读出到一个byte数组中。
     * 
     * @param file 要读取的文件
     * @return ByteArray # @see ByteArray
     * @throws IOException
     */
    public static ByteArray readBytes(File file) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        io(new FileInputStream(file), out);

        return out.toByteArray();
    }

    /** 将指定File的所有内容全部读出到一个byte数组中。 */
    public static ByteArray readBytes(File file, boolean closeIn) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        io(new FileInputStream(file), out, closeIn, true);

        return out.toByteArray();
    }

    /**
     * 通过快速缓冲将指定InputStream的所有内容全部读出到一个byte数组中。
     * 
     * @param in 要读取的InputStream
     * @return byte[]字节数组
     * @throws IOException
     */
    public static byte[] readBytesByFast(InputStream in) throws IOException {
        FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        io(in, out);
        return out.toByteArray();
    }

    /** 通过快速缓冲将指定InputStream的所有内容全部读出到一个byte数组中。 */
    public static byte[] readBytesByFast(InputStream in, boolean closeIn) throws IOException {
        FastByteArrayOutputStream out = new FastByteArrayOutputStream();

        io(in, out, closeIn, true);

        return out.toByteArray();
    }

    /** 将字符串写入到指定输出流中。 */
    public static void writeText(CharSequence chars, OutputStream out, String charset, boolean closeOut)
            throws IOException {
        Writer writer = charset == null ? new OutputStreamWriter(out) : new OutputStreamWriter(out, charset);

        writeText(chars, writer, closeOut);
    }

    /** 将字符串写入到指定Writer中。 */
    public static void writeText(CharSequence chars, Writer out, boolean closeOut) throws IOException {
        try {
            out.write(chars.toString());
            out.flush();
        } finally {
            if (closeOut) {
                try {
                    out.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    /** 将byte数组写入到指定filePath中。 */
    public static void writeBytes(byte[] bytes, String filePath, boolean closeOut) throws IOException {
        writeBytes(new ByteArray(bytes), new FileOutputStream(filePath), closeOut);
    }

    /** 将byte数组写入到指定File中。 */
    public static void writeBytes(byte[] bytes, File file, boolean closeOut) throws IOException {
        writeBytes(new ByteArray(bytes), new FileOutputStream(file), closeOut);
    }

    /** 将byte数组写入到指定OutputStream中。 */
    public static void writeBytes(byte[] bytes, OutputStream out, boolean closeOut) throws IOException {
        writeBytes(new ByteArray(bytes), out, closeOut);
    }

    /** 将byte数组写入到指定OutputStream中。 */
    public static void writeBytes(ByteArray bytes, OutputStream out, boolean closeOut) throws IOException {
        try {
            out.write(bytes.getRawBytes(), bytes.getOffset(), bytes.getLength());
            out.flush();
        } finally {
            if (closeOut) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 关闭流
     * 
     * @param closed 可关闭的流
     */
    public static void close(Closeable closed) {
        if (closed != null) {
            try {
                closed.close();
            } catch (IOException ignore) {
                // can ignore
            }
        }
    }

    /**
     * 同步化的输出流包裹器.
     */
    private static class SynchronizedOutputStream extends OutputStream {
        private OutputStream out;
        private Object lock;

        SynchronizedOutputStream(OutputStream out) {
            this(out, out);
        }

        SynchronizedOutputStream(OutputStream out, Object lock) {
            this.out = out;
            this.lock = lock;
        }

        public void write(int datum) throws IOException {
            synchronized (lock) {
                out.write(datum);
            }
        }

        public void write(byte[] data) throws IOException {
            synchronized (lock) {
                out.write(data);
            }
        }

        public void write(byte[] data, int offset, int length) throws IOException {
            synchronized (lock) {
                out.write(data, offset, length);
            }
        }

        public void flush() throws IOException {
            synchronized (lock) {
                out.flush();
            }
        }

        public void close() throws IOException {
            synchronized (lock) {
                out.close();
            }
        }
    }

}