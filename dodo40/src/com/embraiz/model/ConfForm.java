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
@DynamicInsert(true)
@DynamicUpdate(true)
@Table(name="conf_form")
public class ConfForm {
	@Id
	@Column(name="form_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer formId;
	
	@Column(name="field_name")
	private String fieldName;
	
	@Column(name="sorting")
	private Integer sorting;
	
	@Column(name="conf_id")
	private Integer confId;
	
	@Column(name="field_type")
	private String fieldType;
	
	@Column(name="module_name")
	private String moduleName;
	
	@Column(name="select_table")
	private String selectTable;
	
	@Column(name="select_is_obj_data")
	private Integer selectIsObjData;
	
	@Column(name="select_id_field")
	private String selectIdField;
	
	@Column(name="select_value_field")
	private String selectValueField;
	
	@Column(name="select_order_field")
	private String selectOrderField;
	
	@Column(name="sub_to")
	private Integer subTo;
	
	@Column(name="sub_from")
	private Integer subFrom;
	
	@Column(name="colspan")
	private int colspan;
	
	@Column(name="default_value")
	private String defaultValue;
	
	@Column(name="readonly")
	private Boolean readonly;
	
	@Column(name="allowblank")
	private Boolean allowblank;
	
	@Column(name="display")
	private Boolean display;
	
	@Column(name="input_type")
	private String inputType;
	
	@Column(name="validator_min")
	private String validatorMin;
	
	@Column(name="validator_max")
	private String validatorMax;
	
	@Column(name="validator_email")
	private Boolean validatorEmail;
	
	@Column(name="validator_min_length")
	private String validatorMinLength;
	
	@Column(name="validator_max_length")
	private String validatorMaxLength;
	
	@Column(name="validator_pattern")
	private String validatorPattern;
	
	@Column(name="validator_required_lable")
	private String validatorRequiredLable;
	
	@Column(name="validator_min_lable")
	private String validatorMinLable;
	
	@Column(name="validator_max_lable")
	private String validatorMaxLable;
	
	@Column(name="validator_email_lable")
	private String validatorEmailLable;
	
	@Column(name="validator_min_length_lable")
	private String validatorMinLengthLable;
	
	@Column(name="validator_max_length_lable")
	private String validatorMaxLengthLable;
	
	@Column(name="validator_pattern_lable")
	private String validatorPatternLable;
	
	@Column(name="select_type_field")
	private String selectTypeField;
	
	@Column(name="select_type_id")
	private Integer selectTypeId;
	
	/*  labelText、labelType、langCode 应该是无用的字段,忽略该字段的映射    */
	@Transient
	private String labelText;
	@Transient
	private String labelType;
	@Transient
	private String langCode;
	
	
	public Integer getFormId() {
		return formId;
	}
	public void setFormId(Integer formId) {
		this.formId = formId;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Integer getSorting() {
		return sorting;
	}
	public void setSorting(Integer sorting) {
		this.sorting = sorting;
	}
	public Integer getConfId() {
		return confId;
	}
	public void setConfId(Integer confId) {
		this.confId = confId;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getSelectTable() {
		return selectTable;
	}
	public void setSelectTable(String selectTable) {
		this.selectTable = selectTable;
	}
	public String getSelectIdField() {
		return selectIdField;
	}
	public void setSelectIdField(String selectIdField) {
		this.selectIdField = selectIdField;
	}
	public String getSelectValueField() {
		return selectValueField;
	}
	public void setSelectValueField(String selectValueField) {
		this.selectValueField = selectValueField;
	}
	public String getSelectOrderField() {
		return selectOrderField;
	}
	public void setSelectOrderField(String selectOrderField) {
		this.selectOrderField = selectOrderField;
	}
	public int getColspan() {
		return colspan;
	}
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public Boolean getReadonly() {
		return readonly;
	}
	public void setReadonly(Boolean readonly) {
		this.readonly = readonly;
	}
	public Boolean getAllowblank() {
		return allowblank;
	}
	public void setAllowblank(Boolean allowblank) {
		this.allowblank = allowblank;
	}
	public Boolean getDisplay() {
		return display;
	}
	public void setDisplay(Boolean display) {
		this.display = display;
	}
	public String getLabelText() {
		return labelText;
	}
	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}
	public String getLabelType() {
		return labelType;
	}
	public void setLabelType(String labelType) {
		this.labelType = labelType;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	public Integer getSelectIsObjData() {
		return selectIsObjData;
	}
	public void setSelectIsObjData(Integer selectIsObjData) {
		this.selectIsObjData = selectIsObjData;
	}
	public Integer getSubTo() {
		return subTo;
	}
	public void setSubTo(Integer subTo) {
		this.subTo = subTo;
	}
	public Integer getSubFrom() {
		return subFrom;
	}
	public void setSubFrom(Integer subFrom) {
		this.subFrom = subFrom;
	}
	public String getInputType() {
		return inputType;
	}
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	public String getValidatorMin() {
		return validatorMin;
	}
	public void setValidatorMin(String validatorMin) {
		this.validatorMin = validatorMin;
	}
	public String getValidatorMax() {
		return validatorMax;
	}
	public void setValidatorMax(String validatorMax) {
		this.validatorMax = validatorMax;
	}
	public Boolean getValidatorEmail() {
		return validatorEmail;
	}
	public void setValidatorEmail(Boolean validatorEmail) {
		this.validatorEmail = validatorEmail;
	}
	public String getValidatorMinLength() {
		return validatorMinLength;
	}
	public void setValidatorMinLength(String validatorMinLength) {
		this.validatorMinLength = validatorMinLength;
	}
	public String getValidatorMaxLength() {
		return validatorMaxLength;
	}
	public void setValidatorMaxLength(String validatorMaxLength) {
		this.validatorMaxLength = validatorMaxLength;
	}
	public String getValidatorPattern() {
		return validatorPattern;
	}
	public void setValidatorPattern(String validatorPattern) {
		this.validatorPattern = validatorPattern;
	}
	public String getValidatorRequiredLable() {
		return validatorRequiredLable;
	}
	public void setValidatorRequiredLable(String validatorRequiredLable) {
		this.validatorRequiredLable = validatorRequiredLable;
	}
	public String getValidatorMinLable() {
		return validatorMinLable;
	}
	public void setValidatorMinLable(String validatorMinLable) {
		this.validatorMinLable = validatorMinLable;
	}
	public String getValidatorMaxLable() {
		return validatorMaxLable;
	}
	public void setValidatorMaxLable(String validatorMaxLable) {
		this.validatorMaxLable = validatorMaxLable;
	}
	public String getValidatorEmailLable() {
		return validatorEmailLable;
	}
	public void setValidatorEmailLable(String validatorEmailLable) {
		this.validatorEmailLable = validatorEmailLable;
	}
	public String getValidatorMinLengthLable() {
		return validatorMinLengthLable;
	}
	public void setValidatorMinLengthLable(String validatorMinLengthLable) {
		this.validatorMinLengthLable = validatorMinLengthLable;
	}
	public String getValidatorMaxLengthLable() {
		return validatorMaxLengthLable;
	}
	public void setValidatorMaxLengthLable(String validatorMaxLengthLable) {
		this.validatorMaxLengthLable = validatorMaxLengthLable;
	}
	public String getValidatorPatternLable() {
		return validatorPatternLable;
	}
	public void setValidatorPatternLable(String validatorPatternLable) {
		this.validatorPatternLable = validatorPatternLable;
	}
	public String getSelectTypeField() {
		return selectTypeField;
	}
	public void setSelectTypeField(String selectTypeField) {
		this.selectTypeField = selectTypeField;
	}
	public Integer getSelectTypeId() {
		return selectTypeId;
	}
	public void setSelectTypeId(Integer selectTypeId) {
		this.selectTypeId = selectTypeId;
	}
	
}
