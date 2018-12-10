package com.embraiz.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.Cron;
import com.embraiz.model.CronLog;
import com.embraiz.model.Page;
import com.embraiz.service.BaseService;
import com.embraiz.service.CronService;
import com.embraiz.util.MainUtil;

@Controller
@RequestMapping("/cron")
public class CronController {
	
	
	@Resource
	private CronService cronService;
	@Resource
	private BaseService baseService;
	
	/**
	 * 提供外部调用，利用反射执行Cron任务
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/doCron")
	public void doCron(HttpServletResponse response)
		throws IOException{
				
		Calendar cal = Calendar.getInstance();
				
		
		
		//查询符合条件的Cron任务
		List<Map<String,Object>> objectList = cronService.checkCron(cal);
		
		JSONObject json = new JSONObject();
		
		if(objectList!=null && objectList.size()>0){
			//有符合条件的Cron任务
			
			for(int i=0;i<objectList.size();i++){
				Map<String,Object> map = objectList.get(i);
				
				CronLog cronLog = new CronLog();
				//记录开始时间
				cronLog.setStartTime(MainUtil.getTime());
				
				//记录被执行的cronId
				cronLog.setCronId(Integer.parseInt(map.get("cron_id").toString()));
				
				String className = "";
				if(map.get("class_name")!=null){
					className = map.get("class_name").toString();
				}
				
				String methodName = "";
				if(map.get("method_name")!=null){
					methodName = map.get("method_name").toString();
				}
				
				Integer retentionTime = -1;
				if(map.get("retention_time")!=null){
					//获得保留时间，单位是 天
					retentionTime = Integer.parseInt(map.get("retention_time").toString());
				}
				
				if(retentionTime!=-1){//retentionTime不为-1，即retentionTime有值，需要定时清理
					//得到当前时间减去保留时间的最终时间
					String time = MainUtil.getTime(retentionTime);
					List<Object> ls = cronService.getDelCronFileList(Integer.parseInt(map.get("cron_id").toString()), time);
					
					for(int m=0;m<ls.size();m++){
						CronLog cronLogDel = (CronLog)ls.get(m);
						if(cronLogDel.getPosition()!=null){
							boolean rs = cronService.delFile(cronLogDel.getPosition());
							System.out.println(rs);
						}
					}
				}
				
				Object[] args = null;
				if(map.get("param_name")!=null && !map.get("param_name").toString().equals("")){
					String paramName = map.get("param_name").toString();
					args = paramName.split(",");
				}
				
				if(!className.equals("")){
					//类名不为空
					
					try{
						Class cls = Class.forName("com.embraiz.util."+className);
						
						//调用无参构造  
						Object obj=cls.newInstance();
						
						if(!methodName.equals("")){
							
							if(args!=null){
								//有参数
								
								//定义一个存储数据类型数组
								Class[] argsClass = new Class[args.length]; 
						        for (int m=0;m<args.length;m++) { 
						            argsClass[m] = args[m].getClass(); 
						        } 
						        
						        //获取方法
						        Method method = cls.getMethod(methodName, argsClass);
						        //执行方法
						        Object result = method.invoke(obj, args);
						        if(result!=null){
						        	System.out.println(result.toString());
						        	JSONObject jsStr = JSONObject.parseObject(result.toString());
						        	if(jsStr.get("result")!=null && jsStr.get("result").equals("success")){
						        		//记录状态
										cronLog.setStatus("Success");
										
										if(jsStr.get("size")!=null){
							        		//记录日志
											cronLog.setLog("Executed "+map.get("cron_name").toString()+",fileSize "+jsStr.get("size"));
							        	}
										
										if(jsStr.get("position")!=null){
											//记录文件保存地址
											cronLog.setPosition(jsStr.get("position").toString());
										}
						        	}else if(jsStr.get("result")!=null && jsStr.get("result").equals("fail")){
						        		//记录状态
										cronLog.setStatus("Fail");
										
										//记录日志
										cronLog.setLog("Executed "+map.get("cron_name").toString());
						        	}else{
						        		//记录状态
										cronLog.setStatus("Error");
										
										//记录日志
										cronLog.setLog("Executed "+map.get("cron_name").toString());
						        	}
						        	
						        	json.put("msg", result);
						        	 
						        }else{
						        	json.put("msg", "success");
						        }
								
							}else{
								//无参数
								Method method = cls.getMethod(methodName, new Class[]{});
								Object result = method.invoke(obj, null);
								
								 if(result!=null){
									 System.out.println(result.toString());
									 JSONObject jsStr = JSONObject.parseObject(result.toString());
									 
									 if(jsStr.get("result")!=null && jsStr.get("result").equals("success")){
										//记录状态
										cronLog.setStatus("Success");
											
										if(jsStr.get("size")!=null){
								        	//记录日志
											cronLog.setLog("Executed "+map.get("cron_name").toString()+",fileSize "+jsStr.get("size"));
								        }
										
										if(jsStr.get("position")!=null){
											//记录文件保存地址
											cronLog.setPosition(jsStr.get("position").toString());
										}
									 }else if(jsStr.get("result")!=null && jsStr.get("result").equals("fail")){
										//记录状态
										cronLog.setStatus("Fail");
											
										//记录日志
										cronLog.setLog("Executed "+map.get("cron_name").toString());
									 }else {
										//记录状态
										cronLog.setStatus("Error");
										
										//记录日志
										cronLog.setLog("Executed "+map.get("cron_name").toString());
										 
									 }
									 
						        	 json.put("msg", result);
						        }else{
						        	json.put("msg", "success");
						        }
							}
							
							//记录结束时间
							cronLog.setEndTime(MainUtil.getTime());
							
						}
						
					}catch(Exception e){
						e.printStackTrace();
						json.put("msg", "error");
						
						//记录结束时间
						cronLog.setEndTime(MainUtil.getTime());
						//记录日志
						cronLog.setLog("Executed "+map.get("cron_name").toString());
						//记录状态
						cronLog.setStatus("Fail");
					}
					
				
					//保存日志
					baseService.save(cronLog);
					
					//修改最后运行时间
					Cron cron  = (Cron)baseService.getObject(Cron.class, Integer.parseInt(map.get("cron_id").toString()));
						cron.setLastRun(cronLog.getEndTime());
					baseService.update(cron);
					
				}
			}
			
		}else{
			//没有符合条件的Cron任务
			json.put("data", "null");
			
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 新增Cron任务
	 * @param cron
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/saveCron")
	public void saveCron(@ModelAttribute Cron cron,HttpServletResponse response)
		throws IOException{
		Integer cronId = (Integer)cronService.saveCron(cron);
		
		JSONObject json = new JSONObject();
		json.put("cronId", cronId);
		if(cronId != null){
			json.put("result", true);
		}else{
			json.put("result", false);
		}
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 表格查询列表
	 * @param response
	 * @param request
	 * @param cronName
	 * @param length
	 * @param start
	 * @param sortCol
	 * @param sortOrder
	 * @throws IOException
	 */
	@RequestMapping("/getCronList")
	public void getCronList(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam(value="searchForm", required = false) String searchForm,
			@RequestParam(value="length") int length,
			@RequestParam(value="start") int start,
			@RequestParam(value="order[0][column]",defaultValue="1") String sortCol,
			@RequestParam(value="order[0][dir]",defaultValue="asc") String sortOrder)
		throws IOException{
		
		//获取排序字段
		String sortBy = "";
		if(request.getParameter("columns["+sortCol+"][name]")!=null && !request.getParameter("columns["+sortCol+"][name]").equals("")){
			sortBy = request.getParameter("columns["+sortCol+"][name]");
		}
		
		Page pageBo = new Page();
		pageBo.setCount(length);
		pageBo.setStart(start);
		
		Map<String,Object> map = cronService.getCronList(searchForm, sortBy, sortOrder, pageBo);
		
		JSONObject json = new JSONObject();
		
		json.put("recordsTotal", map.get("recordsTotal"));
		json.put("recordsFiltered", map.get("recordsFiltered"));
		json.put("data", map.get("data"));
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 删除Cron任务
	 * @param response
	 * @param request
	 * @param cronId
	 * @throws IOException
	 */
	@RequestMapping("/deleteCron")
	public void deleteCron(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam(value="cronIds") String cronIds)
					throws IOException{
		JSONObject json = new JSONObject();
		boolean rtn = false;
		
		if(cronIds!=null && cronIds.length()>0){
			String cronIdStr [] = cronIds.split(",");
			for(int i=0;i<cronIdStr.length;i++){
				rtn = cronService.deleteCron(Integer.parseInt(cronIdStr[i]));
				if(!rtn){
					break;
				}
			}
			
		}
		
		json.put("result", rtn);
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 修改Cron
	 * @param response
	 * @param request
	 * @param cron
	 * @throws IOException
	 */
	@RequestMapping("/updateCron")
	public void updateCron(
			HttpServletResponse response,
			HttpServletRequest request,
			@ModelAttribute Cron cron)
		throws IOException{
		JSONObject json = new JSONObject();
		
		if(cron!=null){
			try{
				cronService.updateCron(cron);
				json.put("result", true);
			}catch(Exception e){
				e.printStackTrace();
				json.put("result", false);
			}
			
		}
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 获取详情
	 * @param clientId
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getDetail")
	public void getDetail(
			@RequestParam("keyId") Integer cronId,
			HttpServletResponse response)
					throws IOException{
		JSONObject json = new JSONObject();
		json.put("data", baseService.getObject(Cron.class, cronId));
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 查看所有定时任务日志
	 * @param response
	 * @param request
	 * @param searchForm
	 * @param length
	 * @param start
	 * @param sortCol
	 * @param sortOrder
	 * @throws IOException
	 */
	@RequestMapping("/getCronLogList")
	public void getCronLogList(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam(value="searchForm", required = false) String searchForm,
			@RequestParam(value="length") int length,
			@RequestParam(value="start") int start,
			@RequestParam(value="order[0][column]",defaultValue="1") String sortCol,
			@RequestParam(value="order[0][dir]",defaultValue="asc") String sortOrder) throws IOException{
		
		//获取排序字段
		String sortBy = "startTime";
		if (request.getParameter("columns[" + sortCol + "][name]") != null && !request.getParameter("columns[" + sortCol + "][name]").equals("")) {
			sortBy = request.getParameter("columns[" + sortCol + "][name]");
		}
		
		Page pageBo = new Page();
		pageBo.setCount(length);
		pageBo.setStart(start);
		
		Map<String,Object> map = cronService.getCronLogList(searchForm, sortBy, sortOrder, pageBo);
		
		JSONObject json = new JSONObject();
		
		json.put("recordsTotal", map.get("recordsTotal"));
		json.put("recordsFiltered", map.get("recordsFiltered"));
		json.put("data", map.get("data"));
		
		MainUtil.reponseFlush(response, json.toString());
		
	}
	
	//查看某个任务的日志
	@RequestMapping("/getCronLogDetail")
	public void getCronLogDetail(
			@RequestParam("cronLogId") Integer cronLogId,
			HttpServletResponse response) throws IOException{
		JSONObject json = new JSONObject();
		
		CronLog cronLog = (CronLog)baseService.getObject(CronLog.class, cronLogId);
		Cron cron = (Cron)baseService.getObject(Cron.class, cronLog.getCronId()); 
		cronLog.setCronName(cron.getCronName());
		
		json.put("cronLog", cronLog);
		MainUtil.reponseFlush(response, json.toString());
	}

}
