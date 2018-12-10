package com.embraiz.service;

import java.util.List;
import java.util.Map;

public interface CommonService {

	/**
	 * 根据roleId和moduleName获得该角色在此模块内的权限
	 * @param roleId
	 * @param moduleName
	 * @return
	 */
	public Map<String,Object> getRightInModule(String roleId,String moduleName);
	
	/**
	 * 根据typeId和lang获得对应类型的下拉数据
	 * @param typeId
	 * @param lang
	 * @return
	 */
	public List<Map<String,Object>> getSelectDataInModule(Integer typeId,String lang);
	
	/**
	 * 获取teamData下拉框的数据
	 * @param userId
	 * @return
	 */
	public List<Map<String,Object>> getTeamData(Integer userId,String sqlWhere);
	
	/**
	 * 获得指定模块、语言下的搜索区域的栏位
	 * @param module
	 * @param lang
	 * @return
	 */
	public List<Map<String,Object>> getSearchFieldInModule(String module,String lang);
	
	/**
	 * 获得指定模块、语言下的列表区域的栏位
	 * @param module
	 * @param lang
	 * @return
	 */
	public List<Map<String,Object>> getGridFieldInModule(String module,String lang);
	
	/**
	 * 获得指定模块、语言下的表单区域的栏位
	 * @param module
	 * @param lang
	 * @return
	 */
	public List<Map<String,Object>> getFormFieldInModule(String module,String lang);
	
	/**
	 * 根据表名，获得该表所有字段
	 * @param tableName
	 * @return
	 */
	public List<Map<String,Object>> getTableFields(String tableName);
	/**
	 * 根据模块,获取表字段
	 * @param tableName
	 * @return
	 */
	public List<Map<String, Object>> getFilldByModule(String module);
}
