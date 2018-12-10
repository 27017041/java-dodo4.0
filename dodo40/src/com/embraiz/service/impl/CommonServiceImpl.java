package com.embraiz.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.service.CommonService;

@Service
@Transactional
public class CommonServiceImpl implements CommonService {
	
	@Resource
	private BaseDao baseDao;
	
	private final String dataBaseName = "dodo40_master";

	@Override
	public Map<String,Object> getRightInModule(String roleId,String moduleName){
		String sql = " SELECT `map_menu_role`.right_read AS rightRead, `map_menu_role`.right_insert AS rightInsert, `map_menu_role`.right_update AS rightUpdate, `map_menu_role`.right_delete AS rightDelete "
				+ " FROM `map_menu_role`, core_menu "
				+ " WHERE `map_menu_role`.menu_id = core_menu.menu_id "
				+ " AND core_menu.module_name = :moduleName "
				+ " AND role_id = :roleId ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("moduleName", moduleName);
		params.put("roleId", roleId);
		
		return baseDao.getObject(sql, params);
	}
	
	@Override
	public List<Map<String,Object>> getSelectDataInModule(Integer typeId,String lang){
		String sql = "SELECT option_id as optionId,option_key as optionKey,option_name_"+lang+" as optionName FROM `ref_option` where type_id = :typeId order by sort";
		Map<String,Object> params =  new HashMap<String,Object>();
		params.put("typeId", typeId);
		
		return baseDao.getList(sql, params);
	}
	
	@Override
	public List<Map<String,Object>> getTeamData(Integer userId,String sqlWhere){
		String sql = " SELECT teamId as id ,teamName as value  FROM `v_obj_team` "
				+ " where shareUserId= :userId ";
				
		if(sqlWhere!=null && !"".equals(sqlWhere)){
			sql = sql + sqlWhere;
		}
		
		sql = sql + " ORDER BY teamName asc ";
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		return baseDao.getList(sql, params);
	}
	
	@Override
	public List<Map<String,Object>> getSearchFieldInModule(String module,String lang){
		String sql = "SELECT * FROM `v_search_field` "
				+ " where moduleName = :module and module = :module and langCode = :langCode "
				+ " ORDER BY sorting ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("module", module);
		params.put("langCode", lang);
		return baseDao.getList(sql, params);
	}
	
	@Override
	public List<Map<String,Object>> getGridFieldInModule(String module,String lang){
		String sql = "SELECT * FROM `v_grid_field` "
				+ " where moduleName = :module and module = :module and langCode = :langCode "
				+ " ORDER BY sorting ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("module", module);
		params.put("langCode", lang);
		return baseDao.getList(sql, params);
	}
	
	@Override
	public List<Map<String,Object>> getFormFieldInModule(String module,String lang){
		String sql = "SELECT * FROM `v_form_field` "
				+ " where moduleName = :module and module = :module and langCode = :langCode "
				+ " ORDER BY sorting ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("module", module);
		params.put("langCode", lang);
		return baseDao.getList(sql, params);
	}
	
	@Override
	public List<Map<String,Object>> getTableFields(String tableName){
		String sql = "select COLUMN_NAME as fieldsName from INFORMATION_SCHEMA.Columns where table_name= :tableName and table_schema= :dataBaseName";
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("tableName", tableName);
		params.put("dataBaseName", dataBaseName);
		
		return baseDao.getList(sql, params);
	}

	@Override
	public List<Map<String, Object>> getFilldByModule(String module) {
		String sql = "SELECT field_label FROM core_field INNER  JOIN core_form_item ON core_field.field_id = core_form_item.field_id WHERE core_field.module_name=:module_name ";
		Map<String,Object> params  = new HashMap<>();
		params.put("module_name", module);
		return baseDao.getList(sql, params);
	}
}
