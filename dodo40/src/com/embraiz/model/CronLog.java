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
@Table(name="t_cron_log")
public class CronLog {

	@Id
	@Column(name="cron_log_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer cronLogId;
	
	@Column(name="cron_id")
	private Integer cronId;
	
	@Column(name="start_time")
	private String startTime;
	
	@Column(name="end_time")
	private String endTime;
	
	@Column(name="log")
	private String log;
	
	@Column(name="status")
	private String status;
	
	@Column(name="position")
	private String position;
	
	@Transient
	private String cronName;

	public Integer getCronLogId() {
		return cronLogId;
	}

	public void setCronLogId(Integer cronLogId) {
		this.cronLogId = cronLogId;
	}

	public Integer getCronId() {
		return cronId;
	}

	public void setCronId(Integer cronId) {
		this.cronId = cronId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getCronName() {
		return cronName;
	}

	public void setCronName(String cronName) {
		this.cronName = cronName;
	}
	
	
	
	
}
