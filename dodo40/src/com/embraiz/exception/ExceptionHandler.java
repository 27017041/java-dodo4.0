package com.embraiz.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SessionException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.embraiz.util.ExceptionFormat;
/**
 * 全局异常处理
 * @author sandy
 *
 */
public class ExceptionHandler implements HandlerExceptionResolver {

	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		System.out.println(ExceptionFormat.formatError(ex));
		if (ex instanceof SessionException){
			return new ModelAndView("redirect:/error/401");
		}else{
			return new ModelAndView("redirect:/error/500");
		}
	}
	

}
