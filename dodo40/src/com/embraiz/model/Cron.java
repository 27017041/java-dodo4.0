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
@DynamicInsert(true)
@DynamicUpdate(true)
@Table(name="t_cron")
public class Cron {

	@Id
	@Column(name="cron_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer cronId;
	
	@Column(name="cron_name")
	private String cronName;
	
	@Column(name="month_day")
	private String monthDay;
	
	@Column(name="week_day")
	private String weekDay;
	
	@Column(name="hour_day")
	private String hourDay;
	
	@Column(name="minute_hour")
	private String minuteHour;
	
	@Column(name="last_run")
	private String lastRun;
	
	@Column(name="create_date")
	private String createDate;
	
	@Column(name="class_name")
	private String className;
	
	@Column(name="method_name")
	private String methodName;
	
	@Column(name="param_name")
	private String paramName;
	
	@Column(name="retention_time")
	private Integer retentionTime;

	public Integer getCronId() {
		return cronId;
	}

	public void setCronId(Integer cronId) {
		this.cronId = cronId;
	}

	public String getCronName() {
		return cronName;
	}

	public void setCronName(String cronName) {
		this.cronName = cronName;
	}

	public String getMonthDay() {
		return monthDay;
	}

	public void setMonthDay(String monthDay) {
		this.monthDay = monthDay;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public String getHourDay() {
		return hourDay;
	}

	public void setHourDay(String hourDay) {
		this.hourDay = hourDay;
	}

	public String getMinuteHour() {
		return minuteHour;
	}

	public void setMinuteHour(String minuteHour) {
		this.minuteHour = minuteHour;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public Integer getRetentionTime() {
		return retentionTime;
	}

	public void setRetentionTime(Integer retentionTime) {
		this.retentionTime = retentionTime;
	}

	public String getLastRun() {
		return lastRun;
	}

	public void setLastRun(String lastRun) {
		this.lastRun = lastRun;
	}
	
	
	
}
