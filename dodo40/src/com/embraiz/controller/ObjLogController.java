package com.embraiz.controller;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.Page;
import com.embraiz.service.ObjLogService;

@Controller
@RequestMapping("/objlog")
public class ObjLogController {

	@Resource
	private ObjLogService objLogService;

	/**
	 * 查询obj_log
	 */
	@RequestMapping("/getObjLogList")
	public void getObjLogListAll(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "moduleName", required = false) String moduleName, @RequestParam(value = "length") int length,
			@RequestParam(value = "start") int start, @RequestParam(value = "order[0][column]", defaultValue = "1") String sortCol,
			@RequestParam(value = "order[0][dir]", defaultValue = "asc") String sortOrder) throws IOException {

		// 获取排序字段
		String sortBy = "";
		if (request.getParameter("columns[" + sortCol + "][name]") != null && !request.getParameter("columns[" + sortCol + "][name]").equals("")) {
			sortBy = request.getParameter("columns[" + sortCol + "][name]");
		}

		Page pageBo = new Page();
		pageBo.setCount(length);
		pageBo.setStart(start);

		String lang = (String) session.getAttribute("lang");

		if (lang == null) {
			lang = "en";
		}
		Map<String, Object> objLogList = objLogService.objLogList(moduleName, sortBy, sortOrder, pageBo);
		JSONObject json = new JSONObject();
		json.put("recordsTotal", objLogList.get("recordsTotal"));
		json.put("recordsFiltered", objLogList.get("recordsFiltered"));
		json.put("data", objLogList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 查询某个obj的history
	 */
	@RequestMapping("/getObjHistory")
	public void getObjHistory(@RequestParam("obj_id") int objId, @RequestParam("moduleName") String moduleName, HttpServletResponse response)
			throws IOException {
		JSONObject json = new JSONObject();
		Map<String, Object> statusList = objLogService.getHistory(objId, moduleName);

		json.put("recordsTotal", statusList.get("recordsTotal"));
		json.put("recordsFiltered", statusList.get("recordsFiltered"));
		json.put("data", statusList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
}
