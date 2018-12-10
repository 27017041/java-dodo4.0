package com.embraiz.controller;

import java.io.FileOutputStream;
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
import javax.servlet.ServletException;
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

import com.alibaba.fastjson.JSONObject;
import com.embraiz.dataSource.core.QuotationDs;
import com.embraiz.dataSource.core.QuotationFooterDs;
import com.embraiz.model.Module;
import com.embraiz.model.Payment;
import com.embraiz.model.Quotation;
import com.embraiz.model.QuotationFooter;
import com.embraiz.model.QuotationItem;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.CommonService;
import com.embraiz.service.ConfService;
import com.embraiz.service.PaymentService;
import com.embraiz.util.ConvertToXhtml;
import com.embraiz.util.ExcelExport;
import com.embraiz.util.SendMail;
import com.lowagie.text.DocumentException;

@Controller
@RequestMapping("/payment")
public class PaymentController {

	@Resource
	private PaymentService paymentService;
	@Resource
	private BaseService baseService;
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
			List<Map<String, Object>> fieldListInGrid = commonService.getGridFieldInModule("payment", lang);
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
							+ ") FROM ref_option ref WHERE FIND_IN_SET(ref.option_id,v_payment." + multiselectFieldLabel + ")) AS "
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
			String sql = "select " + fieldStr + " from v_payment where 1=1 ";
			String sqlCount = "select count(1) from v_payment where 1=1  ";

