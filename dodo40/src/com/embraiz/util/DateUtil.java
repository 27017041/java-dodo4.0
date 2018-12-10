package com.embraiz.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cahrlie on 2016/12/12.
 */
public class DateUtil {
	
	/* 
     * 获取当前时间戳（年月日）
     */  
    public static Integer getDate(){
        Long currDate =System.currentTimeMillis();
        String date = currDate.toString().substring(0, 10);
        return Integer.parseInt(date);
    }
    
    /* 
     * 将时间转换为时间戳
     */    
    public static String dateToStamp(String s) throws ParseException{
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }
    
    /* 
     * 将时间戳转换为时间
     */
    public static String timeStampToDate(long timeStamp){  
        Date date = new Date(timeStamp);  
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        String dateStr = simpleDateFormat.format(date);  
        return dateStr;  
    }  
    
    public static String getDate(long timeStamp) {
    	String date = timeStampToDate(timeStamp);  
        String dateStr = date.substring(0, 10); 
		return dateStr;
	}
    
    public static String getTime(long timeStamp) {
    	String date = timeStampToDate(timeStamp);  
        String dateStr = date.substring(11, 19); 
		return dateStr;
	}
  
    public static int getYearByTimeStamp(long timeStamp){  
        String date = timeStampToDate(timeStamp);  
        String year = date.substring(0, 4);  
        return Integer.parseInt(year);  
    }  
  
    public static int getMonthByTimeStamp(long timeStamp){  
        String date = timeStampToDate(timeStamp);  
        String month = date.substring(5, 7);  
        return Integer.parseInt(month);  
    }  
  
    public static int getDayByTimeStamp(long timeStamp){  
        String date = timeStampToDate(timeStamp);  
        String day = date.substring(8, 10);  
        return Integer.parseInt(day);  
    }  
  
    public static int getHourByTimeStamp(long timeStamp){  
        String date = timeStampToDate(timeStamp);  
        String hour = date.substring(11, 13);  
        return Integer.parseInt(hour);  
    }  
  
    public static int getMinuteByTimeStamp(long timeStamp){  
        String date = timeStampToDate(timeStamp);  
        String minute = date.substring(14, 16);  
        return Integer.parseInt(minute);  
    }  
  
    public static int getSecondByTimeStamp(long timeStamp){  
        String date = timeStampToDate(timeStamp);  
        String second = date.substring(17, 19);  
        return Integer.parseInt(second);  
    }
}
