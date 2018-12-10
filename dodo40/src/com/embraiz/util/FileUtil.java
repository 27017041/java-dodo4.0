package com.embraiz.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FileUtil {

	/**
	 * 删除文件
	 * @param path
	 * @return
	 */
	public boolean deleteFile(String path){
		return new File(path).getAbsoluteFile().delete();
	}
	
	
	public String filePath(){
		String url="applicationContext.xml";
		url=getClass().getClassLoader().getResource(url).getPath();		
		DocumentBuilderFactory factory = DocumentBuilderFactory 
		.newInstance();
		String value="";
		Boolean flag=false;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(url);
			NodeList employees = doc.getChildNodes();
			for (int i = 0; i < employees.getLength(); i++) {
				Node employee = employees.item(i);
				NodeList employeeInfo = employee.getChildNodes();
				for (int j = 0; j < employeeInfo.getLength(); j++) {
					Node node = employeeInfo.item(j);
					NodeList employeeMeta = node.getChildNodes();
					for (int k = 0; k < employeeMeta.getLength(); k++) {
						Node t = employeeMeta.item(k);
						NamedNodeMap na = t.getAttributes();
						if (na != null) {
							for (int m = 0; m < na.getLength(); m++) {
								Node c = na.item(m);
								if (c.getTextContent().equals("filePath")) {
									value = t.getTextContent();
									value = value.substring(4);
									value = value.substring(0,
											value.length() - 3);
									flag = true;
								}
								if (flag) {
									break;
								}
							}
						}
						if (flag) {
							break;
						}
					}
				}
				if (flag) {
					break;
				}
			}
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
		return value;
	}
}
