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

package org.eclipse.jetty.xml;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jetty.util.StringUtil;

public class XmlAppendable
{
    private final String SPACES="                                                                 ";
    private final Appendable _out;
    private final int _indent;
    private final Stack<String> _tags = new Stack<>();
    private String _space="";

    public XmlAppendable(OutputStream out,String encoding) throws IOException
    {
        this(new OutputStreamWriter(out,encoding),encoding);
    }
    
    public XmlAppendable(Appendable out) throws IOException
    {
        this(out,2);
    }
    
    public XmlAppendable(Appendable out,String encoding) throws IOException
    {
        this(out,2,encoding);
    }
    
    public XmlAppendable(Appendable out, int indent) throws IOException
    {
        this(out,indent,"utf-8");
    }
    
    public XmlAppendable(Appendable out, int indent, String encoding) throws IOException
    {
        _out=out;
        _indent=indent;
        _out.append("<?xml version=\"1.0\" encoding=\""+encoding+"\"?>\n");
    }

    public XmlAppendable openTag(String tag, Map<String,String> attributes) throws IOException
    {
        _out.append(_space).append('<').append(tag);
        attributes(attributes);
        
        _out.append(">\n");
        _space=_space+SPACES.substring(0,_indent);
        _tags.push(tag);
        return this;
    }
    
    public XmlAppendable openTag(String tag) throws IOException
    {
        _out.append(_space).append('<').append(tag).append(">\n");
        _space=_space+SPACES.substring(0,_indent);
        _tags.push(tag);
        return this;
    }
    
    public XmlAppendable content(String s) throws IOException
    {
        if (s!=null)
            _out.append(StringUtil.sanitizeXmlString(s));
        
        return this;
    }

    public XmlAppendable cdata(String s) throws IOException
    {
        _out.append("<![CDATA[").append(s).append("]]>");
        return this;
    }
    
    public XmlAppendable tag(String tag) throws IOException
    {
        _out.append(_space).append('<').append(tag).append("/>\n");
        return this;
    }
    
    public XmlAppendable tag(String tag, Map<String,String> attributes) throws IOException
    {
        _out.append(_space).append('<').append(tag);
        attributes(attributes);
        _out.append("/>\n");
        return this;
    }
    
    public XmlAppendable tag(String tag,String content) throws IOException
    {
        _out.append(_space).append('<').append(tag).append('>');
        content(content);
        _out.append("</").append(tag).append(">\n");
        return this;
    }
    
    public XmlAppendable tagCDATA(String tag,String data) throws IOException
    {
        _out.append(_space).append('<').append(tag).append('>');
        cdata(data);
        _out.append("</").append(tag).append(">\n");
        return this;
    }
    
    public XmlAppendable tag(String tag, Map<String,String> attributes,String content) throws IOException
    {
        _out.append(_space).append('<').append(tag);
        attributes(attributes);
        _out.append('>');
        content(content);
        _out.append("</").append(tag).append(">\n");
        return this;
    }
    
    public XmlAppendable closeTag() throws IOException
    {
        if (_tags.isEmpty())
            throw new IllegalStateException("Tags closed");
        String tag=_tags.pop();
        _space=_space.substring(0,_space.length()-_indent);
        _out.append(_space).append("</").append(tag).append(">\n");
        if (_tags.isEmpty() && _out instanceof Closeable)
            ((Closeable)_out).close();
        return this;
    }
    
    private void attributes(Map<String,String> attributes) throws IOException
    {
        for (String k:attributes.keySet())
        {
            String v = attributes.get(k);
            _out.append(' ').append(k).append("=\"");
            content(v);
            _out.append('"');
        }
    }
    
    public void literal(String xml) throws IOException
    {
        _out.append(xml);
    }

}
