package com.embraiz.util;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.alibaba.fastjson.JSONObject;

public class BackUpUtil {

	public void doBackUp(){
		System.out.println("Doing back up!");
	}
	
	public String doBackUp(String dataBaseName,String userName,String password,String savePath){
		JSONObject json = new JSONObject();
		
		String rtn = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		//------1、设置文件的路径------ 
		
		String filePath=Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("/WEB-INF/classes/", "");
		
		Calendar calendar = Calendar.getInstance();
    	int year = calendar.get(Calendar.YEAR);
    	int month = calendar.get(Calendar.MONTH) + 1;
    	int day = calendar.get(Calendar.DAY_OF_MONTH);
    	
    	String path = "/backUp/"+savePath+"/"+year+"/"+month+"/"+day+"/";
    	
    	//检查文件夹是否存在，不存在则创建文件夹
    	File backUpFile = new File(filePath+path);
	    if (!backUpFile.exists()&&!backUpFile.isDirectory()){     
	    	backUpFile.mkdirs(); 
	    }
	    
	    //------2、backup sql-------
	    
	    //生成文件名字
		String fileName = format.format(calendar.getTime())+"_"+dataBaseName+".sql";
		
		//文件最终的路径 
		String finalPath = filePath + path + fileName;
		
		//去除首个字符
		finalPath = finalPath.substring(1, finalPath.length());
		
		//将/替换成\
		finalPath=finalPath.replaceAll("/", "\\\\");  
		
		
		System.out.println(finalPath);
		
		
		try{
			Runtime rt = Runtime.getRuntime();
			String cmd =  "mysqldump -u"+userName+" -p"+password+" "+dataBaseName+" -r "+finalPath;
			
			//完整的命令 
			//cmd = "/usr/bin/"+cmd;	//Linux系统命令
			//cmd = "C:\\Program Files\\MySQL\\MySQL Server 5.1\\bin\\"+cmd;//windows系统命令
			
			System.out.println(cmd);
			
			
			Process child = rt.exec(cmd);
			
			int status = child.waitFor();
			
			if(status==0){
				//mysqldump运行成功
				File file = new File(finalPath);
				
				//定义一个存储文件大小的变量
				String filesize = "0";
				
				//判断文件是否存在 
				if(file.exists()){
					FileInputStream fis = new FileInputStream(file);
					double getsize = fis.available();
					DecimalFormat df = new DecimalFormat("#.00");
						if(getsize>1000000){
							getsize = getsize/1024/1024;
							filesize = df.format(getsize)+" MB";
						}else{
							getsize = getsize/1024;
							filesize = df.format(getsize)+" KB";
						}
				}
				
				json.put("result", "success");
				json.put("size", filesize);
				json.put("position", finalPath);
				
				
			}else{
				//mysqldump运行失败 
				json.put("result", "fail");
			}
		}catch(Exception e){
			e.printStackTrace();
			
			json.put("result", "error");
		}
		
		
		return json.toJSONString();
	}
}
