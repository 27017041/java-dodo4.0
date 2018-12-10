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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.ObjLog;
import com.embraiz.model.Page;
import com.embraiz.model.QuotationItemTemplate;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.QuotationItemTemplateService;
import com.embraiz.util.StringFormat;

@Controller
@RequestMapping("/quotationItemTemplate")
public class QuotationItemTemplateController {

	@Resource
	private ObjLogService objLogService;
	@Resource
	private QuotationItemTemplateService quotationItemTemplateService;
	@Resource
	private BaseService baseService;

	/**
	 * Insert
	 */
	@RequestMapping("/saveData")
	public void saveData(@RequestParam("formData") String data, HttpServletResponse response, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;
		int keyId = 0;
		User user = (User) session.getAttribute("user");

		QuotationItemTemplate quotationItemTemplate = JSONObject.toJavaObject(JSONObject.parseObject(StringFormat.encodeStr(data)),
				QuotationItemTemplate.class);

		keyId = quotationItemTemplateService.insert(quotationItemTemplate);
		if (keyId > 0) {
			result = true;
			try {
				// 添加insert记录
				ObjLog objLog = new ObjLog();
				objLog.setObjId(keyId);
				objLog.setUserId(user.getUserId());
				objLog.setDescription("Create QuotationItemTemplate");
				objLog.setModuleName("QuotationItemTemplate");
				objLog.setConfId(0);
				objLogService.createLog(objLog);
			} catch (Exception e) {
			}
		} else {
			result = false;
		}

		json.put("result", result);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * Delete QuotationItem
	 */
	@RequestMapping("/deleteData")
	public void deleteData(HttpServletResponse response, @RequestParam(value = "keyIds") String ids, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		User user = (User) session.getAttribute("user");
		int status = 0;
		String msg = "";
		if (ids != null) {
			int flag = quotationItemTemplateService.deleteData(ids, user);
			if (flag > 0) {
				status = 1;
				msg = "Delete Success";
			} else {
				msg = "Delete Fail";
			}
		}
		json.put("status", status);
		json.put("result", msg);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * Update QuotationItemTemplate
	 * 
	 * @param data
	 * @param changeFields
	 */
	@RequestMapping("/updaDate")
	public void updaDate(HttpServletResponse response, @RequestParam(value = "formData") String data,
			@RequestParam(value = "changeFields") String changeFields, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;
		User user = (User) session.getAttribute("user");
		QuotationItemTemplate quotationFooterTemplate = JSONObject.toJavaObject(JSONObject.parseObject(StringFormat.encodeStr(data)),
				QuotationItemTemplate.class);
		QuotationItemTemplate QuotationItemTemplateDb = (QuotationItemTemplate) baseService.getObject(QuotationItemTemplate.class,
				quotationFooterTemplate.getTitleId());

		JSONObject newdata = JSONObject.parseObject(data);
		if (QuotationItemTemplateDb != null) {
			QuotationItemTemplateDb.setTitle(quotationFooterTemplate.getTitle());
			QuotationItemTemplateDb.setDescription(quotationFooterTemplate.getDescription());
			QuotationItemTemplateDb.setCost(quotationFooterTemplate.getCost());
			QuotationItemTemplateDb.setUnitPrice(quotationFooterTemplate.getUnitPrice());
			QuotationItemTemplateDb.setQty(quotationFooterTemplate.getQty());
			QuotationItemTemplateDb.setDiscount(quotationFooterTemplate.getDiscount());

			try {
				baseService.update(QuotationItemTemplateDb);
				int keyId = Integer.parseInt(newdata.get("titleId").toString());
				String newchangeFields = "[" + changeFields + "]";// 把changeFields变成jsonArray形式
				JSONArray jsonArray = JSONArray.parseArray(newchangeFields);
				// 循环插入Log
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					ObjLog objLog = new ObjLog();
					objLog.setObjId(keyId);
					objLog.setUserId(user.getUserId());
					objLog.setDescription(jsonObject.get("description").toString());
					objLog.setModuleName("QuotationItemTemplate");
					objLog.setConfId(0);
					objLog.setFieldName(jsonObject.get("fieldName").toString());
					objLogService.createLog(objLog);
				}
				result = true;
				json.put("keyId", QuotationItemTemplateDb.getTitleId());
			} catch (Exception e) {
			}
		}

		json.put("result", result);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * Select
	 */
	@RequestMapping("/getQuotationItemTemplateList")
	public void getQuotationItemTemplateList(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "searchForm") String searchForm, @RequestParam(value = "length") int length,
			@RequestParam(value = "start") int start, @RequestParam(value = "order[0][column]", defaultValue = "1") String sortCol,
			@RequestParam(value = "order[0][dir]", defaultValue = "asc") String sortOrder) throws IOException {

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

		Map<String, Object> quotationItemTemplateList = quotationItemTemplateService.getQuotationItemTemplateList(searchForm, sortBy, sortOrder,
				pageBo);
		JSONObject json = new JSONObject();
		json.put("recordsTotal", quotationItemTemplateList.get("recordsTotal"));
		json.put("recordsFiltered", quotationItemTemplateList.get("recordsFiltered"));
		json.put("data", quotationItemTemplateList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * 获取详情
	 */
	@RequestMapping("/getDetail")
	public void getDetail(@RequestParam("keyId") Integer quotationItemTempalteId, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		Map<String, Object> quotationDetail = quotationItemTemplateService.getDetail(quotationItemTempalteId);
		json.put("data", quotationDetail.get("data"));
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

}
