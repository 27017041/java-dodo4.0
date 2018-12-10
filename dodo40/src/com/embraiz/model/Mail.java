package com.embraiz.model;


public class Mail {
	private Long mailId;
	private String sendFrom;
	
	//接收者可以是以英文字符,来分割多个地址
	private String sendTo;
	private String sendCc;
	private String sendBcc;
	private Long ownerId;
	private String subject;
	private String content;
	private String createDate;
	private String sentDate;
	private int status;
	private int sentNumber;
	
	
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public Long getMailId() {
		return mailId;
	}
	public void setMailId(Long mailId) {
		this.mailId = mailId;
	}
	public String getSendFrom() {
		return sendFrom;
	}
	public void setSendFrom(String sendFrom) {
		this.sendFrom = sendFrom;
	}
	public String getSendTo() {
		return sendTo;
	}
	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}
	public String getSendCc() {
		return sendCc;
	}
	public void setSendCc(String sendCc) {
		this.sendCc = sendCc;
	}
	public String getSendBcc() {
		return sendBcc;
	}
	public void setSendBcc(String sendBcc) {
		this.sendBcc = sendBcc;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getSentDate() {
		return sentDate;
	}
	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getSentNumber() {
		return sentNumber;
	}
	public void setSentNumber(int sentNumber) {
		this.sentNumber = sentNumber;
	}
	
	

}
