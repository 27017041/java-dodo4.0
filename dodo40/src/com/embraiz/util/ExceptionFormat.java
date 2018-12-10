package com.embraiz.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
//格式化异常信息
public class ExceptionFormat {
	public static String formatError(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
      }
}
