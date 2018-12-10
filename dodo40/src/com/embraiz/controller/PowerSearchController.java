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
import com.embraiz.service.ObjService;

/**
 * power search
 * @author Tonna
 *
 */
@Controller
@RequestMapping("/powerSearch")
public class PowerSearchController {
	
	@Resource
	private ObjService objService;
	@Resource
	private BaseService baseService;
	
	
	/**
	 * 根据关键字获取所有obj的module名称和module里记录数 
	 * @param keyword
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/getObjModules")
	public void getObjModules( 
			@RequestParam(value="keyword", required=true)String keyword,HttpServletResponse response,
			HttpServletRequest request) throws IOException{
		
		JSONObject json = new JSONObject();
		User user = (User)request.getSession().getAttribute("user");
		int userId=0;
		if(user!=null){
			userId=user.getUserId();
		}
		
		json.put("moduleList",objService.getObjModule(keyword, userId));   //userId暂时设置为0
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	
	/**
	 * 根据关键字和objtype类型获取obj list
	 * 还没test
	 * @param keyword
	 * @param objTypeId
	 * @param response
	 * @param request
	 * @throws IOException
	 */
	@RequestMapping("/getObjs")
	public void getObjs(
			/*@RequestParam(value="keyword", required=true)String keyword,
			@RequestParam(value="objTypeId", required=true)Integer objTypeId,*/
			@RequestParam(value = "searchForm") String searchForm, @RequestParam(value = "length") int length,
			@RequestParam(value = "start") int start, @RequestParam(value = "order[0][column]", defaultValue = "1") String sortCol,
			@RequestParam(value = "order[0][dir]", defaultValue = "asc") String sortOrder,
			HttpServletResponse response,
			HttpServletRequest request) throws IOException{
		
		Map<String,Object> params=new HashMap<>();
		JSONObject json = new JSONObject();
		
		String sql="select obj.obj_title as objTitle,obj.obj_desc as objDesc, "+
				" obj.keyword,obj.obj_type_id as objTypeId,obj_type_ref.obj_title_title as objTypeName, "+
				" obj_type_ref.obj_title_title as moduleName, obj.obj_id AS objId, "+
				" obj.create_date AS createDate, obj_user.login_name As loginName "+
				" from obj join obj_type_ref on obj.obj_type_id=obj_type_ref.obj_type_id "+
				" JOIN obj_user ON obj.owner_id = obj_user.user_id "+
				" where obj.`status` = 1 ";
		
		String sqlCount = "select count(1) from obj where obj.`status` = 1  "; 
		
		// 排序字段
		String sortBy = ""; 
		
		// 如果数据表配置的排序字段是空的，则根据方法传入的字段进行排序
		if (sortBy.equals("")) {
			if (request.getParameter("columns[" + sortCol + "][name]") != null
					&& !request.getParameter("columns[" + sortCol + "][name]").equals("")) {
				sortBy = request.getParameter("columns[" + sortCol + "][name]");
			}
		}
		
		//search的筛选条件
		if (searchForm != null && !searchForm.equals("{}") && !searchForm.equals("")) {
			  JSONObject searchJson = JSONObject.parseObject(searchForm);  
			  String keyword=searchJson.getString("keyword");
			  String objTypeId=searchJson.getString("objTypeId"); 
			  if(keyword != null && !keyword.equals("")){
				  sql +=  " and keyword like :keyword ";
				  sqlCount +=  " and keyword like :keyword ";
				  params.put("keyword", "%"+keyword+"%");
			  }
			  if(objTypeId != null && !objTypeId.equals("")){
				  sql +=  " and obj.obj_type_id= :objTypeId ";
				  sqlCount +=   " and obj.obj_type_id= :objTypeId ";
				  params.put("objTypeId", objTypeId);
			  }
		}
		
		if (sortBy != null && !"".equals(sortBy)) {
			sql = sql + " order by " + sortBy + " " + sortOrder;
		}

		sql = sql + " limit " + start + " , " + length;
		
		int size = baseService.getCountBySql(sqlCount, params);

		json.put("recordsTotal", size);
		json.put("recordsFiltered", size);
		json.put("data", baseService.getList(sql, params));
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

}
