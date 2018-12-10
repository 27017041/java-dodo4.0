package com.embraiz.service.impl;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.MapKey;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.embraiz.dao.BaseDao;
import com.embraiz.model.Conf;
import com.embraiz.model.ConfButton;
import com.embraiz.model.ConfForm;
import com.embraiz.model.ConfFormGrid;
import com.embraiz.model.ConfGird;
import com.embraiz.model.Obj;
import com.embraiz.model.ObjLog;
import com.embraiz.model.Page;
import com.embraiz.model.StoreData;
import com.embraiz.model.User;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.ObjService;
import com.embraiz.service.SystemUtilService;
import com.embraiz.util.StringFormat;

@Service
@Transactional
public class SystemUtilServiceImpl implements SystemUtilService{
	
	@Resource
	private BaseDao baseDao;
	@Resource
	private ObjService objService;
	@Resource
	private ObjLogService objLogService;
	
	@Override
	public Conf getDefaultConf(String moduleName){
		String hql = "from Conf where moduleName = :moduleName and isDefault = 1";
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("moduleName", moduleName);
		
		Object obj = baseDao.getObjectByHql(hql, params);
		if(obj!=null){
			Conf conf = (Conf)obj;
			return conf;
		}else{
			return null;
		}
	}
	
	
	@Override
	public Conf getConfData(int confId){
		return (Conf)baseDao.getObject(Conf.class, confId);
	}
	
	
	@Override
	public List<ConfGird> moduleSearch(int confId,String lang){
//		String sql = "select  grid_id AS gridId, field_name AS fieldName, sorting, conf_id AS confId, search_type AS searchType, "
//				+ "regular_expression AS regularExpression, is_showin_grid AS isShowinGrid, is_showin_search AS isShowinSearch, "
//				+ "is_default_grid AS isDefaultGrid, module_name AS moduleName, select_table AS selectTable, select_id_field AS selectIdField, "
//				+ "select_value_field AS selectValueField, label_text AS labelText, label_type AS labelType, lang_code AS langCode, "
//				+ "select_order_field AS selectOrderField, is_link AS isLink, link_method AS linkMethod, select_from_field AS selectFromField, "
//				+ "select_is_obj_data AS selectIsObjData, sub_to AS subTo, sub_from AS subFrom, is_showin_sub_grid AS isShowinSubGrid "
//				+ " from v_conf_grid where is_showin_search=1 and conf_id = :confId and lang_code = :lang order by sorting asc";
		String sql = "select * from v_conf_grid where is_showin_search=1 and conf_id=:confId and lang_code=:lang order by sorting asc";
		Map<String,Object> params = new HashMap<String,Object>(); 
		params.put("confId", confId);
		params.put("lang", lang);
		
		List<ConfGird> confGirdList = new ArrayList<ConfGird>();
		
		
		List<Object> objectList = baseDao.getList(params, sql, ConfGird.class);
		
		for(int i=0;i<objectList.size();i++){
			ConfGird confGrid =  (ConfGird)objectList.get(i);
			
			confGirdList.add(confGrid);
			
		}
		
		return confGirdList;
	}
	
