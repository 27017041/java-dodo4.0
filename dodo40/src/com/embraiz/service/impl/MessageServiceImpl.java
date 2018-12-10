package com.embraiz.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.model.Message;
import com.embraiz.model.MessageGateway;
import com.embraiz.model.MessageMail;
import com.embraiz.model.MessageSend;
import com.embraiz.model.MessageTemplate;
import com.embraiz.service.MessageService;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

	@Resource
	private BaseDao baseDao;
	
	@Override
	public void addMessageSend(List<MessageSend> messageSends) {
		Session session = null;  
		if(messageSends != null && messageSends.size() > 0){
			try {
				session = baseDao.getSessionFactory().getCurrentSession(); // 获取Session  
				session.beginTransaction(); // 开启事物  
				int i=0;
				for(MessageSend messageSend : messageSends){
					session.save(messageSend); //保存发送记录
					// 批插入的对象立即写入数据库并释放内存  
					if (i % 10 == 0) {  
	                    session.flush();  
	                    session.clear();  
	                }
					i++;
				}
				session.getTransaction().commit(); // 提交事物  
			} catch (Exception e) {
				e.printStackTrace(); // 打印错误信息  
                session.getTransaction().rollback(); // 出错将回滚事物  
			} finally {  
				session.close();
            }  
		}
	}

	@Override
	public int addMessage(Message message) {
		return (Integer) baseDao.save(message);
	}

	@Override
	public List<MessageSend> getInbox(Integer toId) {
		return null;
	}

	@Override
	public List<MessageSend> getOutbox(Integer fromId) {
		return null;
	}
	
	@Override
	public List<Object> getMailToBeSend(){
		String sql = "SELECT * from t_message_mail where status = :status and status_other = :statusOther limit 0,100";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("status", "w");
		params.put("statusOther", 0);
		return baseDao.getList(params, sql, MessageMail.class);
	}
	
	@Override
	public boolean updateMailStatusByList(List<Object> mailList,String status){
		String mailIds = "";
		
		for(int i=0;i<mailList.size();i++){
			MessageMail msgMail = (MessageMail)mailList.get(i);
			mailIds += msgMail.getMailId() + ",";
		}
		
		mailIds = mailIds.substring(0, mailIds.length()-1);
		
		String sql = "update t_message_mail set status = '"+status+"' where mail_id in ("+mailIds+") ";
		Integer rtn = baseDao.updateBySql(sql, null);
		if(rtn>0){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public List<Object> getGatewayByName(String gatewayName,String type){
		String hql = "from MessageGateway where gatewayName = :gatewayName and gatewayType = :type";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("gatewayName", gatewayName);
		params.put("type", type);
		return baseDao.getListByHql(hql, params);
	}
	
	@Override
	public MessageTemplate getTemplateByName(String templateName){
		String hql = "from MessageTemplate where templateName = :templateName";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("templateName", templateName);
		return (MessageTemplate)baseDao.getObjectByHql(hql, params);
	}
	
	
}
