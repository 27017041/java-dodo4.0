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
@Table(name="t_message_send")
public class MessageSend {
	
	@Id
	@Column(name="send_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer sendId;
	
	@Column(name="msg_id")
	private Integer msgId;
	
	@Column(name="from_id")
	private Integer fromId;
	
	@Column(name="to_id")
	private Integer toId;
	
	@Column(name="send_date")
	private Integer sendDate;
	
	@Column(name="is_read")
	private Integer isRead;

	public Integer getSendId() {
		return sendId;
	}

	public void setSendId(Integer sendId) {
		this.sendId = sendId;
	}

	public Integer getMsgId() {
		return msgId;
	}

	public void setMsgId(Integer msgId) {
		this.msgId = msgId;
	}

	public Integer getFromId() {
		return fromId;
	}

	public void setFromId(Integer fromId) {
		this.fromId = fromId;
	}

	public Integer getToId() {
		return toId;
	}

	public void setToId(Integer toId) {
		this.toId = toId;
	}

	public Integer getSendDate() {
		return sendDate;
	}

	public void setSendDate(Integer sendDate) {
		this.sendDate = sendDate;
	}

	public Integer getIsRead() {
		return isRead;
	}

	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}
}
