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
@Table(name = "wf_job")
public class WfJob {

	@Id
	@Column(name = "job_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer joblId;

	@Column(name = "module_id")
	private Integer moduleId;

	@Column(name = "route_id")
	private Integer routeId;

	@Column(name = "job_status")
	private String jobStatus;

	@Column(name = "action_time")
	private Date actionDate;

	@Column(name = "submitted_by")
	private Integer submittedBy;

	@Column(name = "current_approver")
	private Integer currentApprover;

	public Integer getJoblId() {
		return joblId;
	}

	public void setJoblId(Integer joblId) {
		this.joblId = joblId;
	}

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	public Integer getRouteId() {
		return routeId;
	}

	public void setRouteId(Integer routeId) {
		this.routeId = routeId;
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public Integer getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(Integer submittedBy) {
		this.submittedBy = submittedBy;
	}

	public Integer getCurrentApprover() {
		return currentApprover;
	}

	public void setCurrentApprover(Integer currentApprover) {
		this.currentApprover = currentApprover;
	}

}
