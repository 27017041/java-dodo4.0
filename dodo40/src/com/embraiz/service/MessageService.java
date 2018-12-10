package com.embraiz.service;

import java.util.List;

import com.embraiz.model.Message;
import com.embraiz.model.MessageGateway;
import com.embraiz.model.MessageMail;
import com.embraiz.model.MessageSend;
import com.embraiz.model.MessageTemplate;

public interface MessageService {

	/**
	 * 保存消息发送记录
	 * @param messageSends
	 */
	void addMessageSend(List<MessageSend> messageSends);
	
	/**
	 * 保存消息
	 * @param message
	 * @return
	 */
	int addMessage(Message message);
	
	/**
	 * 获取收件箱
	 * @param toId
	 * @return
	 */
	List<MessageSend> getInbox(Integer toId);
	
	/**
	 * 获取发件箱
	 * @param fromId
	 * @return
	 */
	List<MessageSend> getOutbox(Integer fromId);
	
	
	//By Andy
	
	/**
	 * 获取当前待发送的邮件列表(100条)
	 * @return
	 */
	List<Object> getMailToBeSend();
	
	/**
	 * 修改邮件列表的状态
	 * @param mailList
	 * @param status
	 * @return
	 */
	boolean updateMailStatusByList(List<Object> mailList,String status);
	
	/**
	 * 通过gatewayName获取messageGateway列表
	 * @param gatewayName
	 * @return
	 */
	List<Object> getGatewayByName(String gatewayName,String type);
	
	MessageTemplate getTemplateByName(String templateName);
	
	
}
