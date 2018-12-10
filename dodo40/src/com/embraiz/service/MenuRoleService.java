package com.embraiz.service;

import java.util.List;


import com.embraiz.model.MenuRole;

public interface MenuRoleService {
	
	/**
	 * 获取user在这个menu的权限(暂时无用处)
	 * @param menuOrginal
	 * @param userId
	 * @return
	 */
	public MenuRole getMenuRoleData(String menuOrginal,int userId);
	
	/**
	 * 获取role的所有菜单权限
	 * @param roleId
	 * @param lang
	 * @return
	 */
	public List<MenuRole> getRoleRightMap(int roleId,String lang);
	
	/**
	 * 修改更新role的菜单权限
	 * @param roleId
	 * @param mr
	 */
	public void updateRoleRight(int roleId,MenuRole mr);

}
