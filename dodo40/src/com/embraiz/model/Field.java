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
@Table(name="core_field")
public class Field {

	@Id
	@Column(name="field_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer fieldId;
	
	@Column(name="field_type")
	private String fieldType;
	
	@Column(name="validation")
	private String validation;
	
	@Column(name="pattern")
	private String pattern;
	
	@Column(name="field_label")
	private String fieldLabel;
	
	@Column(name="module_name")
	private String moduleName;
	
	@Column(name="identifier")
	private String identifier;
	
	@Column(name="select_type_id")
	private Integer selectTypeId;
	
	@Column(name="min_value")
	private String minValue;
	
	@Column(name="max_value")
	private String maxValue;
	
	@Column(name="min_length_value")
	private Integer minLengthValue;
	
	@Column(name="max_length_value")
	private Integer maxLengthValue;
	
	@Column(name="is_display")
	private Integer isDisplay;
	
	@Column(name = "is_disabled")
	private Integer isDisabled;
	
	@Column(name = "is_readonly")
	private Integer isReadonly;
	
	@Column(name = "linkage_module")
	private String linkageModule;

	@Column(name = "linkage_views")
	private String linkageViews;
	
	@Column(name = "linkage_key")
	private String linkageKey;
	
	public Integer getFieldId() {
		return fieldId;
	}

	public void setFieldId(Integer fieldId) {
		this.fieldId = fieldId;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getFieldLabel() {
		return fieldLabel;
	}

	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Integer getSelectTypeId() {
		return selectTypeId;
	}

	public void setSelectTypeId(Integer selectTypeId) {
		this.selectTypeId = selectTypeId;
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public Integer getMinLengthValue() {
		return minLengthValue;
	}

	public void setMinLengthValue(Integer minLengthValue) {
		this.minLengthValue = minLengthValue;
	}

	public Integer getMaxLengthValue() {
		return maxLengthValue;
	}

	public void setMaxLengthValue(Integer maxLengthValue) {
		this.maxLengthValue = maxLengthValue;
	}

	public Integer getIsDisplay() {
		return isDisplay;
	}

	public void setIsDisplay(Integer isDisplay) {
		this.isDisplay = isDisplay;
	}

	public Integer getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Integer isDisabled) {
		this.isDisabled = isDisabled;
	}

	public Integer getIsReadonly() {
		return isReadonly;
	}

	public void setIsReadonly(Integer isReadonly) {
		this.isReadonly = isReadonly;
	}

	public String getLinkageModule() {
		return linkageModule;
	}

	public void setLinkageModule(String linkageModule) {
		this.linkageModule = linkageModule;
	}

	public String getLinkageViews() {
		return linkageViews;
	}

	public void setLinkageViews(String linkageViews) {
		this.linkageViews = linkageViews;
	}

	public String getLinkageKey() {
		return linkageKey;
	}

	public void setLinkageKey(String linkageKey) {
		this.linkageKey = linkageKey;
	}
	
	
}