	@Override
	public ConfGird getSearchConfGird(int gridId){
		String hql = "from ConfGird where isShowinSearch = 1 and gridId = :gridId ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("gridId", gridId);
		Object obj = baseDao.getObjectByHql(hql, params);
		if(obj!=null){
			ConfGird confGrid = (ConfGird)obj;
			return confGrid;
		}else{
			return null;
		}
	}
	
	
	@Override
	public List<StoreData> storeDataList(String table,String idField,String valueField,String orderField,String sqlWhere){
		List<StoreData> storeDataList = new ArrayList<StoreData>();
		
		if(table!=null && idField!=null && valueField!=null  && !table.equals("") && !idField.equals("") && !valueField.equals("")){
			StringBuffer sql = new StringBuffer();
			sql.append("select "+idField+" as id,"+valueField+" as value");
			sql.append(" from "+table+" where 1=1");
			if(sqlWhere!=null && !"".equals(sqlWhere)){
				sql.append(sqlWhere);
			}
			if(orderField!=null && !"".equals(orderField)){
				sql.append(" order by "+orderField);
			}
			
			Map<String,Object> params = new HashMap<String,Object>();
			List<Map<String,Object>> objectList = baseDao.getList(sql.toString(),params);
			
			for(int i=0;i<objectList.size();i++){
				Map<String, Object> map = objectList.get(i);
				
				StoreData storeData =  new StoreData();
				
				if(map.get("id")!=null){
					storeData.setId(Integer.parseInt(map.get("id").toString()));
				}
				if(map.get("value")!=null){
					storeData.setValue(map.get("value").toString());
				}
				
				storeDataList.add(storeData);
			}
			
		}
		
		return storeDataList;
	}
	
	
	@Override
	public List<StoreData> storeObjDataList(String table,String idField,String valueField,String orderField,int userId,String sqlWhere){
		List<StoreData> storeDataList = new ArrayList<StoreData>();
		
		if(table!=null && idField!=null && valueField!=null && !table.equals("") && !idField.equals("") && !valueField.equals("")){
			StringBuffer sql = new StringBuffer();
			sql.append("select "+idField+" as id,"+valueField+" as value");
			sql.append(" from "+table);
			sql.append(" where share_user_id= :userId ");
			if(sqlWhere!=null && !"".equals(sqlWhere)){
				sql.append(sqlWhere);
			}
			if(orderField!=null && !"".equals(orderField)){
				sql.append(" order by "+orderField);
			}
			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("userId", userId);
			List<Map<String,Object>> objectList = baseDao.getList(sql.toString(),params);
			
			for(int i=0;i<objectList.size();i++){
				Map<String, Object> map = objectList.get(i);
				
				StoreData storeData =  new StoreData();
				
				if(map.get("id")!=null){
					storeData.setId(Integer.parseInt(map.get("id").toString()));
				}
				if(map.get("value")!=null){
					storeData.setValue(map.get("value").toString());
				}
				
				storeDataList.add(storeData);
			}
		}
		
		return storeDataList;
	}
	
	
	@Override
	public List<ConfGird> moduleGird(int confId,String lang){
//		String sql = "select  grid_id AS gridId, field_name AS fieldName, sorting, conf_id AS confId, search_type AS searchType,"
//				+ " regular_expression AS regularExpression, is_showin_grid AS isShowinGrid, is_showin_search AS isShowinSearch,"
//				+ " is_default_grid AS isDefaultGrid, module_name AS moduleName, select_table AS selectTable, select_id_field AS selectIdField,"
//				+ " select_value_field AS selectValueField, label_text AS labelText, label_type AS labelType, lang_code AS langCode,"
//				+ " select_order_field AS selectOrderField, is_link AS isLink, link_method AS linkMethod, select_from_field AS selectFromField,"
//				+ " select_is_obj_data AS selectIsObjData, sub_to AS subTo, sub_from AS subFrom, is_showin_sub_grid AS isShowinSubGrid "
//				+ " from v_conf_grid where is_showin_grid=1 and conf_id= :confId and lang_code= :lang order by sorting asc";
		String sql = "select * from v_conf_grid where is_showin_grid=1 and conf_id=:confId and lang_code=:lang order by sorting asc";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("confId", confId);
		params.put("lang", lang);
		
		List<ConfGird> confGridList = new ArrayList<ConfGird>();
		
		//List<Map<String,Object>> objectList = baseDao.getList(sql,params);
		List<Object> objectList = baseDao.getList(params, sql, ConfGird.class);
		for(int i=0;i<objectList.size();i++){
			ConfGird  confGird = (ConfGird)objectList.get(i);
			
			confGridList.add(confGird);
		}
		return confGridList;
	}
	
	
	@Override
	public List<ConfButton> moduleButtons(int confId,String lang){
//		String sql = "select button_id AS buttonId, conf_id AS confId, button_name AS buttonName, "
//				+ " module_name AS moduleName, sorting, button_icon AS buttonIcon, is_selected AS isSelected,"
//				+ " show_right AS showRight, method_name AS methodName, label_text AS labelText, label_type AS labekType,"
//				+ " lang_code AS langCode "
//				+ " from v_conf_button where conf_id= :confId and lang_code= :lang ";
		
		String sql = "select * from v_conf_button where conf_id=:confId and lang_code=:lang";
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("confId", confId);
		params.put("lang", lang);
		
		List<ConfButton> confButtonList = new ArrayList<ConfButton>();
		
		//List<Map<String,Object>> objectList = baseDao.getList(sql,params);
		
		List<Object> objectList = baseDao.getList(params, sql, ConfButton.class);
		
		for(int i=0;i<objectList.size();i++){
			ConfButton confButton = (ConfButton)objectList.get(i);
			
			confButtonList.add(confButton);
		}
		return confButtonList;
	}
	
	
	@Override
	public Map<String, Object> getList(String searchForm,Page page, String sortBy, String sortOrder,Object o,int userId,int confId,String sqlWhere,String lang){
		Map<String,Object> params = new HashMap<String,Object>();
		
		List<ConfGird> search = moduleSearch(confId,"en");
		List<ConfGird> select = moduleGirdSelect(confId,"en");
		Conf conf = getConfData(confId);
		
		List<Object> list = null;
		int size = 0;
		
		if(conf!=null && conf.getSearchTable()!=null && !conf.getSearchTable().equals("")){
			String searchTable = conf.getSearchTable();
			
			String sql = "select * from "+searchTable+" where 1=1";
			String totalSql = "select count(1) from "+searchTable+" where 1=1";
			
			if(searchForm!=null && !"".equals(searchForm)){
				JSONObject searchJson = JSONObject.parseObject(searchForm);
				for(ConfGird cg:search){
					String fieldName = cg.getFieldName();
					if(searchJson.get(fieldName)!=null && !"".equals(searchJson.get(fieldName))){
						if(cg.getSearchType().equals("select")){
							sql = sql + " and "+StringFormat.fieldNameConvertWithLine(fieldName)+" = "+searchJson.get(fieldName).toString();
							totalSql = totalSql + " and "+StringFormat.fieldNameConvertWithLine(fieldName)+" = "+searchJson.get(fieldName).toString();
						}else if(cg.getSearchType().equals("multiSelect")){
							String value = searchJson.get(fieldName).toString();
							//ui-select返回的值中包含“[]”,所以要做一些处理
							if(!value.equals("[]")){
								if(value.indexOf("[")!=-1){
									value = value.replace("[", "");
								}
								if(value.indexOf("]")!=-1){
									value = value.replace("]", "");
								}
								/*sql = sql + " and ("+fieldName+" = '"+value+"' or "+fieldName+" like '"+value+",%' or "+fieldName+" like '%,"+value+",%' or "+fieldName+" like '%,"+value+"') ";
								totalSql = totalSql + " and ("+fieldName+" = '"+value+"' or "+fieldName+" like '"+value+",%' or "+fieldName+" like '%,"+value+",%' or "+fieldName+" like '%,"+value+"') ";*/
								
								//sql = sql + " and "+StringFormat.fieldNameConvertWithLine(fieldName)+" in ("+value+")";
								//totalSql = totalSql + " and "+StringFormat.fieldNameConvertWithLine(fieldName)+" in ("+value+")";
								sql = sql + " and ("+StringFormat.fieldNameConvertWithLine(fieldName)+" like '%"+value+"%' or find_in_set('"+value+"',"+StringFormat.fieldNameConvertWithLine(fieldName)+"))";
								totalSql = totalSql + " and ("+StringFormat.fieldNameConvertWithLine(fieldName)+" like '%"+value+"%' or find_in_set('"+value+"',"+StringFormat.fieldNameConvertWithLine(fieldName)+"))";
							}
						}else{
							sql = sql + " and "+StringFormat.fieldNameConvertWithLine(fieldName)+" like '%"+searchJson.get(fieldName)+"%'";
							totalSql = totalSql + " and "+StringFormat.fieldNameConvertWithLine(fieldName)+" like '%"+searchJson.get(fieldName)+"%'";
						}
					}
				}
			}
			
			if(sqlWhere!=null && !sqlWhere.equals("")){
				sql = sql + sqlWhere;
				totalSql = totalSql + sqlWhere;
			}
			if(conf.getIsObj()==1){//obj
				sql = sql + " and share_user_id="+userId;
				totalSql = totalSql + " and share_user_id="+userId;
			}
			if(!sortBy.equals("") && !sortOrder.equals("")){
				sortBy = StringFormat.fieldNameConvertWithLine(sortBy);
				sql = sql + " order by "+sortBy+" "+sortOrder;
			}
			
			sql = sql + " limit "+page.getStart()+" , "+page.getCount();
			
			list = baseDao.getList(params, sql,o.getClass());
			size = baseDao.getCountBySql(totalSql, params);			
			
		}
		
		List<Object> list1 = new ArrayList<Object>();
		if(list.size()>0){
			for(Object t:list){
				//对有选择框的，寻找到匹配的value值
				if(select.size()>0){
					for(ConfGird cg :select){
						if(cg.getSelectFromField()!=null && !("").equals(cg.getSelectFromField())){
							String first = cg.getSelectFromField().substring(0, 1).toUpperCase();
							String selectField = first + StringFormat.fieldNameConvert(cg.getSelectFromField()).substring(1);
							
							String vfirst = cg.getFieldName().substring(0, 1).toUpperCase();
							String valueField = vfirst + StringFormat.fieldNameConvert(cg.getFieldName().substring(1));
							
							try{
								Method m = t.getClass().getMethod("get"+selectField, new Class[]{});
								Object selectIds = m.invoke(t, new Object[]{});
								
								Method s = t.getClass().getMethod("set"+valueField, String.class);
								
								String whereSql = "";
								if(cg.getSelectIsObjData()==1){//搜索的数据是obj的数据，考虑mapping权限
									whereSql = " and share_user_id = " + userId;
								}
								
								if(cg.getSelectTypeField()!=null && cg.getSelectTypeId()!=null){
									whereSql = whereSql + " and "+cg.getSelectTypeField() + " = "+ cg.getSelectTypeId();
								}
								
								String selectValueField = cg.getSelectValueField();
								if(lang.equals("cn")){
									selectValueField = selectValueField + "_cn";
								}else if(lang.equals("en")){
									selectValueField = selectValueField + "_en";
								}else{
									selectValueField = selectValueField + "_tc";
								}
								
								String selectValue = multname(cg.getSelectTable(), cg.getSelectIdField(), selectValueField, selectIds+"", whereSql);
								s.invoke(t, selectValue);
								
							}catch(Exception e){
								e.printStackTrace();
							}
							
						}
					}
				}
				list1.add(t);
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", size);
	    map.put("recordsFiltered", size);
	    map.put("data", list1);
        return map;
	}
	
	
	@Override
	public Map<String, Object> getformGridList(int relationId,String relationField,Page page, String sortBy, String sortOrder,Object o,int gridConfId,int userId){
		Map<String,Object> params = new HashMap<String,Object>();
		
		List<ConfGird> select = moduleGirdSelect(gridConfId,"en-us");
		Conf conf = getConfData(gridConfId);
		List<Object> list = null;
		List<Object> list1 = new ArrayList<Object>();
		int size = 0;
		
		if(relationField!=null && !("").equals(relationField)){//关联的field不为空
			if(conf!=null && conf.getSearchTable()!=null && !("").equals(conf.getSearchTable())){
				String searchTable = conf.getSearchTable();
				
				String sql = "select * from "+searchTable+" where 1=1";
				String totalSql = "select count(1) from "+searchTable+" where 1=1";
				
				//关联条件搜索
				if(relationField!=null && !"".equals(relationField)){
					sql = sql + " and ("+relationField+" = '"+relationId+"' or "+relationField+" like '"+relationId+",%' or "+relationField+" like '%,"+relationId+",%' or "+relationField+" like '%,"+relationId+"') ";
					totalSql = totalSql + " and ("+relationField+" = '"+relationId+"' or "+relationField+" like '"+relationId+",%' or "+relationField+" like '%,"+relationId+",%' or "+relationField+" like '%,"+relationId+"') ";
				}
				if(!sortBy.equals("") && !sortOrder.equals("")){
					sql = sql + " order by "+sortBy+" "+sortOrder;
				}
				sql = sql + " limit "+page.getStart()+" , "+page.getCount();
				
				list = baseDao.getList(params, sql,o.getClass());
				size = baseDao.getCountBySql(totalSql, params);
			}
		}
		
		if(list.size()>0){
			for(Object t:list){
				//对有选择框的，寻找到匹配的value值
				if(select.size()>0){
					for(ConfGird cg:select){
						if(cg.getSelectFromField()!=null && !("").equals(cg.getSelectFromField())){
							String first = cg.getSelectFromField().substring(0, 1).toUpperCase();
							String selectField = first + StringFormat.fieldNameConvert(cg.getSelectFromField()).substring(1);

							String vfirst = cg.getFieldName().substring(0, 1).toUpperCase();
							String valueField = vfirst + StringFormat.fieldNameConvert(cg.getFieldName()).substring(1);
							
							try{
								Method m = t.getClass().getMethod("get"+selectField, new Class[]{});
								
								String selectIds = m.invoke(t, new Object[]{}).toString();
								selectIds = StringFormat.multSelectValueFormat(selectIds);
								
								Method s = t.getClass().getMethod("set"+valueField, String.class);
								
								String whereSql = "";
								if(cg.getSelectIsObjData() == 1){//搜索的数据是obj的数据，考虑mapping权限
									whereSql = " and share_user_id="+userId;
								}
								
								String selectValue = multname(cg.getSelectTable(), cg.getSelectIdField(), cg.getSelectValueField(), selectIds+"", whereSql);
								s.invoke(t, selectValue);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
				}
				list1.add(t);
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", size);
	    map.put("recordsFiltered", size);
	    map.put("data", list1);
        return map;
	}
	
	
	@Override
	public String multname(String table, String key, String field, String id,String sqlWhere){
		String name = "";
		try{
			if (id != null && !id.equals("null") && !id.equals("")) {
				String[] spid = id.split(",");
				
				for (int i = 0; i < spid.length; i++) {
					String sql = "select "+field+" from "+table+" where "+key+" =  '"+spid[i]+"'";
					if(sqlWhere!=null && !"".equals(sqlWhere)){
						sql= sql + sqlWhere;
					}
					
					Map<String,Object> params = new HashMap<String,Object>();
					Map<String,Object> map = baseDao.getObject(sql, params);
					name += map.get(field).toString() +",";
				}
			}
			if (!name.equals("")) {
                name = name.substring(0, name.length() - 1);
            }        
		}catch(Exception e){
			e.printStackTrace();
		}
		return name;
	}
	
	@Override
	public List<ConfForm> moduleForm(int confId,String lang){
		String sql = "select * from v_conf_form where conf_id = :confId and lang_code = :lang  order by sorting asc";
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("confId", confId);
		params.put("lang", lang);
		List<Object> objectList = baseDao.getList(params, sql,ConfForm.class);
		
		List<ConfForm> confFormList = new ArrayList<ConfForm>();
		
		for(int i=0;i<objectList.size();i++){
			ConfForm confForm = (ConfForm)objectList.get(i);
			confFormList.add(confForm);
		}
		return confFormList;
	}
	
	
	@Override
	public List<ConfFormGrid> formGrids(int confId){
		List<ConfFormGrid> confFormGridList = new ArrayList<ConfFormGrid>();
		
		String sql = "select * from conf_form_grid where conf_id= :confId ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("confId", confId);
		
		List<Object> objectList = baseDao.getList(params, sql,ConfFormGrid.class);
		for(int i=0;i<objectList.size();i++){
			ConfFormGrid confFormGrid = (ConfFormGrid)objectList.get(i);
			confFormGridList.add(confFormGrid);
		}
		return confFormGridList;
	}
	
	@Override
	public ConfForm getFormConfField(int confFormId){
		String hql = "from ConfForm where formId = :confFormId ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("confFormId", confFormId);
		
		ConfForm confForm = (ConfForm)baseDao.getObjectByHql(hql, params);
		
		return confForm;
	}
	
	
	@Override
	public Object getViewData(Conf conf,int keyId,Object o){
		String sql = "select * from "+conf.getSearchTable()+" where "+conf.getKeyField()+" = :keyId ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("keyId", keyId);
		List<Object> objectList = baseDao.getList(params, sql,o.getClass());
		return objectList.size()>0?objectList.get(0):o;
	}
	
	
	@Override
	public int updateData(Conf conf,JSONObject data,int keyId,int userId,String changeFields){
		int status = 0;
		List<ConfForm> list = moduleForm(conf.getConfId(), "en");
		if(list.size()>0){
			String updateItem = "";
			int index = 0;
			Map map = new HashMap();
			
			//把changeFields改成JSONArray形式
			JSONArray jsonArray = JSONArray.parseArray(changeFields);
			if(conf.getIsObj()==1){//这个module是obj
				
				//获取这个obj
				Obj obj = (Obj)baseDao.getObject(Obj.class, keyId);
				
				
				if(obj!=null){
					for(ConfForm cf:list){
						String field = StringFormat.fieldNameConvert(cf.getFieldName());
						
						if(data.get(field)!=null){
							String value = data.get(field).toString();
							if(cf.getFieldType().equals("multiSelect")){//多选框去除“[]”
								value = StringFormat.multSelectValueFormat(value);
							}
							
							if(cf.getFieldName().equals(conf.getObjTitleField())){//这个字段是obj title
								obj.setObjTitle(value);
								obj.setObjDesc(value);
							}
						/**缺少parentId修改的时候的后续操作*/	
							else if(cf.getFieldName().equals(conf.getObjParentField())){//这个字段是obj parentId
								obj.setObjParentId(Integer.parseInt(value));
							}
							else{
								updateItem = updateItem+" "+cf.getFieldName()+" = ?,";
								index++;
								map.put(index, value);
							}
						}
					}
					
					//获取obj keywork
					if(conf.getObjKeyWordField()!=null && !conf.getObjKeyWordField().equals("")){
						String keyworkValue = "";
						String[] kf = conf.getObjKeyWordField().split(",");
						for (int i = 0; i < kf.length; i++) {
							String keywordField = StringFormat.fieldNameConvert(kf[i]);
							if(data.get(keywordField)!=null && !data.get(keywordField).toString().equals("")){
								keyworkValue = keyworkValue+data.get(keywordField).toString()+",";
							}
						}
						if(!keyworkValue.equals("")){
							keyworkValue = keyworkValue.substring(0, keyworkValue.length()-1);
							obj.setKeyword(keyworkValue);
						}
					}
					
					//更新obj
					String sql = "update obj set obj_title= :objTitle ,obj_desc= :objDesc ,keyword= :keyword ,update_date= :updateDate where obj_id= :objId ";
					Map<String,Object> params = new HashMap<String,Object>();
						params.put("objTitle", obj.getObjTitle());
						params.put("objDesc", obj.getObjDesc());
						params.put("keyword", obj.getKeyword());
						params.put("updateDate", new Date());
						params.put("objId", obj.getObjId());
					status =baseDao.updateBySql(sql, params);
				}
			}
			else{//这个module非obj
				for(ConfForm cf:list){
					String field = StringFormat.fieldNameConvert(cf.getFieldName());
					if(data.get(field)!=null){
						updateItem = updateItem+" "+StringFormat.fieldNameConvertWithLine(cf.getFieldName())+" = ?,";
						index++;
						String value = data.get(field).toString();
						if(cf.getFieldType().equals("multiSelect")){//多选框去除“[]”
							value = StringFormat.multSelectValueFormat(value);
						}
						
						map.put(index, value);
					}
				}
				
			}
			
			//更新到表
			if(!updateItem.equals("")){
				final Iterator iter = map.entrySet().iterator();
				final Map map1 = map;
				final int idIndex = index;
				final int id = keyId;
				
				String sql = "update "+conf.getTableName()+" set "+updateItem.substring(0, updateItem.length()-1)+" where "+conf.getKeyField()+" = ?";
				System.out.println(sql);
				
				Map<Object,Object> params = new HashMap<Object,Object>();
				
				//因为在上面对updateItem进行了拼接，这里采用“按参数位置绑定”方式进行赋值----By Andy
				while(iter.hasNext()){
					Map.Entry entry = (Map.Entry) iter.next();
					int key = Integer.parseInt(entry.getKey().toString());
					params.put(key-1,  entry.getValue().toString());
				}
				params.put(idIndex, id);
				
				status =baseDao.updateBySqlInObjectMap(sql, params);
			
			}
			
			if(status>0 && !changeFields.equals("")){				
				ObjLog objLog = new ObjLog();
				if(conf.getIsObj()==1){
					//循环插入数据
					for(int i=0;i<jsonArray.size();i++){
						JSONObject jsonObject = (JSONObject) jsonArray.get(i);
						objLog.setObjId(keyId);
						objLog.setUserId(userId);
						objLog.setDescription(jsonObject.get("description").toString());
						objLog.setModuleName(conf.getModuleName());
						objLog.setConfId(conf.getConfId());
						objLog.setFieldName(jsonObject.get("fieldName").toString());
						objLogService.createLog(objLog);
					}					
				}else{
					//循环插入数据
					for(int i=0;i<jsonArray.size();i++){
						JSONObject jsonObject = (JSONObject) jsonArray.get(i);
						objLog.setObjId(0);
						objLog.setUserId(userId);
						objLog.setDescription(jsonObject.get("description").toString());
						objLog.setModuleName(conf.getModuleName());
						objLog.setConfId(conf.getConfId());
						objLog.setFieldName(jsonObject.get("fieldName").toString());
						objLogService.createLog(objLog);
					}
				}			
			}			
		}		
		return status;
	}
	
	
	@Override
	public int insertData(Conf conf,User user,JSONObject data){
		int keyId = 0;
		String sql1="";
		String sql2="";
		int index = 0;
		Map map = new HashMap();
		List<ConfForm> list = moduleForm(conf.getConfId(), "en-us");
		
		if(list.size()>0){
			if(conf.getIsObj()==1){//这个module是obj
				
				if(conf.getObjTypeName()!=null && !conf.getObjTypeName().equals("")){
					int objTypeId =  objService.searchObjType(conf.getObjTypeName());
					
					if(objTypeId==0){//找不到这个obj type
						keyId = -1;
					}else{
						int parentId = 0;
						String titleValue = "";
						for(ConfForm cf:list){
							String field = StringFormat.fieldNameConvert(cf.getFieldName());
							if(data.get(field)!=null){
								//获取字段的值
								String value = data.get(field).toString();
								if(cf.getFieldType().equals("multiSelect")){//多选框去除“[]”
									value = StringFormat.multSelectValueFormat(value);
								}
								
								if(cf.getFieldName().equals(conf.getObjParentField())){//这个字段是parent_id
									parentId = Integer.parseInt(value);
								}else if(cf.getFieldName().equals(conf.getObjTitleField())){//这个字段是obj_title
									titleValue = value;
								}else{//这个字段是表的字段
									sql1 = sql1+cf.getFieldName()+",";
									sql2 = sql2+"?,";
									index++;
									map.put(index, value);
								}
							}
						}
						
						//获取obj keywork
						String keyworkValue = "";
						if(conf.getObjKeyWordField()!=null && !conf.getObjKeyWordField().equals("")){
							String[] kf = conf.getObjKeyWordField().split(",");
							for (int i = 0; i < kf.length; i++) {
								String keywordField = StringFormat.fieldNameConvert(kf[i]);
								if(data.get(keywordField)!=null && !data.get(keywordField).toString().equals("")){
									keyworkValue = keyworkValue+data.get(keywordField).toString()+",";
								}
							}
							if(!keyworkValue.equals("")){
								keyworkValue = keyworkValue.substring(0, keyworkValue.length()-1);
							}
						}
						
						Obj obj = new Obj();
							obj.setObjTypeId(objTypeId);
							obj.setObjTitle(titleValue);
							obj.setObjDesc(titleValue);
							obj.setKeyword(keyworkValue);
							obj.setObjParentId(parentId);
							obj.setOwnerId(user.getUserId());
						keyId = objService.insertObj(obj);
						
						if(keyId>0){//更新到module表
							//加入key Id
							sql1 = sql1+conf.getKeyField();
							sql2 = sql2+"?";
							index++;
							map.put(index, keyId);
							
							final String sql = "insert into "+conf.getTableName()+"("+sql1+") values ("+sql2+")";
							System.out.println(sql);
							insertObject(sql, map);
						}
					}
					
				}else{
					keyId = -1;
				}
			}
			else{//非obj module，insert返回keyId
				for(ConfForm cf:list){
					String field = StringFormat.fieldNameConvert(cf.getFieldName());
					if(data.get(field)!=null){
						sql1 = sql1+cf.getFieldName()+",";
						sql2 = sql2+"?,";
						index++;
						String value = data.get(field).toString();
						if(cf.getFieldType().equals("multiSelect")){//多选框去除“[]”
							value = StringFormat.multSelectValueFormat(value);
						}
						
						map.put(index, value);
					}
				}
				if(!sql1.equals("")){
					final String sql = "insert into "+conf.getTableName()+"("+sql1.substring(0,sql1.length()-1)+") values ("+sql2.substring(0, sql2.length()-1)+")";
					System.out.println(sql);
					keyId = insert(sql, map,conf.getTableName());
				}
			}
			
			if(keyId>0){
				ObjLog objLog = new ObjLog();
				if(conf.getIsObj()==1){
					objLog.setObjId(keyId);
					objLog.setUserId(user.getUserId());
					objLog.setDescription("Create");
					objLog.setModuleName(conf.getModuleName());
					objLog.setConfId(conf.getConfId());
				}else{
					objLog.setObjId(0);
					objLog.setUserId(user.getUserId());
					objLog.setDescription("Create");
					objLog.setModuleName(conf.getModuleName());
					objLog.setConfId(conf.getConfId());
				}
				objLogService.createLog(objLog);
			}
		}
		
		return keyId;
	}
	
	@Override
	public int deleteData(Conf conf,String ids,int updateUserId){
		int flag = 0;
		String[] userIdArray = ids.split(",");
		String sql = "";
		if(conf.getIsObj()==1){//是obj
			sql = "update obj set status=0 where obj_id=?";
		}else{
			sql = "delete from "+conf.getTableName()+" where "+conf.getKeyField()+"=?";
		}
		
		for (int i = 0; i < userIdArray.length; i++) {
			Map<Object,Object> params = new HashMap<Object,Object>();
			
			int status = 0;
			String delId = userIdArray[i];
			
			//******待验证**********
			params.put(0, delId);
			status = baseDao.updateBySqlInObjectMap(sql, params);
			//******待验证**********
			
			if(status>0){
				flag++;
				
				ObjLog objLog = new ObjLog();
					objLog.setObjId(Integer.parseInt(delId));
					objLog.setUserId(updateUserId);
					objLog.setDescription("Delete "+conf.getConfName());
					objLog.setModuleName(conf.getModuleName());
					objLog.setConfId(conf.getConfId());
				objLogService.createLog(objLog);
			}
		}
		return flag;
	}
	
	public List<ConfGird> moduleGirdSelect(int confId,String lang){
		String sql = "select * from v_conf_grid where is_showin_grid=1 and conf_id= :confId  and lang_code= :lang and search_type='selectValue' order by sorting asc";
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("confId", confId);
		params.put("lang", lang);
		
		List<ConfGird> confGridList = new ArrayList<ConfGird>();
		
		List<Object> objectList = baseDao.getList(params, sql,ConfGird.class);
		for(int i=0;i<objectList.size();i++){
			ConfGird confGrid = (ConfGird)objectList.get(i);
			
			confGridList.add(confGrid);
		}
		
		return confGridList;
	}
	
	/**
	 * obj module插入数据
	 * @param sql
	 * @param map
	 */
	public void insertObject(final String sql,Map map){
		final Iterator iter = map.entrySet().iterator();
		final Map map1 = map;
		
		Map<Object,Object> params = new HashMap<Object,Object>();
		
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			int key = Integer.parseInt(entry.getKey().toString());
			params.put(key-1, entry.getValue().toString());
		}
		
		baseDao.insertBySql(sql, params);//用update方法insert,有待验证
	}
	
	/**
	 * 非obj module插入数据，返回keyId
	 * @param sql
	 * @param map
	 * @return
	 */
	public int insert(final String sql,Map map,String tableName){
		int keyId = 0;
		final Iterator iter = map.entrySet().iterator();
		final Map map1 = map;
		//KeyHolder key = new GeneratedKeyHolder();
		
		Map<Object,Object> params = new HashMap<Object,Object>();
		
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			int key = Integer.parseInt(entry.getKey().toString());
			params.put(key-1, entry.getValue().toString());
		}
		
		int status = baseDao.insertBySql(sql, params);//用update方法进行插入
		
		if(status>0){
			//插入成功
			
			//获得最近一次插入的Id
			//(---因为原版本采用的是sql+spring方式生成主键策略并返回主键，换成hibernate不方便使用sql插入后返回主键，所以采用了LAST_INSERT_ID()这个方法---)
			String sqlLastInsertId = "select LAST_INSERT_ID() as id";
			Map<String,Object> paramsStr = new HashMap<String,Object>();
			Map<String,Object> lastInsertMap = baseDao.getObject(sqlLastInsertId, paramsStr);
			keyId = Integer.parseInt(lastInsertMap.get("id").toString());
			
		}
		
		/*
		 * spring获得自增主键方法
		 * 	
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps=con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				while(iter.hasNext()){
					Map.Entry entry = (Map.Entry) iter.next();
					int key = Integer.parseInt(entry.getKey().toString());
					ps.setObject(key, entry.getValue().toString());
				}
				return ps;
			}
		},key);
		
		keyId = key.getKey().intValue();
		
		*/
		
		return keyId;
	}

}
