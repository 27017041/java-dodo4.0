package com.embraiz.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.model.Field;
import com.embraiz.model.Module;
import com.embraiz.model.Relational;
import com.embraiz.service.ConfService;

@Service
@Transactional
public class ConfServiceImpl implements ConfService {
	
	@Resource
	private BaseDao baseDao;

	@Override
	public List<Map<String, Object>> moduleList(String lang, String roleIds) {
		String sql = "SELECT moduleName, labelText FROM v_module_role "
					+ " WHERE langCode = :lang "
					+ " AND rightRead = 1 "
					+ " AND rightUpdate = 1 "
					+ " AND rightInsert = 1 "
					+ " AND rightDelete = 1 "
					+ " AND roleId IN ( :roleIds ) "
					+ " ORDER BY sort";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("lang", lang);
		params.put("roleIds", roleIds);
		return baseDao.getList(sql, params);
		
	}
	
	@Override
	public Module getModule(String moduleName){
		return (Module)baseDao.getObject(Module.class, moduleName);
	}
	
	@Override
	public List<Map<String,Object>> getSearchField(String lang,String moduleName){
		String sql = "SELECT * "
				+ " FROM v_search_field "
				+ " WHERE moduleName = :moduleName "
				+ " AND module = :moduleName "
				+ " AND langCode = :lang "
				+ " ORDER BY sorting";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("moduleName", moduleName);
		params.put("lang", lang);
		return baseDao.getList(sql, params);
	}
	
	@Override
	public List<Map<String,Object>> getGridField(String lang,String moduleName){
		String sql = "SELECT * "
				+ " FROM v_grid_field "
				+ " WHERE moduleName = :moduleName "
				+ " AND module = :moduleName "
				+ " AND langCode = :lang "
				+ " ORDER BY sorting";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("moduleName", moduleName);
		params.put("lang", lang);
		return baseDao.getList(sql, params);
	}
	
	@Override
	public List<Map<String,Object>> getFormField(String lang,String moduleName){
		String sql = "SELECT * "
				+ " FROM v_form_field "
				+ " WHERE moduleName = :moduleName "
				+ " AND module = :moduleName "
				+ " AND langCode = :lang "
				+ " ORDER BY sorting";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("moduleName", moduleName);
		params.put("lang", lang);
		return baseDao.getList(sql, params);
	}
	
	@Override
	public List<Object> getFieldByModule(String moduleName){
		String hql = "from Field where moduleName = :moduleName";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("moduleName", moduleName);
		return baseDao.getListByHql(hql, params);
	}
	
	@Override
	public Integer deleteFieldInView(Integer itemId,Object objectName){
		return baseDao.deleteById(objectName.getClass(), itemId);
	} 
	
	@Override
	public Integer getCountSearchField(Integer field){
		String sqlCount = "select count(*) from core_search_item where field_id = "+field;
		return baseDao.getCountBySql(sqlCount, null);
	}
	
	@Override
	public Integer getCountFormField(Integer field){
		String sqlCount = "select count(*) from core_form_item where field_id = "+field;
		return baseDao.getCountBySql(sqlCount, null);
	}
	
	@Override
	public Integer getCountGridField(Integer field){
		String sqlCount = "select count(*) from core_grid_item where field_id = "+field;
		return baseDao.getCountBySql(sqlCount, null);
	}
	
	@Override
	public Integer deleteFieldInModule(Integer field){
		return baseDao.deleteById(Field.class, field);
	}
	
	@Override
	public List<Map<String,Object>> getLabelType(String moduleName,Integer typeId){
		String sql = "SELECT option_id AS optionId, type_id AS typeId, option_key AS optionKey, option_name_en AS optionNameEn, "
				+ " option_name_cn AS optionNameCn, option_name_tc AS optionNameTc, module_name AS moduleName "
				+ " FROM `ref_option` WHERE module_name = :moduleName AND option_key = 'text' ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("moduleName", moduleName);
		if(typeId!=null){
			sql = sql + " and type_id = :typeId ";
			params.put("typeId", typeId);
		}
		sql = sql + " ORDER BY sort ";
		return baseDao.getList(sql, params);
	}
	
	@Override
	public List<Map<String,Object>> getSelectOption(String moduleName){
		String sql = "SELECT type_id as typeId,type_name_cn as typeNameCn,type_name_en as typeNameEn,type_name_tc as typeNameTc "
				+ " FROM `ref_option_type` where module_name = :moduleName ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("moduleName", moduleName);
		return baseDao.getList(sql, params);
	}
	
	@Override
	public Integer deleteLabelByFieldLabel(String fieldLabel){
		String sql = "delete from conf_label where label_orginal = :fieldLabel";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("fieldLabel", fieldLabel);
		return baseDao.deleteBySql(sql, params);
	}
	
	@Override
	public Map<String,Object> getMaxSortInModule(String moduleName,String viewName){
		String sql = "SELECT MAX(sorting) as maxSorting from ";
		if(viewName.equals("search")){
			sql = sql + " v_search_field ";
		}else if(viewName.equals("grid")){
			sql = sql + " v_grid_field  ";
		}else if(viewName.equals("form")){
			sql = sql + " v_form_field ";
		}
		
		sql = sql + " where moduleName = :moduleName ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("moduleName", moduleName);
		
		return baseDao.getObject(sql, params);
	}
	
