package com.embraiz.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.web.context.WebApplicationContext;

import com.embraiz.dao.BaseDao;
import com.embraiz.model.User;


public class UserSessionListenerController implements HttpSessionListener {



	@Override
	public void sessionCreated(HttpSessionEvent event) {

	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		try{
			WebApplicationContext ctx = (WebApplicationContext) event.getSession().getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);			
			HttpSession hs = event.getSession();
			if(hs!=null){
				User user = (User) hs.getAttribute("user");					
				BaseDao baseDao=(BaseDao)ctx.getBean("baseDao",BaseDao.class);	
				Map<String, Object> params = new HashMap<String, Object>();
				String sql = "update t_login_log set logout_time = :logoutTime,login_status = :status where user_id = :userId and login_status= :Status and logout_time is null ";
				params.put("logoutTime", new Date());
				params.put("status", "0");
				params.put("userId", user.getUserId());
				baseDao.updateBySql(sql, params);
				hs.removeAttribute("user");
			}
		}catch(Exception e){
		}	
	}

}
