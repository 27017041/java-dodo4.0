package com.embraiz.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.embraiz.service.LoginLogService;

@Controller
@RequestMapping("/loginlog")
public class LoginLogContreller {

	@Resource
	private LoginLogService loginLogService;

	
	// 获取在线人数
	@RequestMapping("/getnum")
	public void getnum(HttpServletResponse response, HttpServletRequest request) throws IOException {
		int i = loginLogService.selectnum();
		System.out.println(i);
		response.getWriter().print(i);
		response.getWriter().flush();
	}

}