	@Override
	public List<Map<String,Object>> getModuleList(String roleId,String lang){
		String sql = " SELECT DISTINCT menuId, moduleName, hasChilds FROM v_menu_role "
				+ " WHERE STATUS = 1 AND langCode = :lang AND rightRead = 1 "
				+ " AND rightUpdate = 1 AND hasChilds = 0 AND roleId = :roleId "
				+ " ORDER BY moduleName ";
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("lang", lang);
		params.put("roleId", roleId);
		
		return baseDao.getList(sql, params); 
	}
	
	@Override
	public Map<String,Object> getRefOptionListByTypeId(Integer typeId,int start,int length,String sortBy, String sortOrder){
		Map<String,Object> map = new HashMap<String,Object>();
		
		String sql = "select option_id as optionId,type_id as typeId,option_key as optionKey,option_name_en as optionNameEn,option_name_cn as optionNameCn,option_name_tc as optionNameTc,parent_id as parentId,sort,module_name as moduleName from ref_option where 1=1 ";
		String sqlCount  = "select count(1) from ref_option where 1=1 ";
		Map<String,Object> params = new HashMap<String,Object>();
		
		if(typeId!=null){
			sql = sql + " and type_id = :typeId ";
			sqlCount = sqlCount + " and type_id = :typeId ";
			params.put("typeId", typeId);
		}
		
		//获得行数
		int listCount = baseDao.getCountBySql(sqlCount, params);
		map.put("listCount", listCount);
		
		sql = sql + " order by " + sortBy + " " + sortOrder + " limit " + start + "," + length;
		map.put("data", baseDao.getList(sql, params));
		
		return map;
	}
	
	@Override
	public List<Object> getRefOptionListByTypeId(Integer typeId){
		String hql = "from RefOption where typeId = :typeId";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("typeId", typeId);
		
		return baseDao.getListByHql(hql, params);
	}
	
	@Override
	public Map<String,Object> getConfLabelListByLabelType(Integer labelType,String labelText,String labelOrginal,int start,int length,String sortBy, String sortOrder){
		Map<String,Object> map = new HashMap<String,Object>();
		
		String sql = " SELECT label_id as labelId,label_text as labelText,lang,module,label_orginal as labelOrginal,label_type as labelType  FROM `conf_label`  where 1=1 ";
		String sqlCount = " select count(1) from `conf_label`  where 1=1 ";
		
		Map<String,Object> params = new HashMap<String,Object>(); 
		
		if(labelType!=null){
			sql = sql + " and label_type = :labelType ";
			sqlCount = sqlCount + " and label_type = :labelType  ";
			params.put("labelType", labelType);
		}
		
		if(labelText!=null){
			sql = sql + " and label_text like :labelText ";
			sqlCount = sqlCount + " and label_text like :labelText ";
			params.put("labelText", "%"+labelText+"%");
		}
		
		if(labelOrginal!=null){
			sql = sql + " and label_orginal like :labelOrginal ";
			sqlCount = sqlCount + " and label_orginal like :labelOrginal ";
			params.put("labelOrginal", "%"+labelOrginal+"%");
		}
		
		//获得行数
		int listCount = baseDao.getCountBySql(sqlCount, params);
		map.put("listCount", listCount);
		
		sql = sql + " order by " + sortBy + " " + sortOrder + " limit " + start + "," + length;
		map.put("data", baseDao.getList(sql, params));
		
		return map;
	}
	
	@Override
	public List<Object> getConfLabelDetailList (String labelOriginal,Integer labelType){
		String  hql = "from ConfLabel where labelOrginal = :labelOrginal and labelType = :labelType";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("labelOrginal", labelOriginal);
		params.put("labelType", labelType);
		
		return baseDao.getListByHql(hql, params);
	}
	
	@Override
	public Integer getMaxSortInRefOptionByTypeId(Integer typeId){
		String sql = "SELECT MAX(sort) as sort FROM `ref_option` where type_id = "+ typeId;
		Map<String,Object> map = baseDao.getObject(sql, null);
		
		if(map.get("sort")!=null){
			return Integer.parseInt(map.get("sort").toString()) + 1;
		}else{
			return 1;
		}
	}
	
	@Override
	public List<Object> getRefOptionListByModuleOptionKey(String moduleName,String optionKey){
		String hql = "from RefOption where optionKey = :optionKey and moduleName = :moduleName ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("optionKey", optionKey);
		params.put("moduleName", moduleName);
		return baseDao.getListByHql(hql, params);
	}
	
	@Override
	public List<Map<String,Object>> getLabelListByModuleLabelType(String moduleName,String labelType){
		String sql = "select * from conf_label where module = :moduleName and label_type in (:labelType) GROUP BY label_orginal ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("moduleName", moduleName);
		params.put("labelType", labelType);
		return baseDao.getList(sql, params);
	}
	
	@Override
	public List<Object> getRelationalList(String moduleName){
		String hql = "from Relational where moduleName = :moduleName";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("moduleName", moduleName);
		return baseDao.getListByHql(hql, params);
	}
	
	@Override
	public List<Object> getRelationalItemList(List<Object> ls){
		Integer idArray [] = new Integer [ls.size()];
		for(int i=0;i<ls.size();i++){
			Relational relational = (Relational)ls.get(i);
			idArray[i] = relational.getId();
			
		}
		String hql = "from RelationalItem where relationalId in (:relationalId) order by sort ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("relationalId", idArray);
		return baseDao.getListByHql(hql, params);
	}
	
	@Override
	public Map<String,Object> getMaxSortInRelationalItem(Integer relationalId){
		String sql = "SELECT MAX(sort) as maxSort FROM `core_relational_item`where relational_id = :relationalId ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("relationalId", relationalId);
		return baseDao.getObject(sql, params);
	}

}
