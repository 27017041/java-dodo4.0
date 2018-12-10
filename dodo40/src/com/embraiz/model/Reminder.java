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
@Table(name = "t_reminder")
public class Reminder {

	@Transient
	private Integer EmailTempId;
	@Transient
	private Object classObject;

	@Id
	@Column(name = "reminder_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer reminderId;

	@Column(name = "reminder_time")
	private Date reminderTime;

	@Column(name = "subject")
	private String subject;

	@Column(name = "reminder_content")
	private String reminderContent;

	@Column(name = "email")
	private String email;

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "module")
	private String module;

	@Column(name = "module_id")
	private Integer moduleId;

	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "reminder_interval")
	private Long reminderInterval;

	@Column(name = "reminder_type")
	private Integer reminderType;

	@Column(name = "reminder_status")
	private Integer reminderStatus;
	
	@Column(name = "status")
	private Integer status;


	public Integer getEmailTempId() {
		return EmailTempId;
	}

	public void setEmailTempId(Integer emailTempId) {
		EmailTempId = emailTempId;
	}

	public Object getClassObject() {
		return classObject;
	}

	public void setClassObject(Object classObject) {
		this.classObject = classObject;
	}

	public Integer getReminderId() {
		return reminderId;
	}

	public void setReminderId(Integer reminderId) {
		this.reminderId = reminderId;
	}

	public Date getReminderTime() {
		return reminderTime;
	}

	public void setReminderTime(Date reminderTime) {
		this.reminderTime = reminderTime;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getReminderContent() {
		return reminderContent;
	}

	public void setReminderContent(String reminderContent) {
		this.reminderContent = reminderContent;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Long getReminderInterval() {
		return reminderInterval;
	}

	public void setReminderInterval(Long reminderInterval) {
		this.reminderInterval = reminderInterval;
	}

	public Integer getReminderType() {
		return reminderType;
	}

	public void setReminderType(Integer reminderType) {
		this.reminderType = reminderType;
	}

	public Integer getReminderStatus() {
		return reminderStatus;
	}

	public void setReminderStatus(Integer reminderStatus) {
		this.reminderStatus = reminderStatus;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
