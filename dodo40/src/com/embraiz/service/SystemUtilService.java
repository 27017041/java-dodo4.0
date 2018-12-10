package com.embraiz.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.Conf;
import com.embraiz.model.ConfButton;
import com.embraiz.model.ConfForm;
import com.embraiz.model.ConfFormGrid;
import com.embraiz.model.ConfGird;
import com.embraiz.model.Page;
import com.embraiz.model.StoreData;
import com.embraiz.model.User;

public interface SystemUtilService {
	
	/**
	 * 获取这module默认的conf配置
	 * @param moduleName
	 * @return
	 */
	public Conf getDefaultConf(String moduleName);
	
	/**
	 * 获取conf的数据
	 * @param confId
	 * @return
	 */
	public Conf getConfData(int confId);
	
	/**
	 * 获取module搜索框的项目
	 * @param confId
	 * @param lang
	 * @return
	 */
	public List<ConfGird> moduleSearch(int confId,String lang);
	
	/**
	 * 获取module搜索框的单个项目数据
	 * @param gridId
	 * @return
	 */
	public ConfGird getSearchConfGird(int gridId);
	
	/**
	 * 根据配置获取下拉框数据
	 * @param table
	 * @param idField
	 * @param valueField
	 * @param orderField
	 * @param sqlWhere
	 * @return
	 */
	public List<StoreData> storeDataList(String table,String idField,String valueField,String orderField,String sqlWhere);
	
	/**
	 * select的数据是obj，受mapping的user和access_right的权限控制
	 * @param table
	 * @param idField
	 * @param valueField
	 * @param orderField
	 * @param userId
	 * @param sqlWhere
	 * @return
	 */
	public List<StoreData> storeObjDataList(String table,String idField,String valueField,String orderField,int userId,String sqlWhere);
	
	/**
	 * 获取module grid的项目
	 * @param confId
	 * @param lang
	 * @return
	 */
	public List<ConfGird> moduleGird(int confId,String lang);
	
	/**
	 * 获取module的grid的按钮
	 * @param confId
	 * @param lang
	 * @return
	 */
	public List<ConfButton> moduleButtons(int confId,String lang);
	
	/**
	 * 分页查询表格数据
	 * @param searchForm
	 * @param page
	 * @param sortBy
	 * @param sortOrder
	 * @param o
	 * @param userId
	 * @param confId
	 * @param sqlWhere
	 * @return
	 */
	public Map<String, Object> getList(String searchForm,Page page, String sortBy, String sortOrder,Object o,int userId,int confId,String sqlWhere,String lang);
	
	/**
	 * 分页查询form下面配置的表格的数据
	 * @param relationId
	 * @param relationField
	 * @param page
	 * @param sortBy
	 * @param sortOrder
	 * @param o
	 * @param gridConfId
	 * @param userId
	 * @return
	 */
	public Map<String, Object> getformGridList(int relationId,String relationField,Page page, String sortBy, String sortOrder,Object o,int gridConfId,int userId);
	
	/**
	 * 获取多选name或某字段名
	 */
	public String multname(String table, String key, String field, String id,String sqlWhere);
	
	/**
	 * 获取module的form的fields
	 * @param confId
	 * @param lang
	 * @return
	 */
	public List<ConfForm> moduleForm(int confId,String lang);
	
	/**
	 * 获取这个conf的form配置的表格
	 * @param confId
	 * @return
	 */
	public List<ConfFormGrid> formGrids(int confId);
	
	/**
	 * 获取moduleForm的单个field数据
	 * @param confFormId
	 * @return
	 */
	public ConfForm getFormConfField(int confFormId);
	
	/**
	 * 根据id获取数据
	 * @param conf
	 * @param keyId
	 * @param o
	 * @return
	 */
	public Object getViewData(Conf conf,int keyId,Object o);
	
	/**
	 * 更新module数据
	 * @param conf
	 * @param data
	 * @param keyId
	 * @param userId
	 * @param changeFields
	 * @return
	 */
	public int updateData(Conf conf,JSONObject data,int keyId,int userId,String changeFields);
	
	/**
	 * 执行插入操作
	 * @param conf
	 * @param user
	 * @param data
	 * @return
	 */
	public int insertData(Conf conf,User user,JSONObject data);
	
	/**
	 * 删除
	 * @param conf
	 * @param ids
	 * @param updateUserId
	 * @return
	 */
	public int deleteData(Conf conf,String ids,int updateUserId);
}
