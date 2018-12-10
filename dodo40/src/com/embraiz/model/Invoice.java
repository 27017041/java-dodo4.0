package com.embraiz.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate(true)
@DynamicInsert(true)
@Table(name = "t_invoice")
public class Invoice {

	@Transient
	private String companyName;
	@Transient
	private String payername;

	@Id
	@Column(name = "invoice_id")
	private Integer invoiceId;

	@Column(name = "invoice_no")
	private String invoiceNo;

	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "payment")
	private BigDecimal payment;

	@Column(name = "balance")
	private BigDecimal balance;

	@Column(name = "cnote_no")
	private Integer cnoteNo;

	@Column(name = "paid")
	private Integer paid;

	@Column(name = "invoice_pay_due")
	private Date invoicePayDue;

	@Column(name = "payer")
	private Integer payer;

	@Column(name = "is_approval")
	private Integer isApproval;

	@Column(name = "approval_ip")
	private String approvalIp;

	@Column(name = "approval_date")
	private Date approvalDate;

	@Column(name = "void_remark")
	private String voidRemark;

	@Column(name = "console_amount")
	private BigDecimal consoleAmount;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "status")
	private Integer status;

	@Transient
	private String quotationAmount;
	@Transient
	private String quotationNo;
	@Transient
	private String statusName;
	@Transient
	private Integer quotationId;

	public Integer getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getPayment() {
		return payment;
	}

	public void setPayment(BigDecimal payment) {
		this.payment = payment;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Integer getCnoteNo() {
		return cnoteNo;
	}

	public void setCnoteNo(Integer cnoteNo) {
		this.cnoteNo = cnoteNo;
	}

	public Integer getPaid() {
		return paid;
	}

	public void setPaid(Integer paid) {
		this.paid = paid;
	}

	public Date getInvoicePayDue() {
		return invoicePayDue;
	}

	public void setInvoicePayDue(Date invoicePayDue) {
		this.invoicePayDue = invoicePayDue;
	}

	public Integer getPayer() {
		return payer;
	}

	public void setPayer(Integer payer) {
		this.payer = payer;
	}

	public Integer getIsApproval() {
		return isApproval;
	}

	public void setIsApproval(Integer isApproval) {
		this.isApproval = isApproval;
	}

	public String getApprovalIp() {
		return approvalIp;
	}

	public void setApprovalIp(String approvalIp) {
		this.approvalIp = approvalIp;
	}

	public Date getApprovalDate() {
		return approvalDate;
	}

	public void setApprovalDate(Date approvalDate) {
		this.approvalDate = approvalDate;
	}

	public String getVoidRemark() {
		return voidRemark;
	}

	public void setVoidRemark(String voidRemark) {
		this.voidRemark = voidRemark;
	}

	public BigDecimal getConsoleAmount() {
		return consoleAmount;
	}

	public void setConsoleAmount(BigDecimal consoleAmount) {
		this.consoleAmount = consoleAmount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPayername() {
		return payername;
	}

	public void setPayername(String payername) {
		this.payername = payername;
	}

	public String getQuotationAmount() {
		return quotationAmount;
	}

	public void setQuotationAmount(String quotationAmount) {
		this.quotationAmount = quotationAmount;
	}

	public String getQuotationNo() {
		return quotationNo;
	}

	public void setQuotationNo(String quotationNo) {
		this.quotationNo = quotationNo;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

}
