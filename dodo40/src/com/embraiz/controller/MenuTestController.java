package com.embraiz.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.Menu;
import com.embraiz.model.User;
import com.embraiz.service.MenuService;

@Controller
@RequestMapping("/menuTest")
public class MenuTestController {
	
	@Resource
	private MenuService menuService;
	
	/**
	 * 获取用户完整菜单
	 * @param parentId
	 * @param request
	 * @return
	 */
	@RequestMapping("/menuList")
	public void menuList(
			HttpServletResponse response,
			HttpSession session)
		throws IOException{
		User user = (User) session.getAttribute("user");
		
		String roleIds = user.getRoleId();
		String lang = (String) session.getAttribute("lang");
		
		if(lang==null){
			lang = "en";
		}
		List<Menu> menuList = menuService.menuList(lang, roleIds);
		
		String str = JSON.toJSON(menuList).toString();
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(str);
		response.getWriter().flush();
		
	}
	
	
	/**
	 * 获取菜单地图
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/getMenuMap")
	public void getMenuMap(
			HttpServletResponse response,
			HttpSession session) throws IOException{
		String lang = (String) session.getAttribute("lang");
		if(lang==null){
			lang = "en";
		}
		List<Menu> menuList = menuService.getMenuMap(lang);
		//String json = JSONArray.fromObject(menuList).toString();
		String str = JSON.toJSON(menuList).toString();
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(str);
		response.getWriter().flush();
	}

}
