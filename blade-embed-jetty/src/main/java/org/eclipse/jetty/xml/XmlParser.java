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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.jetty.util.LazyList;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.Resource;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/*--------------------------------------------------------------*/
/**
 * XML Parser wrapper. This class wraps any standard JAXP1.1 parser with convieniant error and
 * entity handlers and a mini dom-like document tree.
 * <p>
 * By default, the parser is created as a validating parser only if xerces is present. This can be
 * configured by setting the "org.eclipse.jetty.xml.XmlParser.Validating" system property.
 */
public class XmlParser
{
    private static final Logger LOG = Log.getLogger(XmlParser.class);

    private Map<String,URL> _redirectMap = new HashMap<String,URL>();
    private SAXParser _parser;
    private Map<String,ContentHandler> _observerMap;
    private Stack<ContentHandler> _observers = new Stack<ContentHandler>();
    private String _xpath;
    private Object _xpaths;
    private String _dtd;

    /* ------------------------------------------------------------ */
    /**
     * Construct
     */
    public XmlParser()
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        boolean validating_dft = factory.getClass().toString().startsWith("org.apache.xerces.");
        String validating_prop = System.getProperty("org.eclipse.jetty.xml.XmlParser.Validating", validating_dft ? "true" : "false");
        boolean validating = Boolean.valueOf(validating_prop).booleanValue();
        setValidating(validating);
    }

    /* ------------------------------------------------------------ */
    public XmlParser(boolean validating)
    {
        setValidating(validating);
    }

    /* ------------------------------------------------------------ */
    public void setValidating(boolean validating)
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(validating);
            _parser = factory.newSAXParser();

            try
            {
                if (validating)
                    _parser.getXMLReader().setFeature("http://apache.org/xml/features/validation/schema", validating);
            }
            catch (Exception e)
            {
                if (validating)
                    LOG.warn("Schema validation may not be supported: ", e);
                else
                    LOG.ignore(e);
            }

            _parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", validating);
            _parser.getXMLReader().setFeature("http://xml.org/sax/features/namespaces", true);
            _parser.getXMLReader().setFeature("http://xml.org/sax/features/namespace-prefixes", false);
            try
            {
                if (validating)
                    _parser.getXMLReader().setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", validating);
            }
            catch (Exception e)
            {
                LOG.warn(e.getMessage());
            }
        }
        catch (Exception e)
        {
            LOG.warn(Log.EXCEPTION, e);
            throw new Error(e.toString());
        }
    }

    /* ------------------------------------------------------------ */
    public boolean isValidating()
    {
        return _parser.isValidating();
    }
    
    /* ------------------------------------------------------------ */
    public synchronized void redirectEntity(String name, URL entity)
    {
        if (entity != null)
            _redirectMap.put(name, entity);
    }

    /* ------------------------------------------------------------ */
    /**
     *
     * @return Returns the xpath.
     */
    public String getXpath()
    {
        return _xpath;
    }

    /* ------------------------------------------------------------ */
    /**
     * Set an XPath A very simple subset of xpath is supported to select a partial tree. Currently
     * only path like "/node1/nodeA | /node1/nodeB" are supported.
     *
     * @param xpath The xpath to set.
     */
    public void setXpath(String xpath)
    {
        _xpath = xpath;
        StringTokenizer tok = new StringTokenizer(xpath, "| ");
        while (tok.hasMoreTokens())
            _xpaths = LazyList.add(_xpaths, tok.nextToken());
    }

    /* ------------------------------------------------------------ */
    public String getDTD()
    {
        return _dtd;
    }

    /* ------------------------------------------------------------ */
    /**
     * Add a ContentHandler. Add an additional _content handler that is triggered on a tag name. SAX
     * events are passed to the ContentHandler provided from a matching start element to the
     * corresponding end element. Only a single _content handler can be registered against each tag.
     *
     * @param trigger Tag local or q name.
     * @param observer SAX ContentHandler
     */
    public synchronized void addContentHandler(String trigger, ContentHandler observer)
    {
        if (_observerMap == null)
            _observerMap = new HashMap<>();
        _observerMap.put(trigger, observer);
    }

    /* ------------------------------------------------------------ */
    public synchronized Node parse(InputSource source) throws IOException, SAXException
    {
        _dtd=null;
        Handler handler = new Handler();
        XMLReader reader = _parser.getXMLReader();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.setEntityResolver(handler);
        if (LOG.isDebugEnabled())
            LOG.debug("parsing: sid=" + source.getSystemId() + ",pid=" + source.getPublicId());
        _parser.parse(source, handler);
        if (handler._error != null)
            throw handler._error;
        Node doc = (Node) handler._top.get(0);
        handler.clear();
        return doc;
    }

    /* ------------------------------------------------------------ */
    /**
     * Parse String URL.
     * @param url the url to the xml to parse
     * @return the root node of the xml
     * @throws IOException if unable to load the xml
     * @throws SAXException if unable to parse the xml
     */
    public synchronized Node parse(String url) throws IOException, SAXException
    {
        if (LOG.isDebugEnabled())
            LOG.debug("parse: " + url);
        return parse(new InputSource(url));
    }

    /* ------------------------------------------------------------ */
    /**
     * Parse File.
     * @param file the file to the xml to parse 
     * @return the root node of the xml
     * @throws IOException if unable to load the xml
     * @throws SAXException if unable to parse the xml
     */
    public synchronized Node parse(File file) throws IOException, SAXException
    {
        if (LOG.isDebugEnabled())
            LOG.debug("parse: " + file);
        return parse(new InputSource(Resource.toURL(file).toString()));
    }

    /* ------------------------------------------------------------ */
    /**
     * Parse InputStream.
     * @param in the input stream of the xml to parse
     * @return the root node of the xml
     * @throws IOException if unable to load the xml
     * @throws SAXException if unable to parse the xml
     */
    public synchronized Node parse(InputStream in) throws IOException, SAXException
    {
        _dtd=null;
        Handler handler = new Handler();
        XMLReader reader = _parser.getXMLReader();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        reader.setEntityResolver(handler);
        _parser.parse(new InputSource(in), handler);
        if (handler._error != null)
            throw handler._error;
        Node doc = (Node) handler._top.get(0);
        handler.clear();
        return doc;
    }


    /* ------------------------------------------------------------ */
    protected InputSource resolveEntity(String pid, String sid)
    {
        if (LOG.isDebugEnabled())
            LOG.debug("resolveEntity(" + pid + ", " + sid + ")");

        if (sid!=null && sid.endsWith(".dtd"))
            _dtd=sid;

        URL entity = null;
        if (pid != null)
            entity = (URL) _redirectMap.get(pid);
        if (entity == null)
            entity = (URL) _redirectMap.get(sid);
        if (entity == null)
        {
            String dtd = sid;
            if (dtd.lastIndexOf('/') >= 0)
                dtd = dtd.substring(dtd.lastIndexOf('/') + 1);

            if (LOG.isDebugEnabled())
                LOG.debug("Can't exact match entity in redirect map, trying " + dtd);
            entity = (URL) _redirectMap.get(dtd);
        }

        if (entity != null)
        {
            try
            {
                InputStream in = entity.openStream();
                if (LOG.isDebugEnabled())
                    LOG.debug("Redirected entity " + sid + " --> " + entity);
                InputSource is = new InputSource(in);
                is.setSystemId(sid);
                return is;
            }
            catch (IOException e)
            {
                LOG.ignore(e);
            }
        }
        return null;
    }
    
    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    private class NoopHandler extends DefaultHandler
    {
        Handler _next;
        int _depth;

        NoopHandler(Handler next)
        {
            this._next = next;
        }

        /* ------------------------------------------------------------ */
        public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException
        {
            _depth++;
        }

        /* ------------------------------------------------------------ */
        public void endElement(String uri, String localName, String qName) throws SAXException
        {
            if (_depth == 0)
                _parser.getXMLReader().setContentHandler(_next);
            else
                _depth--;
        }
    }

    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    private class Handler extends DefaultHandler
    {
        Node _top = new Node(null, null, null);
        SAXParseException _error;
        private Node _context = _top;
        private NoopHandler _noop;

        Handler()
        {
            _noop = new NoopHandler(this);
        }

        /* ------------------------------------------------------------ */
        void clear()
        {
            _top = null;
            _error = null;
            _context = null;
        }

        /* ------------------------------------------------------------ */
        public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException
        {
            String name = null;
            if (_parser.isNamespaceAware())
                name = localName;

            if (name == null || "".equals(name))
                name = qName;

            Node node = new Node(_context, name, attrs);


            // check if the node matches any xpaths set?
            if (_xpaths != null)
            {
                String path = node.getPath();
                boolean match = false;
                for (int i = LazyList.size(_xpaths); !match && i-- > 0;)
                {
                    String xpath = (String) LazyList.get(_xpaths, i);

                    match = path.equals(xpath) || xpath.startsWith(path) && xpath.length() > path.length() && xpath.charAt(path.length()) == '/';
                }

                if (match)
                {
                    _context.add(node);
                    _context = node;
                }
                else
                {
                    _parser.getXMLReader().setContentHandler(_noop);
                }
            }
            else
            {
                _context.add(node);
                _context = node;
            }

            ContentHandler observer = null;
            if (_observerMap != null)
                observer = (ContentHandler) _observerMap.get(name);
            _observers.push(observer);

            for (int i = 0; i < _observers.size(); i++)
                if (_observers.get(i) != null)
                    ((ContentHandler) _observers.get(i)).startElement(uri, localName, qName, attrs);
        }

        /* ------------------------------------------------------------ */
        public void endElement(String uri, String localName, String qName) throws SAXException
        {
            _context = _context._parent;
            for (int i = 0; i < _observers.size(); i++)
                if (_observers.get(i) != null)
                    ((ContentHandler) _observers.get(i)).endElement(uri, localName, qName);
            _observers.pop();
        }

        /* ------------------------------------------------------------ */
        public void ignorableWhitespace(char buf[], int offset, int len) throws SAXException
        {
            for (int i = 0; i < _observers.size(); i++)
                if (_observers.get(i) != null)
                    ((ContentHandler) _observers.get(i)).ignorableWhitespace(buf, offset, len);
        }

        /* ------------------------------------------------------------ */
        public void characters(char buf[], int offset, int len) throws SAXException
        {
            _context.add(new String(buf, offset, len));
            for (int i = 0; i < _observers.size(); i++)
                if (_observers.get(i) != null)
                    ((ContentHandler) _observers.get(i)).characters(buf, offset, len);
        }

        /* ------------------------------------------------------------ */
        public void warning(SAXParseException ex)
        {
            LOG.debug(Log.EXCEPTION, ex);
            LOG.warn("WARNING@" + getLocationString(ex) + " : " + ex.toString());
        }

        /* ------------------------------------------------------------ */
        public void error(SAXParseException ex) throws SAXException
        {
            // Save error and continue to report other errors
            if (_error == null)
                _error = ex;
            LOG.debug(Log.EXCEPTION, ex);
            LOG.warn("ERROR@" + getLocationString(ex) + " : " + ex.toString());
        }

        /* ------------------------------------------------------------ */
        public void fatalError(SAXParseException ex) throws SAXException
        {
            _error = ex;
            LOG.debug(Log.EXCEPTION, ex);
            LOG.warn("FATAL@" + getLocationString(ex) + " : " + ex.toString());
            throw ex;
        }

        /* ------------------------------------------------------------ */
        private String getLocationString(SAXParseException ex)
        {
            return ex.getSystemId() + " line:" + ex.getLineNumber() + " col:" + ex.getColumnNumber();
        }

        /* ------------------------------------------------------------ */
        public InputSource resolveEntity(String pid, String sid)
        {
            return XmlParser.this.resolveEntity(pid,sid);
        }
    }

    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    /**
     * XML Attribute.
     */
    public static class Attribute
    {
        private String _name;
        private String _value;

        Attribute(String n, String v)
        {
            _name = n;
            _value = v;
        }

        public String getName()
        {
            return _name;
        }

        public String getValue()
        {
            return _value;
        }
    }

    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    /**
     * XML Node. Represents an XML element with optional attributes and ordered content.
     */
    public static class Node extends AbstractList<Object>
    {
        Node _parent;
        private ArrayList<Object> _list;
        private String _tag;
        private Attribute[] _attrs;
        private boolean _lastString = false;
        private String _path;

        /* ------------------------------------------------------------ */
        Node(Node parent, String tag, Attributes attrs)
        {
            _parent = parent;
            _tag = tag;

            if (attrs != null)
            {
                _attrs = new Attribute[attrs.getLength()];
                for (int i = 0; i < attrs.getLength(); i++)
                {
                    String name = attrs.getLocalName(i);
                    if (name == null || name.equals(""))
                        name = attrs.getQName(i);
                    _attrs[i] = new Attribute(name, attrs.getValue(i));
                }
            }
        }

        /* ------------------------------------------------------------ */
        public Node getParent()
        {
            return _parent;
        }

        /* ------------------------------------------------------------ */
        public String getTag()
        {
            return _tag;
        }

        /* ------------------------------------------------------------ */
        public String getPath()
        {
            if (_path == null)
            {
                if (getParent() != null && getParent().getTag() != null)
                    _path = getParent().getPath() + "/" + _tag;
                else
                    _path = "/" + _tag;
            }
            return _path;
        }

        /* ------------------------------------------------------------ */
        /**
         * Get an array of element attributes.
         * @return the attributes
         */
        public Attribute[] getAttributes()
        {
            return _attrs;
        }

        /* ------------------------------------------------------------ */
        /**
         * Get an element attribute.
         * 
         * @param name the name of the attribute 
         * @return attribute or null.
         */
        public String getAttribute(String name)
        {
            return getAttribute(name, null);
        }

        /* ------------------------------------------------------------ */
        /**
         * Get an element attribute.
         * 
         * @param name the name of the element 
         * @param dft the default value
         * @return attribute or null.
         */
        public String getAttribute(String name, String dft)
        {
            if (_attrs == null || name == null)
                return dft;
            for (int i = 0; i < _attrs.length; i++)
                if (name.equals(_attrs[i].getName()))
                    return _attrs[i].getValue();
            return dft;
        }

        /* ------------------------------------------------------------ */
        /**
         * Get the number of children nodes.
         */
        public int size()
        {
            if (_list != null)
                return _list.size();
            return 0;
        }

        /* ------------------------------------------------------------ */
        /**
         * Get the ith child node or content.
         *
         * @return Node or String.
         */
        public Object get(int i)
        {
            if (_list != null)
                return _list.get(i);
            return null;
        }

        /* ------------------------------------------------------------ */
        /**
         * Get the first child node with the tag.
         *
         * @param tag the name of the tag
         * @return Node or null.
         */
        public Node get(String tag)
        {
            if (_list != null)
            {
                for (int i = 0; i < _list.size(); i++)
                {
                    Object o = _list.get(i);
                    if (o instanceof Node)
                    {
                        Node n = (Node) o;
                        if (tag.equals(n._tag))
                            return n;
                    }
                }
            }
            return null;
        }

        /* ------------------------------------------------------------ */
        @Override
        public void add(int i, Object o)
        {
            if (_list == null)
                _list = new ArrayList<Object>();
            if (o instanceof String)
            {
                if (_lastString)
                {
                    int last = _list.size() - 1;
                    _list.set(last, (String) _list.get(last) + o);
                }
                else
                    _list.add(i, o);
                _lastString = true;
            }
            else
            {
                _lastString = false;
                _list.add(i, o);
            }
        }

        /* ------------------------------------------------------------ */
        public void clear()
        {
            if (_list != null)
                _list.clear();
            _list = null;
        }

        /* ------------------------------------------------------------ */
        /**
         * Get a tag as a string.
         *
         * @param tag The tag to get
         * @param tags IF true, tags are included in the value.
         * @param trim If true, trim the value.
         * @return results of get(tag).toString(tags).
         */
        public String getString(String tag, boolean tags, boolean trim)
        {
            Node node = get(tag);
            if (node == null)
                return null;
            String s = node.toString(tags);
            if (s != null && trim)
                s = s.trim();
            return s;
        }

        /* ------------------------------------------------------------ */
        public synchronized String toString()
        {
            return toString(true);
        }

        /* ------------------------------------------------------------ */
        /**
         * Convert to a string.
         *
         * @param tag If false, only _content is shown.
         * @return the string value
         */
        public synchronized String toString(boolean tag)
        {
            StringBuilder buf = new StringBuilder();
            toString(buf, tag);
            return buf.toString();
        }

        /* ------------------------------------------------------------ */
        /**
         * Convert to a string.
         *
         * @param tag If false, only _content is shown.
         * @param trim true to trim the content
         * @return the trimmed content
         */
        public synchronized String toString(boolean tag, boolean trim)
        {
            String s = toString(tag);
            if (s != null && trim)
                s = s.trim();
            return s;
        }

        /* ------------------------------------------------------------ */
        private synchronized void toString(StringBuilder buf, boolean tag)
        {
            if (tag)
            {
                buf.append("<");
                buf.append(_tag);

                if (_attrs != null)
                {
                    for (int i = 0; i < _attrs.length; i++)
                    {
                        buf.append(' ');
                        buf.append(_attrs[i].getName());
                        buf.append("=\"");
                        buf.append(_attrs[i].getValue());
                        buf.append("\"");
                    }
                }
            }

            if (_list != null)
            {
                if (tag)
                    buf.append(">");
                for (int i = 0; i < _list.size(); i++)
                {
                    Object o = _list.get(i);
                    if (o == null)
                        continue;
                    if (o instanceof Node)
                        ((Node) o).toString(buf, tag);
                    else
                        buf.append(o.toString());
                }
                if (tag)
                {
                    buf.append("</");
                    buf.append(_tag);
                    buf.append(">");
                }
            }
            else if (tag)
                buf.append("/>");
        }

        /* ------------------------------------------------------------ */
        /**
         * Iterator over named child nodes.
         *
         * @param tag The tag of the nodes.
         * @return Iterator over all child nodes with the specified tag.
         */
        public Iterator<Node> iterator(final String tag)
        {
            return new Iterator<Node>()
            {
                int c = 0;
                Node _node;

                /* -------------------------------------------------- */
                public boolean hasNext()
                {
                    if (_node != null)
                        return true;
                    while (_list != null && c < _list.size())
                    {
                        Object o = _list.get(c);
                        if (o instanceof Node)
                        {
                            Node n = (Node) o;
                            if (tag.equals(n._tag))
                            {
                                _node = n;
                                return true;
                            }
                        }
                        c++;
                    }
                    return false;
                }

                /* -------------------------------------------------- */
                public Node next()
                {
                    try
                    {
                        if (hasNext())
                            return _node;
                        throw new NoSuchElementException();
                    }
                    finally
                    {
                        _node = null;
                        c++;
                    }
                }

                /* -------------------------------------------------- */
                public void remove()
                {
                    throw new UnsupportedOperationException("Not supported");
                }
            };
        }
    }
}
