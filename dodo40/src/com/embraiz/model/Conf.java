package com.embraiz.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate(true)
@DynamicInsert(true)
@Table(name="conf")
public class Conf {
	@Id
	@Column(name="conf_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer confId;
	
	@Column(name="is_default")
	private Integer isDefault;
	
	@Column(name="conf_name")
	private String confName;
	
	@Column(name="module_name")
	private String moduleName;
	
	@Column(name="table_name")
	private String tableName;
	
	@Column(name="search_table")
	private String searchTable;
	
	@Column(name="key_field")
	private String keyField;
	
	@Column(name="has_checkbox")
	private Integer hasCheckbox;
	
	//obj
	@Column(name="is_obj")
	private Integer isObj;
	
	@Column(name="obj_title_field")
	private String objTitleField;
	
	@Column(name="obj_key_word_field")
	private String objKeyWordField;
	
	@Column(name="obj_type_name")
	private String objTypeName;
	
	@Column(name="obj_parent_field")
	private String objParentField;
	
	@Column(name="search_link")
	private String searchLink;
	
	@Column(name="update_link")
	private String updateLink;
	
	@Column(name="delete_link")
	private String deleteLink;
	
	public Integer getConfId() {
		return confId;
	}
	public void setConfId(Integer confId) {
		this.confId = confId;
	}
	public Integer getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
	}
	public String getConfName() {
		return confName;
	}
	public void setConfName(String confName) {
		this.confName = confName;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSearchTable() {
		return searchTable;
	}
	public void setSearchTable(String searchTable) {
		this.searchTable = searchTable;
	}
	public String getKeyField() {
		return keyField;
	}
	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}
	public Integer getHasCheckbox() {
		return hasCheckbox;
	}
	public void setHasCheckbox(Integer hasCheckbox) {
		this.hasCheckbox = hasCheckbox;
	}
	public Integer getIsObj() {
		return isObj;
	}
	public void setIsObj(Integer isObj) {
		this.isObj = isObj;
	}
	public String getSearchLink() {
		return searchLink;
	}
	public void setSearchLink(String searchLink) {
		this.searchLink = searchLink;
	}
	public String getObjTitleField() {
		return objTitleField;
	}
	public void setObjTitleField(String objTitleField) {
		this.objTitleField = objTitleField;
	}
	public String getObjKeyWordField() {
		return objKeyWordField;
	}
	public void setObjKeyWordField(String objKeyWordField) {
		this.objKeyWordField = objKeyWordField;
	}
	public String getObjTypeName() {
		return objTypeName;
	}
	public void setObjTypeName(String objTypeName) {
		this.objTypeName = objTypeName;
	}
	public String getUpdateLink() {
		return updateLink;
	}
	public void setUpdateLink(String updateLink) {
		this.updateLink = updateLink;
	}
	public String getDeleteLink() {
		return deleteLink;
	}
	public void setDeleteLink(String deleteLink) {
		this.deleteLink = deleteLink;
	}
	public String getObjParentField() {
		return objParentField;
	}
	public void setObjParentField(String objParentField) {
		this.objParentField = objParentField;
	}
	
	
}
