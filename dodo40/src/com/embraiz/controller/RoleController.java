package com.embraiz.controller;

import java.io.IOException;
import java.util.ArrayList;
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
import com.embraiz.helper.SystemHelper;
import com.embraiz.model.Conf;
import com.embraiz.model.Obj;
import com.embraiz.model.ObjLog;
import com.embraiz.model.Page;
import com.embraiz.model.Role;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.CommonService;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.ObjService;
import com.embraiz.service.RoleService;
import com.embraiz.service.SystemUtilService;
import com.embraiz.util.StringFormat;

@Controller
@RequestMapping("/role")
public class RoleController {
	@Resource
	private RoleService roleService;
	@Resource
	private ObjService objSerivce;
	@Resource
	private SystemUtilService systemUtilSerivce;
	@Resource
	private SystemHelper systemHelper;
	@Resource
	private ObjLogService objLogService;
	@Resource
	private BaseService baseService;
	@Resource
	private CommonService commonService;

	/**
	 * 根据user所属的entity 获取这个entity下role的下拉数据
	 * 
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/roleSelectData")
	public void getRoleSelectData(HttpServletResponse response, HttpSession session) throws IOException {
		User user = (User) session.getAttribute("user");
		JSONArray json = new JSONArray();
		if (user != null) {
			Obj obj = objSerivce.getObjData(user.getUserId());
			if (obj != null) {
				List<Role> list = new ArrayList<Role>();
				if (obj.getObjParentId() == 0) {// 不隶属任何entity的superadmin
					list = roleService.getAllRole();
				} else {
					list = roleService.getRoleByEntity(obj.getL0());
				}
				json.add(list);
			}
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 查询表格数据
	 * 
	 * @param response
	 * @param request
	 * @param model
	 * @param searchForm
	 * @param confId
	 * @param length
	 * @param start
	 * @param sortCol
	 * @param sortOrder
	 * @throws Exception
	 */
	@RequestMapping("/gridData")
	public void gridData(HttpServletResponse response,
			HttpServletRequest request,
			HttpSession session,
			// @RequestParam(value="model") String model,
			@RequestParam(value = "searchForm") String searchForm,
			// @RequestParam(value="confId") int confId,
			@RequestParam(value = "length") int length, @RequestParam(value = "start") int start,
			@RequestParam(value = "order[0][column]", defaultValue = "1") String sortCol,
			@RequestParam(value = "order[0][dir]", defaultValue = "asc") String sortOrder) throws Exception {
		try {
			User user = (User) session.getAttribute("user");
			String lang = (String) session.getAttribute("lang");
			if (lang == null) {
				lang = "en";
			}

			/*
			 * int userId = 0; if(user!=null){ userId = user.getUserId(); }
			 * 
			 * Class<?> module = Class.forName("com.embraiz.model."+ model);
			 * Object o = module.newInstance();
			 */

			// 获取排序字段
			String sortBy = "";
			if (request.getParameter("columns[" + sortCol + "][name]") != null && !request.getParameter("columns[" + sortCol + "][name]").equals("")) {
				sortBy = request.getParameter("columns[" + sortCol + "][name]");
			}

			Page pageBo = new Page();
			pageBo.setCount(length);
			pageBo.setStart(start);

			JSONObject json = new JSONObject();

			// 限定entity
			Obj obj = objSerivce.getObjData(user.getUserId());
			String sqlWhere = " and entity_id=";
			if (obj != null) {
				if (obj.getObjParentId() == 0) {// 不隶属任何entity的superadmin
					sqlWhere = "";
				} else {// 指定entity
					sqlWhere = sqlWhere + obj.getL0();
				}
			} else {
				sqlWhere = sqlWhere + "0";
			}

			// Map<String, Object> map = systemUtilSerivce.getList(searchForm,
			// pageBo, sortBy, sortOrder, o,userId, confId,sqlWhere,lang);
			Map<String, Object> map = roleService.getList(searchForm, pageBo, sortBy, sortOrder, sqlWhere, lang);

			json.put("recordsTotal", map.get("recordsTotal"));
			json.put("recordsFiltered", map.get("recordsFiltered"));
			json.put("data", map.get("data"));

			System.out.println(json);
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(json);
			response.getWriter().flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * insert或update数据
	 * 
	 * @param response
	 * @param request
	 * @param formData
	 * @param confId
	 * @throws IOException
	 */
	@RequestMapping("/updateData")
	public void updateData(HttpServletResponse response, HttpSession session, @RequestParam(value = "formData") String formData,
			@RequestParam(value = "changeFields") String changeFields
	// @RequestParam(value="confId") int confId
	) throws IOException {
		JSONObject json = new JSONObject();
		int status = 0;
		int keyId = 0;
		String msg = "";
		User user = (User) session.getAttribute("user");
		if (user != null) {
			// Conf conf = systemUtilSerivce.getConfData(confId);
			if (formData != null && !formData.equals("")) {
				formData = StringFormat.encodeStr(formData);// 解决中文乱码
				changeFields = changeFields;

				JSONObject data = JSONObject.parseObject(formData);
				String newchangeFields = "[" + changeFields + "]";// 把changeFields变成jsonArray形式
				JSONArray jsonArray = JSONArray.parseArray(newchangeFields);

				if (data.get("roleId") == null || data.get("roleId") == "0") {
					Obj obj = objSerivce.getObjData(user.getUserId());
					formData = StringFormat.multSelectValueFormat(formData); // 多选框去除“[]”
					Role role = JSONObject.toJavaObject(JSONObject.parseObject(formData), Role.class);
					role.setEntityId(obj.getL0());// 获取entity
					keyId = roleService.insert(role);
					if (keyId > 0) {
						status = 1;
						msg = "succAdd";
						try {
							// 新增添加到记录表
							ObjLog objLog = new ObjLog();
							objLog.setObjId(keyId);
							objLog.setUserId(user.getUserId());
							objLog.setDescription("Create Role");
							objLog.setModuleName("Role");
							objLog.setConfId(0);
							objLogService.createLog(objLog);
						} catch (Exception e) {
						}
					} else if (keyId < 0) {// 在conf中配置obj的信息有误
						status = 0;
						msg = "confErr";
					} else {
						status = 0;
						msg = "failAdd";
					}
				} else {// update
					keyId = Integer.parseInt(data.get("roleId").toString());
					// status = systemUtilSerivce.updateData(conf, data,
					// keyId,user.getUserId(),changeFields);

					Role role = new Role();
					role.setRoleId(keyId);
					role.setRoleName(data.get("roleName").toString());
					role.setStatus(Integer.parseInt(data.get("status").toString()));

					status = roleService.update(role);

					if (status > 0 && !changeFields.equals("")) {
						// 循环插入Log
						for (int i = 0; i < jsonArray.size(); i++) {
							JSONObject jsonObject = (JSONObject) jsonArray.get(i);
							ObjLog objLog = new ObjLog();
							objLog.setObjId(keyId);
							objLog.setUserId(user.getUserId());
							objLog.setDescription(jsonObject.get("description").toString());
							objLog.setModuleName("Role");
							objLog.setConfId(0);
							objLog.setFieldName(jsonObject.get("fieldName").toString());
							objLogService.createLog(objLog);
						}

					}

					if (status == 1) {
						msg = "succUpdate";
					} else {
						msg = "failUpdate";
					}
				}
			} else {
				msg = "dataErr";
			}
		} else {
			msg = "userErr";
		}

		json.put("status", status);
		json.put("msg", msg);
		json.put("keyId", keyId);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 删除数据
	 * 
	 * @param response
	 * @param request
	 * @param ids
	 * @param confId
	 * @throws IOException
	 */
	@RequestMapping("/deleteData")
	public void deleteData(HttpServletResponse response, HttpServletRequest request, HttpSession session, @RequestParam(value = "ids") String ids)
			throws IOException {
		JSONObject json = new JSONObject();
		int status = 0;
		String msg = "";

		// Conf conf = systemUtilSerivce.getConfData(confId);

		User user = (User) session.getAttribute("user");

		if (!ids.equals("")) {
			status = roleService.delete(ids, user);
		}

		if (status > 0) {
			msg = "succDel";
		} else if (status < 0) {
			msg = "failDel";
		} else {
			msg = "failDel";
		}
		json.put("status", status);
		json.put("msg", msg);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 获取详情
	 * 
	 * @param response
	 * @param request
	 * @param roleId
	 * @throws IOException
	 */
	@RequestMapping("/getRoleData")
	public void getRoleData(HttpServletResponse response, HttpServletRequest request, @RequestParam("roleId") Integer roleId) throws IOException {
		User user = (User) request.getSession().getAttribute("user");

		JSONObject json = new JSONObject();

		if (user != null) {
			json.put("formData", baseService.getObject(Role.class, roleId));
			json.put("formOldData", baseService.getObject(Role.class, roleId));

			String lang = (String) request.getAttribute("lang");
			if (lang == null) {
				lang = "en";
			}
			json.put("formSelectStore", commonService.getSelectDataInModule(1, lang));
		}

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

}
