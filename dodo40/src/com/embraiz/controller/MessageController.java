package com.embraiz.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.PathParam;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.MessageGateway;
import com.embraiz.model.MessageMail;
import com.embraiz.model.MessageTemplate;
import com.embraiz.model.RefOption;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.MessageService;
import com.embraiz.util.ExceptionFormat;
import com.embraiz.util.MainUtil;
import com.embraiz.util.SendMail;

@Controller
@RequestMapping("/message")
public class MessageController {
	
	@Resource
	private MessageService messageService;
	@Resource
	private BaseService baseService;

	//自动发送邮件
	@RequestMapping("autoSendMail")
	public void autoSendMail(){
		//获取status为w的邮件列表
		List<Object> mailList = messageService.getMailToBeSend();
		
		if(mailList!=null && mailList.size()>0){
			//先将所有待发送邮件的status设为p（发送中）,避免被重复读取
			boolean rtn = messageService.updateMailStatusByList(mailList, "p");
			
			if(rtn){
				
				SendMail sendMail = new SendMail();
				
				for(int i=0;i<mailList.size();i++){
					
					//发送邮件
					MessageMail messageMailDB = (MessageMail)mailList.get(i);
					int m=0;
					//若发送失败，则再发送2次
					while(m<3){
						m++;
						try {
							sendMail.sendMail(messageMailDB.getMailFrom(), messageMailDB.getMailTo(), messageMailDB.getMailCc(), messageMailDB.getMailBcc(), messageMailDB.getMailSubject(), messageMailDB.getMailContent(), messageMailDB.getSmtp(), messageMailDB.getUserName(), messageMailDB.getPassword(), messageMailDB.getPort(), messageMailDB.getAttachmentsFile());
							messageMailDB.setStatus("S");
							messageMailDB.setErrorCode(null);
							break;
						} catch (Exception e) {
							messageMailDB.setErrorCode(ExceptionFormat.formatError(e));
							messageMailDB.setStatus("F");
						}
						
					}

					//修改邮件状态
					messageMailDB.setStatusOther("1");
					messageMailDB.setTimes(m);
					messageMailDB.setSendDate(MainUtil.getTime());
					baseService.update(messageMailDB);
					
				}
				
			}
		}
	}
	
	//可调用发送邮件
	@RequestMapping("sendMessage")
	public void sendMessage(
			String gatewayName, 
			String templateName, 
			String variableString, 
			String type,String mailTo,
			HttpServletResponse response) throws IOException{
		boolean rtn = false;
		JSONObject json = new JSONObject();
		
		MessageTemplate messageTemplateDB = (MessageTemplate)messageService.getTemplateByName(templateName);
		if(messageTemplateDB!=null){
			String content = messageTemplateDB.getContentEmail();
			
			if(variableString!=null && variableString.length()>0){
				String variableArray [] = variableString.split(",");
				
				for(int i=0;i<variableArray.length;i++){
					String subVariableArray[] = variableArray[i].split("=");
					content = content.replace(subVariableArray[0], subVariableArray[1]);
				}
			}
			
			List<Object> list = messageService.getGatewayByName(gatewayName,type);
			if(list!=null && list.size()>0){
				for(int i=0;i<list.size();i++){
					MessageGateway messageGateway = (MessageGateway)list.get(i);
					
					MessageMail messageMail = new MessageMail();
					messageMail.setMailTo(mailTo);
					messageMail.setMailContent(content);
					messageMail.setSmtp(messageGateway.getGatewayName());
					messageMail.setUserName(messageGateway.getLogin());
					messageMail.setPassword(messageGateway.getPassword());
					messageMail.setInsertDate((new Date()).toString());
					//没有subject
					//没有设置Port
					//没有设置SSL
					baseService.save(messageMail);
				}
				rtn = true;
			}
		}else{
			rtn = false;
		}
		
		json.put("success", rtn);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(json);
		response.getWriter().flush();
		
	}
	
