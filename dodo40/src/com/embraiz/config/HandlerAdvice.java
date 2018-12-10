package com.embraiz.config;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.SessionException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import com.embraiz.model.AuthToken;
import com.embraiz.model.User;
import com.embraiz.service.AuthTokenService;
import com.embraiz.service.UserService;
import com.embraiz.util.DateUtil;
import com.embraiz.util.MD5Util;

@ControllerAdvice
public class HandlerAdvice {

	@Resource
	private AppInfo appInfo;
	@Resource
	private UserService userService;
	@Resource
	private AuthTokenService authTokenService;
	

	@ModelAttribute
	public void addAttributes(HttpServletRequest request, 
			HttpServletResponse response, HttpSession session,
			@RequestHeader(value="x-auth-token", required=false) String token,
			@RequestHeader(value="uid", required=false) Integer uid,
			@RequestHeader(value="x-lang", required=false) String lang,
			Model model) throws Exception{
		MD5Util md5Util = new MD5Util();
		String url = request.getRequestURI();
		Boolean flag = false;
		//拦截url,白名单内的请求不用经过token验证
		for(String accessUrl : appInfo.getAccessUrls()) {
			if(url.contains(accessUrl)){
				flag = true;
			}
		}
		if(lang == null) {
			lang = "en";
		}
		session.setAttribute("lang", lang);
		if(!flag) {
			if(token != null) {
				AuthToken authToken = authTokenService.getToken(token);
				if(authToken != null){
					//判断uid是否一致
					if(authToken.getUid().intValue()==uid.intValue()){
						if(authToken.getToken().equals(md5Util.setMD5(token))) {
							//判断token是否已经失效
							if(authToken.getOverdue().intValue()>=DateUtil.getDate().intValue()) {
								if(uid != null) {
									User user = userService.getUserData(uid, lang);
									//判断lang是否一致，不一致则更新user的lang
									if(!user.getLang().equals(lang)){
										userService.updateLang(lang, user.getUserId());
										user.setLang(lang);
									}
									session.setAttribute("user", user);
									flag = true;
								}
							}
						}
					}
				}
			}
		}
		if(!flag) {
			//删掉过期的token
			authTokenService.deleteToken();
			session.removeAttribute("user");
			response.setStatus(401);
			throw new SessionException("登录状态已过期");
		}
	}
}
