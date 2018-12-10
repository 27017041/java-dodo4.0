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
import com.embraiz.model.Client;
import com.embraiz.model.Module;
import com.embraiz.model.Obj;
import com.embraiz.model.ObjLog;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.CommonService;
import com.embraiz.service.ConfService;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.ObjService;

@Controller
@RequestMapping("/client")
public class ClientController {

	@Resource
	private BaseService baseService;
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

		// core module信息
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

	/**
	 * 获取搜索区域的栏位
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

			json.put("searchDataList", commonService.getSearchFieldInModule("client", lang));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * 获取列表区域的栏位，并返回返回表格数据
	 * 
	 * @param request
	 * @param searchForm
	 * @param length
	 * @param start
	 * @param sortCol
	 * @param sortOrder
	 */
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
			List<Map<String, Object>> fieldListInGrid = commonService.getGridFieldInModule("client", lang);
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
							+ ") FROM ref_option ref WHERE FIND_IN_SET(ref.option_id,v_obj_client." + multiselectFieldLabel + ")) AS "
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

			String sql = "select " + fieldStr + " from v_obj_client where 1=1 ";
			String sqlCount = "select count(1) from v_obj_client where 1=1  ";

			// 搜索区域用到的栏位
			List<Map<String, Object>> fieldListInSearch = commonService.getSearchFieldInModule("client", lang);

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
	 * 获得表单区域的栏位和数据
	 * 
	 * @param response
	 * @param session
	 * @param userId
	 * @throws IOException
	 */
	@RequestMapping("/getFormData")
	public void getFormData(@RequestParam("primaryId") Integer clientId, HttpServletResponse response, HttpSession session) throws IOException {
		User user = (User) session.getAttribute("user");
		JSONObject json = new JSONObject();

		if (user != null) {
			String lang = (String) session.getAttribute("lang");
			if (lang == null) {
				lang = "en";
			}

			// 获得表单区域需要显示的栏位
			List<Map<String, Object>> fieldListInForm = commonService.getFormFieldInModule("client", lang);
			json.put("fieldListInForm", fieldListInForm);

			// 存储拼接的字段
			String fieldStr = "";

			for (int i = 0; i < fieldListInForm.size(); i++) {
				fieldStr = fieldStr + fieldListInForm.get(i).get("fieldLabel").toString() + ",";

			}
			fieldStr = fieldStr.substring(0, fieldStr.length() - 1);// 去除逗号
			String sql = "select " + fieldStr + " from v_obj_client where clientId = :clientId";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("clientId", clientId);

			json.put("formData", baseService.getList(sql, params));
		}

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 新增client
	 * 
	 * @param data
	 * @param response
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	@RequestMapping("/saveData")
	public void saveData(@RequestParam("formData") String data, HttpServletResponse response, HttpServletRequest request, HttpSession session)
			throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		JSONObject json = new JSONObject();
		boolean result = false;

		int keyId = 0;

		// 先注释by tonna User user =
		// User user = (User) session.getAttribute("user");
		// 先注释by tonna if(user!=null){
		User user = (User) session.getAttribute("user");

		int objTypeId = objService.searchObjType("client");

		Client client = JSONObject.toJavaObject(JSONObject.parseObject(data), Client.class);
		client.setStatus(1);

		Obj obj = new Obj();
		obj.setObjTypeId(objTypeId);
		obj.setObjTitle(client.getClientName());
		obj.setObjDesc(client.getClientName());
		String keyword = "";
		keyword = objService.getKeyword("client", client); // get keyword
		obj.setKeyword(keyword);
		obj.setObjParentId(3);
		obj.setOwnerId(user.getUserId());
		// by tonna 先注释obj.setObjParentId(user.getUserId());
		// by tonna 先注释obj.setOwnerId(user.getUserId());
		keyId = objService.insertObj(obj);

		if (keyId > 0) {
			client.setClientId(keyId);
			baseService.save(client);
			try {
				// 添加insert记录
				ObjLog objLog = new ObjLog();
				objLog.setObjId(keyId);
				objLog.setUserId(user.getUserId());
				objLog.setDescription("Create Client");
				objLog.setModuleName("Client");
				objLog.setConfId(0);
				objLogService.createLog(objLog);
			} catch (Exception e) {
			}
			result = true;
		} else {
			result = false;
		}

		// by tonna 先注释}

		json.put("result", result);

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 修改client
	 * 
	 * @param data
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/updateData")
	public void updateData(@RequestParam("formData") String data, @RequestParam(value = "changeFields") String changeFields,
			HttpServletResponse response, HttpSession session) throws IOException {

		JSONObject json = new JSONObject();
		boolean result = false;

		Client client = JSONObject.toJavaObject(JSONObject.parseObject(data), Client.class);

		Client clientDb = (Client) baseService.getObject(Client.class, client.getClientId());

		if (clientDb != null) {
			clientDb.setClientName(client.getClientName());
			clientDb.setMobilePhone(client.getMobilePhone());
			clientDb.setGeneralPhone(client.getGeneralPhone());
			clientDb.setFloorAddr(client.getFloorAddr());
			clientDb.setBlockAddr(client.getBlockAddr());
			clientDb.setBuildingAddr(client.getBuildingAddr());
			clientDb.setStreetAddr(client.getStreetAddr());
			clientDb.setDistrict(client.getDistrict());
			clientDb.setState(client.getState());
			clientDb.setEmail(client.getEmail());
			clientDb.setWebsite(client.getWebsite());
			clientDb.setFax(client.getFax());
			clientDb.setPostalCode(client.getPostalCode());
			clientDb.setBirthday(client.getBirthday());
			clientDb.setJob(client.getJob());
			clientDb.setSourceId(client.getSourceId());
			clientDb.setCompanyId(client.getCompanyId());
			clientDb.setCompanyName(client.getCompanyName());
			clientDb.setLinkageModule(client.getLinkageModule());
			

			Obj obj = objService.getObjData(client.getClientId());
			obj.setObjTitle(client.getClientName());
			obj.setObjDesc(client.getClientName());
			obj.setKeyword(client.getClientName());

			try {
				baseService.update(clientDb);
				objService.updateObj(obj);
				result = true;
				try {
					String newchangeFields = "[" + changeFields + "]";
					JSONArray jsonArray = JSONArray.parseArray(newchangeFields);// 转成jsonArray
					User user = (User) session.getAttribute("user");
					// 根据修改字段循环插入
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject jsonObject = (JSONObject) jsonArray.get(i);
						ObjLog objLog = new ObjLog();
						objLog.setObjId(client.getClientId());
						objLog.setUserId(user.getUserId());
						objLog.setDescription(jsonObject.get("description").toString());
						objLog.setModuleName("Client");
						objLog.setConfId(0);
						objLog.setFieldName(jsonObject.get("fieldName").toString());
						objLogService.createLog(objLog);
					}
				} catch (Exception e) {

				}

				json.put("keyId", clientDb.getClientId());
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
	 * 获取详情
	 */
	@RequestMapping("/getDetail")
	public void getDetail(@RequestParam("keyId") String clientId, HttpServletResponse response) throws IOException {

		JSONObject json = new JSONObject();

		List<Map<String, Object>> fieldList = commonService.getFilldByModule("client");

		Client client = null;
		String fields = "";
		for (int i = 0; i < fieldList.size(); i++) {
			fields += fieldList.get(i).get("field_label") + ",";
		}

		if (fields != null && !"".equals(fields)) {
			fields = fields.substring(0, fields.length() - 1);

			Map<String, Object> params = new HashMap<>();
			String sql = "select  " + fields + " from v_obj_client where clientId=:clientId";
			params.put("clientId", clientId);
			List<Object> list = baseService.getList(params, sql, Client.class);

			for (int k = 0; k < list.size(); k++) {
				client = (Client) list.get(k);
			}
		}

		json.put("data", client);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 删除client
	 * 
	 * @param clientId
	 * @param response
	 */
	@RequestMapping("/deleteData")
	public void deleteData(@RequestParam("keyIds") String clientIds, HttpServletResponse response, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;

		User user = (User) session.getAttribute("user");

		if (clientIds != null && clientIds.length() > 0) {
			String clientIdStr[] = clientIds.split(",");

			for (int i = 0; i < clientIdStr.length; i++) {
				Obj obj = objService.getObjData(Integer.parseInt(clientIdStr[i]));
				obj.setStatus(0);
				try {
					baseService.update(obj);

					try {
						ObjLog objlog = new ObjLog();
						objlog.setObjId(Integer.parseInt(clientIdStr[i]));
						objlog.setUserId(user.getUserId());
						objlog.setDescription("Delete Client");
						objlog.setModuleName("Client");
						objlog.setConfId(0);
						objLogService.createLog(objlog);

					} catch (Exception e) {
					}
					result = true;
				} catch (Exception e) {
					e.printStackTrace();
					result = false;
					break;
				}

			}
			String sql = "update obj_client set status=0  where client_id in(" + clientIds + ")";
			baseService.updateBySql(sql, null);
		}

		json.put("result", result);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * All Client
	 */
	@RequestMapping("/getClient")
	public void getClient(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		String sql = "select client_id,client_name from obj_client where status=1 ";
		String sqlCount = "select count(*) from obj_client where status=1 ";

		int listCount = baseService.getCountBySql(sqlCount.toString(), null);
		List<Object> list = baseService.getList(null, sql, Client.class);

		json.put("recordsTotal", listCount);
		json.put("recordsFiltered", listCount);
		json.put("data", list);

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

}
