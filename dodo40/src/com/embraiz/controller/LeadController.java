package com.embraiz.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.Client;
import com.embraiz.model.Contact;
import com.embraiz.model.Lead;
import com.embraiz.model.Module;
import com.embraiz.model.Obj;
import com.embraiz.model.ObjLog;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.CommonService;
import com.embraiz.service.ConfService;
import com.embraiz.service.LeadService;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.ObjService;
import com.embraiz.util.ExcelExport;
import com.embraiz.util.StringFormat;

@Controller
@RequestMapping("/lead")
public class LeadController {

	@Resource
	private BaseService baseService;
	@Resource
	private LeadService leadService;
	@Resource
	private CommonService commonService;
	@Resource
	private ConfService confService;
	@Resource
	private ObjService objService;
	@Resource
	private ObjLogService objLogService;

	/**
	 * 根据moduleName，获取该模块下的所有栏位配置信息
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
			List<Map<String, Object>> fieldListInGrid = commonService.getGridFieldInModule("lead", lang);
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
							+ ") FROM ref_option ref WHERE FIND_IN_SET(ref.option_id,v_lead." + multiselectFieldLabel + ")) AS "
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
			String sql = "select " + fieldStr + " from v_lead where 1=1 ";
			String sqlCount = "select count(1) from v_lead where 1=1  ";

			// 搜索区域用到的栏位
			List<Map<String, Object>> fieldListInSearch = commonService.getSearchFieldInModule("company", lang);

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
			json.put("searchDataList", commonService.getSearchFieldInModule("lead", lang));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 获得表单区域的栏位和数据
	 */
	@RequestMapping("/getFormData")
	public void getFormData(HttpServletResponse response, HttpSession session, @RequestParam("primaryId") Integer leadId) throws IOException {
		User user = (User) session.getAttribute("user");
		JSONObject json = new JSONObject();
		if (user != null) {
			String lang = (String) session.getAttribute("lang");
			if (lang == null) {
				lang = "en";
			}
			// 获得表单区域需要显示的栏位
			List<Map<String, Object>> fieldListInForm = commonService.getFormFieldInModule("lead", lang);
			json.put("fieldListInForm", fieldListInForm);
			// 存储拼接的字段
			String fieldStr = "";
			for (int i = 0; i < fieldListInForm.size(); i++) {
				fieldStr = fieldStr + fieldListInForm.get(i).get("fieldLabel").toString() + ",";
			}
			fieldStr = fieldStr.substring(0, fieldStr.length() - 1);
			String sql = "select " + fieldStr + " from v_lead where leadId = :leadId";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("leadId", leadId);
			json.put("formData", baseService.getList(sql, params));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * Insert
	 * 
	 * @param data
	 */
	@RequestMapping("/saveData")
	public void saveData(@RequestParam("formData") String data, HttpServletResponse response, HttpSession session) throws IOException {
		Lead lead = JSONObject.toJavaObject(JSONObject.parseObject(data), Lead.class);
		JSONObject json = new JSONObject();
		boolean result = false;
		int keyId = 0;
		int objkeyId = 0;
		User user = (User) session.getAttribute("user");
		// 插入OBJ表
		Obj obj = new Obj();
		obj.setObjTitle(lead.getLeadName());
		obj.setObjDesc(lead.getLeadName());
		obj.setStatus(1);
		obj.setKeyword(lead.getLeadName());
		obj.setObjTypeId(20);
		obj.setOwnerId(user.getUserId());
		objkeyId = objService.insertObj(obj);

		// 返回Obj表的Id做为Lead的Id
		lead.setLeadId(objkeyId);
		keyId = leadService.insert(lead);

		if (keyId > 0) {
			result = true;
			try {
				// 添加insert记录
				ObjLog objLog = new ObjLog();
				objLog.setObjId(keyId);
				objLog.setUserId(user.getUserId());
				objLog.setDescription("Create lead");
				objLog.setModuleName("Lead");
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
	 * DeleteData
	 */
	@RequestMapping("/deleteData")
	public void deleteDataAll(HttpSession session, @RequestParam(value = "keyIds") String ids, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		User user = (User) session.getAttribute("user");
		int status = 0;
		String msg = "";
		if (ids != null) {
			int flag = leadService.deleteData(ids, user);
			if (flag > 0) {
				status = 1;
				msg = "succDel";
			} else {
				msg = "failDel";
			}
		}
		json.put("status", status);
		json.put("result", msg);
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
	public void updateData(HttpServletResponse response, @RequestParam(value = "formData") String formData,
			@RequestParam(value = "changeFields") String changeFields, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;
		Lead lead = JSONObject.toJavaObject(JSONObject.parseObject(StringFormat.encodeStr(formData)), Lead.class);
		Lead leadDb = (Lead) baseService.getObject(Lead.class, lead.getLeadId());
		User user = (User) session.getAttribute("user");
		JSONObject newdata = JSONObject.parseObject(formData);

		if (leadDb != null) {
			leadDb.setLeadName(lead.getLeadName());
			leadDb.setSourceId(lead.getSourceId());
			leadDb.setNotes(lead.getNotes());
			leadDb.setAssignTo(lead.getAssignTo());
			leadDb.setAssignExpiryTime(lead.getAssignExpiryTime());
			leadDb.setClient(lead.getClient());
			leadDb.setDoingBusinessOtherName(lead.getDoingBusinessOtherName());
			leadDb.setClientPhone(lead.getClientPhone());
			leadDb.setClientFax(lead.getClientFax());
			leadDb.setAddress1(lead.getAddress1());
			leadDb.setAddress2(lead.getAddress2());
			leadDb.setDistrictId(lead.getDistrictId());
			leadDb.setLocationId(lead.getLocationId());
			leadDb.setWebsite(lead.getWebsite());
			leadDb.setClientEmail(lead.getClientEmail());
			leadDb.setNatureofbusinessId(lead.getNatureofbusinessId());
			leadDb.setContact(lead.getContact());
			leadDb.setContactTitle(lead.getContactTitle());
			leadDb.setContactDirectLine(lead.getContactDirectLine());
			leadDb.setContactDirectFax(lead.getContactDirectFax());
			leadDb.setContactMobile(lead.getContactMobile());
			leadDb.setContactTitle(lead.getContactTitle());
			leadDb.setContactEmail(lead.getContactEmail());
			leadDb.setConverted(lead.getConverted());
			leadDb.setOptOutTime(lead.getOptOutTime());
			leadDb.setMarketingCode(lead.getMarketingCode());
			leadDb.setShopFlatRoomNo(lead.getShopFlatRoomNo());
			leadDb.setFloor(lead.getFloor());
			leadDb.setBlock(lead.getBlock());
			leadDb.setEstate(lead.getEstate());
			leadDb.setStreetNo(lead.getStreetNo());
			leadDb.setPostalCode(lead.getPostalCode());
			leadDb.setOptOut(lead.getOptOut());
			leadDb.setReturnMail(lead.getReturnMail());

			Obj obj = objService.getObjData(lead.getLeadId());
			obj.setObjTitle(lead.getLeadName());
			obj.setObjDesc(lead.getLeadName());
			obj.setKeyword(lead.getLeadName());

			try {
				baseService.update(leadDb);
				objService.updateObj(obj);
				int keyId = Integer.parseInt(newdata.get("leadId").toString());
				String newchangeFields = "[" + changeFields + "]";// 把changeFields变成jsonArray形式
				JSONArray jsonArray = JSONArray.parseArray(newchangeFields);
				// 循环插入Log
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					ObjLog objLog = new ObjLog();
					objLog.setObjId(keyId);
					objLog.setUserId(user.getUserId());
					objLog.setDescription(jsonObject.get("description").toString());
					objLog.setModuleName("Lead");
					objLog.setConfId(0);
					objLog.setFieldName(jsonObject.get("fieldName").toString());
					objLogService.createLog(objLog);
				}
				result = true;
				json.put("keyId", leadDb.getLeadId());
			} catch (Exception e) {
			}
		}
		json.put("result", result);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * 获取详情
	 */
	@RequestMapping("/getDetail")
	public void getDetail(@RequestParam("keyId") Integer leadId, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();

		List<Map<String, Object>> fieldList = commonService.getFilldByModule("lead");

		Lead lead = null;
		String fields = "";
		for (int i = 0; i < fieldList.size(); i++) {
			fields += fieldList.get(i).get("field_label") + ",";
		}

		if (fields != null && !"".equals(fields)) {
			fields = fields.substring(0, fields.length() - 1);

			Map<String, Object> params = new HashMap<>();
			String sql = "select  " + fields + " from v_lead where leadId=:leadId";
			params.put("leadId", leadId);
			List<Object> list = baseService.getList(params, sql, Lead.class);

			for (int k = 0; k < list.size(); k++) {
				lead = (Lead) list.get(k);
			}
		}

		json.put("data", lead);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * Convert
	 */
	@RequestMapping("/convert")
	public void convert(@RequestParam("clientdata") String clientdata, @RequestParam("contactdata") String contactdata, HttpSession session,
			HttpServletResponse response) throws IOException {

		JSONObject json = new JSONObject();
		boolean result = false;
		int objkeyId = 0;
		int clientId = 0;
		int contactId = 0;
		User user = (User) session.getAttribute("user");

		for (int i = 0; i < 2; i++) {
			Client client = JSONObject.toJavaObject(JSONObject.parseObject(clientdata), Client.class);
			// 插入OBJ表(client)
			Obj obj1 = new Obj();
			obj1.setObjTitle(client.getClientName());
			obj1.setObjDesc(client.getClientName());
			obj1.setStatus(1);
			obj1.setKeyword(client.getClientName());
			obj1.setObjTypeId(3);
			obj1.setOwnerId(user.getUserId());
			objkeyId = objService.insertObj(obj1);
			client.setClientId(objkeyId);
			clientId = (Integer) baseService.save(client);

			this.createlog(clientId, user.getUserId(), "client");

			Contact contact = JSONObject.toJavaObject(JSONObject.parseObject(contactdata), Contact.class);

			// 插入OBJ表(contact)
			Obj obj2 = new Obj();
			obj2.setObjTitle(client.getClientName());
			obj2.setObjDesc(client.getClientName());
			obj2.setStatus(1);
			obj2.setKeyword(client.getClientName());
			obj2.setObjTypeId(7);
			obj2.setObjParentId(6);
			obj2.setOwnerId(user.getUserId());
			objkeyId = objService.insertObj(obj2);
			client.setClientId(objkeyId);
			contactId = (Integer) baseService.save(contact);

			this.createlog(contactId, user.getUserId(), "contact");
		}
		if (clientId != 0 && contactId != 0) {
			result = true;
		} else {
			result = false;
		}

		json.put("result", result);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * excel 导出
	 */
	@RequestMapping("/exportFile")
	public void exportFile(@RequestParam(value = "searchForm", defaultValue = "") String searchForm, HttpServletResponse response) throws Exception {
		List<Map<String, Object>> exportList = leadService.getexportList(searchForm);
		ExcelExport excel = new ExcelExport();
		excel.createExcelByLead(exportList, response);
	}

	/**
	 * excel 导入
	 */
	@RequestMapping("/insertexportFile")
	public void insertexportFile(@RequestParam(value = "filename") MultipartFile file, HttpServletResponse response) throws IOException {
		String Msg = "";
		boolean result = false;
		JSONObject json = new JSONObject();
		if (file == null) {
			Msg = "文件名为空";
		} else {
			String name = file.getOriginalFilename();
			// 进一步判断文件是否为空（即判断其大小是否为0或其名称是否为null）
			long size = file.getSize();
			if (name == null || ("").equals(name) && size == 0) {
				Msg = "文件名为空";
			} else {
				result = leadService.insertexportFile(name, file);
				if (result) {
					Msg = "批量导入EXCEL成功！";
				} else {
					Msg = "批量导入EXCEL失败！";
				}
			}
		}
		json.put("result", result);
		json.put("Msg", Msg);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * Area
	 */
	@RequestMapping("/getArea")
	public void getArea(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		Map<String, Object> AreaList = leadService.getArea();

		json.put("recordsTotal", AreaList.get("recordsTotal"));
		json.put("recordsFiltered", AreaList.get("recordsFiltered"));
		json.put("data", AreaList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * ReturnMailList
	 */
	@RequestMapping("/getReturnMail")
	public void getReturnMail(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		Map<String, Object> ReturnMailList = leadService.getReturnMail();

		json.put("recordsTotal", ReturnMailList.get("recordsTotal"));
		json.put("recordsFiltered", ReturnMailList.get("recordsFiltered"));
		json.put("data", ReturnMailList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * Opt out
	 */
	@RequestMapping("/getOptout")
	public void getOptout(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		Map<String, Object> OptoutList = leadService.getOptout();

		json.put("recordsTotal", OptoutList.get("recordsTotal"));
		json.put("recordsFiltered", OptoutList.get("recordsFiltered"));
		json.put("data", OptoutList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	public void createlog(int id, int userId, String module) {
		// 添加insert记录
		ObjLog objLog = new ObjLog();
		objLog.setObjId(id);
		objLog.setUserId(userId);
		objLog.setDescription("Create " + module);
		objLog.setModuleName(module);
		objLog.setConfId(0);
		objLogService.createLog(objLog);
	}

}
