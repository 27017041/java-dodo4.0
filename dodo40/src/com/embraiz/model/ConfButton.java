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
@Table(name="conf_button")
public class ConfButton {
	@Id
	@Column(name="button_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer buttonId;
	
	@Column(name="conf_id")
	private Integer confId;
	
	@Column(name="button_name")
	private String buttonName;
	
	@Column(name="module_name")
	private String moduleName;
	
	@Column(name="sorting")
	private int sorting;
	
	@Column(name="button_icon")
	private String buttonIcon;
	
	@Column(name="is_selected")
	private Boolean isSelected;
	
	@Column(name="show_right")
	private String showRight;
	
	@Column(name="method_name")
	private String methodName;
	
	/* labelText、labelType、langCode 应该是无用的字段,忽略该字段的映射   */
	@Transient
	private String labelText;
	@Transient
	private String labekType;
	@Transient
	private String langCode;
	
	
	public Integer getButtonId() {
		return buttonId;
	}
	public void setButtonId(Integer buttonId) {
		this.buttonId = buttonId;
	}
	public Integer getConfId() {
		return confId;
	}
	public void setConfId(Integer confId) {
		this.confId = confId;
	}
	public String getButtonName() {
		return buttonName;
	}
	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public int getSorting() {
		return sorting;
	}
	public void setSorting(int sorting) {
		this.sorting = sorting;
	}
	public String getButtonIcon() {
		return buttonIcon;
	}
	public void setButtonIcon(String buttonIcon) {
		this.buttonIcon = buttonIcon;
	}
	public Boolean getIsSelected() {
		return isSelected;
	}
	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}
	public String getShowRight() {
		return showRight;
	}
	public void setShowRight(String showRight) {
		this.showRight = showRight;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getLabelText() {
		return labelText;
	}
	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}
	public String getLabekType() {
		return labekType;
	}
	public void setLabekType(String labekType) {
		this.labekType = labekType;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	
	
	
	
}
