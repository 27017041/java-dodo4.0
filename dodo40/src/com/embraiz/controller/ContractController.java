package com.embraiz.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.Contract;
import com.embraiz.model.ContractRenew;
import com.embraiz.model.Module;
import com.embraiz.model.Obj;
import com.embraiz.model.ObjLog;
import com.embraiz.model.Quotation;
import com.embraiz.model.Reminder;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.CommonService;
import com.embraiz.service.ConfService;
import com.embraiz.service.ContractService;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.ObjService;
import com.embraiz.util.SendMail;
import com.embraiz.util.StringFormat;
import com.embraiz.util.SynchronizingTask;

@Controller
@RequestMapping("/contract")
public class ContractController {

	@Resource
	private BaseService baseService;
	@Resource
	private ConfService confService;
	@Resource
	private CommonService commonService;
	@Resource
	private ObjService objService;
	@Resource
	private ObjLogService objLogService;
	@Resource
	private ContractService contractService;

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
			List<Map<String, Object>> fieldListInGrid = commonService.getGridFieldInModule("contract", lang);
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
							+ ") FROM ref_option ref WHERE FIND_IN_SET(ref.option_id,v_contract." + multiselectFieldLabel + ")) AS "
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
			String sql = "select " + fieldStr + " from v_contract where 1=1 ";
			String sqlCount = "select count(1) from v_contract where 1=1  ";

