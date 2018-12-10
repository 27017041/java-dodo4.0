package com.embraiz.model;

import java.util.Date;

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
@Table(name="t_backup_job")
public class BackupJob {
	
	@Id
	@Column(name="job_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer jobId;
	
	@Column(name="job_name")
	private String jobName;
	
	@Column(name="job_description")
	private String jobDescription;
	
	@Column(name="segment")
	private String segment;
	
	@Column(name="unit")
	private int unit;
	
	@Column(name="current_time")
	private Date currentTime;
	
	@Column(name="job_status")
	private String jobStatus;
	
	@Column(name="backup_folder")
	private String backupFolder;
	
	@Column(name="backup_number")
	private int backupNumber;
	
	@Column(name="backup_max")
	private int backupMax;
	
	
	public Integer getJobId() {
		return jobId;
	}
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobDescription() {
		return jobDescription;
	}
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public int getUnit() {
		return unit;
	}
	public void setUnit(int unit) {
		this.unit = unit;
	}
	public Date getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	public String getBackupFolder() {
		return backupFolder;
	}
	public void setBackupFolder(String backupFolder) {
		this.backupFolder = backupFolder;
	}
	public int getBackupNumber() {
		return backupNumber;
	}
	public void setBackupNumber(int backupNumber) {
		this.backupNumber = backupNumber;
	}
	public int getBackupMax() {
		return backupMax;
	}
	public void setBackupMax(int backupMax) {
		this.backupMax = backupMax;
	}
	
	
	
	
	
}
