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
@Table(name="ref_option_type")
public class RefOptionType {

	@Id
	@Column(name="type_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer typeId;
	
	@Column(name="type_name_en")
	private String typeNameEn;
	
	@Column(name="type_name_cn")
	private String typeNameCn;
	
	@Column(name="type_name_tc")
	private String typeNameTc;
	
	@Column(name="parent_type_id")
	private Integer parentTypeId;
	
	@Column(name="module_name")
	private String moduleName;

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getTypeNameEn() {
		return typeNameEn;
	}

	public void setTypeNameEn(String typeNameEn) {
		this.typeNameEn = typeNameEn;
	}

	public String getTypeNameCn() {
		return typeNameCn;
	}

	public void setTypeNameCn(String typeNameCn) {
		this.typeNameCn = typeNameCn;
	}

	public String getTypeNameTc() {
		return typeNameTc;
	}

	public void setTypeNameTc(String typeNameTc) {
		this.typeNameTc = typeNameTc;
	}

	public Integer getParentTypeId() {
		return parentTypeId;
	}

	public void setParentTypeId(Integer parentTypeId) {
		this.parentTypeId = parentTypeId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	
}
