package com.blade.security;

import com.blade.security.web.filter.HTMLFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class HTMLFilterTest {

    protected HTMLFilter vFilter;

    @Before
    public void setUp() {
        vFilter = new HTMLFilter(true);
    }

    @After
    public void tearDown() {
        vFilter = null;
    }

    @Test
    public void testBasics() {
        Assert.assertEquals("", vFilter.filter(""));
        Assert.assertEquals("hello", vFilter.filter("hello"));
    }

    @Test
    public void testBalancingTags() {
        Assert.assertEquals("<b>hello</b>", vFilter.filter("<b>hello"));
        Assert.assertEquals("<b>hello</b>", vFilter.filter("<b>hello"));
        Assert.assertEquals("hello", vFilter.filter("hello<b>"));
        Assert.assertEquals("hello", vFilter.filter("hello</b>"));
        Assert.assertEquals("hello", vFilter.filter("hello<b/>"));
        Assert.assertEquals("<b><b><b>hello</b></b></b>", vFilter.filter("<b><b><b>hello"));
        Assert.assertEquals("", vFilter.filter("</b><b>"));
    }

    @Test
    public void testEndSlashes() {
        Assert.assertEquals("<img />", vFilter.filter("<img>"));
        Assert.assertEquals("<img />", vFilter.filter("<img/>"));
        Assert.assertEquals("", vFilter.filter("<b/></b>"));
    }

    @Test
    public void testBalancingAngleBrackets() {
        if (vFilter.isAlwaysMakeTags()) {
            Assert.assertEquals("<img src=\"foo\" />", vFilter.filter("<img src=\"foo\""));
            Assert.assertEquals("", vFilter.filter("i>"));
            Assert.assertEquals("<img src=\"foo\" />", vFilter.filter("<img src=\"foo\"/"));
            Assert.assertEquals("", vFilter.filter(">"));
            Assert.assertEquals("foo", vFilter.filter("foo<b"));
            Assert.assertEquals("<b>foo</b>", vFilter.filter("b>foo"));
            Assert.assertEquals("", vFilter.filter("><b"));
            Assert.assertEquals("", vFilter.filter("b><"));
            Assert.assertEquals("", vFilter.filter("><b>"));
        } else {
            Assert.assertEquals("&lt;img src=\"foo\"", vFilter.filter("<img src=\"foo\""));
            Assert.assertEquals("b&gt;", vFilter.filter("b>"));
            Assert.assertEquals("&lt;img src=\"foo\"/", vFilter.filter("<img src=\"foo\"/"));
            Assert.assertEquals("&gt;", vFilter.filter(">"));
            Assert.assertEquals("foo&lt;b", vFilter.filter("foo<b"));
            Assert.assertEquals("b&gt;foo", vFilter.filter("b>foo"));
            Assert.assertEquals("&gt;&lt;b", vFilter.filter("><b"));
            Assert.assertEquals("b&gt;&lt;", vFilter.filter("b><"));
            Assert.assertEquals("&gt;", vFilter.filter("><b>"));
        }
    }

    @Test
    public void testAttributes() {
        Assert.assertEquals("<img src=\"foo\" />", vFilter.filter("<img src=foo>"));
        Assert.assertEquals("<img />", vFilter.filter("<img asrc=foo>"));
        Assert.assertEquals("<img src=\"test\" />", vFilter.filter("<img src=test test>"));
    }

    @Test
    public void testDisallowScriptTags() {
        Assert.assertEquals("", vFilter.filter("<script>"));
        String result = vFilter.isAlwaysMakeTags() ? "" : "&lt;script";
        Assert.assertEquals(result, vFilter.filter("<script"));
        Assert.assertEquals("", vFilter.filter("<script/>"));
        Assert.assertEquals("", vFilter.filter("</script>"));
        Assert.assertEquals("", vFilter.filter("<script woo=yay>"));
        Assert.assertEquals("", vFilter.filter("<script woo=\"yay\">"));
        Assert.assertEquals("", vFilter.filter("<script woo=\"yay>"));
        Assert.assertEquals("", vFilter.filter("<script woo=\"yay<b>"));
        Assert.assertEquals("", vFilter.filter("<script<script>>"));
        Assert.assertEquals("script", vFilter.filter("<<script>script<script>>"));
        Assert.assertEquals("", vFilter.filter("<<script><script>>"));
        Assert.assertEquals("", vFilter.filter("<<script>script>>"));
        Assert.assertEquals("", vFilter.filter("<<script<script>>"));
    }

    @Test
    public void testProtocols() {
        Assert.assertEquals("<a href=\"http://foo\">bar</a>", vFilter.filter("<a href=\"http://foo\">bar</a>"));
        Assert.assertEquals("<a href=\"https://foo\">bar</a>", vFilter.filter("<a href=\"https://foo\">bar</a>"));
        // we don't allow ftp. t("<a href=\"ftp://foo\">bar</a>", "<a href=\"ftp://foo\">bar</a>");
        Assert.assertEquals("<a href=\"mailto:foo\">bar</a>", vFilter.filter("<a href=\"mailto:foo\">bar</a>"));
        Assert.assertEquals("<a href=\"#foo\">bar</a>", vFilter.filter("<a href=\"javascript:foo\">bar</a>"));
        Assert.assertEquals("<a href=\"#foo\">bar</a>", vFilter.filter("<a href=\"java script:foo\">bar</a>"));
        Assert.assertEquals("<a href=\"#foo\">bar</a>", vFilter.filter("<a href=\"java\tscript:foo\">bar</a>"));
        Assert.assertEquals("<a href=\"#foo\">bar</a>", vFilter.filter("<a href=\"java\nscript:foo\">bar</a>"));
        Assert.assertEquals("<a href=\"#foo\">bar</a>", vFilter.filter("<a href=\"java" + HTMLFilter.chr(1) + "script:foo\">bar</a>"));
        Assert.assertEquals("<a href=\"#foo\">bar</a>", vFilter.filter("<a href=\"jscript:foo\">bar</a>"));
        Assert.assertEquals("<a href=\"#foo\">bar</a>", vFilter.filter("<a href=\"vbscript:foo\">bar</a>"));
        Assert.assertEquals("<a href=\"#foo\">bar</a>", vFilter.filter("<a href=\"view-source:foo\">bar</a>"));
    }

    @Test
    public void testSelfClosingTags() {
        Assert.assertEquals("<img src=\"a\" />", vFilter.filter("<img src=\"a\">"));
        Assert.assertEquals("<img src=\"a\" />foo", vFilter.filter("<img src=\"a\">foo</img>"));
        Assert.assertEquals("", vFilter.filter("</img>"));
    }

    @Test
    public void testComments() {
        if (vFilter.isStripComments()) {
            Assert.assertEquals("", vFilter.filter("<!-- a<b --->"));
        } else {
            Assert.assertEquals("<!-- a&lt;b --->", vFilter.filter("<!-- a<b --->"));
        }
    }

    @Test
    public void testEntities() {
        Assert.assertEquals("&amp;nbsp;", vFilter.filter("&nbsp;"));
        Assert.assertEquals("&amp;", vFilter.filter("&amp;"));
        Assert.assertEquals("test &amp;nbsp; test", vFilter.filter("test &nbsp; test"));
        Assert.assertEquals("test &amp; test", vFilter.filter("test &amp; test"));
        Assert.assertEquals("&amp;nbsp;&amp;nbsp;", vFilter.filter("&nbsp;&nbsp;"));
        Assert.assertEquals("&amp;&amp;", vFilter.filter("&amp;&amp;"));
        Assert.assertEquals("test &amp;nbsp;&amp;nbsp; test", vFilter.filter("test &nbsp;&nbsp; test"));
        Assert.assertEquals("test &amp;&amp; test", vFilter.filter("test &amp;&amp; test"));
        Assert.assertEquals("&amp;&amp;nbsp;", vFilter.filter("&amp;&nbsp;"));
        Assert.assertEquals("test &amp;&amp;nbsp; test", vFilter.filter("test &amp;&nbsp; test"));
    }

    @Test
    public void testDollar() {
        String text   = "modeling & US MSRP $81.99, (Not Included)";
        String result = "modeling &amp; US MSRP $81.99, (Not Included)";

        Assert.assertEquals(result, vFilter.filter(text));
    }

    @Test
    public void testBr() {
        final Map<String, List<String>> allowed = new HashMap<String, List<String>>();
        for (String allow : "span;br;b;strong;em;u;i".split("\\s*;\\s*")) {
            if (0 < allow.indexOf(':')) {
                final String   name       = allow.split(":")[0];
                final String[] attributes = allow.split(":")[0].split("\\s*,\\s*");
                allowed.put(name, Arrays.asList(attributes));
            } else {
                allowed.put(allow, Collections.<String>emptyList());
            }
        }

        Map<String, Object> config = new HashMap<String, Object>() {{
            put("vAllowed", allowed);
            put("vSelfClosingTags", "img,br".split("\\s*,\\s*"));
            put("vNeedClosingTags", "a,b,strong,i,em".split("\\s*,\\s*"));
            put("vDisallowed", "".split("\\s*,\\s*"));
            put("vAllowedProtocols", "src,href".split("\\s*,\\s*"));
            put("vProtocolAtts", "".split("\\s*,\\s*"));
            put("vRemoveBlanks", "a,b,strong,i,em".split("\\s*,\\s*"));
            put("vAllowedEntities", "mdash,euro,quot,amp,lt,gt,nbsp,iexcl,cent,pound,curren,yen,brvbar,sect,uml,copy,ordf,laquo,not,shy,reg,macr,deg,plusmn,sup2,sup3,acute,micro,para,middot,cedil,sup1,ordm,raquo,frac14,frac12,frac34,iquest,Agrave,Aacute,Acirc,Atilde,Auml,Aring,AElig,Ccedil,Egrave,Eacute,Ecirc,Euml,Igrave,Iacute,Icirc,Iuml,ETH,Ntilde,Ograve,Oacute,Ocirc,Otilde,Ouml,times,Oslash,Ugrave,Uacute,Ucirc,Uuml,Yacute,THORN,szlig,agrave,aacute,acirc,atilde,auml,aring,aelig,ccedil,egrave,eacute,ecirc,euml,igrave,iacute,icirc,iuml,eth,ntilde,ograve,oacute,ocirc,otilde,ouml,divide,oslash,ugrave,uacute,ucirc,uuml,yacute,thorn,yuml,#34,#38,#60,#62,#160,#161,#162,#163,#164,#165,#166,#167,#168,#169,#170,#171,#172,#173,#174,#175,#176,#177,#178,#179,#180,#181,#182,#183,#184,#185,#186,#187,#188,#189,#190,#191,#192,#193,#194,#195,#196,#197,#198,#199,#200,#201,#202,#203,#204,#205,#206,#207,#208,#209,#210,#211,#212,#213,#214,#215,#216,#217,#218,#219,#220,#221,#222,#223,#224,#225,#226,#227,#228,#229,#230,#231,#232,#233,#234,#235,#236,#237,#238,#239,#240,#241,#242,#243,#244,#245,#246,#247,#248,#249,#250,#251,#252,#253,#254,#255".split("\\s*,\\s*"));
            put("stripComment", Boolean.TRUE);
            put("alwaysMakeTags", Boolean.TRUE);
        }};

        vFilter = new HTMLFilter(config);

        Assert.assertEquals("test <br /> test <br />", vFilter.filter("test <br> test <br>"));
    }

}