			// 搜索区域用到的栏位
			List<Map<String, Object>> fieldListInSearch = commonService.getSearchFieldInModule("payment", lang);

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
			json.put("searchDataList", commonService.getSearchFieldInModule("payment", lang));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 获得表单区域的栏位和数据
	 */
	@RequestMapping("/getFormData")
	public void getFormData(HttpServletResponse response, HttpSession session, @RequestParam("primaryId") Integer paymentId) throws IOException {
		User user = (User) session.getAttribute("user");
		JSONObject json = new JSONObject();
		if (user != null) {
			String lang = (String) session.getAttribute("lang");
			if (lang == null) {
				lang = "en";
			}
			// 获得表单区域需要显示的栏位
			List<Map<String, Object>> fieldListInForm = commonService.getFormFieldInModule("payment", lang);
			json.put("fieldListInForm", fieldListInForm);
			// 存储拼接的字段
			String fieldStr = "";
			for (int i = 0; i < fieldListInForm.size(); i++) {
				fieldStr = fieldStr + fieldListInForm.get(i).get("fieldLabel").toString() + ",";
			}
			fieldStr = fieldStr.substring(0, fieldStr.length() - 1);
			String sql = "select " + fieldStr + " from v_payment where payment_id = :payment_id";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("payment_id", paymentId);
			json.put("formData", baseService.getList(sql, params));
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * PaymentDetail
	 */
	@RequestMapping("/getDetail")
	public void getDetail(@RequestParam("keyId") Integer paymentId, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();

		List<Map<String, Object>> fieldList = commonService.getFilldByModule("payment");
		Payment payment = null;
		String fields = "";
		for (int i = 0; i < fieldList.size(); i++) {
			fields += fieldList.get(i).get("field_label") + ",";
		}
		if (fields != null && !"".equals(fields)) {
			fields = fields.substring(0, fields.length() - 1);
			Map<String, Object> params = new HashMap<>();
			String sql = "select  " + fields + " from v_payment where paymentId=:paymentId";
			params.put("paymentId", paymentId);
			List<Object> list = baseService.getList(params, sql, Payment.class);

			for (int k = 0; k < list.size(); k++) {
				payment = (Payment) list.get(k);
			}
		}
		json.put("data", payment);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();

	}

	@RequestMapping("/printPDF")
	public void printPDF(@RequestParam(value = "obj_id") String obj_id, HttpSession sesison, HttpServletResponse response, HttpServletRequest request)
			throws IOException, DocumentException, Exception {
		String sql = " select obj_title from obj WHERE obj_id=:obj_id ";
		Map<String, Object> params = new HashMap<>();
		params.put("obj_id", obj_id);
		Map<String, Object> result = baseService.getObject(sql, params);
		String path = request.getContextPath();
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
		System.out.println(basePath);
		String url = basePath + "payment/getpdf?obj_id=" + obj_id + "";
		ConvertToXhtml.convertToXhtml(url, response, request, result.get("obj_title").toString());
	}

	@RequestMapping("/getpdf")
	public String getpaymentById(@RequestParam(value = "obj_id") String obj_id, HttpSession sesison, HttpServletResponse response,
			HttpServletRequest request) throws ServletException, IOException {
		SimpleDateFormat sdf = new SimpleDateFormat(" MMMM dd,yyyy", Locale.ENGLISH);
		List<Payment> paymentItem = paymentService.getpaymentById(obj_id);
		request.setAttribute("paymentItem", paymentItem);
		request.setAttribute("date", sdf.format(new Date()));

		String payment = "";
		String mf = "$";
		String cna = paymentItem.get(0).getCurrencyName();
		if (cna != null && "RMB".equals(cna))
			mf = "￥";
		else if (cna != null && "HKD".equals(cna))
			mf = "HK$";
		else if (cna != null && "SGD".equals(cna))
			mf = "S$";
		else if (cna != null && "TWD".equals(cna))
			mf = "NT$";
		else if (cna != null && "GBP".equals(cna))
			mf = "￡";
		else
			mf = "$";
		payment = mf + " " + paymentItem.get(0).getTotalAmount();

		request.setAttribute("payment", payment);
		return "forward:receiptpdf.jsp";
	}

	/**
	 * 这个是测试
	 * @param obj_id
	 */
	@SuppressWarnings("static-access")
	@RequestMapping("/testmail")
	public void testmail(@RequestParam(value = "obj_id") String obj_id, HttpServletResponse response, HttpServletRequest request) {
		SendMail sendmail = new SendMail();
		try {
			String mailto = "hadly.qiu@embraiz.com";
			String mailSubject = "hadly.qiu@embraiz.com";
			String mailContent = "<strong>Dear root123,</strong><br/><br/>Quotation:Q2018050002 was approve,Please wait for the next step <br/><br/>Regards<br/><br/>Embraiz CRM System";
			String url = this.printPDFFromMail(Integer.parseInt(obj_id), request, response);
			sendmail.sendMailcc(mailto, mailSubject, mailContent, url);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String mailprintPDF(@RequestParam(value = "obj_id") String obj_id, HttpServletResponse response) throws IOException, DocumentException,
			Exception {
		String sql = " select obj_title from obj WHERE obj_id=:obj_id ";
		Map<String, Object> params = new HashMap<>();
		params.put("obj_id", obj_id);
		Map<String, Object> result = baseService.getObject(sql, params);

		String url = "http://localhost:8080/dodo40_master_new/payment/getpdf?obj_id=" + obj_id + "";
		String pdfurl = ConvertToXhtml.convertToXhtmlmail(url, response, result.get("obj_title").toString());

		return pdfurl;
	}
	
	/**
	 * 这个是测试
	 * @param obj_id
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public String printPDFFromMail(@RequestParam(value = "obj_id") int quotationId, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setCharacterEncoding("utf-8");
		SimpleDateFormat dateFormat3 = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);
		DecimalFormat numFormat = new DecimalFormat("###,###,##0.00");

		Quotation quotation = null;
		List quotationItems = new ArrayList<>();
		List quotationFooters = new ArrayList<>();
		Quotation[] data = new Quotation[0];

		String sql = "select * from v_quotation where 1=1  and quotation_id=:quotation_id ";
		Map<String, Object> params = new HashMap<>();
		params.put("quotation_id", quotationId);
		List<Object> quotationList = baseService.getList(params, sql, Quotation.class);
		for (int i = 0; i < quotationList.size(); i++) {
			quotation = (Quotation) quotationList.get(i);
		}

		String sql1 = "select * from v_quotation_item where 1=1 and  parent_id=:parent_id order by sort";
		params.put("parent_id", quotationId);
		List<Object> list = baseService.getList(params, sql1, QuotationItem.class);
		for (int i = 0; i < list.size(); i++) {
			QuotationItem qi = (QuotationItem) list.get(i);
			quotationItems.add(qi);
		}

		String sql2 = "select * from t_quotation_footer where 1=1 and  t_quotation_footer.quotation_id=:quotation_id ";
		params.put("quotation_id", quotationId);
		List<Object> quotationFooterList = baseService.getList(params, sql2, QuotationFooter.class);
		for (int i = 0; i < quotationFooterList.size(); i++) {
			QuotationFooter qf = (QuotationFooter) quotationFooterList.get(i);
			quotationFooters.add(qf);
		}

		String root_path = System.getProperty("evan.webapp");
		String pdfurl = "";
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String fname = df.format(new Date()) + ".pdf";
			;
			pdfurl = root_path + "\\mail_file\\" + fname;
			FileOutputStream ouputStream = new FileOutputStream(root_path + "\\mail_file\\" + fname + "");

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

			String sql3 = "select sum(v_quotation_item.cost) AS cost from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
			params.put("parent_id", quotationId);
			List<Map<String, Object>> result = baseService.getList(sql3, params);
			if (result.get(0).get("cost").toString() != null && !"".equals(result.get(0).get("cost").toString())) {
				originalTotalHKD = new BigDecimal(result.get(0).get("cost").toString());
			} else {
				originalTotalHKD = new BigDecimal(0);
			}

			String sql4 = "select sum(v_quotation_item.sub_total) AS sub_total from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
			params.put("parent_id", quotationId);
			List<Map<String, Object>> result1 = baseService.getList(sql4, params);
			if (result1.get(0).get("sub_total").toString() != null && !"".equals(result1.get(0).get("sub_total").toString())) {
				totalHKD = new BigDecimal(result1.get(0).get("sub_total").toString());
			} else {
				totalHKD = new BigDecimal(0);
			}

			String sql5 = "select sum(v_quotation_item.discount) AS discount  from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
			params.put("parent_id", quotationId);
			Map<String, Object> result2 = baseService.getObject(sql5, params);
			if (result2.get("discount").toString() != null && !"".equals(result2.get("discount").toString())) {
				discounted = new BigDecimal(result2.get("discount").toString());
			} else {
				discounted = new BigDecimal(0);
			}

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
		return pdfurl;

	}

	/**
	 * excel 导出
	 */
	@RequestMapping("/exportFile")
	public void exportFile(@RequestParam(value = "searchForm", defaultValue = "") String searchForm, HttpServletResponse response) throws Exception {
		List<Map<String, Object>> exportList = paymentService.getexportList(searchForm);
		ExcelExport excel = new ExcelExport();
		excel.createExcelByPayment(exportList, response);
	}

}
