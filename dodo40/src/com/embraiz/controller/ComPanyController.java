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
import com.embraiz.model.Company;
import com.embraiz.model.Module;
import com.embraiz.model.Obj;
import com.embraiz.model.ObjLog;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.ComPanyService;
import com.embraiz.service.CommonService;
import com.embraiz.service.ConfService;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.ObjService;
import com.embraiz.util.StringFormat;

@Controller
@RequestMapping("/company")
public class ComPanyController {

	@Resource
	private ComPanyService comPanyService;
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
			List<Map<String, Object>> fieldListInGrid = commonService.getGridFieldInModule("company", lang);
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
							+ ") FROM ref_option ref WHERE FIND_IN_SET(ref.option_id,v_obj_company." + multiselectFieldLabel + ")) AS "
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
			String sql = "select " + fieldStr + " from v_obj_company where 1=1 ";
			String sqlCount = "select count(1) from v_obj_company where 1=1  ";

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
			json.put("searchDataList", commonService.getSearchFieldInModule("company", lang));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 获得表单区域的栏位和数据
	 */
	@RequestMapping("/getFormData")
	public void getFormData(HttpServletResponse response, HttpSession session, @RequestParam("primaryId") Integer companyId) throws IOException {
		User user = (User) session.getAttribute("user");
		JSONObject json = new JSONObject();
		if (user != null) {
			String lang = (String) session.getAttribute("lang");
			if (lang == null) {
				lang = "en";
			}
			// 获得表单区域需要显示的栏位
			List<Map<String, Object>> fieldListInForm = commonService.getFormFieldInModule("company", lang);
			json.put("fieldListInForm", fieldListInForm);
			// 存储拼接的字段
			String fieldStr = "";
			for (int i = 0; i < fieldListInForm.size(); i++) {
				fieldStr = fieldStr + fieldListInForm.get(i).get("fieldLabel").toString() + ",";
			}
			fieldStr = fieldStr.substring(0, fieldStr.length() - 1);
			String sql = "select " + fieldStr + " from v_obj_company where companyId = :companyId";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("companyId", companyId);
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
		Company company = JSONObject.toJavaObject(JSONObject.parseObject(data), Company.class);
		JSONObject json = new JSONObject();
		boolean result = false;
		int keyId = 0;
		int objkeyId = 0;
		User user = (User) session.getAttribute("user");
		// 插入Obj表
		Obj obj = new Obj();
		obj.setObjTitle(company.getCompanyName());
		obj.setObjDesc(company.getCompanyName());
		obj.setStatus(1);
		obj.setKeyword(company.getCompanyName());
		obj.setObjTypeId(3);
		obj.setObjParentId(0);
		obj.setOwnerId(user.getUserId());
		objkeyId = objService.insertObj(obj);
		

		// 返回Obj表的Id做为Company的Id
		company.setCompanyId(objkeyId);
		keyId = comPanyService.insert(company);

		if (keyId > 0) {
			result = true;
			try {
				// 添加insert记录
				ObjLog objLog = new ObjLog();
				objLog.setObjId(keyId);
				objLog.setUserId(user.getUserId());
				objLog.setDescription("Create Company");
				objLog.setModuleName("Company");
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
			int flag = comPanyService.deleteData(ids, user);
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
		Company company = JSONObject.toJavaObject(JSONObject.parseObject(StringFormat.encodeStr(formData)), Company.class);
		Company companyDb = (Company) baseService.getObject(Company.class, company.getCompanyId());
		User user = (User) session.getAttribute("user");
		JSONObject newdata = JSONObject.parseObject(formData);

		if (companyDb != null) {
			companyDb.setCompanyEmail(company.getCompanyEmail());
			companyDb.setCompanyPhone(company.getCompanyPhone());
			companyDb.setCompanyName(company.getCompanyName());
			companyDb.setEntityId(company.getEntityId());
			companyDb.setEntityName(company.getEntityName());
			// 修改ObJ表
			Obj obj = objService.getObjData(company.getCompanyId());
			obj.setObjTitle(company.getCompanyName());
			obj.setObjDesc(company.getCompanyName());
			obj.setKeyword(company.getCompanyName());
			if (company.getStatus() != null) {
				obj.setStatus(company.getStatus());
			}
			try {
				baseService.update(companyDb);
				objService.updateObj(obj);
				int keyId = Integer.parseInt(newdata.get("companyId").toString());
				String newchangeFields = "[" + changeFields + "]";// 把changeFields变成jsonArray形式
				JSONArray jsonArray = JSONArray.parseArray(newchangeFields);
				// 循环插入Log
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					ObjLog objLog = new ObjLog();
					objLog.setObjId(keyId);
					objLog.setUserId(user.getUserId());
					objLog.setDescription(jsonObject.get("description").toString());
					objLog.setModuleName("Company");
					objLog.setConfId(0);
					objLog.setFieldName(jsonObject.get("fieldName").toString());
					objLogService.createLog(objLog);
				}
				result = true;
				json.put("keyId", companyDb.getCompanyId());
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
	 * 
	 * @param companyId
	 */
	@RequestMapping("/getDetail")
	public void getDetail(@RequestParam("keyId") Integer companyId, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();

		List<Map<String, Object>> fieldList = commonService.getFilldByModule("company");

		Company company = null;
		String fields = "";
		for (int i = 0; i < fieldList.size(); i++) {
			fields += fieldList.get(i).get("field_label") + ",";
		}

		if (fields != null && !"".equals(fields)) {
			fields = fields.substring(0, fields.length() - 1);

			Map<String, Object> params = new HashMap<>();
			String sql = "select  " + fields + " from v_obj_company where companyId=:companyId";
			params.put("companyId", companyId);
			List<Object> list = baseService.getList(params, sql, Company.class);

			for (int k = 0; k < list.size(); k++) {
				company = (Company) list.get(k);
			}
		}

		json.put("data", company);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 获取全部公司名字的数据源
	 */
	@RequestMapping("/getDatasourse")
	public void getDatasourse(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		Map<String, Object> companyList = comPanyService.getAllCompanyName();

		json.put("recordsTotal", companyList.get("recordsTotal"));
		json.put("recordsFiltered", companyList.get("recordsFiltered"));
		json.put("data", companyList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

}
