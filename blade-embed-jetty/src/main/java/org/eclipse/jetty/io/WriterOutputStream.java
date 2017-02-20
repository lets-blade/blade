//
//  ========================================================================
//  Copyright (c) 1995-2016 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;


/* ------------------------------------------------------------ */
/** Wrap a Writer as an OutputStream.
 * When all you have is a Writer and only an OutputStream will do.
 * Try not to use this as it indicates that your design is a dogs
 * breakfast (JSP made me write it).
 * 
 */
public class WriterOutputStream extends OutputStream
{
    protected final Writer _writer;
    protected final Charset _encoding;
    private final byte[] _buf=new byte[1];
    
    /* ------------------------------------------------------------ */
    public WriterOutputStream(Writer writer, String encoding)
    {
        _writer=writer;
        _encoding=encoding==null?null:Charset.forName(encoding);
    }
    
    /* ------------------------------------------------------------ */
    public WriterOutputStream(Writer writer)
    {
        _writer=writer;
        _encoding=null;
    }

    /* ------------------------------------------------------------ */
    @Override
    public void close()
        throws IOException
    {
        _writer.close();
    }
    
    /* ------------------------------------------------------------ */
    @Override
    public void flush()
        throws IOException
    {
        _writer.flush();
    }
    
    /* ------------------------------------------------------------ */
    @Override
    public void write(byte[] b) 
        throws IOException
    {
        if (_encoding==null)
            _writer.write(new String(b));
        else
            _writer.write(new String(b,_encoding));
    }
    
    /* ------------------------------------------------------------ */
    @Override
    public void write(byte[] b, int off, int len)
        throws IOException
    {
        if (_encoding==null)
            _writer.write(new String(b,off,len));
        else
            _writer.write(new String(b,off,len,_encoding));
    }
    
    /* ------------------------------------------------------------ */
    @Override
    public synchronized void write(int b)
        throws IOException
    {
        _buf[0]=(byte)b;
        write(_buf);
    }
}

