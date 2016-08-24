package com.blade.kit;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

public class XmlKit {
	
	private Document document;
	
	public XmlKit(String filePath) {
		String xmlPath = XmlKit.class.getResource(filePath).toString();
		if (xmlPath.substring(5).indexOf(":") > 0) {
			// 路径中含有：分隔符，windows系统
			xmlPath = xmlPath.substring(6);
		} else {
			xmlPath = xmlPath.substring(5);
		}
		SAXReader reader = new SAXReader();
		try {
			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			this.document = reader.read(new File(xmlPath));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public String attrValue(String strXPath) {
		Node n = document.selectSingleNode(strXPath);
		if (n != null) {
			return (n.valueOf("@value"));
		} else {
			return null;
		}
	}
	
}
