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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.zip.ZipFile;

import com.blade.kit.io.FastByteArrayOutputStream;

/**
 * IO工具类
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class IOKit {

    public static final String LINE_SEPARATOR;

    private static final int EOF = -1;
    
    static {
        // avoid security issues
        StringWriter buf = new StringWriter(4); // NOSONAR
        PrintWriter out = new PrintWriter(buf);
        out.println();
        LINE_SEPARATOR = buf.toString();
    }

    /**
     * The default buffer size to use.
     */
    public static final int DEFAULT_BUFFER_SIZE = 0x1000;

    private IOKit() {
    }

    public static String toString(InputStream input) throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }
    
    public static String toString(File file) throws IOException {
    	try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder data = readFromBufferedReader(reader);
            reader.close();
            return new String(data.toString().getBytes(), "utf-8");
        } catch (IOException ex) {
            throw new RuntimeException("File " + file + " not found.");
        }
    }
    
    private static StringBuilder readFromBufferedReader(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        int numRead = 0;
        while((numRead = reader.read(buffer)) != EOF) {
            builder.append(String.valueOf(buffer, 0, numRead));
            buffer = new char[DEFAULT_BUFFER_SIZE];
        }
        return builder;
    }
    
    public static byte[] toByteArray(InputStream input) throws IOException {
        @SuppressWarnings("resource")
		FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        byte[] buf = new byte[1024];
        for (int n = input.read(buf); n != EOF; n = input.read(buf)) {
            os.write(buf, 0, n);
        }
        return os.toByteArray();
    }

   

    public static long copyLarge(final InputStream input, final OutputStream output)
        throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0L;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void copy(InputStream input, Writer output)
        throws IOException {
        InputStreamReader in = new InputStreamReader(input); // NOSONAR
        copy(in, output);
    }

    public static long copyLarge(Reader input, Writer output) throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long count = 0L;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void write(byte[] data, File file) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			os.write(data);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			closeQuietly(os);
		}
	}

	public static void write(char[] data, File file, String charsetName) {
		write(data, file, Charset.forName(charsetName));
	}

	public static void write(char[] data, File file, Charset charset) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			os.write(new String(data).getBytes(charset));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			closeQuietly(os);
		}
	}

	public static void write(String data, File file, String charsetName) {
		write(data, file, Charset.forName(charsetName));
	}

	public static void write(String data, File file, Charset charset) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			os.write(data.getBytes(charset));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			closeQuietly(os);
		}
	}

	public static int copy(InputStream input, OutputStream output)
			throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
          return -1;
        }
        return (int) count;
	}

	public static int copy(InputStream input, Writer output, String charsetName)
			throws IOException {
		return copy(new InputStreamReader(input, Charset.forName(charsetName)),
				output);
	}

	public static int copy(InputStream input, Writer output, Charset charset)
			throws IOException {
		return copy(new InputStreamReader(input, charset), output);
	}

	public static int copy(Reader input, Writer output) throws IOException {
	    long count = copyLarge(input, output);
	    if (count > Integer.MAX_VALUE) {
	      return -1;
	    }
	    return (int) count;
	}

	public static void closeQuietly(ZipFile obj) {
		try {
			if (obj != null) {
				obj.close();
			}
		} catch (IOException e) {
		}
	}

	public static void closeQuietly(Socket socket) {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
		}
	}

	public static void closeQuietly(ServerSocket socket) {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
		}
	}

	public static void closeQuietly(Selector selector) {
		try {
			if (selector != null) {
				selector.close();
			}
		} catch (IOException e) {
		}
	}

	public static void closeQuietly(URLConnection conn) {
		if (conn != null) {
			if (conn instanceof HttpURLConnection) {
				((HttpURLConnection) conn).disconnect();
			}
		}
	}

	public static void closeQuietly(Closeable closeable) {
		if (null != closeable) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
    public static String toString(Reader input) throws IOException {
        StringBuilder output = new StringBuilder();
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.append(buffer, 0, n);
        }
        return output.toString();
    }
}
