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
@Table(name = "t_obj_contract_renew")
public class ContractRenew {

	@Id
	@Column(name = "renew_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer renewId;

	@Column(name = "contract_id")
	private Integer contractId;

	@Column(name = "client_id")
	private Integer clientId;

	@Column(name = "quotation_id")
	private Integer quotationId;

	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "remark")
	private String remark;

	@Column(name = "no")
	private Integer no;

	public Integer getRenewId() {
		return renewId;
	}

	public void setRenewId(Integer renewId) {
		this.renewId = renewId;
	}

	public Integer getContractId() {
		return contractId;
	}

	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
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

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

}
