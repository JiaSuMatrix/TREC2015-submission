package edu.hit.wilab.trec.index;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLFile {

	public static void main(String[] args) {

		XMLFile xmlFile = new XMLFile();

		try {
			xmlFile.parserXML("dataset/2630847.nxml");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<String> parserXML(String filePath)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		dbf.setNamespaceAware(true);
		dbf.setValidating(false);

		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId)
					throws SAXException, IOException {
				return new InputSource(new ByteArrayInputStream(
						"<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
			}
		});

		Document document = null;

		try {
			document = db.parse(new File(filePath));// 把文件解析成DOCUMENT类型
		} catch (SAXParseException e) {
			// TODO: handle exception
			return null;
		}

		Element root = document.getDocumentElement();
		System.out.println("*****解析XML文件" + filePath + "*****");

		// pmcid
		String element_pmcid = new String();
		NodeList nodeList_pmcid = root.getElementsByTagName("article-id");
		if (nodeList_pmcid.getLength() > 1) {
			element_pmcid = nodeList_pmcid.item(1).getTextContent();
		} else {
			return null;
		}
		// System.out.println(element_pmcid.replaceAll("\\s+", " "));
		// title
		String element_title = new String();
		NodeList nodeList_title = root.getElementsByTagName("article-title");
		if (nodeList_title.getLength() > 0) {
			element_title = nodeList_title.item(0).getTextContent();
		} else {
			element_title = "";
		}
		// System.out.println(element_title.replaceAll("\\s+", " "));
		// abstract
		String element_abstract = new String();
		NodeList nodeList_abstract = root.getElementsByTagName("abstract");
		if (nodeList_abstract.getLength() > 0) {
			element_abstract = nodeList_abstract.item(0).getTextContent();
		} else {
			element_abstract = "";
		}
		// System.out.println(element_abstract.replaceAll("\\s+", " "));
		// keywords
		String element_keywords = new String();
		NodeList nodelist = root.getElementsByTagName("kwd");
		for (int i = 0; i < nodelist.getLength(); i++) {
			element_keywords += nodelist.item(i).getTextContent() + " ";
		}
		// System.out.println(element_keywords.replaceAll("\\s+", " "));
		// body
		String element_body = new String();
		NodeList nodeList_body = root.getElementsByTagName("body");
		if (nodeList_body.getLength() > 0) {
			element_body = nodeList_body.item(0).getTextContent();
		} else {
			element_body = "";
		}
		// System.out.println(element_body);
		// reference
		String element_reference = new String();
		NodeList nodeList_reference = root
				.getElementsByTagName("article-title");
		for (int i = 0; i < nodeList_reference.getLength(); i++) {
			element_reference += nodeList_reference.item(i).getTextContent()
					+ " ";
		}

		List<String> list_content = new ArrayList<String>();

		list_content.add(element_pmcid.replaceAll("\\s+", " "));
		list_content.add(element_title.replaceAll("\\s+", " "));
		list_content.add(element_abstract.replaceAll("\\s+", " "));
		list_content.add(element_keywords.replaceAll("\\s+", " "));
		list_content.add(element_reference.replaceAll("\\s+", " "));
		list_content.add(element_body);

		return list_content;

	}

	public String[] parserXML_Indri(String[] filePath)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		dbf.setNamespaceAware(true);
		dbf.setValidating(false);

		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId)
					throws SAXException, IOException {
				return new InputSource(new ByteArrayInputStream(
						"<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
			}
		});

		Document document = null;

		try {
			document = db.parse(new File(filePath[0]));// 把文件解析成DOCUMENT类型
		} catch (SAXParseException e) {
			// TODO: handle exception
			return null;
		}

		Element root = document.getDocumentElement();
		System.out.println("*****解析XML文件" + filePath[0] + "*****");

		// pmcid
		String element_pmcid = new String();
		NodeList nodeList_pmcid = root.getElementsByTagName("article-id");
		if (nodeList_pmcid.getLength() > 1) {
			element_pmcid = nodeList_pmcid.item(1).getTextContent();
		} else {
			return null;
		}
		// System.out.println(element_pmcid.replaceAll("\\s+", " "));
		// title
		String element_title = new String();
		NodeList nodeList_title = root.getElementsByTagName("article-title");
		if (nodeList_title.getLength() > 0) {
			element_title = nodeList_title.item(0).getTextContent();
		} else {
			element_title = "";
		}
		// System.out.println(element_title.replaceAll("\\s+", " "));
		// abstract
		String element_abstract = new String();
		NodeList nodeList_abstract = root.getElementsByTagName("abstract");
		if (nodeList_abstract.getLength() > 0) {
			element_abstract = nodeList_abstract.item(0).getTextContent();
		} else {
			element_abstract = "";
		}
		// System.out.println(element_abstract.replaceAll("\\s+", " "));
		// keywords
		String element_keywords = new String();
		NodeList nodelist = root.getElementsByTagName("kwd");
		for (int i = 0; i < nodelist.getLength(); i++) {
			element_keywords += nodelist.item(i).getTextContent() + " ";
		}
		// System.out.println(element_keywords.replaceAll("\\s+", " "));
		// body
		String element_body = new String();
		NodeList nodeList_body = root.getElementsByTagName("body");
		if (nodeList_body.getLength() > 0) {
			element_body = nodeList_body.item(0).getTextContent();
		} else {
			element_body = "";
		}
		// System.out.println(element_body);
		// reference
		String element_reference = new String();
		NodeList nodeList_reference = root
				.getElementsByTagName("article-title");
		for (int i = 0; i < nodeList_reference.getLength(); i++) {
			element_reference += nodeList_reference.item(i).getTextContent()
					+ " ";
		}

		String[] article = new String[7];

		article[0] = element_pmcid.replaceAll("\\s+", " ");
		article[1] = element_title.replaceAll("\\s+", " ");
		article[2] = element_abstract.replaceAll("\\s+", " ");
		article[3] = element_keywords.replaceAll("\\s+", " ");
		article[4] = element_body;
		article[5] = filePath[1];
		article[6] = element_reference.replaceAll("\\s+", " ");

		return article;

	}

	public List<String> getKeyword(String filePath)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		dbf.setNamespaceAware(true);
		dbf.setValidating(false);

		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId)
					throws SAXException, IOException {
				return new InputSource(new ByteArrayInputStream(
						"<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
			}
		});

		Document document = null;

		try {
			document = db.parse(new File(filePath));// 把文件解析成DOCUMENT类型
		} catch (SAXParseException e) {
			// TODO: handle exception
			return null;
		}

		Element root = document.getDocumentElement();
		System.out.println("*****解析XML文件" + filePath + "*****");

		// keywords
		List<String> list_keyword = new ArrayList<String>();
		NodeList nodelist = root.getElementsByTagName("kwd");
		for (int i = 0; i < nodelist.getLength(); i++) {
			list_keyword.add(nodelist.item(i).getTextContent());
		}

		return list_keyword;

	}

}
