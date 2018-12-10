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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.Contact;
import com.embraiz.model.Module;
import com.embraiz.model.Obj;
import com.embraiz.model.ObjLog;
import com.embraiz.model.Page;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.CommonService;
import com.embraiz.service.ConfService;
import com.embraiz.service.ContactService;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.ObjService;
import com.embraiz.util.ExcelExport;
import com.embraiz.util.StringFormat;

@Controller
@RequestMapping("/contact")
public class ContactController {

	@Resource
	private ContactService contactService;
	@Resource
	private ObjService objSerivce;
	@Resource
	private BaseService baseService;
	@Resource
	private ObjLogService objLogService;
	@Resource
	private ObjService objService;
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
			List<Map<String, Object>> fieldListInGrid = commonService.getGridFieldInModule("contact", lang);
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
							+ ") FROM ref_option ref WHERE FIND_IN_SET(ref.option_id,v_obj_contact." + multiselectFieldLabel + ")) AS "
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
			String sql = "select " + fieldStr + " from v_obj_contact where 1=1 ";
			String sqlCount = "select count(1) from v_obj_contact where 1=1  ";

			// 搜索区域用到的栏位
			List<Map<String, Object>> fieldListInSearch = commonService.getSearchFieldInModule("contact", lang);

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
			json.put("searchDataList", commonService.getSearchFieldInModule("contact", lang));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 获得表单区域的栏位和数据
	 */
	@RequestMapping("/getFormData")
	public void getFormData(HttpServletResponse response, HttpSession session, @RequestParam("primaryId") Integer contactId) throws IOException {
		User user = (User) session.getAttribute("user");
		JSONObject json = new JSONObject();
		if (user != null) {
			String lang = (String) session.getAttribute("lang");
			if (lang == null) {
				lang = "en";
			}
			// 获得表单区域需要显示的栏位
			List<Map<String, Object>> fieldListInForm = commonService.getFormFieldInModule("contact", lang);
			json.put("fieldListInForm", fieldListInForm);
			// 存储拼接的字段
			String fieldStr = "";
			for (int i = 0; i < fieldListInForm.size(); i++) {
				fieldStr = fieldStr + fieldListInForm.get(i).get("fieldLabel").toString() + ",";
			}
			fieldStr = fieldStr.substring(0, fieldStr.length() - 1);
			String sql = "select " + fieldStr + " from v_obj_contact where company_id = :company_id";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("company_id", contactId);
			json.put("formData", baseService.getList(sql, params));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * insert Contact
	 * 
	 * @param data
	 */
	@RequestMapping("/saveData")
	public void saveData(@RequestParam("formData") String data, HttpServletResponse response, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;

		int keyId = 0;
		int objkeyId = 0;
		User user = (User) session.getAttribute("user");
		Contact contact = JSONObject.toJavaObject(JSONObject.parseObject(data), Contact.class);
		int objTypeId = objService.searchObjType("contact");// 返回quotation的TypeId
		// 插入Obj表
		Obj obj = new Obj();
		obj.setObjTitle(contact.getContactName());
		obj.setObjDesc(contact.getContactName());
		obj.setStatus(1);
		obj.setKeyword(contact.getContactName());
		obj.setObjTypeId(objTypeId);
		obj.setObjParentId(6);// contact的上级ClientId
		obj.setOwnerId(user.getUserId());
		objkeyId = objService.insertObj(obj);
		contact.setContactId(objkeyId);
		keyId = contactService.insert(contact);
		if (keyId > 0) {
			try {
				// 添加insert记录
				ObjLog objLog = new ObjLog();
				objLog.setObjId(keyId);
				objLog.setUserId(user.getUserId());
				objLog.setDescription("Create Contact");
				objLog.setModuleName("Contact");
				objLog.setConfId(0);
				objLogService.createLog(objLog);
			} catch (Exception e) {
			}
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
	 * delete Contact
	 * 
	 * @param ids
	 */
	@RequestMapping("/deleteData")
	public void deleteData(HttpServletResponse response, HttpServletRequest request, @RequestParam(value = "keyIds") String ids) throws IOException {
		JSONObject json = new JSONObject();
		User user = (User) request.getSession().getAttribute("user");
		int status = 0;
		String msg = "";
		if (ids != null) {
			int flag = contactService.deleteData(ids, user.getUserId());
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
	 * update Contact
	 * 
	 * @param data
	 */
	@RequestMapping("/updateData")
	public void updateData(HttpServletResponse response, @RequestParam(value = "formData") String data,
			@RequestParam(value = "changeFields") String changeFields, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;
		Contact contact = JSONObject.toJavaObject(JSONObject.parseObject(StringFormat.encodeStr(data)), Contact.class);
		Contact contactDb = (Contact) baseService.getObject(Contact.class, contact.getContactId());

		if (contactDb != null) {
			contactDb.setGeneralPhone(contact.getGeneralPhone());
			contactDb.setShopFlatRoomAddr(contact.getShopFlatRoomAddr());
			contactDb.setFloorAddr(contact.getFloorAddr());
			contactDb.setBlockAddr(contact.getBlockAddr());
			contactDb.setBuildingAddr(contact.getBuildingAddr());
			contactDb.setStreetNoAddr(contact.getStreetNoAddr());
			contactDb.setTreeNameAddr(contact.getTreeNameAddr());
			contactDb.setDistrict(contact.getDistrict());
			contactDb.setArea(contact.getArea());
			contactDb.setEmail(contact.getEmail());
			contactDb.setWebsite(contact.getWebsite());
			contactDb.setFax(contact.getFax());
			contactDb.setNameInLocalLanguage(contact.getNameInLocalLanguage());
			contactDb.setPostalCode(contact.getPostalCode());
			contactDb.setBrCode(contact.getBrCode());
			contactDb.setDirectLine(contact.getDirectLine());
			contactDb.setMobile(contact.getMobile());
			contactDb.setFirstName(contact.getFirstName());
			contactDb.setLastName(contact.getLastName());
			contactDb.setJobTitle(contact.getJobTitle());
			contactDb.setMrOrMiss(contact.getMrOrMiss());
			contactDb.setCompanyId(contact.getCompanyId());
			contactDb.setCompanyName(contact.getCompanyName());
			contactDb.setClientId(contact.getClientId());
			contactDb.setClientName(contact.getClientName());
			contactDb.setContactName(contact.getContactName());
			contactDb.setLinkageModule(contact.getLinkageModule());

			// 修改ObJ表
			Obj obj = objService.getObjData(contact.getContactId());
			obj.setObjTitle(contact.getContactName());
			obj.setObjDesc(contact.getContactName());
			obj.setKeyword(contact.getContactName());
			//obj.setStatus(contact.getStatus());

			try {
				baseService.update(contactDb);
				objService.updateObj(obj);
				result = true;
				User user = (User) session.getAttribute("user");

				// 添加到objLog表
				try {
					String newchangeFields = "[" + StringFormat.encodeStr(changeFields) + "]";
					JSONArray jsonArray = JSONArray.parseArray(newchangeFields);
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject jsonObject = (JSONObject) jsonArray.get(i);
						ObjLog objLog = new ObjLog();
						objLog.setObjId(contact.getContactId());
						objLog.setUserId(user.getUserId());
						objLog.setDescription(jsonObject.get("description").toString());
						objLog.setModuleName("Contact");
						objLog.setConfId(0);
						objLog.setFieldName(jsonObject.get("fieldName").toString());
						objLogService.createLog(objLog);
					}

				} catch (Exception e) {
				}
				json.put("keyId", contactDb.getContactId());
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
			}
		}

		json.put("result", result);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * 查询Contact
	 */
	@RequestMapping("/getContactList")
	public void getContactList(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "searchForm") String searchForm, @RequestParam(value = "length") int length,
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
		Map<String, Object> contactList = contactService.seleteContactList(searchForm, sortBy, sortOrder, pageBo);
		JSONObject json = new JSONObject();
		json.put("recordsTotal", contactList.get("recordsTotal"));
		json.put("recordsFiltered", contactList.get("recordsFiltered"));
		json.put("data", contactList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 获取全部联系人名字的数据源
	 */
	@RequestMapping("/getDatasourse")
	public void getDatasourse(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		Map<String, Object> contactList = contactService.getAllContactName();

		json.put("recordsTotal", contactList.get("recordsTotal"));
		json.put("recordsFiltered", contactList.get("recordsFiltered"));
		json.put("data", contactList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 获取详情
	 * 
	 * @param contactId
	 */
	@RequestMapping("/getDetail")
	public void getDetail(@RequestParam("keyId") Integer contactId, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();

		List<Map<String, Object>> fieldList = commonService.getFilldByModule("contact");

		Contact contact = null;
		String fields = "";
		for (int i = 0; i < fieldList.size(); i++) {
			fields += fieldList.get(i).get("field_label") + ",";
		}
		if (fields != null && !"".equals(fields)) {
			fields = fields.substring(0, fields.length() - 1);
			Map<String, Object> params = new HashMap<>();
			String sql = "select  " + fields + " from v_obj_contact where contactId=:contactId";
			params.put("contactId", contactId);
			List<Object> list = baseService.getList(params, sql, Contact.class);

			for (int k = 0; k < list.size(); k++) {
				contact = (Contact) list.get(k);
			}
		}

		json.put("data", contact);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * excel 导出
	 */
	@RequestMapping("/exportFile")
	public void exportFile(@RequestParam(value = "searchForm", defaultValue = "") String searchForm, HttpServletResponse response) throws Exception {
		List<Map<String, Object>> exportList = contactService.getexportList(searchForm);
		ExcelExport excel = new ExcelExport();
		excel.createExcelByContact(exportList, response);
	}

}
