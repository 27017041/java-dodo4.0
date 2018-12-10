package com.embraiz.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.pdf.BaseFont;

public class ConvertToXhtml {
	public static void convertToXhtml(String servletUrl, HttpServletResponse response, HttpServletRequest request, String title) throws Exception {
		URL url = new URL(servletUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Accept-Charset", "UTF-8");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		ByteArrayOutputStream tidyOut = new ByteArrayOutputStream();
		Tidy tidy = new Tidy();
		tidy.setXHTML(true);
		tidy.setInputEncoding("UTF-8");
		tidy.setOutputEncoding("UTF-8");
		tidy.parse(con.getInputStream(), tidyOut);

		InputStream tidyIn = new ByteArrayInputStream(tidyOut.toByteArray());
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.FALSE);
		dbf.setFeature("http://xml.org/sax/features/validation", Boolean.FALSE);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(tidyIn);

		Element style = doc.createElement("style");
		style.setTextContent("body { font-family: \"SimSun\"; }");

		Element root = doc.getDocumentElement();
		root.getElementsByTagName("head").item(0).appendChild(style);
		String root_path = System.getProperty("evan.webapp");
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocument(doc, null);
		ITextFontResolver fontResolver = renderer.getFontResolver();
		fontResolver.addFont(root_path + "WEB-INF\\classes\\SIMSUN.TTC", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		renderer.getSharedContext().setBaseURL("file://" + root_path);

		String fname = title + ".pdf";
		response.setContentType("application/pdf");
		String fileName = new String(fname.getBytes("UTF-8"), "ISO-8859-1");
		fileName = "\"" + fileName + "\"";
		response.setHeader("Content-disposition", "attachment; filename=" + fileName);
		ServletOutputStream os = response.getOutputStream();
		renderer.layout();
		renderer.createPDF(os, false);
		renderer.finishPDF();
		os.close();

	}

	public static String convertToXhtmlmail(String servletUrl, HttpServletResponse response, String title)
			throws Exception {
		URL url = new URL(servletUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Accept-Charset", "UTF-8");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		ByteArrayOutputStream tidyOut = new ByteArrayOutputStream();
		Tidy tidy = new Tidy();
		tidy.setXHTML(true);
		tidy.setInputEncoding("UTF-8");
		tidy.setOutputEncoding("UTF-8");
		tidy.parse(con.getInputStream(), tidyOut);

		InputStream tidyIn = new ByteArrayInputStream(tidyOut.toByteArray());
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.FALSE);
		dbf.setFeature("http://xml.org/sax/features/validation", Boolean.FALSE);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(tidyIn);

		Element style = doc.createElement("style");
		style.setTextContent("body { font-family: \"SimSun\"; }");

		Element root = doc.getDocumentElement();
		root.getElementsByTagName("head").item(0).appendChild(style);
		String root_path = System.getProperty("evan.webapp");
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocument(doc, null);
		ITextFontResolver fontResolver = renderer.getFontResolver();
		fontResolver.addFont(root_path + "WEB-INF\\classes\\SIMSUN.TTC", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		renderer.getSharedContext().setBaseURL("file://" + root_path);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		
		String fname = sdf.format(new Date()) + ".pdf";
		String pdfurl = root_path + "\\mail_file\\" + fname;
		FileOutputStream os = new FileOutputStream(root_path + "\\mail_file\\" + fname + "");

		renderer.layout();
		renderer.createPDF(os, false);
		renderer.finishPDF();
		os.close();

		return pdfurl;
	}

}
