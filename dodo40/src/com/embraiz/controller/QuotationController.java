package com.embraiz.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.embraiz.dataSource.core.QuotationDs;
import com.embraiz.dataSource.core.QuotationFooterDs;
import com.embraiz.model.Module;
import com.embraiz.model.Obj;
import com.embraiz.model.ObjLog;
import com.embraiz.model.Page;
import com.embraiz.model.Quotation;
import com.embraiz.model.QuotationFooter;
import com.embraiz.model.QuotationItem;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.CommonService;
import com.embraiz.service.ConfService;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.ObjService;
import com.embraiz.service.QuotationService;
import com.embraiz.service.WorkflowService;
import com.embraiz.util.StringFormat;

@Controller
@RequestMapping("/quotation")
public class QuotationController {

	@Resource
	private QuotationService quotationService;
	@Resource
	private BaseService baseService;
	@Resource
	private ObjLogService objLogService;
	@Resource
	private ObjService objService;
	@Resource
	private WorkflowService workflowService;
	@Resource
	private ConfService confService;
	@Resource
	private CommonService commonService;

	/**
	 * 根据moduleName，获取该模块下的所有栏位配置信息
	 * 
	 * @param moduleName
	 */
	@RequestMapping("/getAllFieldByModule")
	public void getAllFieldByModule(@RequestParam("moduleName") String moduleName, HttpServletResponse response, HttpSession session)
			throws IOException {
		JSONObject json = new JSONObject();
		String lang = (String) session.getAttribute("lang");
		if (lang == null) {
			lang = "en";
		}
		json.put("module", (Module) baseService.getObject(Module.class, moduleName));

		// 搜索区域的栏位列表
		json.put("searchFieldList", confService.getSearchField(lang, moduleName));
		// 列表区域的栏位列表
		json.put("gridFieldList", confService.getGridField(lang, moduleName));
		// 内页区域的栏位列表
		json.put("formFieldList", confService.getFormField(lang, moduleName));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	@RequestMapping("/searchData")
	public void searchData(HttpServletResponse response, HttpSession session, HttpServletRequest request,
			@RequestParam(value = "searchForm") String searchForm, @RequestParam(value = "length") int length,
			@RequestParam(value = "start") int start, @RequestParam(value = "order[0][column]", defaultValue = "1") String sortCol,
			@RequestParam(value = "order[0][dir]", defaultValue = "asc") String sortOrder) throws IOException {
		User user = (User) session.getAttribute("user");

		JSONObject json = new JSONObject();

		if (user != null) {
			String lang = (String) session.getAttribute("lang");
			if (lang == null) {
				lang = "en";
			}

			// 需要显示的字段集合
			List<Map<String, Object>> fieldListInGrid = commonService.getGridFieldInModule("quotation", lang);
			json.put("fieldListInGrid", fieldListInGrid);

			// 存储拼接的字段
			String fieldStr = "";
			// 排序字段
			String sortBy = "";
			// 获得需要在grid中显示的字段
			for (int i = 0; i < fieldListInGrid.size(); i++) {
				fieldStr = fieldStr + fieldListInGrid.get(i).get("fieldLabel").toString() + ",";
				if (sortBy.equals("") && fieldListInGrid.get(i).get("fieldLabel") != null) {
					if (fieldListInGrid.get(i).get("sortBy") != null) {
						sortBy = fieldListInGrid.get(i).get("sortBy").toString();
					}
				}
				// 判断在grid中显示的字段是否有多选字段
				if (fieldListInGrid.get(i).get("fieldType").toString().equals("multiselect")) {
					String multiselectFieldLabel = fieldListInGrid.get(i).get("fieldLabel").toString();
					fieldStr = fieldStr + " (SELECT GROUP_CONCAT(ref.option_name_" + lang
							+ ") FROM ref_option ref WHERE FIND_IN_SET(ref.option_id,v_quotation." + multiselectFieldLabel + ")) AS "
							+ multiselectFieldLabel + "Value " + ",";
				}
			}

			fieldStr = fieldStr.substring(0, fieldStr.length() - 1);
			// 如果数据表配置的排序字段是空的，则根据方法传入的字段进行排序
			if (sortBy.equals("")) {
				if (request.getParameter("columns[" + sortCol + "][name]") != null
						&& !request.getParameter("columns[" + sortCol + "][name]").equals("")) {
					sortBy = request.getParameter("columns[" + sortCol + "][name]");
				}
			}
			String sql = "select " + fieldStr + " from v_quotation where 1=1 ";
			String sqlCount = "select count(1) from v_quotation where 1=1  ";

			// 搜索区域用到的栏位
			List<Map<String, Object>> fieldListInSearch = commonService.getSearchFieldInModule("quotation", lang);

			if (searchForm != null && !searchForm.equals("")) {
				JSONObject searchJson = JSONObject.parseObject(searchForm);

				// 根据搜索栏位，判断searchJson是否有对应的搜索数据
				for (int i = 0; i < fieldListInSearch.size(); i++) {
					if (searchJson.get(fieldListInSearch.get(i).get("fieldLabel").toString()) != null
							&& !"".equals(searchJson.get(fieldListInSearch.get(i).get("fieldLabel").toString()))) {
						// 根据栏位类型，使用不同的sql语句
						if (fieldListInSearch.get(i).get("fieldType").toString().equals("text")
								|| fieldListInSearch.get(i).get("fieldType").toString().equals("date")) {
							// text || date 栏位，使用like
							sql = sql + " and " + fieldListInSearch.get(i).get("fieldLabel").toString() + " like '%"
									+ searchJson.get(fieldListInSearch.get(i).get("fieldLabel").toString()) + "%'";
							sqlCount = sqlCount + " and " + fieldListInSearch.get(i).get("fieldLabel").toString() + " like '%"
									+ searchJson.get(fieldListInSearch.get(i).get("fieldLabel").toString()) + "%'";
						} else if (fieldListInSearch.get(i).get("fieldType").toString().equals("select")
								|| fieldListInSearch.get(i).get("fieldType").toString().equals("autocomplete")) {
							// select || autocomplete 栏位，使用=
							sql = sql + " and " + fieldListInSearch.get(i).get("fieldLabel").toString() + " = "
									+ searchJson.get(fieldListInSearch.get(i).get("fieldLabel").toString()) + " ";
							sqlCount = sqlCount + " and " + fieldListInSearch.get(i).get("fieldLabel").toString() + " = "
									+ searchJson.get(fieldListInSearch.get(i).get("fieldLabel").toString()) + " ";
						} else if (fieldListInSearch.get(i).get("fieldType").toString().equals("multiselect")) {
							// multiselect 栏位，
							if (searchJson.get(fieldListInSearch.get(i).get("fieldLabel").toString()) != null) {
								String str = "";
								String searchValue = searchJson.get(fieldListInSearch.get(i).get("fieldLabel").toString()).toString();
								String searchValueArray[] = searchValue.split(",");
								for (int n = 0; n < searchValueArray.length; n++) {
									str = str + fieldListInSearch.get(i).get("fieldLabel").toString() + " like '%" + searchValueArray[n] + "%' or ";
								}
								str = str.substring(0, str.length() - 3);
								sql = sql + " and ( " + str + " )";
							}
						}
					}
				}
			}
			if (sortBy != null && !"".equals(sortBy)) {
				sql = sql + " order by " + sortBy + " " + sortOrder;
			}
			sql = sql + " limit " + start + " , " + length;
			int size = baseService.getCountBySql(sqlCount, null);
			json.put("recordsTotal", size);
			json.put("recordsFiltered", size);
			json.put("data", baseService.getList(sql, null));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 获取艘搜索区域的栏位
	 */
	@RequestMapping("/getSearchData")
	public void getSearchData(HttpServletResponse response, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		User user = (User) session.getAttribute("user");
		if (user != null) {
			String lang = (String) session.getAttribute("lang");
			if (lang == null) {
				lang = "en";
			}
			json.put("searchDataList", commonService.getSearchFieldInModule("quotation", lang));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 获得表单区域的栏位和数据
	 */
	@RequestMapping("/getFormData")
	public void getFormData(HttpServletResponse response, HttpSession session, @RequestParam("primaryId") Integer quotationId) throws IOException {
		User user = (User) session.getAttribute("user");
		JSONObject json = new JSONObject();
		if (user != null) {
			String lang = (String) session.getAttribute("lang");
			if (lang == null) {
				lang = "en";
			}
			// 获得表单区域需要显示的栏位
			List<Map<String, Object>> fieldListInForm = commonService.getFormFieldInModule("quotation", lang);
			json.put("fieldListInForm", fieldListInForm);
			// 存储拼接的字段
			String fieldStr = "";
			for (int i = 0; i < fieldListInForm.size(); i++) {
				fieldStr = fieldStr + fieldListInForm.get(i).get("fieldLabel").toString() + ",";
			}
			fieldStr = fieldStr.substring(0, fieldStr.length() - 1);
			String sql = "select " + fieldStr + " from v_quotation where quotation_id = :quotation_id";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("quotation_id", quotationId);
			json.put("formData", baseService.getList(sql, params));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * Insert
	 */
	@RequestMapping("/saveData")
	public void saveData(@RequestParam("formData") String data, HttpServletResponse response, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;
		int keyId = 0;
		int objkeyId = 0;

		User user = (User) session.getAttribute("user");

		Quotation quotation = JSONObject.toJavaObject(JSONObject.parseObject(data), Quotation.class);
		String quotationNo = quotationService.quotationNo();// 生成一个No
		quotation.setQuotationNo(quotationNo);

		// 插入Obj表
		Obj obj = new Obj();
		obj.setObjTitle(quotation.getQuotationNo());
		obj.setObjDesc(quotation.getQuotationNo());
		obj.setStatus(1);
		obj.setKeyword(quotation.getQuotationNo());
		obj.setObjTypeId(16);
		obj.setObjParentId(7);// quotation的上级contactId
		obj.setOwnerId(user.getUserId());
		objkeyId = objService.insertObj(obj);

		// 返回Obj表的Id做为Quotation的Id
		quotation.setQuotationId(objkeyId);
		quotation.setStatus(1);// 1:草案
		quotation.setIsApproval(0);// 0:未审核
		keyId = quotationService.insert(quotation);

		if (keyId > 0) {
			result = true;
			try {
				// 添加insert记录
				ObjLog objLog = new ObjLog();
				objLog.setObjId(keyId);
				objLog.setUserId(user.getUserId());
				objLog.setDescription("Create Quotation");
				objLog.setModuleName("Quotation");
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
	 * Delete
	 * 
	 * @param keyIds
	 */
	@RequestMapping("/deleteData")
	public void deleteData(HttpServletResponse response, HttpSession session, @RequestParam(value = "keyIds") String ids) throws IOException {
		JSONObject json = new JSONObject();
		User user = (User) session.getAttribute("user");
		int status = 0;
		String msg = "";
		if (ids != null) {
			int flag = quotationService.deleteData(ids, user);
			if (flag > 0) {
				status = 1;
				msg = "Delete Success";
			} else {
				msg = "Delete Fail";
			}
		}
		json.put("status", status);
		json.put("msg", msg);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * Update
	 * 
	 * @param data
	 * @param changeFields
	 */
	@RequestMapping("/updateData")
	public void updateData(HttpServletResponse response, @RequestParam(value = "formData") String data,
			@RequestParam(value = "changeFields") String changeFields, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;
		String quotationNo = quotationService.quotationNo();// 生成一个No

		Quotation quotation = JSONObject.toJavaObject(JSONObject.parseObject(data), Quotation.class);
		quotation.setQuotationNo(quotationNo);
		Quotation quotationDb = (Quotation) baseService.getObject(Quotation.class, quotation.getQuotationId());
		User user = (User) session.getAttribute("user");
		JSONObject newdata = JSONObject.parseObject(data);

		if (quotationDb != null) {
			quotationDb.setQuotationId(quotation.getQuotationId());
			quotationDb.setQuotationNo(quotation.getQuotationNo());
			quotationDb.setClientId(quotation.getClientId());
			quotationDb.setContactId(quotation.getContactId());
			quotationDb.setTotalCost(quotation.getTotalCost());
			quotationDb.setDiscount(quotation.getDiscount());
			quotationDb.setExpiryDate(quotation.getExpiryDate());
			quotationDb.setTitle(quotation.getTitle());
			quotationDb.setCurrency(quotation.getCurrency());
			quotationDb.setExchangeRate(quotation.getExchangeRate());
			quotationDb.setLinkageModule(quotation.getLinkageModule());
			if (quotation.getTotalCost() != null && quotation.getExchangeRate() != null) {
				// amount in local currency = EXCHANGE RATE 乘TOTAL COST
				quotationDb.setLocalCurrency(quotation.getTotalCost().multiply(quotation.getExchangeRate()));
			}
			// 修改ObJ表
			Obj obj = objService.getObjData(quotation.getQuotationId());
			obj.setObjTitle(quotation.getQuotationNo());
			obj.setObjDesc(quotation.getQuotationNo());
			obj.setKeyword(quotation.getQuotationNo());
			if (quotation.getStatus() != null) {
				obj.setStatus(quotation.getStatus());
			}
			try {
				baseService.update(quotationDb);
				objService.updateObj(obj);
				int keyId = Integer.parseInt(newdata.get("quotationId").toString());
				String newchangeFields = "[" + changeFields + "]";// 把changeFields变成jsonArray形式
				JSONArray jsonArray = JSONArray.parseArray(newchangeFields);
				// 循环插入Log
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					ObjLog objLog = new ObjLog();
					objLog.setObjId(keyId);
					objLog.setUserId(user.getUserId());
					objLog.setDescription(jsonObject.get("description").toString());
					objLog.setModuleName("Quotation");
					objLog.setConfId(0);
					objLog.setFieldName(jsonObject.get("fieldName").toString());
					objLogService.createLog(objLog);
				}
				result = true;
				json.put("keyId", quotationDb.getQuotationId());
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
	@RequestMapping("/getQuotationList")
	public void getQuotationList(HttpServletResponse response, HttpServletRequest request, HttpSession session,
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

		Map<String, Object> quotationList = quotationService.seleteQuotationList(searchForm, sortBy, sortOrder, pageBo);
		JSONObject json = new JSONObject();
		json.put("recordsTotal", quotationList.get("recordsTotal"));
		json.put("recordsFiltered", quotationList.get("recordsFiltered"));
		json.put("data", quotationList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * Submit
	 */
	@RequestMapping("/submit")
	public void submit(@RequestParam("obj_id") String obj_id, HttpSession session, HttpServletResponse response) throws IOException {
		User user = (User) session.getAttribute("user");

		int keyId = workflowService.createJob(obj_id, user);
		JSONObject json = new JSONObject();
		if (keyId > 0) {
			json.put("Msg", "Success");
			json.put("Id", keyId);
		} else {
			json.put("Msg", "Error");
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * Create QuotationItem
	 */
	@RequestMapping("/createItem")
	public void createItem(@RequestParam("data") String data, @RequestParam("obj_id") String obj_id, HttpServletResponse response, HttpSession session)
			throws IOException {
		JSONObject json = new JSONObject();
		User user = (User) session.getAttribute("user");
		QuotationItem quotationItem = JSONObject.toJavaObject(JSONObject.parseObject(data), QuotationItem.class);
		int keyId = 0;
		QuotationItem quotationItemDb = new QuotationItem();
		quotationItemDb.setTitle(quotationItem.getTitle());
		quotationItemDb.setDescription(quotationItem.getDescription());
		if (quotationItem.getUnitPrice() != null && quotationItem.getQty() != null) {
			quotationItemDb.setCost(quotationItem.getUnitPrice().multiply(quotationItem.getQty()));
		}
		quotationItemDb.setUnitPrice(quotationItem.getUnitPrice());
		quotationItemDb.setQty(quotationItem.getQty());
		quotationItemDb.setDiscount(quotationItem.getDiscount());
		quotationItemDb.setParentId(Integer.parseInt(obj_id));
		quotationItemDb.setSort(quotationItem.getSort());
		quotationItemDb.setOptional(quotationItem.getOptional()); // 0和1
		quotationItemDb.setUnitCost(quotationItem.getUnitCost());
		if (quotationItem.getDiscount() != null) {
			quotationItemDb.setSubTotal(quotationItem.getUnitPrice().multiply(quotationItem.getQty()).multiply(quotationItem.getDiscount()));
		} else {
			quotationItemDb.setSubTotal(quotationItem.getUnitPrice().multiply(quotationItem.getQty()));
		}

		keyId = quotationService.insertItem(quotationItemDb);
		if (keyId > 0) {
			quotationService.updateTotalCost(obj_id);
			try {
				// 添加insert记录
				ObjLog objLog = new ObjLog();
				objLog.setObjId(keyId);
				objLog.setUserId(user.getUserId());
				objLog.setDescription("Create QuotationItem");
				objLog.setModuleName("QuotationItem");
				objLog.setConfId(0);
				objLogService.createLog(objLog);
			} catch (Exception e) {
			}
			json.put("Msg", "Success");
			json.put("Id", keyId);
		} else {
			json.put("Msg", "Error");
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * Delete Quotation Item
	 */
	@RequestMapping("/deleteItem")
	public void deleteItem(HttpServletResponse response, @RequestParam(value = "keyIds") String ids, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		User user = (User) session.getAttribute("user");
		int status = 0;
		String msg = "";
		if (ids != null) {
			int flag = quotationService.deleteItem(ids, user);
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
	 * updateQuotationItem
	 * 
	 * @param data
	 * @param changeFields
	 */
	@RequestMapping("/updateItem")
	public void updateItem(HttpServletResponse response, @RequestParam(value = "data") String data,
			@RequestParam(value = "changeFields") String changeFields, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;
		User user = (User) session.getAttribute("user");
		QuotationItem quotationItem = JSONObject.toJavaObject(JSONObject.parseObject(StringFormat.encodeStr(data)), QuotationItem.class);
		QuotationItem quotationItemDb = (QuotationItem) baseService.getObject(QuotationItem.class, quotationItem.getTitleId());

		JSONObject newdata = JSONObject.parseObject(data);
		if (quotationItemDb != null) {
			quotationItemDb.setTitle(quotationItem.getTitle());
			quotationItemDb.setDescription(quotationItem.getDescription());
			quotationItemDb.setCost(quotationItem.getCost());
			quotationItemDb.setUnitPrice(quotationItem.getUnitPrice());
			quotationItemDb.setQty(quotationItem.getQty());
			quotationItemDb.setDiscount(quotationItem.getDiscount());
			quotationItemDb.setParentId(quotationItem.getParentId());
			quotationItemDb.setSort(quotationItem.getSort());
			quotationItemDb.setUnitCost(quotationItem.getUnitCost());
			quotationItemDb.setSubTotal(quotationItem.getSubTotal());

			try {
				baseService.update(quotationItemDb);
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
					objLog.setModuleName("QuotationItem");
					objLog.setConfId(0);
					objLog.setFieldName(jsonObject.get("fieldName").toString());
					objLogService.createLog(objLog);
				}
				result = true;
				json.put("keyId", quotationItemDb.getTitleId());
			} catch (Exception e) {
			}
		}

		json.put("result", result);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * 根据QuotationID 获取 Item
	 */
	@RequestMapping("/getQuotationItemList")
	public void getQuotationItemList(HttpServletResponse response, HttpServletRequest request, HttpSession session,
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

		Map<String, Object> quotationItemList = quotationService.getQuotationItemList(searchForm, sortBy, sortOrder, pageBo);
		JSONObject json = new JSONObject();
		json.put("recordsTotal", quotationItemList.get("recordsTotal"));
		json.put("recordsFiltered", quotationItemList.get("recordsFiltered"));
		json.put("data", quotationItemList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	@RequestMapping("/printPDF")
	public void printPDF(@RequestParam("quotationId") int quotationId, HttpServletRequest request, HttpServletResponse response) throws Exception {

		request.setCharacterEncoding("utf-8");
		SimpleDateFormat dateFormat3 = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);
		DecimalFormat numFormat = new DecimalFormat("###,###,##0.00");

		Quotation quotation = null;
		List quotationItems = new ArrayList<>();
		List quotationFooters = new ArrayList<>();
		Quotation[] data = new Quotation[0];

		List<Object> quotationList = quotationService.getQuotationById(quotationId);
		for (int i = 0; i < quotationList.size(); i++) {
			quotation = (Quotation) quotationList.get(i);
		}

		List<Object> list = quotationService.getQuotationItemsByParentId(quotationId);
		for (int i = 0; i < list.size(); i++) {
			QuotationItem qi = (QuotationItem) list.get(i);
			quotationItems.add(qi);
		}

		List<Object> quotationFooterList = quotationService.getQuotationFootersByParentId(quotationId);
		for (int i = 0; i < quotationFooterList.size(); i++) {
			QuotationFooter qf = (QuotationFooter) quotationFooterList.get(i);
			quotationFooters.add(qf);
		}

		String root_path = System.getProperty("evan.webapp");

		try {
			String formNo = quotation.getQuotationNo();
			String fname = formNo + ".pdf";
			response.setContentType("application/pdf");
			String fileName = new String(fname.getBytes("UTF-8"), "ISO-8859-1");
			fileName = "\"" + fileName + "\"";
			response.setHeader("Content-disposition", "attachment; filename=" + fileName);
			ServletOutputStream ouputStream = response.getOutputStream();

			if (quotation != null) {
				data = new Quotation[1];
				data[0] = quotation;
			}
			if (quotationItems == null) {
				quotationItems = new ArrayList<QuotationItem>();
			}

			if (quotationFooters == null) {
				quotationFooters = new ArrayList<QuotationFooter>();
			}

			QuotationDs qds = new QuotationDs(data, quotationItems);
			QuotationFooterDs qfds = new QuotationFooterDs(quotationFooters);

			BigDecimal originalTotalHKD = new BigDecimal(0);
			BigDecimal totalHKD = new BigDecimal(0);
			BigDecimal discounted = new BigDecimal(0);// sum of item discount
			BigDecimal totalDiscount = new BigDecimal(0);
			originalTotalHKD = quotationService.getOriginalTotalByQuotationId(quotation.getQuotationId());
			totalHKD = quotationService.getSubTotalByQuotationId(quotation.getQuotationId());
			discounted = quotationService.getDiscountByQuotationId(quotation.getQuotationId());
			BigDecimal quotationDiscount = quotation.getDiscount();
			if (quotationDiscount != null) {
				totalDiscount = totalDiscount.add(quotationDiscount);
				totalHKD = totalHKD.subtract(quotationDiscount);
			}
			if (discounted != null) {
				totalDiscount = totalDiscount.add(discounted);
			}

			/* *begin prepare pdf data */

			HashMap hm = new HashMap();// 转载参数的map
			hm.put("totalDiscount", numFormat.format(totalDiscount));

			String embraiz_logo = root_path + "images/pdf/embraiz_logo.jpg";
			String report = root_path + "images/pdf/sig_new.jpg";
			String watermark = root_path + "images/pdf/watermark.jpg";

			if (quotation != null) {
				// get company's name
				String companyName = quotation.getClientName();
				hm.put("enFlag", 1);
				if (companyName == null || "".equals(companyName.trim())) {
					hm.put("enFlag", 0);
				}
				hm.put("companyName", companyName);
			}

			hm.put("embraiz_logo", embraiz_logo);
			hm.put("report", report);
			hm.put("contactName", quotation.getContactName());
			hm.put("contactEmail", quotation.getContactEmail());
			if (quotation.getStatus() != null && quotation.getStatus() < 6) {
				hm.put("watermark", watermark);
			}
			if (originalTotalHKD == null || originalTotalHKD.doubleValue() == 0)
				hm.put("originalTotalHKD", 0);
			else
				hm.put("originalTotalHKD", numFormat.format(originalTotalHKD.doubleValue()));

			if (totalHKD == null || totalHKD.doubleValue() == 0)
				hm.put("totalHKD", 0);
			else
				hm.put("totalHKD",
						(quotation.getClientName() != null ? quotation.getClientName() : "HK$") + " " + numFormat.format(totalHKD.doubleValue()));

			// 获取quotation的createDate
			if (quotation.getCreateDate() != null && !"".equals(quotation.getCreateDate())) {
				hm.put("createDate", dateFormat3.format(quotation.getCreateDate()));
			} else {
				hm.put("createDate", "");
			}
			hm.put("currentDate", "Date:" + dateFormat3.format(new Date()));

			// 等待写入的文件流
			JRPdfExporter exporter = new JRPdfExporter();

			String report1 = "";
			String report2 = "";
			report1 = root_path + "WEB-INF/quotationTemplet/quotation1.jasper";
			report2 = root_path + "WEB-INF/quotationTemplet/quotation2.jasper";
			QuotationDs qds2 = new QuotationDs(data, quotationItems);
			QuotationFooterDs qfds2 = new QuotationFooterDs(quotationFooters);
			JasperPrint jasperPrint1 = JasperFillManager.fillReport(report1, hm, qds);
			JasperPrint jasperPrint2 = JasperFillManager.fillReport(report2, hm, qfds);
			// get count of report1
			hm.put("totalPageCount1", jasperPrint1.getPages().size());
			hm.put("totalPageCount2", jasperPrint2.getPages().size());
			// get page count of report2
			hm.put("totalPageCount", jasperPrint1.getPages().size() + jasperPrint2.getPages().size());

			JasperPrint jasperPrint11 = JasperFillManager.fillReport(report1, hm, qds2);
			JasperPrint jasperPrint22 = JasperFillManager.fillReport(report2, hm, qfds2);

			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			jasperPrintList.add(jasperPrint11);
			jasperPrintList.add(jasperPrint22);

			exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, ouputStream);

			exporter.exportReport();
			ouputStream.flush();
			ouputStream.close();
		} catch (JRException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add Quotation Footer
	 */
	@RequestMapping("/saveFooter")
	public void saveFooter(@RequestParam("data") String data, HttpServletResponse response, HttpSession session) throws IOException {
		User user = (User) session.getAttribute("user");
		QuotationFooter quotationFooter = JSONObject.toJavaObject(JSONObject.parseObject(data), QuotationFooter.class);
		JSONObject json = new JSONObject();
		int keyId = 0;

		QuotationFooter quotationFooterDb = new QuotationFooter();
		if (quotationFooter.getDescription() != null && !"".equals(quotationFooter.getDescription())) {
			quotationFooterDb.setDescription(StringFormat.encodeStr(quotationFooter.getDescription()));
		}
		if (quotationFooter.getTitle() != null && !"".equals(quotationFooter.getTitle())) {
			quotationFooterDb.setTitle(StringFormat.encodeStr(quotationFooter.getTitle()));
		}
		quotationFooterDb.setQuotationId(quotationFooter.getQuotationId());

		keyId = quotationService.insertFooter(quotationFooterDb);
		if (keyId > 0) {
			try {
				// 添加insert记录
				ObjLog objLog = new ObjLog();
				objLog.setObjId(keyId);
				objLog.setUserId(user.getUserId());
				objLog.setDescription("Create QuotationFooter");
				objLog.setModuleName("QuotationFooter");
				objLog.setConfId(0);
				objLogService.createLog(objLog);
			} catch (Exception e) {
			}
			json.put("Msg", "Success");
			json.put("Id", keyId);
		} else {
			json.put("Msg", "Error");
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * Delete QuotationFooter
	 */
	@RequestMapping("/deleteQuotationFooter")
	public void deleteQuotationFooter(HttpSession session, HttpServletResponse response, @RequestParam(value = "keyIds") String ids)
			throws IOException {
		JSONObject json = new JSONObject();
		User user = (User) session.getAttribute("user");
		int status = 0;
		String msg = "";
		if (ids != null) {
			int flag = quotationService.deleteFooter(ids, user);
			if (flag > 0) {
				status = 1;
				msg = "Delete Success";
			} else {
				msg = "Delete Fail";
			}
		}
		json.put("status", status);
		json.put("msg", msg);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * UpdateQuotationFooter
	 * 
	 * @param data
	 * @param changeFields
	 */
	@RequestMapping("/updateQuotationFooter")
	public void updateQuotationFooter(HttpServletResponse response, @RequestParam(value = "data") String data,
			@RequestParam(value = "changeFields") String changeFields, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;
		User user = (User) session.getAttribute("user");
		QuotationFooter quotationfooter = JSONObject.toJavaObject(JSONObject.parseObject(StringFormat.encodeStr(data)), QuotationFooter.class);
		QuotationFooter quotationfooterDb = (QuotationFooter) baseService.getObject(QuotationFooter.class, quotationfooter.getId());

		JSONObject newdata = JSONObject.parseObject(data);
		if (quotationfooterDb != null) {
			quotationfooterDb.setDescription(quotationfooter.getDescription());
			quotationfooterDb.setTitle(quotationfooter.getTitle());
			quotationfooterDb.setQuotationId(quotationfooter.getQuotationId());

			try {
				baseService.update(quotationfooterDb);
				int keyId = Integer.parseInt(newdata.get("Id").toString());
				String newchangeFields = "[" + changeFields + "]";// 把changeFields变成jsonArray形式
				JSONArray jsonArray = JSONArray.parseArray(newchangeFields);
				// 循环插入Log
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					ObjLog objLog = new ObjLog();
					objLog.setObjId(keyId);
					objLog.setUserId(user.getUserId());
					objLog.setDescription(jsonObject.get("description").toString());
					objLog.setModuleName("QuotationFooter");
					objLog.setConfId(0);
					objLog.setFieldName(jsonObject.get("fieldName").toString());
					objLogService.createLog(objLog);
				}
				result = true;
				json.put("keyId", quotationfooterDb.getId());
			} catch (Exception e) {
			}
		}

		json.put("result", result);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * 根据QuotationID 获取 Footer
	 */
	@RequestMapping("/getQuotationFooterList")
	public void getQuotationFooterList(HttpServletResponse response, HttpServletRequest request, HttpSession session,
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

		Map<String, Object> quotationFooterList = quotationService.getQuotationFooterList(searchForm, sortBy, sortOrder, pageBo);
		JSONObject json = new JSONObject();
		json.put("recordsTotal", quotationFooterList.get("recordsTotal"));
		json.put("recordsFiltered", quotationFooterList.get("recordsFiltered"));
		json.put("data", quotationFooterList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * 获取货币数据
	 */
	@RequestMapping("/getcurrency")
	public void getcurrency(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();

		Map<String, Object> currencyList = quotationService.getcurrency();
		json.put("recordsTotal", currencyList.get("recordsTotal"));
		json.put("recordsFiltered", currencyList.get("recordsFiltered"));
		json.put("data", currencyList.get("data"));
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * 获取详情
	 */
	@RequestMapping("/getDetail")
	public void getDetail(@RequestParam("keyId") Integer quotationId, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		Map<String, Object> quotationDetail = quotationService.getDetail(quotationId);
		json.put("data", quotationDetail.get("data"));
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * Quotation Item 内TEMP的下拉数据
	 */
	@RequestMapping("/getTemplateDatasourse")
	public void getTemplateDatasourse(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();

		Map<String, Object> TemplateList = quotationService.getTemplateDatasourse();
		json.put("recordsTotal", TemplateList.get("recordsTotal"));
		json.put("recordsFiltered", TemplateList.get("recordsFiltered"));
		json.put("data", TemplateList.get("data"));
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

}
