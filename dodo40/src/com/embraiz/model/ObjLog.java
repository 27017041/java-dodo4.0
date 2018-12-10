package com.embraiz.model;

import java.util.Date;

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
@Table(name = "obj_log")
public class ObjLog {

	@Transient
	private String userName;

	@Id
	@Column(name = "obj_log_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer objLogId;

	@Column(name = "obj_id")
	private Integer objId;

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "description")
	private String description;

	@Column(name = "action_date")
	private Date actionDate;

	@Column(name = "module_name")
	private String moduleName;

	@Column(name = "conf_id")
	private Integer confId;

	@Column(name = "field_name")
	private String fieldName;

	public Integer getObjLogId() {
		return objLogId;
	}

	public void setObjLogId(Integer objLogId) {
		this.objLogId = objLogId;
	}

	public Integer getObjId() {
		return objId;
	}

	public void setObjId(Integer objId) {
		this.objId = objId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Integer getConfId() {
		return confId;
	}

	public void setConfId(Integer confId) {
		this.confId = confId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
