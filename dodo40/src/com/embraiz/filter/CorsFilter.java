package com.embraiz.filter;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CorsFilter implements Filter {
	 public CorsFilter() {
	 }


	 public void doFilter(ServletRequest request, ServletResponse response,
	 FilterChain chain) throws IOException, ServletException {


	 HttpServletResponse httpServletResponse = (HttpServletResponse) response;

	 //表示接受任意域名的请求
	 httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");

	 //头信息字段
	 httpServletResponse
	 .setHeader(
	 "Access-Control-Allow-Headers",
	 "User-Agent,Origin,Cache-Control,Content-type,Date,Server,withCredentials,x-auth-token,x-lang,uid");

	 //允许把Cookie发到服务器
	 httpServletResponse.setHeader("Access-Control-Allow-Credentials",
	 "true");

	 //浏览器的CORS请求会用到哪些HTTP方法
	 httpServletResponse.setHeader("Access-Control-Allow-Methods",
	 "GET, POST, PUT, DELETE, OPTIONS, HEAD");

	 //本次预检请求的有效期,单位为秒
	 httpServletResponse.setHeader("Access-Control-Max-Age", "1209600");

	 //头信息字段
	 httpServletResponse.setHeader("Access-Control-Expose-Headers",
	 "x-auth-token,x-lang,uid");

	 //头信息字段
	 httpServletResponse.setHeader("Access-Control-Request-Headers",
	 "x-auth-token,x-lang,uid");

	//缓存失效时间
	 httpServletResponse.setHeader("Expires", "-1");

	//缓存设置
	 httpServletResponse.setHeader("Cache-Control", "no-cache");

	 //缓存设置
	 httpServletResponse.setHeader("pragma", "no-cache");


	 chain.doFilter(request, response);


	 }


	 public void init(FilterConfig fConfig) throws ServletException {


	 }


	 public void destroy() {
	 }


}
