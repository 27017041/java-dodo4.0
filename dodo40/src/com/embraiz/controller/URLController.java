package com.embraiz.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.embraiz.model.Url;
import com.embraiz.service.BaseService;
import com.embraiz.util.SendMail;
import com.embraiz.util.UrlUtil;

@Controller
@RequestMapping("/url")
public class URLController {
	
	@Resource
	private BaseService baseService;

	@RequestMapping("/isAvailable")
	public void isAvailable(
			HttpServletResponse response)throws IOException{
		
		//从数据取得需要请求的urlList
		List<Object> urlList = baseService.getListByHql(Url.class);
		
		SendMail sendMail = new SendMail();
	
		for(int i=0;i<urlList.size();i++){
			Url url = (Url)urlList.get(i);
			
			UrlUtil urlUtil = new UrlUtil();
			boolean rtn = urlUtil.isAvailable(url.getUrl());
			if(!rtn){
				//请求不成功，发送邮件通知对应的admin
				String emailArray[] = url.getEmail().split(",");
				
				for(int n=0;n<emailArray.length;n++){
					String email = emailArray[n];
					sendMail.sendMail(email, "Website Error", "This url ( "+url.getUrl()+" ) has a problem,responseCode :"+urlUtil.getCode());
				}
			}
		}
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print("finished");
		response.getWriter().flush();
	}
}
