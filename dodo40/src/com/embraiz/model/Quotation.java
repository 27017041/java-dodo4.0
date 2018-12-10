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
@Table(name = "t_quotation")
public class Quotation {

	@Transient
	private String companyName;
	@Transient
	private String clientName;
	@Transient
	private String currencyName;
	@Transient
	private String contactName;
	@Transient
	private String clientEmail;
	@Transient
	private String contactEmail;
	@Transient
	private String prepareBy;
	@Transient
	private String paymentPercent;
	@Transient
	private String quotationName;
	@Transient
	private String statusName;

	@Id
	@Column(name = "quotation_id")
	private Integer quotationId;

	@Column(name = "quotation_no")
	private String quotationNo;
	
	@Column(name = "company_id")
	private Integer companyId;

	@Column(name = "client_id")
	private Integer clientId;

	@Column(name = "contact_id")
	private Integer contactId;

	@Column(name = "status")
	private Integer status;

	@Column(name = "total_cost")
	private BigDecimal totalCost;

	@Column(name = "discount")
	private BigDecimal discount;

	@Column(name = "expiry_date")
	private String expiryDate;

	@Column(name = "title")
	private String title;

	@Column(name = "currency")
	private Integer currency;

	@Column(name = "exchange_rate")
	private BigDecimal exchangeRate;

	@Column(name = "local_currency")
	private BigDecimal localCurrency;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "invoice_id")
	private Integer invoiceId;

	@Column(name = "balance")
	private BigDecimal balance;

	@Column(name = "payment")
	private BigDecimal payment;

	@Column(name = "is_approval")
	private Integer isApproval;

	@Column(name = "approval_ip")
	private String approvalIp;

	@Column(name = "approval_date")
	private Date approvalDate;
	
	@Column(name = "linkage_module")
	private String linkageModule;

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	public Integer getQuotationId() {
		return quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

	public String getQuotationNo() {
		return quotationNo;
	}

	public void setQuotationNo(String quotationNo) {
		this.quotationNo = quotationNo;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getCurrency() {
		return currency;
	}

	public void setCurrency(Integer currency) {
		this.currency = currency;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public BigDecimal getLocalCurrency() {
		return localCurrency;
	}

	public void setLocalCurrency(BigDecimal localCurrency) {
		this.localCurrency = localCurrency;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getPrepareBy() {
		return prepareBy;
	}

	public void setPrepareBy(String prepareBy) {
		this.prepareBy = prepareBy;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public Integer getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getPayment() {
		return payment;
	}

	public void setPayment(BigDecimal payment) {
		this.payment = payment;
	}

	public String getPaymentPercent() {
		return paymentPercent;
	}

	public void setPaymentPercent(String paymentPercent) {
		this.paymentPercent = paymentPercent;
	}

	public String getQuotationName() {
		return quotationName;
	}

	public void setQuotationName(String quotationName) {
		this.quotationName = quotationName;
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

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getLinkageModule() {
		return linkageModule;
	}

	public void setLinkageModule(String linkageModule) {
		this.linkageModule = linkageModule;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	
}
