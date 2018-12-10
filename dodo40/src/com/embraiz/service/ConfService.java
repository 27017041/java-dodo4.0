package com.embraiz.service;

import java.util.List;
import java.util.Map;

import com.embraiz.model.Module;

public interface ConfService {

	/**
	 * 根据语言类型和角色获取对应的模块列表
	 * @param lang
	 * @param roleIds
	 * @return
	 */
	public List<Map<String,Object>> moduleList(String lang,String roleIds);
	
	/**
	 * 根据模块名字获得模块信息
	 * @param moduleName
	 * @return
	 */
	public Module getModule(String moduleName);
	
	/**
	 * 根据语言类型和模块名字获得search区域的栏位列表
	 * @param lang
	 * @param moduleName
	 * @return
	 */
	public List<Map<String,Object>> getSearchField(String lang,String moduleName);
	
	/**
	 * 根据语言类型和模块名字获得form区域的栏位列表
	 * @param lang
	 * @param moduleName
	 * @return
	 */
	public List<Map<String,Object>> getFormField(String lang,String moduleName);
	
	/**
	 * 根据语言类型和模块名字获得grid区域的栏位列表
	 * @param lang
	 * @param moduleName
	 * @return
	 */
	public List<Map<String,Object>> getGridField(String lang,String moduleName);
	
	/**
	 * 根据模块名字获得栏位列表
	 * @param moduleName
	 * @return
	 */
	public List<Object> getFieldByModule(String moduleName);
	
	/**
	 * 删除在某个区域内的栏位
	 * @param itemId
	 * @param objectName
	 * @return
	 */
	public Integer deleteFieldInView(Integer itemId,Object objectName);
	
	/**
	 * 根据fieldId，得到栏位在search区域使用的个数
	 * @param field
	 * @return
	 */
	public Integer getCountSearchField(Integer field);
	
	/**
	 * 根据fieldId，得到栏位在form区域使用的个数
	 * @param field
	 * @return
	 */
	public Integer getCountFormField(Integer field);
	
	/**
	 * 根据fieldId，得到栏位在grid区域使用的个数
	 * @param field
	 * @return
	 */
	public Integer getCountGridField(Integer field);
	
	/**
	 * 删除模块中的栏位
	 * @param field
	 * @return
	 */
	public Integer deleteFieldInModule(Integer field);
	
	/**
	 * 根据moduleName，获取ref_option中对应的optiond_id
	 * @param moduleName
	 * @return
	 */
	public List<Map<String,Object>> getLabelType(String moduleName,Integer typeId);
	
	/**
	 * 根据moduleName，获取ref_option_type中对应的type_id
	 * @param moduleName
	 * @return
	 */
	public List<Map<String,Object>> getSelectOption(String moduleName);
	
	/**
	 * 根据fieldLabel,删除conf_label对应的栏位信息
	 * @param fieldLabel
	 * @return
	 */
	public Integer deleteLabelByFieldLabel(String fieldLabel);
	
	/**
	 * 根据moduleName和viewName,获得对应视图中，指定模块下field的最大排序值
	 * @param moduleName
	 * @param viewName
	 * @return
	 */
	public Map<String,Object> getMaxSortInModule(String moduleName,String viewName);
	
	
	/**
	 * 根据roleId和lang，获得模块下拉列表
	 * @param roleId
	 * @param lang
	 * @return
	 */
	public List<Map<String,Object>> getModuleList(String roleId,String lang);
	
	/**
	 * 表格查询，根据typeId返回RefOption列表
	 * @param typeId
	 * @return
	 */
	public Map<String,Object> getRefOptionListByTypeId(Integer typeId,int start,int length,String sortBy, String sortOrder);
	
	/**
	 * 根据typeId返回RefOption列表
	 * @param typeId
	 * @return
	 */
	public List<Object> getRefOptionListByTypeId(Integer typeId);
	
	/**
	 * 表格查询，根据ref_option的option_id（conf_label的label_type）,查询conf_label表
	 * @param labelType
	 * @param start
	 * @param length
	 * @param sortBy
	 * @param sortOrder
	 * @return
	 */
	public Map<String,Object> getConfLabelListByLabelType(Integer labelType,String labelText,String labelOrginal,int start,int length,String sortBy, String sortOrder);
	
	/**
	 * 根据labelOrginal和labelType,获得3种语言的label详情
	 * @param labelOrginal
	 * @param labelType
	 * @return
	 */
	public List<Object> getConfLabelDetailList (String labelOriginal,Integer labelType);
	
	/**
	 * 根据typeId，获得在ref_option中排序最大的值
	 * @param typeId
	 * @return
	 */
	public Integer getMaxSortInRefOptionByTypeId(Integer typeId);
	
	/**
	 * 根据moduleName 和 optionKey，获得refOption列表
	 * @param moduleName
	 * @param optionKey
	 * @return
	 */
	public List<Object> getRefOptionListByModuleOptionKey(String moduleName,String optionKey);
	
	/**
	 * 根据moduleName 和 labelType,获得confLabel列表
	 * @param moduleName
	 * @param labelType
	 * @return
	 */
	public List<Map<String,Object>> getLabelListByModuleLabelType(String moduleName,String labelType);
	
	/**
	 * 根据模块名字，获得该模块下的关联信息
	 * @param moduleName
	 * @return
	 */
	public List<Object> getRelationalList(String moduleName);
	
	/**
	 * 根据RelationalList的id数组，获取到RelationalItemList
	 * @param ls
	 * @return
	 */
	public List<Object> getRelationalItemList(List<Object> ls);
	
	/**
	 * 根据relationalId获取最大排序值
	 * @param relationalId
	 * @return
	 */
	public Map<String,Object> getMaxSortInRelationalItem(Integer relationalId);
	
	
}