			// 搜索区域用到的栏位
			List<Map<String, Object>> fieldListInSearch = commonService.getSearchFieldInModule("contract", lang);

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
			json.put("searchDataList", commonService.getSearchFieldInModule("contract", lang));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 获得表单区域的栏位和数据
	 */
	@RequestMapping("/getFormData")
	public void getFormData(HttpServletResponse response, HttpSession session, @RequestParam("primaryId") Integer contractId) throws IOException {
		User user = (User) session.getAttribute("user");
		JSONObject json = new JSONObject();
		if (user != null) {
			String lang = (String) session.getAttribute("lang");
			if (lang == null) {
				lang = "en";
			}
			// 获得表单区域需要显示的栏位
			List<Map<String, Object>> fieldListInForm = commonService.getFormFieldInModule("contract", lang);
			json.put("fieldListInForm", fieldListInForm);
			// 存储拼接的字段
			String fieldStr = "";
			for (int i = 0; i < fieldListInForm.size(); i++) {
				fieldStr = fieldStr + fieldListInForm.get(i).get("fieldLabel").toString() + ",";
			}
			fieldStr = fieldStr.substring(0, fieldStr.length() - 1);
			String sql = "select " + fieldStr + " from v_contract where contractId = :contractId";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("contractId", contractId);
			json.put("formData", baseService.getList(sql, params));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * Insert
	 */
	@Transactional
	@RequestMapping("/saveData")
	public void saveData(@RequestParam("formData") String data, HttpServletResponse response, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;
		int keyId = 0;
		int objkeyId = 0;
		User user = (User) session.getAttribute("user");
		Contract contract = JSONObject.toJavaObject(JSONObject.parseObject(StringFormat.encodeStr(data)), Contract.class);
		String contractNo = contractService.contractNo();// 生成一个No
		contract.setContractNo(contractNo);
		// 插入Obj表
		Obj obj = new Obj();
		obj.setObjTitle(contract.getContractNo());
		obj.setObjDesc(contract.getContractNo());
		obj.setStatus(1);
		obj.setKeyword(contract.getContractNo());
		obj.setObjTypeId(19);
		obj.setOwnerId(user.getUserId());
		objkeyId = objService.insertObj(obj);
		// 返回Obj表的Id做为Contract的Id
		contract.setContractId(objkeyId);
		keyId = contractService.insert(contract, user);
		if (keyId > 0) {
			result = true;
			try {
				// 添加insert记录
				ObjLog objLog = new ObjLog();
				objLog.setObjId(keyId);
				objLog.setUserId(user.getUserId());
				objLog.setDescription("Create Contract");
				objLog.setModuleName("Contract");
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
	 */
	@RequestMapping("/deleteData")
	public void deleteData(HttpServletResponse response, HttpSession session, @RequestParam(value = "keyIds") String ids) throws IOException {
		JSONObject json = new JSONObject();
		User user = (User) session.getAttribute("user");
		int status = 0;
		String msg = "";
		if (ids != null) {
			int flag = contractService.deleteData(ids, user);
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
	 * Update
	 */
	@RequestMapping("/updateData")
	public void updateData(HttpServletResponse response, @RequestParam(value = "formData") String data,
			@RequestParam(value = "changeFields") String changeFields, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;
		Map<String, Object> params = new HashMap<>();

		Contract contract = JSONObject.toJavaObject(JSONObject.parseObject(StringFormat.encodeStr(data)), Contract.class);
		Contract contractDB = (Contract) baseService.getObject(Contract.class, contract.getContractId());
		User user = (User) session.getAttribute("user");
		JSONObject newdata = JSONObject.parseObject(data);

		if (contractDB != null) {
			contractDB.setClientId(contract.getClientId());
			contractDB.setQuotationId(contract.getQuotationId());
			contractDB.setTitle(contract.getTitle());
			contractDB.setContractType(contract.getContractType());
			contractDB.setStartDate(contract.getStartDate());
			contractDB.setEndDate(contract.getEndDate());
			contractDB.setCreateDate(contract.getCreateDate());
			contractDB.setStatus(contract.getStatus());
			contractDB.setRemark(contract.getRemark());
			contractDB.setReminderInterval(contract.getReminderInterval());
			contractDB.setReminderType(contract.getReminderType());

			// 如果修改了ReminderInterval Or ReminderType
			if (contract.getReminderInterval() != contractDB.getReminderInterval() || contract.getReminderType() != contractDB.getReminderType()) {

				String sql = "";
				// 清空旧的reminder
				sql = "update t_reminder set reminder_status=0,status=0 where module_id=:module_id ";
				params.put("module_id", contract.getContractId());

				// 创建新的reminder
				sql = "select reminder_id from t_reminder where module_id=:module_id ";
				Map<String, Object> reminderId = baseService.getObject(sql, params);

				Reminder reminderDB = (Reminder) baseService.getObject(Reminder.class, Integer.parseInt(reminderId.get("reminder_id").toString()));
				reminderDB.setReminderId(null);
				reminderDB.setReminderStatus(1);
				baseService.save(reminderDB);
				this.updateTimer(reminderDB);

			}

			// 修改ObJ表
			Obj obj = objService.getObjData(contractDB.getContractId());
			obj.setObjTitle(contractDB.getContractNo());
			obj.setObjDesc(contractDB.getContractNo());
			obj.setKeyword(contractDB.getContractNo());

			try {
				baseService.update(contractDB);
				objService.updateObj(obj);
				int keyId = Integer.parseInt(newdata.get("contractId").toString());
				String newchangeFields = "[" + changeFields + "]";// 把changeFields变成jsonArray形式
				JSONArray jsonArray = JSONArray.parseArray(newchangeFields);
				// 循环插入Log
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					ObjLog objLog = new ObjLog();
					objLog.setObjId(keyId);
					objLog.setUserId(user.getUserId());
					objLog.setDescription(jsonObject.get("description").toString());
					objLog.setModuleName("Contract");
					objLog.setConfId(0);
					objLog.setFieldName(jsonObject.get("fieldName").toString());
					objLogService.createLog(objLog);
				}
				result = true;
				json.put("keyId", contractDB.getContractId());
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
	public void getDetail(@RequestParam("keyId") Integer contractId, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		List<Map<String, Object>> fieldList = commonService.getFilldByModule("contract");
		Contract contract = null;
		String fields = "";
		for (int i = 0; i < fieldList.size(); i++) {
			fields += fieldList.get(i).get("field_label") + ",";
		}
		if (fields != null && !"".equals(fields)) {
			fields = fields.substring(0, fields.length() - 1);
			Map<String, Object> params = new HashMap<>();
			String sql = "select  " + fields + " from v_contract where contractId=:contractId";
			params.put("contractId", contractId);
			List<Object> list = baseService.getList(params, sql, Quotation.class);
			for (int k = 0; k < list.size(); k++) {
				contract = (Contract) list.get(k);
			}
		}
		json.put("data", contract);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	@RequestMapping("/updateReminder")
	public void updateReminder(@RequestParam(value = "data") String data, @RequestParam(value = "changeFields") String changeFields,
			HttpSession session, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;
		User user = (User) session.getAttribute("user");
		Reminder reminder = JSONObject.toJavaObject(JSONObject.parseObject(StringFormat.encodeStr(data)), Reminder.class);
		Reminder reminderDB = (Reminder) baseService.getObject(Reminder.class, reminder.getReminderId());
		if (reminderDB != null) {
			reminderDB.setEndDate(reminder.getEndDate());
			reminderDB.setReminderStatus(reminder.getReminderStatus());
			try {
				Map<String, Object> params = new HashMap<>();
				/**
				 * 如果修改了时间间隔或类型,取消定时,重新生成
				 */
				if (reminder.getReminderInterval() != reminderDB.getReminderInterval() || reminder.getReminderType() != reminderDB.getReminderType()) {

					reminderDB.setReminderInterval(reminder.getReminderInterval());
					reminderDB.setReminderType(reminder.getReminderType());

					String sql = " update t_reminder LEFT JOIN t_contract ON t_reminder.module_id=t_contract.contract_id "
							+ " set t_reminder.status = 0, t_reminder.reminder_status = 0," + " t_contract.reminder_interval=:reminder_interval, "
							+ " t_reminder.reminder_interval=:reminder_interval," + " t_contract.reminder_type=:reminder_type,"
							+ " t_reminder.reminder_type=:reminder_type, " + " t_contract.end_date=:end_date," + " t_reminder.end_date=:end_date "
							+ " where reminder_id =:reminder_id";

					params.put("reminder_interval", reminderDB.getReminderInterval() == null ? "" : reminderDB.getReminderInterval());
					params.put("reminder_type", reminderDB.getReminderType() == null ? "" : reminderDB.getReminderType());
					params.put("end_date", StringFormat.formatData(reminderDB.getEndDate()));
					params.put("reminder_id", reminderDB.getReminderId());
					int keyId = (Integer) baseService.updateBySql(sql, params);

					// 清空Id设置定时状态为1
					reminderDB.setReminderId(null);
					reminderDB.setReminderStatus(1);
					baseService.save(reminderDB);

					if (keyId > 0) {
						this.updateTimer(reminderDB);
					}
				} else {
					String sql = " update t_reminder LEFT JOIN t_contract ON t_reminder.module_id=t_contract.contract_id "
							+ " set t_contract.end_date=:end_date,t_reminder.end_date=:end_date " + " where reminder_id =:reminder_id";
					params.put("end_date", reminderDB.getEndDate());
					params.put("reminder_id", reminderDB.getReminderId());
					baseService.updateBySql(sql, params);
				}

				String newchangeFields = "[" + changeFields + "]";// 把changeFields变成jsonArray形式
				JSONArray jsonArray = JSONArray.parseArray(newchangeFields);
				// 循环插入Log
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(i);
					ObjLog objLog = new ObjLog();
					objLog.setObjId(reminderDB.getModuleId());
					objLog.setUserId(user.getUserId());
					objLog.setDescription(jsonObject.get("description").toString());
					objLog.setModuleName("Reminder");
					objLog.setConfId(0);
					objLog.setFieldName(jsonObject.get("fieldName").toString());
					objLogService.createLog(objLog);
				}
				result = true;
				json.put("keyId", reminderDB.getReminderId());
			} catch (Exception e) {
			}
		}
		json.put("result", result);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * 获取Contract status
	 */
	@RequestMapping("/getstatus")
	public void getstatus(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		Map<String, Object> statusList = contractService.getAllstatus();

		json.put("recordsTotal", statusList.get("recordsTotal"));
		json.put("recordsFiltered", statusList.get("recordsFiltered"));
		json.put("data", statusList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 获取Contract Type
	 */
	@RequestMapping("/getType")
	public void getType(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		Map<String, Object> statusList = contractService.getAllType();

		json.put("recordsTotal", statusList.get("recordsTotal"));
		json.put("recordsFiltered", statusList.get("recordsFiltered"));
		json.put("data", statusList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 获取选择公司后的Quotation DataSourse
	 */
	@RequestMapping("/getQuotationByCompanyId")
	public void getQuotationByCompanyId(@RequestParam(value = "keyId") int companyId, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		Map<String, Object> quotationList = contractService.getQuotationByCompanyId(companyId);

		json.put("recordsTotal", quotationList.get("recordsTotal"));
		json.put("recordsFiltered", quotationList.get("recordsFiltered"));
		json.put("data", quotationList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * Create Renew
	 */
	@RequestMapping("/saveRenew")
	public void saveRenew(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;
		int keyId = 0;
		int no = 0;
		Contract contract = JSONObject.toJavaObject(JSONObject.parseObject(StringFormat.encodeStr(data)), Contract.class);

		ContractRenew contractRenew = new ContractRenew();
		contractRenew.setContractId(contract.getContractId());
		contractRenew.setClientId(contract.getClientId());
		contractRenew.setQuotationId(contract.getQuotationId());
		contractRenew.setEndDate(contract.getEndDate());
		contractRenew.setRemark(contract.getRemark());
		String lastno = "0";
		Map<String, Object> resultNo = contractService.getlastNo(contract);
		if (resultNo != null) {
			if (resultNo.get("no") != null && !"".equals(resultNo.get("no"))) {
				lastno = resultNo.get("no").toString();
			}
		}
		no = Integer.parseInt(lastno) + 1;

		contractRenew.setNo(no);
		keyId = contractService.saveRenew(contractRenew);
		if (keyId > 0) {
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
	 * Delete Renew
	 */
	@RequestMapping("/deleteRenew")
	public void deleteRenew(@RequestParam("keyIds") String ids, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		int status = 0;
		String msg = "";

		int flag = contractService.deleteRenew(ids);
		if (flag > 0) {
			msg = "Delete Success";
		} else {
			msg = "Delete Fail";
		}

		json.put("status", status);
		json.put("msg", msg);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	// 修改定时发送
	public void updateTimer(Reminder reminderDB) {
		final Reminder re1 = reminderDB;

		final Timer timer = new Timer();
		SynchronizingTask task = new SynchronizingTask(re1) {

			@Override
			public void run() {
				String sql = "select * from t_reminder where module_id =:module_id and status=1 ";
				Map<String, Object> params = new HashMap<>();
				params.put("module_id", re1.getModuleId());
				Map<String, Object> result = baseService.getObject(sql, params);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar calBegin = Calendar.getInstance();
				Date nowDate = calBegin.getTime();
				String reminderDate = sdf.format(result.get("end_date"));

				boolean flag = false;
				try {
					if (nowDate.before(sdf.parse(reminderDate))) {
						flag = true;
					} else {
						flag = false;
						// 过期修改合同status
						String update = " update t_contract SET status=3 WHERE contract_id=:contract_id ";
						params.put("contract_id", re1.getModuleId());
						baseService.updateBySql(update, params);
					}
					if (result.get("reminder_status").toString().equals("0") || flag == false) {
						timer.cancel();
					} else {
						SendMail sendMail = new SendMail();
						sendMail.sendMail(result.get("email").toString(), result.get("subject").toString(), result.get("reminder_content").toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		if (reminderDB.getReminderType() == 1) {
			timer.schedule(task, 0, reminderDB.getReminderInterval() * 24 * 60 * 60 * 1000);// Days
		} else if (reminderDB.getReminderType() == 2) {
			timer.schedule(task, 0, reminderDB.getReminderInterval() * 7 * 24 * 60 * 60 * 1000);// Week
		} else if (reminderDB.getReminderType() == 3) {
			timer.schedule(task, 0, reminderDB.getReminderInterval() * 30 * 24 * 60 * 60 * 1000);// month
		} else if (reminderDB.getReminderType() == 4) {
			timer.schedule(task, 0, reminderDB.getReminderInterval() * 4 * 30 * 24 * 60 * 60 * 1000);// Season
		} else if (reminderDB.getReminderType() == 5) {
			timer.schedule(task, 0, reminderDB.getReminderInterval() * 12 * 30 * 24 * 60 * 60 * 1000);// Year
		}
	}

}
