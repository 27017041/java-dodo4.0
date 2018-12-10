package com.embraiz.util;

import java.net.HttpURLConnection;
import java.net.URL;

public class UrlUtil {
	
	private URL url = null;
	private HttpURLConnection conn = null;
	private int state = -1;

	/**
	 * 判断url是否能够正常访问
	 * @param url
	 * @return
	 */
	public boolean isAvailable(String requestUrl){
		
		
		
		boolean result = false;
		
		int count = 0;
		
		while(count<3){
			try{
				url = new URL(requestUrl);
				conn = (HttpURLConnection) url.openConnection();
				//设置连接主机超时（单位：毫秒）
				conn.setConnectTimeout(3000);
				//设置从主机读取数据超时（单位：毫秒）
				conn.setReadTimeout(3000);
				
				//获取返回的状态码
				state = conn.getResponseCode();
				
				if(state==200){
					//请求成功
					result = true;
					break;
					
				}else{
					//请求失败
					result = false;
				}
				count++;
			}catch(Exception e){
				e.printStackTrace();
				count++;
				result = false;
				
				continue;
			}
			
		}
		
		//断开连接
		if(url!=null){
			//重新初始化
			url = null;
		}
		if(conn!=null){
			conn.disconnect();
			
			//重新初始化
			conn = null;
		}
		
		
		return result;
		
	}
	
	public int getCode(){
		return state;
	}
	
}
