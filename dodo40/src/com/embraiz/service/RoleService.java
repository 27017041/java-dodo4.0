package com.embraiz.service;

import java.util.List;
import java.util.Map;

import com.embraiz.model.Page;
import com.embraiz.model.Role;
import com.embraiz.model.User;

public interface RoleService {
	
	/**
	 * 获取所有的role，一般是superadmin即user id是1
	 * @return
	 */
	public List<Role> getAllRole();
	
	/**
	 * 获取entityId下的所有role
	 * @param entityId
	 * @return
	 */
	public List<Role> getRoleByEntity(int entityId);
	
	public int delete(String roleIds,User user);
	
	public int insert(Role role);
	
	public int update(Role role);
	
	public Role checkRole(String roleName);
	
	public Role getRoleData(int roleId);
	
	public Map<String, Object> getRoleList(Role searchRole, Page page, String sortBy, String sortOrder);
	
	
	/**
	 * 查询表格数据
	 * @param searchForm
	 * @param page
	 * @param sortBy
	 * @param sortOrder
	 * @param o
	 * @param userId
	 * @param confId
	 * @param sqlWhere
	 * @param lang
	 * @return
	 */
	public Map<String,Object> getList(String searchForm,Page page, String sortBy, String sortOrder,String sqlWhere,String lang);
	
}
