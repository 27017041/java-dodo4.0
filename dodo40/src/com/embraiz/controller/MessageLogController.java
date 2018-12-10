package com.embraiz.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;

@Controller
@RequestMapping("/messageLog")
public class MessageLogController {

	@Resource
	private BaseService baseService;
	
	/**
	 * 返回messageLog表格数据
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
	@RequestMapping("/getMessageLogList")
	public void getMessageLogList(
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
			
			String sql = " SELECT * FROM `t_message_log` where 1 = 1";
			String sqlCount = " SELECT count(*) FROM `t_message_log` where 1 = 1 ";
			Map<String, Object> params =  new HashMap<String,Object>();
			
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			
			if(searchJson.get("action")!=null && !("").equals(searchJson.get("action"))){
				sql = sql + " and action = :action ";
				sqlCount = sqlCount + " and action = :action ";
				params.put("action", searchJson.get("action").toString());
			}
			
			if(searchJson.get("msgType")!=null && !("").equals(searchJson.get("msgType"))){
				sql = sql + " and msg_type = :msgType ";
				sqlCount = sqlCount + " and msg_type = :msgType ";
				params.put("msgType", searchJson.get("msgType").toString());
			}
			
			if(searchJson.get("sentBy")!=null && !("").equals(searchJson.get("sentBy"))){
				sql = sql + " and sent_by = :sentBy ";
				sqlCount = sqlCount + " and sent_by = :sentBy ";
				params.put("sentBy", searchJson.get("sentBy").toString());
			}

			if((searchJson.get("timeStart")!=null && !("").equals(searchJson.get("timeStart"))) 
					&& (searchJson.get("timeEnd")!=null && !("").equals(searchJson.get("timeEnd")))){
				sql = sql + " and (log_time > :timeStart and log_time < :timeEnd ) ";
				sqlCount = sqlCount + " and (log_time > :timeStart and log_time < :timeEnd ) ";
				params.put("timeStart", searchJson.get("timeStart").toString());
				params.put("timeEnd", searchJson.get("timeEnd").toString());
			}
			
			if(searchJson.get("title")!=null && !("").equals(searchJson.get("title"))){
				sql = sql + " and title = :title ";
				sqlCount = sqlCount + " and title = :title ";
				params.put("title", searchJson.get("title").toString());
			}
			
			String sortBy = "log_id";
			if (sortBy.equals("")) {
				if (request.getParameter("columns[" + sortCol + "][name]") != null && !request.getParameter("columns[" + sortCol + "][name]").equals("")) {
					sortBy = request.getParameter("columns[" + sortCol + "][name]");
				}
			}
			
			sql = sql + " order by " + sortBy + " " + sortOrder;
			sql = sql + " limit " + start + " , " + length;
			
			int size = baseService.getCountBySql(sqlCount, params);
			
			json.put("recordsTotal", size);
			json.put("recordsFiltered", size);
			json.put("data", baseService.getList(sql, params));
			
		}
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
}
