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

import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.Invoice;
import com.embraiz.model.Module;
import com.embraiz.model.Obj;
import com.embraiz.model.ObjLog;
import com.embraiz.model.Payment;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.CommonService;
import com.embraiz.service.ConfService;
import com.embraiz.service.InvoiceService;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.ObjService;
import com.embraiz.service.PaymentService;

@Controller
@RequestMapping("/newpayment")
public class NewPaymentController {

	@Resource
	private PaymentService paymentService;
	@Resource
	private InvoiceService invoiceService;
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
			List<Map<String, Object>> fieldListInGrid = commonService.getGridFieldInModule("newpayment", lang);
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
							+ ") FROM ref_option ref WHERE FIND_IN_SET(ref.option_id,v_invoice." + multiselectFieldLabel + ")) AS "
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
			String sql = "SELECT DISTINCT t_invoice.*, obj_company.company_id, obj_company.company_name AS companyName FROM t_invoice  "
					+ " LEFT JOIN t_quotation ON t_invoice.invoice_id = t_quotation.invoice_id "
					+ " LEFT JOIN obj_company ON t_invoice.payer = obj_company.company_id "
					+ " WHERE t_invoice.amount != t_invoice.payment AND t_invoice.balance > 0 "
					+ " AND t_invoice. STATUS IN (3, 4, 5) GROUP BY t_invoice.invoice_id ";

			String sqlCount = "SELECT COUNT(*)  FROM t_invoice  "
					+ " LEFT JOIN t_quotation ON t_invoice.invoice_id = t_quotation.invoice_id "
					+ " LEFT JOIN obj_company ON t_invoice.payer = obj_company.company_id "
					+ " WHERE t_invoice.amount != t_invoice.payment AND t_invoice.balance > 0 "
					+ " AND t_invoice. STATUS IN (3, 4, 5) GROUP BY t_invoice.invoice_id ";

			// 搜索区域用到的栏位
			List<Map<String, Object>> fieldListInSearch = commonService.getSearchFieldInModule("newpayment", lang);

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
			json.put("searchDataList", commonService.getSearchFieldInModule("newpayment", lang));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 获得表单区域的栏位和数据
	 */
	@RequestMapping("/getFormData")
	public void getFormData(HttpServletResponse response, HttpSession session, @RequestParam("primaryId") Integer invoiceId) throws IOException {
		User user = (User) session.getAttribute("user");
		JSONObject json = new JSONObject();
		if (user != null) {
			String lang = (String) session.getAttribute("lang");
			if (lang == null) {
				lang = "en";
			}
			// 获得表单区域需要显示的栏位
			List<Map<String, Object>> fieldListInForm = commonService.getFormFieldInModule("newpayment", lang);
			json.put("fieldListInForm", fieldListInForm);
			// 存储拼接的字段
			String fieldStr = "";
			for (int i = 0; i < fieldListInForm.size(); i++) {
				fieldStr = fieldStr + fieldListInForm.get(i).get("fieldLabel").toString() + ",";
			}
			fieldStr = fieldStr.substring(0, fieldStr.length() - 1);
			String sql = "select " + fieldStr + " from v_invoice where invoice_id = :invoice_id";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("invoice_id", invoiceId);
			json.put("formData", baseService.getList(sql, params));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * PaymentSave
	 */
	@RequestMapping("/saveData")
	public void saveData(@RequestParam("data") String data, HttpSession session, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;
		int keyId = 0;
		int objkeyId = 0;
		User user = (User) session.getAttribute("user");

		Payment payment = JSONObject.toJavaObject(JSONObject.parseObject(data), Payment.class);
		String paymentNo = paymentService.paymentNo();// 生成一个No
		payment.setPaymentNo(paymentNo);
		// 插入Obj表
		Obj obj = new Obj();
		obj.setObjTitle(payment.getPaymentNo());
		obj.setObjDesc(payment.getPaymentNo());
		obj.setStatus(1);
		obj.setKeyword(payment.getPaymentNo());
		obj.setObjTypeId(18);
		obj.setObjParentId(17);
		obj.setOwnerId(user.getUserId());
		objkeyId = objService.insertObj(obj);

		// 返回Obj表的Id做为Invoice的Id
		payment.setPaymentId(objkeyId);
		// (判断payment的status)
		keyId = paymentService.insert(payment);

		if (keyId > 0) {
			result = true;
			try {
				// 添加insert记录
				ObjLog objLog = new ObjLog();
				objLog.setObjId(keyId);
				objLog.setUserId(user.getUserId());
				objLog.setDescription("Create Payment");
				objLog.setModuleName("Payment");
				objLog.setConfId(0);
				objLogService.createLog(objLog);

				String sql = "insert into t_obj_invoice_payment(invoice_id,payment_id,amount,is_void) values(" + payment.getInvoiceId() + ","
						+ objkeyId + "," + payment.getAmount() + ",0)";
				baseService.insertBySql(sql, null);
				
			
				Map<String, Object> params = new HashMap<>();
			     Invoice invoice = null;
			     List<Object> invoiceList = invoiceService.getinvoiceById(payment.getInvoiceId());
					for (int i = 0; i < invoiceList.size(); i++) {
						invoice = (Invoice) invoiceList.get(i);
					}

				if(invoice.getPayment()!=null){
					if(invoice.getAmount().compareTo(invoice.getPayment())==0){		
						//Full Payment receive
						String updateinvoiceStatus = "update t_invoice set status=5,payment=:payment,balance=:balance where invoice_id=:invoice_id";
						params.put("invoice_id", invoice.getInvoiceId());
						params.put("payment", payment.getAmount());
						params.put("balance", payment.getTotalAmount().subtract(payment.getAmount()).doubleValue());
						baseService.updateBySql(updateinvoiceStatus, params);
						
						String updateqStatus = "update t_quotation set status=12,payment=:payment,balance=:balance where quotation_id=:quotation_id";	
						params.put("quotation_id", invoice.getQuotationId());
						baseService.updateBySql(updateqStatus, params);	
						
					}else{
						
						//Partial Payment received
						String updateinvoiceStatus = "update t_invoice set status=4,payment=:payment,balance=:balance where invoice_id=:invoice_id";
						params.put("invoice_id", invoice.getInvoiceId());
						params.put("payment", payment.getAmount());
						params.put("balance", payment.getTotalAmount().subtract(payment.getAmount()).doubleValue());
						baseService.updateBySql(updateinvoiceStatus, params);
						
						String updateqStatus = "update t_quotation set status=9,payment=:payment,balance=:balance where quotation_id=:quotation_id";						
						params.put("quotation_id", invoice.getQuotationId());
						baseService.updateBySql(updateqStatus, params);	
					}
				}
				
				
				
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
	 * payment Method
	 */
	@RequestMapping("/getDatasourse")
	public void getDatasourse(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		Map<String, Object> paymentMethodList = paymentService.getDatasourse();

		json.put("recordsTotal", paymentMethodList.get("recordsTotal"));
		json.put("recordsFiltered", paymentMethodList.get("recordsFiltered"));
		json.put("data", paymentMethodList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

}
