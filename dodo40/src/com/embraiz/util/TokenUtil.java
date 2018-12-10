package com.embraiz.util;

import java.util.UUID;


public class TokenUtil {
	
	public static String createUUID()
	{
		String uuid=UUID.randomUUID().toString();  
		return uuid;
	}
	
	
	public static String getToken(String uuid) {
		MD5Util md5Util = new MD5Util();
		return md5Util.setMD5(uuid);
	}
}
