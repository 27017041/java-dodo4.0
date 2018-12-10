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
@DynamicInsert(true)
@DynamicUpdate(true)
@Table(name="t_message_log")
public class MessageLog {

	@Id
	@Column(name="log_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer logId;
	
	@Column(name="action")
	private String action;//insert or send
	
	@Column(name="msg_type")
	private String msgType;
	
	@Column(name="sent_by")
	private Integer sentBy;
	
	@Column(name="log_time")
	private String logTime;
	
	@Column(name="title")
	private String title;

	public Integer getLogId() {
		return logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public Integer getSentBy() {
		return sentBy;
	}

	public void setSentBy(Integer sentBy) {
		this.sentBy = sentBy;
	}

	public String getLogTime() {
		return logTime;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
