package com.embraiz.model;

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
@Table(name="t_message_mail")
public class MessageMail {

	@Id
	@Column(name="mail_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer mailId;
	
	@Column(name="status")
	private String status;
	
	@Column(name="status_other")
	private String statusOther;//用來輔助查詢未send email的標誌，未send為0，已send為1
	
	@Column(name="mail_from")
	private String mailFrom;
	
	@Column(name="mail_to")
	private String mailTo;
	
	@Column(name="mail_cc")
	private String mailCc;//抄送邮件
	
	@Column(name="mail_bcc")
	private String mailBcc;//私密抄送邮件
	
	@Column(name="mail_subject")
	private String mailSubject;
	
	@Column(name="mail_content")
	private String mailContent;
	
	@Column(name="system_remark")
	private String systemRemark;
	
	@Column(name="session_id")
	private String sessionId;
	
	@Column(name="smtp")
	private String smtp;
	
	@Column(name="user_name")
	private String userName;
	
	@Column(name="password")
	private String password;
	
	@Column(name="insert_date")
	private String insertDate;
	
	@Column(name="send_date")
	private String sendDate;
	
	@Column(name="parent")
	private String parent;
	
	@Column(name="mass_id")
	private Integer massId;
	
	@Column(name="open_count")
	private Integer openCount;//邮件被打开次数
	
	@Column(name="attachments_name")
	private String attachmentsName;
	
	@Column(name="attachments_file")
	private String attachmentsFile;
	
	@Column(name="error_code")
	private String errorCode;
	
	@Column(name="return_time")
	private String returnTime;
	
	@Column(name="mail_code")
	private String mailCode;
	
	@Column(name="reply_email")
	private String replyEmail;
	
	@Column(name="template_file")
	private String templateFile;
	
	@Column(name="template_file_name")
	private String templateFileName;
	
	@Column(name="times")
	private Integer times;
	
	@Column(name="port")
	private Integer port;

	public Integer getMailId() {
		return mailId;
	}

	public void setMailId(Integer mailId) {
		this.mailId = mailId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusOther() {
		return statusOther;
	}

	public void setStatusOther(String statusOther) {
		this.statusOther = statusOther;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getMailTo() {
		return mailTo;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public String getMailCc() {
		return mailCc;
	}

	public void setMailCc(String mailCc) {
		this.mailCc = mailCc;
	}

	public String getMailBcc() {
		return mailBcc;
	}

	public void setMailBcc(String mailBcc) {
		this.mailBcc = mailBcc;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getMailContent() {
		return mailContent;
	}

	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	public String getSystemRemark() {
		return systemRemark;
	}

	public void setSystemRemark(String systemRemark) {
		this.systemRemark = systemRemark;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSmtp() {
		return smtp;
	}

	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}

	public String getSendDate() {
		return sendDate;
	}

	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public Integer getMassId() {
		return massId;
	}

	public void setMassId(Integer massId) {
		this.massId = massId;
	}

	public Integer getOpenCount() {
		return openCount;
	}

	public void setOpenCount(Integer openCount) {
		this.openCount = openCount;
	}

	public String getAttachmentsName() {
		return attachmentsName;
	}

	public void setAttachmentsName(String attachmentsName) {
		this.attachmentsName = attachmentsName;
	}

	public String getAttachmentsFile() {
		return attachmentsFile;
	}

	public void setAttachmentsFile(String attachmentsFile) {
		this.attachmentsFile = attachmentsFile;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(String returnTime) {
		this.returnTime = returnTime;
	}

	public String getMailCode() {
		return mailCode;
	}

	public void setMailCode(String mailCode) {
		this.mailCode = mailCode;
	}

	public String getReplyEmail() {
		return replyEmail;
	}

	public void setReplyEmail(String replyEmail) {
		this.replyEmail = replyEmail;
	}

	public String getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(String templateFile) {
		this.templateFile = templateFile;
	}

	public String getTemplateFileName() {
		return templateFileName;
	}

	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
	
	
}
