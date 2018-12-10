package com.embraiz.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate(true)
@DynamicInsert(true)
@Table(name = "t_contract")
public class Contract {

	@Id
	@Column(name = "contract_id")
	private Integer contractId;

	@Column(name = "quotation_id")
	private Integer quotationId;

	@Column(name = "title")
	private String title;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "remark")
	private String remark;

	@Column(name = "contract_no")
	private String contractNo;

	@Column(name = "reminder_interval")
	private Long reminderInterval;

	@Column(name = "reminder_type")
	private Integer reminderType;

	@Column(name = "client_id")
	private Integer clientId;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "contract_type")
	private Integer contractType;

	@Column(name = "create_by")
	private Integer createBy;

	@Column(name = "create_name")
	private String createName;

	@Column(name = "status")
	private Integer status;

	public Integer getContractId() {
		return contractId;
	}

	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
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

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getContractType() {
		return contractType;
	}

	public void setContractType(Integer contractType) {
		this.contractType = contractType;
	}

	public Integer getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Integer createBy) {
		this.createBy = createBy;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
