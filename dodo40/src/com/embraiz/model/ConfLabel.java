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
@Table(name="conf_label")
public class ConfLabel {
	@Id
	@Column(name="label_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer labelId;
	
	@Column(name="label_text")
	private String labelText;
	
	@Column(name="lang")
	private String lang;
	
	@Column(name="module")
	private String module;
	
	@Column(name="label_orginal")
	private String labelOrginal;
	
	@Column(name="label_type")
	private Integer labelType;
	
	@Transient
	private String typeName;
	
	@Transient
	private String typeKey;

	public Integer getLabelId() {
		return labelId;
	}

	public void setLabelId(Integer labelId) {
		this.labelId = labelId;
	}

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getLabelOrginal() {
		return labelOrginal;
	}

	public void setLabelOrginal(String labelOrginal) {
		this.labelOrginal = labelOrginal;
	}

	public Integer getLabelType() {
		return labelType;
	}

	public void setLabelType(Integer labelType) {
		this.labelType = labelType;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeKey() {
		return typeKey;
	}

	public void setTypeKey(String typeKey) {
		this.typeKey = typeKey;
	}
}
