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
import com.embraiz.service.ConfService;
import com.embraiz.service.RelationalService;

@Controller
@RequestMapping("/relational")
public class RelationalController {

	@Resource
	private RelationalService relationalService;
	@Resource
	private ConfService confService;

	// 通过obj_Id,模块名 查询改模块的字段和数据
	@RequestMapping("/getRelational")
	public void getRelational(@RequestParam("keyId") Integer keyId, @RequestParam("moduleName") String moduleName,
			@RequestParam("relationalName") String relationalName, @RequestParam(value = "length") int length,
			@RequestParam(value = "start") int start, @RequestParam(value = "order[0][column]", defaultValue = "1") String sortCol,
			@RequestParam(value = "order[0][dir]", defaultValue = "asc") String sortOrder, HttpServletResponse response, HttpSession session,
			HttpServletRequest request) throws IOException {
		JSONObject json = new JSONObject();

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

		Map<String, Object> data = relationalService.getRelational(keyId, moduleName, relationalName, sortBy, sortOrder, pageBo, start, length);
		json.put("fieldNameList", data.get("fieldNameList")); // 字段信息
		// 内页区域的栏位列表
		json.put("formFieldList", confService.getFormField(lang, relationalName));

		json.put("recordsTotal", data.get("recordsTotal"));
		json.put("recordsFiltered", data.get("recordsFiltered"));
		json.put("data", data.get("data"));// 数据
		json.put("pageBo", data.get("pageBo"));// 数据

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	@RequestMapping("/getCoreRelational")
	public void getCoreRelational(@RequestParam("moduleName") String moduleName, HttpServletResponse response, HttpSession session)
			throws IOException {
		JSONObject json = new JSONObject();

		String lang = (String) session.getAttribute("lang");
		if (lang == null) {
			lang = "en";
		}

		Map<String, Object> data = relationalService.getCoreRelational(moduleName);

		json.put("relationalItem", data.get("RelationalItem"));
		json.put("relational", data.get("data"));
		json.put("listCount", data.get("listCount"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * 通过视图名字获取联动模块的数据
	 */
	@RequestMapping("/getLinkageModuleList")
	public void getLinkageModuleList(@RequestParam("viewsName") String viewsName, HttpServletResponse response, HttpSession session)
			throws IOException {
		JSONObject json = new JSONObject();

		String lang = (String) session.getAttribute("lang");
		if (lang == null) {
			lang = "en";
		}

		Map<String, Object> data = relationalService.getLinkageModuleList(viewsName);

		json.put("data", data.get("data"));
		json.put("listCount", data.get("listCount"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * 获取下一级联动模块的数据
	 */
	@RequestMapping("/getLinkageModuleListById")
	public void getLinkageModuleListById(@RequestParam("viewsName") String viewsName, @RequestParam("keyName") String keyName,
			@RequestParam("keyId") Integer keyId, HttpServletResponse response, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();

		String lang = (String) session.getAttribute("lang");
		if (lang == null) {
			lang = "en";
		}

		Map<String, Object> data = relationalService.getLinkageModuleListById(viewsName, keyName, keyId);

		json.put("data", data.get("data"));
		json.put("listCount", data.get("listCount"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

}
