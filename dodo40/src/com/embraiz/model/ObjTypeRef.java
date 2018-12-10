package com.embraiz.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate(true)
@DynamicInsert(true)
@Table(name="obj_type_ref")
public class ObjTypeRef {
	@Id
	@Column(name="obj_type_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer objTypeId;
	
	@Column(name="obj_title_title")
	private String objTitleTitle;
	
	@Column(name="keyword_field")
	private String keywordField;
	
	@Column(name="icon")
	private String icon;
	
	@Column(name="display_name")
	private String displayName;
	
	@Column(name="module_name")
	private String moduleName;
	
	@Column(name="is_searchable")
	private Integer isSearchable;
	
	@Transient
	private Integer moduleCount;

	public Integer getObjTypeId() {
		return objTypeId;
	}

	public void setObjTypeId(Integer objTypeId) {
		this.objTypeId = objTypeId;
	}

	public String getObjTitleTitle() {
		return objTitleTitle;
	}

	public void setObjTitleTitle(String objTitleTitle) {
		this.objTitleTitle = objTitleTitle;
	}

	public String getKeywordField() {
		return keywordField;
	}

	public void setKeywordField(String keywordField) {
		this.keywordField = keywordField;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Integer getIsSearchable() {
		return isSearchable;
	}

	public void setIsSearchable(Integer isSearchable) {
		this.isSearchable = isSearchable;
	}
	
	public Integer getModuleCount() {
		return moduleCount;
	}

	public void setModuleCount(Integer moduleCount) {
		this.moduleCount = moduleCount;
	}
	
	
}