	/**
	 * 记录邮件打开次数
	 * @param mailId
	 */
	@RequestMapping("mailOpen/{mailId}")
	public void mailOpenCount(@PathVariable Integer mailId){
		MessageMail messageMailDB = (MessageMail)baseService.getObject(MessageMail.class, mailId);
		if(messageMailDB!=null){
			if(messageMailDB.getOpenCount()!=null){
				messageMailDB.setOpenCount(messageMailDB.getOpenCount() + 1);
			}else{
				messageMailDB.setOpenCount(1);
			}
		}
		baseService.update(messageMailDB);
	}
	
	/**
	 * 获取邮件记录列表
	 * @param searchForm
	 * @param length
	 * @param start
	 * @param sortCol
	 * @param sortOrder
	 * @param response
	 * @param request
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/getMailList")
	public void getMailList(
			@RequestParam(value = "searchForm") String searchForm, 
			@RequestParam(value = "length") int length,
			@RequestParam(value = "start") int start, 
			@RequestParam(value = "order[0][column]", defaultValue = "1") String sortCol,
			@RequestParam(value = "order[0][dir]", defaultValue = "asc") String sortOrder,
			HttpServletResponse response,
			HttpServletRequest request,
			HttpSession session) throws IOException{
		
		User user = (User) session.getAttribute("user");
		
		JSONObject json = new JSONObject();
		
		if(user!=null){
			
			MainUtil mainUtil = new MainUtil(); 
			
			String sql = " SELECT mail_id as mailId,send_date as sendDate,mail_from as mailFrom, mail_to as mailTo,mail_subject as mailSubject,status FROM `t_message_mail` where 1 = 1 ";
			String sqlCount = " SELECT count(*) FROM `t_message_mail` where 1 = 1 ";
			Map<String, Object> params =  new HashMap<String,Object>();
			
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			
			if(searchJson.get("status")!=null && !("").equals(searchJson.get("status"))){
				RefOption refOption = (RefOption)baseService.getObject(RefOption.class, Integer.parseInt(searchJson.get("status").toString()));
				
				if(refOption.getOptionKey().equals("successful")){
					params.put("status", "s");
				}
				else if(refOption.getOptionKey().equals("failed")){
					params.put("status", "f");
				}
				else if(refOption.getOptionKey().equals("waiting")){
					params.put("status", "w");
				}
				else{
					params.put("status", "p");
				}
				
				sql = sql + " and status = :status ";
				sqlCount = sqlCount + " and status = :status ";
				
			}
			
			if(searchJson.get("mailSubject")!=null && !("").equals(searchJson.get("mailSubject"))){
				sql = sql + " and mail_subject = :mailSubject ";
				sqlCount = sqlCount + " and mail_subject = :mailSubject ";
				params.put("mailSubject", searchJson.get("mail_subject").toString());
			}
			
			if(searchJson.get("dateFrom")!=null && searchJson.get("dateTo")!=null){
				sql = sql + " and (send_date >= :dateFrom and send_date < :dateTo) ";
				sqlCount = sqlCount + " and (send_date >= :dateFrom and send_date < :dateTo) ";
				params.put("dateFrom", searchJson.get("dateFrom").toString());
				params.put("dateTo", searchJson.get("dateTo").toString());
			}
			
			String sortBy = "mail_id";
			if (request.getParameter("columns[" + sortCol + "][name]") != null && !request.getParameter("columns[" + sortCol + "][name]").equals("")) {
				sortBy = request.getParameter("columns[" + sortCol + "][name]");
			}
			
			sql = sql + " order by " + sortBy + " " + sortOrder;
			sql = sql + " limit " + start + " , " + length;
			
			int size = baseService.getCountBySql(sqlCount, params);
			
			json.put("recordsTotal", size);
			json.put("recordsFiltered", size);
			json.put("data", baseService.getList(sql, params));
			
			mainUtil.reponseFlush(response, json.toString());
		}
		
	}
	
	/**
	 * 获得邮件记录详情
	 * @param mailId
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/getMailDetail/{mailId}")
	public void getMailDetail(
			@PathVariable Integer mailId,
			HttpServletResponse response) throws IOException{
		MainUtil mainUtil = new MainUtil();
		JSONObject json = new JSONObject();
		
		MessageMail messageMailDB = (MessageMail)baseService.getObject(MessageMail.class, mailId);
		if(messageMailDB!=null){
			json.put("messageMail", messageMailDB);
		}
		mainUtil.reponseFlush(response, json.toString());
	}
}
