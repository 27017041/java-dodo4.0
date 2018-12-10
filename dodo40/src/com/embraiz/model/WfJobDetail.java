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
@DynamicUpdate(true)
@DynamicInsert(true)
@Table(name = "wf_job_detail")
public class WfJobDetail {

	@Id
	@Column(name = "job_detail_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer jobDetailId;

	@Column(name = "step_status")
	private String stepStatus;

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "step_id")
	private Integer stepId;

	@Column(name = "action_time")
	private Date actionDate;

	@Column(name = "job_id")
	private Integer jobid;

	public Integer getJobDetailId() {
		return jobDetailId;
	}

	public void setJobDetailId(Integer jobDetailId) {
		this.jobDetailId = jobDetailId;
	}

	public String getStepStatus() {
		return stepStatus;
	}

	public void setStepStatus(String stepStatus) {
		this.stepStatus = stepStatus;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getStepId() {
		return stepId;
	}

	public void setStepId(Integer stepId) {
		this.stepId = stepId;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public Integer getJobid() {
		return jobid;
	}

	public void setJobid(Integer jobid) {
		this.jobid = jobid;
	}

}
