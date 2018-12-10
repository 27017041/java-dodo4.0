package com.embraiz.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.sql.*;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.service.BaseService;

public class MainUtil {
	
	public static void reponseFlush(HttpServletResponse response, String varName) throws IOException{
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(varName);
		response.getWriter().flush();
	}
	
	public void sqlToJsonResponse(HttpServletResponse response, String varName, List<Map<String, Object>> list) 
			throws IOException {
		
		JSONObject json = new JSONObject();
		
		list = formatConvert(list);
		json.put(varName, list);
		MainUtil.reponseFlush(response, json.toJSONString());
	}
	
	public List<Map<String, Object>> formatConvert(List<Map<String, Object>> list) {
		//handle all the format issue
		try{
			
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
			
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					String key = entry.getKey();
					
					if(key.contains("_time")){
						SimpleDateFormat dateFormat = new SimpleDateFormat(
					            "yyyy-MM-dd hh:mm");
						Date dateTime = (Timestamp)entry.getValue();
						String value = dateFormat.format(dateTime);
						map.put(key, value);
					}else if(key.contains("_date")){
						SimpleDateFormat dateFormat = new SimpleDateFormat(
					            "yyyy-MM-dd");
						Date dateTime = (Timestamp)entry.getValue();
						String value = dateFormat.format(dateTime);
						map.put(key, value);
					}else if(key.contains("_currency")){
						Double money = Double.parseDouble((String)entry.getValue());
						DecimalFormat formatter = new DecimalFormat("#,###.00");
						String value = formatter.format(money);
						map.put(key, value);
					}
				}
				list.set(i, map);
			}
		}catch(Exception e){
			//do nothing
					
		}
		return list;
	}
	
	

	public  void sqlToJsonResponse(HttpServletResponse response,  List<Map<String, Object>> list) 
			throws IOException {
		sqlToJsonResponse(response, "formData", list);
		
	}
	
	public static String getPwd(int length) {
		char ch[]={ '0', '1', '2', '3', '4', '5', '6', '7', '8',
	            '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
	            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
	            'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
	            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
	            'z', '0', '1' };
		Random random = new Random();
        if (length > 0) {
            int index = 0;
            char[] temp = new char[length];
            int num = random.nextInt();
            for (int i = 0; i < length % 5; i++) {
                temp[index++] = ch[num & 63];
                num >>= 6;                
            }
            for (int i = 0; i < length / 5; i++) {
                num = random.nextInt();
                for (int j = 0; j < 5; j++) {
                    temp[index++] = ch[num & 63];
                    num >>= 6;
                }
            }
            return new String(temp, 0, length);
        } else if (length == 0) {
            return "";
        } else {
            throw new IllegalArgumentException();
        }
    }
	
	/**
	 * 获得当前系统时间
	 * @return
	 */
	public static String getTime(){
		//设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//获取当前系统时间
		return df.format(new Date());
	}
	
	/**
	 * 当前时间减去一定的天数
	 * @param day
	 * @return
	 */
	public static String getTime(int day){
        String dateTime= getTime();  
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        
        Date date=null;  
        String remindTime=null;
        
        try {
			date=sdf.parse(dateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}  
        Calendar calendar=Calendar.getInstance();  
        calendar.setTime(date);  
           
         calendar.set(calendar.DATE,calendar.get(calendar.DATE) - day);  
         //返回减去后的最终时间  
        remindTime =sdf.format(calendar.getTime());
        
        return remindTime;
	}
	
	/**
	 * 通过反射给传入的对象setValue
	 * @param obj (需要被setValue的对象)
	 * @param objValue (带有值的对象)
	 * @return
	 */
	public <T>T setValueByReflection(Object obj,Object objValue){
		Class<?> cls = obj.getClass();
		Field[] fields = cls.getDeclaredFields();
		
		Class<?> clsValue = objValue.getClass();
		Field[] fieldsValue = clsValue.getDeclaredFields();
		
		if(fields.length>0 && fieldsValue.length>0){
			for(int i=0;i<fields.length;i++){
				fields[i].setAccessible(true);
				
				for(int n=0;n<fieldsValue.length;n++){
					fieldsValue[n].setAccessible(true);
					if(fields[i].getName().equals(fieldsValue[n].getName())){
						try {
							if(fieldsValue[n].get(objValue)!=null){
								fields[i].set(obj, fieldsValue[n].get(objValue));
							}
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				}
				
			}
		}
		return (T) obj;
	} 

}
