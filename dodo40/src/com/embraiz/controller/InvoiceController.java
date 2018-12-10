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
import com.embraiz.dataSource.core.InvoiceDs;
import com.embraiz.dataSource.core.InvoiceDs2;
import com.embraiz.model.Invoice;
import com.embraiz.model.Module;
import com.embraiz.model.Obj;
import com.embraiz.model.ObjLog;
import com.embraiz.model.Page;
import com.embraiz.model.Quotation;
import com.embraiz.model.QuotationItem;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.CommonService;
import com.embraiz.service.ConfService;
import com.embraiz.service.InvoiceService;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.ObjService;
import com.embraiz.service.WorkflowService;
import com.embraiz.util.ExcelExport;
import com.embraiz.util.StringFormat;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {

	@Resource
	private InvoiceService invoiceService;
	@Resource
	private ObjService objService;
	@Resource
	private ObjLogService objLogService;
	@Resource
	private BaseService baseService;
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
			List<Map<String, Object>> fieldListInGrid = commonService.getGridFieldInModule("invoice", lang);
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
			String sql = "select " + fieldStr + " from v_invoice where 1=1 ";
			String sqlCount = "select count(1) from v_invoice where 1=1  ";

			// 搜索区域用到的栏位
			List<Map<String, Object>> fieldListInSearch = commonService.getSearchFieldInModule("invoice", lang);

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
			json.put("searchDataList", commonService.getSearchFieldInModule("invoice", lang));
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
			List<Map<String, Object>> fieldListInForm = commonService.getFormFieldInModule("invoice", lang);
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
	 * createInovice1(在invoice版面create invoice)
	 * 
	 */
	@RequestMapping("/createInovice")
	public void createInovice(HttpServletResponse response, @RequestParam("keyId") int quotationId) throws IOException {
		JSONObject json = new JSONObject();
		Quotation quotation = null;
		List<Object> quotationList = invoiceService.createInovice(quotationId);
		for (int i = 0; i < quotationList.size(); i++) {
			quotation = (Quotation) quotationList.get(i);
		}
		BigDecimal totalCost = null;
		Map<String, Object> params = new HashMap<>();
		String sql = "select sum(sub_total) as sub_total from v_quotation_item where  optional=0 and parent_id=:parent_id";
		params.put("parent_id", quotationId);
		Map<String, Object> sub_total = baseService.getObject(sql, params);

		if (sub_total.get("sub_total") != null && quotation.getDiscount() != null) {
			totalCost = new BigDecimal(sub_total.get("sub_total").toString()).setScale(2);
			totalCost = totalCost.subtract(new BigDecimal(quotation.getDiscount().toString()));
		} else {
			totalCost = new BigDecimal(sub_total.get("sub_total").toString()).setScale(2);
		}

		json.put("data", quotationList);
		json.put("totalCost", totalCost);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * createInovice2(在invoice版面create invoice) 按了save之后 传公司Id和totalCost
	 */
	@RequestMapping("/saveData")
	public void saveData(@RequestParam("formData") String data,@RequestParam("keyId") String quotationId,  HttpSession session, HttpServletResponse response) throws IOException {

		Invoice invoice = JSONObject.toJavaObject(JSONObject.parseObject(data), Invoice.class);
		JSONObject json = new JSONObject();
		boolean result = false;
		int keyId = 0;
		int objkeyId = 0;
		User user = (User) session.getAttribute("user");

		String invoiceNo = invoiceService.invoiceNo();// 生成一个No
		invoice.setInvoiceNo(invoiceNo);
		// 插入Obj表
		Obj obj = new Obj();
		obj.setObjTitle(invoice.getInvoiceNo());
		obj.setObjDesc(invoice.getInvoiceNo());
		obj.setStatus(1);
		obj.setKeyword(invoice.getInvoiceNo());
		obj.setObjTypeId(17);
		obj.setObjParentId(16);
		obj.setOwnerId(user.getUserId());
		objkeyId = objService.insertObj(obj);

		// 返回Obj表的Id做为Invoice的Id
		invoice.setInvoiceId(objkeyId);
		invoice.setPayer(invoice.getPayer());
		invoice.setAmount(invoice.getAmount());
		invoice.setPayment(invoice.getPayment());
		invoice.setBalance(invoice.getBalance());
		invoice.setConsoleAmount(invoice.getConsoleAmount());
		invoice.setCnoteNo(0);
		invoice.setStatus(1);
		invoice.setIsApproval(0);
		keyId = invoiceService.insert(invoice);

		if (keyId > 0) {
			result = true;
			try {
				// 添加insert记录
				ObjLog objLog = new ObjLog();
				objLog.setObjId(keyId);
				objLog.setUserId(user.getUserId());
				objLog.setDescription("Create Invoice");
				objLog.setModuleName("Invoice");
				objLog.setConfId(0);
				objLogService.createLog(objLog);

				String sql = "insert into t_obj_quotation_invoice(quotation_id,invoice_id,payment) values(" + quotationId + ","
						+ objkeyId + "," + invoice.getAmount() + ")";
				baseService.insertBySql(sql, null);

				Map<String, Object> params = new HashMap<>();
				String updateStatus = "update t_quotation set status=8,invoice_id=:invoice_id where quotation_id=:quotation_id";
				params.put("quotation_id", invoice.getQuotationId());
				params.put("invoice_id", invoice.getInvoiceId());
				baseService.updateBySql(updateStatus, params);

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
	 * Void Invoice
	 */
	@RequestMapping("/updatevoidInvoice")
	public void updatevoidInvoice(HttpSession session, HttpServletResponse response, @RequestParam(value = "id") String id,
			@RequestParam(value = "voidRemark") String voidRemark) throws IOException {
		JSONObject json = new JSONObject();
		User user = (User) session.getAttribute("user");
		int status = 0;
		String msg = "";
		if (id != null) {
			int flag = invoiceService.voidInvoice(id, user, voidRemark);
			if (flag > 0) {
				status = 1;
				msg = "Void Success";
			} else {
				msg = "Void Fail";
			}
		}
		json.put("status", status);
		json.put("msg", msg);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 
	 */
	@RequestMapping("/getInvoice")
	public void getInvoice(HttpServletResponse response, HttpServletRequest request, HttpSession session,
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

		Map<String, Object> invoiceList = invoiceService.selectInvoice(searchForm, sortBy, sortOrder, pageBo);
		JSONObject json = new JSONObject();
		json.put("recordsTotal", invoiceList.get("recordsTotal"));
		json.put("recordsFiltered", invoiceList.get("recordsFiltered"));
		json.put("data", invoiceList.get("data"));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	/**
	 * Delete
	 * 
	 * @param ids
	 */
	@RequestMapping("/deleteData")
	public void deleteDataAll(HttpServletResponse response, HttpSession session, @RequestParam(value = "keyIds") String ids) throws IOException {
		JSONObject json = new JSONObject();
		User user = (User) session.getAttribute("user");
		int status = 0;
		String msg = "";
		if (ids != null) {
			int flag = invoiceService.deleteData(ids, user);
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
	 * Update Invoice @param data @param changeFields
	 */
	@RequestMapping("/updateData")
	public void updateData(HttpServletResponse response, @RequestParam(value = "formData") String data,
			@RequestParam(value = "changeFields") String changeFields, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		boolean result = false;
		Invoice invoice = JSONObject.toJavaObject(JSONObject.parseObject(StringFormat.encodeStr(data)), Invoice.class);
		Invoice invoiceDb = (Invoice) baseService.getObject(Invoice.class, invoice.getInvoiceId());
		User user = (User) session.getAttribute("user");
		JSONObject newdata = JSONObject.parseObject(data);

		if (invoiceDb != null) {
			invoiceDb.setCreateDate(invoice.getCreateDate());
			invoiceDb.setVoidRemark(invoice.getVoidRemark());
			try {
				baseService.update(invoiceDb);
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
					objLog.setModuleName("Invoice");
					objLog.setConfId(0);
					objLog.setFieldName(jsonObject.get("fieldName").toString());
					objLogService.createLog(objLog);
				}
				result = true;
				json.put("keyId", invoiceDb.getInvoiceId());
			} catch (Exception e) {
			}
		}
		json.put("result", result);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	@RequestMapping("/invoiceSubmit")
	public void invoiceSubmit(@RequestParam("obj_id") String obj_id, HttpSession session, HttpServletResponse response) throws IOException {
		User user = (User) session.getAttribute("user");
		int keyId = workflowService.createInvoiceJob(obj_id, user);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/printPDF")
	public void printPDF(@RequestParam("obj_id") int obj_id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);
		DecimalFormat numFormat = new DecimalFormat("###,###,##0.00");
		Invoice invoice = null;
		Invoice[] data = null;
		List<QuotationItem> quotationItems = new ArrayList<QuotationItem>();

		List<Object> invoiceList = invoiceService.getinvoiceById(obj_id);
		for (int i = 0; i < invoiceList.size(); i++) {
			invoice = (Invoice) invoiceList.get(i);

		}
		StringBuffer quoationsNo = new StringBuffer();
		try {
			List<Quotation> quotationList = invoiceService.getByInvoicId(obj_id);
			BigDecimal originalTotalHKD = new BigDecimal(0);
			BigDecimal totalHKD = new BigDecimal(0);
			BigDecimal totalDiscount = new BigDecimal(0);

			BigDecimal totalPayment = new BigDecimal(0.00);
			BigDecimal totalCost = new BigDecimal(0.00);

			if (quotationList != null && quotationList.size() > 0) {
				List<QuotationItem> tempItems = null;
				for (int i = 0; i < quotationList.size(); i++) {
					BigDecimal tempOriginal = null;
					BigDecimal tempSubTotal = null;
					BigDecimal tempDiscount = null;
					tempOriginal = invoiceService.getOriginalTotalByQuotationId(quotationList.get(i).getQuotationId());
					originalTotalHKD = originalTotalHKD.add(tempOriginal == null ? new BigDecimal(0.00) : tempOriginal);

					tempDiscount = invoiceService.getDiscountByQuotationId(quotationList.get(i).getQuotationId());
					if (tempDiscount == null) {
						tempDiscount = new BigDecimal(0.00);
					}

					tempDiscount = tempDiscount.add(quotationList.get(i).getDiscount() == null ? new BigDecimal(0.00) : quotationList.get(i)
							.getDiscount());
					totalDiscount = totalDiscount.add(tempDiscount);

					if (tempOriginal != null && tempDiscount != null) {
						tempSubTotal = tempOriginal.subtract(tempDiscount);
					}
					totalHKD = totalHKD.add(tempSubTotal == null ? new BigDecimal(0.00) : tempSubTotal);

					quoationsNo = quoationsNo.append(quotationList.get(i).getQuotationNo() + " ");// 所有的quotationNo
					tempItems = invoiceService.getQuotationItemsByParentId(quotationList.get(i).getQuotationId() + "");
					quotationList.get(i).setTotalCost(invoiceService.getSubTotalByQuotationId(quotationList.get(i).getQuotationId()));
					quotationList.get(i).setPayment(invoiceService.getPayment(obj_id, quotationList.get(i).getQuotationId() + ""));

					if (quotationList.get(i).getPayment() != null && quotationList.get(i).getTotalCost() != null) {
						BigDecimal percent = (quotationList.get(i).getPayment()).divide(quotationList.get(i).getTotalCost(), 4,
								BigDecimal.ROUND_HALF_UP).multiply((new BigDecimal(100)));
						quotationList.get(i).setPaymentPercent(percent.setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
					}
					if (tempItems != null)
						quotationItems.addAll(tempItems);
					if (quotationList.get(i).getTotalCost() != null) {
						if (quotationList.get(i).getDiscount() != null) {
							quotationList.get(i).setTotalCost(quotationList.get(i).getTotalCost().subtract(quotationList.get(i).getDiscount()));
						}
					}
					totalCost = totalCost.add(quotationList.get(i).getTotalCost());
					if (quotationList.get(i).getPayment() != null)
						totalPayment = totalPayment.add(quotationList.get(i).getPayment());
				}
			}

			if (invoice != null) {
				data = new Invoice[1];
				data[0] = invoice;
			}

			/* prepare pdf data */
			HashMap hm = new HashMap();// 转载参数的map
			String root_path = System.getProperty("evan.webapp");

			// begin
			String embraiz_logo = root_path + "images/pdf/embraiz_logo.jpg";
			String report = root_path + "images/pdf/sig_new.jpg";
			String watermark = root_path + "images/pdf/watermark.jpg";
			hm.put("embraiz_logo", embraiz_logo);
			hm.put("report", report);
			hm.put("invoiceId", (invoice.getInvoiceNo() != null ? invoice.getInvoiceNo() : invoice.getInvoiceId()));
			hm.put("currentDate", "Date:" + dateFormat2.format(new Date()));
			hm.put("originalTotalHKD", originalTotalHKD.setScale(2));
			hm.put("totalDiscount", totalDiscount.setScale(2));
			if (quotationList != null && quotationList.size() > 0) {
				String companyName = quotationList.get(0).getClientName();
				hm.put("enFlag", 1);
				if (companyName == null || "".equals(companyName.trim())) {
					companyName = "";
					hm.put("enFlag", 0);
				}
				hm.put("contactName", quotationList.get(0).getContactName());
				hm.put("companyName", companyName);
				hm.put("prepareBy", quotationList.get(0).getPrepareBy());
				hm.put("companyEmail", quotationList.get(0).getContactEmail());
			}
			String percentStr = "";
			if (totalCost.doubleValue() != 0) {
				BigDecimal percent = (totalPayment.divide(totalCost, 4, BigDecimal.ROUND_HALF_UP)).multiply((new BigDecimal(100)));
				percentStr = percent.setScale(2, BigDecimal.ROUND_HALF_UP) + "%";
			}
			String amountPercentStr = percentStr + " of the total payment: " + numFormat.format(totalPayment);
			hm.put("amountPercentStr", amountPercentStr);

			String currencyName = "";
			if (quotationList != null && quotationList.size() > 0) {
				for (int i = 0; i < quotationList.size(); i++) {
					if (quotationList.get(i).getCurrencyName() != null && !"".equals(quotationList.get(i).getCurrencyName())) {
						currencyName = quotationList.get(i).getCurrencyName();
						break;
					}
				}
			}

			hm.put("totalHKD", "Total Cost: " + currencyName + " " + numFormat.format(totalCost) + "  Total Payment: " + currencyName + " "
					+ numFormat.format(totalPayment));
			hm.put("totalHKD2", currencyName + " " + numFormat.format(totalCost));
			// 获取quotation的createDate
			if (invoice.getCreateDate() != null && !"".equals(invoice.getCreateDate())) {
				hm.put("createDate", dateFormat2.format(invoice.getCreateDate()));
			} else
				hm.put("createDate", "");

			if (invoice.getStatus() != null && invoice.getStatus() != 3 && invoice.getStatus() != 4 && invoice.getStatus() != 5)
				hm.put("watermark", watermark);

			if (invoice.getStatus() != null && invoice.getStatus() == 18)
				hm.put("watermark", root_path + "images/pdf/watermark2.png");

			/* end */
			String formNo = "Invoice" + invoice.getInvoiceId() + "_" + (dateFormat.format(new Date()).trim());
			String fname = formNo + ".pdf";

			response.setContentType("application/pdf");

			String fileName = new String(fname.getBytes("UTF-8"), "ISO-8859-1");
			fileName = "\"" + fileName + "\"";

			response.setHeader("Content-disposition", "attachment; filename=" + fileName);
			ServletOutputStream ouputStream = response.getOutputStream();

			// 等待写入的文件流
			JRPdfExporter exporter = new JRPdfExporter();

			String report1 = "";
			JasperPrint jasperPrint1 = null;
			if (quotationList != null && quotationList.size() > 1) {
				// more than one quotation to one invoice
				InvoiceDs ids = new InvoiceDs(data, quotationList);
				InvoiceDs ids2 = new InvoiceDs(data, quotationList);
				report1 = root_path + "WEB-INF/quotationTemplet/invoice_new.jasper";
				jasperPrint1 = JasperFillManager.fillReport(report1, hm, ids);
				hm.put("totalPages", jasperPrint1.getPages().size());
				jasperPrint1 = JasperFillManager.fillReport(report1, hm, ids2);
			} else {
				// one quotation to one invoice
				hm.put("quotationTitle", quotationList.get(0).getTitle());
				InvoiceDs2 ids_2 = new InvoiceDs2(data, quotationItems);//
				InvoiceDs2 ids2_2 = new InvoiceDs2(data, quotationItems);
				report1 = root_path + "WEB-INF/quotationTemplet/invoice_new2.jasper";
				jasperPrint1 = JasperFillManager.fillReport(report1, hm, ids_2);
				hm.put("totalPages", jasperPrint1.getPages().size());
				jasperPrint1 = JasperFillManager.fillReport(report1, hm, ids2_2);
			}

			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			jasperPrintList.add(jasperPrint1);

			exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, ouputStream);

			exporter.exportReport();

			ouputStream.flush();
			ouputStream.close();

		} catch (JRException e) {

			e.printStackTrace();

		}

	}

	@RequestMapping("/getDetail")
	public void getDetail(@RequestParam("keyId") Integer invoiceId, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();

		List<Map<String, Object>> fieldList = commonService.getFilldByModule("invoice");
		Invoice invoice = null;
		String fields = "";
		for (int i = 0; i < fieldList.size(); i++) {
			fields += fieldList.get(i).get("field_label") + ",";
		}
		if (fields != null && !"".equals(fields)) {
			fields = fields.substring(0, fields.length() - 1);
			Map<String, Object> params = new HashMap<>();
			String sql = "select  " + fields + " from v_invoice where invoiceId=:invoiceId";
			params.put("invoiceId", invoiceId);
			List<Object> list = baseService.getList(params, sql, Invoice.class);

			for (int k = 0; k < list.size(); k++) {
				invoice = (Invoice) list.get(k);
			}
		}
		json.put("data", invoice);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	

/*	@RequestMapping("/printPDFtest")
	public void printPDFtest(@RequestParam(value = "obj_id") String obj_id, HttpSession sesison, HttpServletResponse response,
			HttpServletRequest request) throws IOException, DocumentException, Exception {

		String sql = " select obj_title from obj WHERE obj_id=:obj_id ";
		Map<String, Object> params = new HashMap<>();
		params.put("obj_id", obj_id);
		Map<String, Object> result = baseService.getObject(sql, params);

		String url = "http://localhost:8080/dodo40_master_new/invoice/getpdf?obj_id=" + obj_id + "";
		ConvertToXhtml.convertToXhtml(url, response, request, result.get("obj_title").toString());
	}

	@RequestMapping("/getpdf")
	public String getpdf(@RequestParam(value = "obj_id") String obj_id, HttpSession sesison, HttpServletResponse response, HttpServletRequest request)
			throws ServletException, IOException {
		List<QuotationItem> quotationItem = invoiceService.getQuotationItemsByParentId(obj_id);
		request.setAttribute("quotationItem", quotationItem);
		request.setAttribute("title", "Test Inovice");
		return "forward:testInvoice.jsp";
	}*/

	/**
	 * excel 导出
	 */
	@RequestMapping("/exportFile")
	public void exportFile(@RequestParam(value = "searchForm", defaultValue = "") String searchForm, HttpServletResponse response) throws Exception {
		List<Invoice> exportList = invoiceService.getexportList(searchForm);
		ExcelExport excel = new ExcelExport();
		excel.createExcelAlInvoice(exportList, response);
	}

}
