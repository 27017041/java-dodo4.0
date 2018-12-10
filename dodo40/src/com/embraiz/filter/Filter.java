package com.embraiz.filter;

import java.io.File;
import java.io.IOException;


import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.embraiz.util.ImageUtil;

public class Filter implements javax.servlet.Filter{
	
	@Override
	public void init(FilterConfig arg0)
			throws ServletException{
	}
	
	@Override
	public void doFilter(ServletRequest request,ServletResponse response,FilterChain filterChain)
			throws IOException,ServletException{
		HttpServletRequest req = (HttpServletRequest)request;
		String requestUrl = req.getRequestURI();
		
		String filePath=Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("/WEB-INF/classes/", "");
		
		String path = req.getContextPath(); 
		
		String systemPath = filePath.replace(path, "");
		
		if(requestUrl.indexOf(".")!=-1){
			String requestType = requestUrl.substring(requestUrl.indexOf(".")+1, requestUrl.length());
			if(requestType.equals("jpg") || requestType.equals("png") || requestType.equals("gif") || requestType.equals("bmp") ){
				
				if(requestUrl.indexOf("x")!=-1){
					//访问的是压缩图片
					
					File file = new File(systemPath+requestUrl);
					
					if(file.exists()){
						//文件存在
						filterChain.doFilter(request, response);
					}else{
						//文件不存在，寻找原文件是否存在
						String urlArray[] = requestUrl.split("/");
						String thumbFileName =  urlArray[urlArray.length-1];
						
						//得到原文件名字
						thumbFileName = thumbFileName.substring(0, thumbFileName.length()-11);
						
						//原文件存储路径
						String originalPath = "/image/original/";
						
						String originalFilePath = filePath+originalPath+thumbFileName+"."+requestType;
						
						File originalFile = new File(originalFilePath);
						
						if(originalFile.exists()){
							//原文件存在，则进行压缩
							ImageUtil imageUtil = new ImageUtil();
							
							try {
								boolean rtn = imageUtil.compressImage(originalFilePath, thumbFileName, requestType, "thumbnail,medium,large");
								if(rtn){
									File thumbFile = new File(originalFilePath);
									if(thumbFile.exists()){
										//休眠5秒钟，否则可能读取不到生成的图片
										Thread.sleep(5000);
										
										filterChain.doFilter(request, response);
									}
									
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						}else{
							filterChain.doFilter(request, response);
						}
						
						
						
					}
					
				}else{
					//访问的是原图片
					filterChain.doFilter(request, response);
				}
				
				
			}
		}else{
			
			filterChain.doFilter(request, response);
		}
	}
	
	@Override
	public void destroy(){
	}
}
