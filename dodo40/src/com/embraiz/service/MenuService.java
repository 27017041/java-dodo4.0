package com.embraiz.service;

import java.util.List;

import com.embraiz.model.Menu;

public interface MenuService {
	
	/**
	 * 获取用户完整菜单
	 * @param parentId
	 * @param lang
	 * @param roleIds
	 * @return
	 */
	public List<Menu> menuList(int parentId,String lang,String roleIds);
	
	/**
	 * 获取菜单地图
	 * @param lang
	 * @return
	 */
	public List<Menu> getMenuMap(String lang);
	
	
	/**
	 * 一次性获取用户完整菜单
	 * @param lang
	 * @param roleIds
	 * @return
	 */
	public List<Menu> menuList(String lang,String roleIds);
}
