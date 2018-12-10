package com.embraiz.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

//校验发信人权限的方法
public class MyAuthenticator extends Authenticator{
	private String userName;
	private String password;
	
	public MyAuthenticator(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}
	
	protected PasswordAuthentication getPasswordAuthentication() {
	      return new PasswordAuthentication(userName, password);
    }
}
