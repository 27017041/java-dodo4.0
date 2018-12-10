package com.embraiz.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.User;
import com.embraiz.service.CommonService;
import com.embraiz.service.RoleService;

@Controller
@RequestMapping("/common")
public class CommonController {
	
	@Resource
	private CommonService commonService;

	/**
	 * 获取用户在指定模块下的权限
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/getRightInModule")
	public void getRightInModule(
			HttpServletResponse response,
			HttpSession session,
			@RequestParam("moduleName") String moduleName)
		throws IOException{
		
		JSONObject json = new JSONObject();
		User user = (User)session.getAttribute("user");
		if(user!=null){
			String roleId = user.getRoleId();
			json.put("result", commonService.getRightInModule(roleId,moduleName));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
		
	}
	
	/**
	 * 获取指定模块下的下拉选项值
	 * @param response
	 * @param session
	 * @param typeId
	 * @throws IOException
	 */
	@RequestMapping("/getSelectDataInModule")
	public void getSelectDataInModule(
			HttpServletResponse response,
			HttpSession session,
			@RequestParam("typeId") Integer typeId)
		throws IOException{
		JSONObject json = new JSONObject();
		User user = (User)session.getAttribute("user");
		
		if(user!=null){
			String lang = (String) session.getAttribute("lang");
			if(lang==null){
				lang = "en";
			}	
			
			json.put("result", commonService.getSelectDataInModule(typeId, lang));
		}
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
}
