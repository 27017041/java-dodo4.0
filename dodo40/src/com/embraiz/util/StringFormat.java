package com.embraiz.util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.embraiz.util.StringFormat;

public class StringFormat {
	// convert Master_course to masterCourse
	public static String classNameConvert(String str) {

		int pos = str.indexOf("_");
		if (pos >= 0) {
			char second_name = Character.toUpperCase(str.charAt(pos + 1));
			str = str.substring(0, pos) + second_name + str.substring(pos + 2);
		}
		return str;
	}

	public static String fieldNameConvert(String str) {
		// change from master_course_id to masterCourseId
		while (str.indexOf("_") >= 0) {
			str = StringFormat.classNameConvert(str);
		}
		return str;
	}

	// change roleName to role_name
	public static String fieldNameConvertWithLine(String str) {

		String strRtn = str;
		int m = 0;

		for (int i = 0; i < str.length(); i++) {
			char chr = str.charAt(i);
			if (!Character.isLowerCase(chr)) {// 判断是否大写
				char lowerChr = Character.toLowerCase(chr);// 将大写字符转成小写字符
				char underLine = '_';

				m++;

				strRtn = strRtn.replace(chr, underLine);// 先将大写字符替换成_
				StringBuffer strBf = new StringBuffer(strRtn);
				strBf.insert(i + m, lowerChr);

				strRtn = strBf.toString();
			}
			;
		}

		return strRtn;
	}

	/**
	 * 多选框的值去除“[]”
	 * 
	 * @param value
	 * @return
	 */
	public static String multSelectValueFormat(String value) {
		if (value.indexOf("[") != -1) {
			value = value.replace("[", "");
		}
		if (value.indexOf("]") != -1) {
			value = value.replace("]", "");
		}
		return value;
	}

	public static String encodeStr(String str) {
		try {
			return new String(str.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String formatData(Object j) {
		String value = "";
		try {
			String type = j.getClass().toString();
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
			if (type.contains("Date") || type.contains("Time")) {
				Date d = (Date) j;
				value = f.format(d);
			} else {
				value = utf8format(j.toString());
			}
		} catch (Exception e) {

		}
		return value;
	}

	/**
	 * 特殊符号转义
	 * 
	 * @param stringdata
	 * @return
	 */
	public static String utf8format(String stringdata) {
		String data = "";
		StringBuffer sb = new StringBuffer();
		if (stringdata != null) {
			for (int i = 0; i < stringdata.length(); i++) {
				char c = stringdata.charAt(i);
				switch (c) {
				case '\'':
					sb.append("\\'");
					break;
				case '\"':
					sb.append("\\\"");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				case '/':
					sb.append("\\/");
					break;
				case '\b':
					sb.append("\\b");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\t':
					sb.append("\\t");
					break;
				default:
					sb.append(c);
				}
			}
		}
		data = sb.toString();
		return data;

	}

	/**
	 * 首字母大写
	 */
	public static String ConvertclassName(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1, str.length()).toLowerCase();
	}

}
