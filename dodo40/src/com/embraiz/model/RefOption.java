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
@Table(name="ref_option")
public class RefOption {

	@Id
	@Column(name="option_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer optionId;
	
	@Column(name="type_id")
	private Integer typeId;
	
	@Column(name="option_key")
	private String optionKey;
	
	@Column(name="option_name_en")
	private String optionNameEn;
	
	@Column(name="option_name_cn")
	private String optionNameCn;
	
	@Column(name="option_name_tc")
	private String optionNameTc;
	
	@Column(name="parent_id")
	private Integer parentId;
	
	@Column(name="sort")
	private Integer sort;
	
	@Column(name="module_name")
	private String moduleName;

	public Integer getOptionId() {
		return optionId;
	}

	public void setOptionId(Integer optionId) {
		this.optionId = optionId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getOptionKey() {
		return optionKey;
	}

	public void setOptionKey(String optionKey) {
		this.optionKey = optionKey;
	}

	public String getOptionNameEn() {
		return optionNameEn;
	}

	public void setOptionNameEn(String optionNameEn) {
		this.optionNameEn = optionNameEn;
	}

	public String getOptionNameCn() {
		return optionNameCn;
	}

	public void setOptionNameCn(String optionNameCn) {
		this.optionNameCn = optionNameCn;
	}

	public String getOptionNameTc() {
		return optionNameTc;
	}

	public void setOptionNameTc(String optionNameTc) {
		this.optionNameTc = optionNameTc;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	
}
