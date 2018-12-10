package com.embraiz.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.embraiz.model.Menu;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.MenuService;

@Controller
@RequestMapping("/menu")
public class MenuController {
	
	@Resource
	private BaseService baseService;
	@Resource
	private MenuService menuService;
	
	@RequestMapping("/menuList")
	public void menuList(
			HttpServletResponse response,HttpServletRequest request,
			@RequestParam(value="parentId") int parentId,
			@RequestParam(value="lang") String lang){
		System.out.println(parentId);
	}
	
	
	/**
	 * 获取用户完整菜单
	 * @param parentId
	 * @param request
	 * @return
	 */
	public List<Menu> menuList(int parentId,HttpServletRequest request){
		User user = (User) request.getSession().getAttribute("user");
		String roleIds = user.getRoleId();
		String lang = (String) request.getSession().getAttribute("lang");
		if(lang==null){
			lang = "en";
		}
		return menuService.menuList(parentId, lang, roleIds);
	}
	
	
	/**
	 * 获取菜单地图
	 * @param request
	 * @return
	 */
	public List<Menu> getMenuMap(HttpServletRequest request){
		String lang = (String) request.getSession().getAttribute("lang");
		if(lang==null){
			lang = "en";
		}
		return menuService.getMenuMap(lang);
	}
	
}
