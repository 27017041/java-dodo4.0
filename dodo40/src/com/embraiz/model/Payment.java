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
@Table(name = "t_payment")
public class Payment {

	@Transient
	private String quotationNo;
	@Transient
	private String invoiceNo;
	@Transient
	private String currencyName;
	@Transient
	private String userName;
	@Transient
	private String clientName;
	@Transient
	private String contactName;
	@Transient
	private Integer invoiceId;

	@Id
	@Column(name = "payment_id")
	private Integer paymentId;

	@Column(name = "payment_no")
	private String paymentNo;

	@Column(name = "payer")
	private String payer;

	@Column(name = "cash")
	private BigDecimal cash;

	@Column(name = "cheque")
	private BigDecimal cheque;

	@Column(name = "credit_card")
	private BigDecimal creditCard;

	@Column(name = "eps")
	private BigDecimal eps;

	@Column(name = "total_amount")
	private BigDecimal totalAmount;

	@Column(name = "banked")
	private Integer banked;

	@Column(name = "method_id")
	private Integer methodId;

	@Column(name = "print_rcpt")
	private Integer printRcpt;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "staff")
	private Integer staff;

	@Column(name = "cheque_no")
	private String chequeNo;

	@Column(name = "cheque_date")
	private Date chequeDate;

	@Column(name = "bank")
	private String bank;

	@Column(name = "branch")
	private String branch;

	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "console_amount")
	private BigDecimal consoleAmount;

	@Column(name = "date")
	private Date date;

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	public BigDecimal getCash() {
		return cash;
	}

	public void setCash(BigDecimal cash) {
		this.cash = cash;
	}

	public BigDecimal getCheque() {
		return cheque;
	}

	public void setCheque(BigDecimal cheque) {
		this.cheque = cheque;
	}

	public BigDecimal getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(BigDecimal creditCard) {
		this.creditCard = creditCard;
	}

	public BigDecimal getEps() {
		return eps;
	}

	public void setEps(BigDecimal eps) {
		this.eps = eps;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getBanked() {
		return banked;
	}

	public void setBanked(Integer banked) {
		this.banked = banked;
	}

	public Integer getPrintRcpt() {
		return printRcpt;
	}

	public void setPrintRcpt(Integer printRcpt) {
		this.printRcpt = printRcpt;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getStaff() {
		return staff;
	}

	public void setStaff(Integer staff) {
		this.staff = staff;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getConsoleAmount() {
		return consoleAmount;
	}

	public void setConsoleAmount(BigDecimal consoleAmount) {
		this.consoleAmount = consoleAmount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Integer getMethodId() {
		return methodId;
	}

	public void setMethodId(Integer methodId) {
		this.methodId = methodId;
	}

	public String getQuotationNo() {
		return quotationNo;
	}

	public void setQuotationNo(String quotationNo) {
		this.quotationNo = quotationNo;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

}
