package com.embraiz.service.impl;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.embraiz.dao.BaseDao;
import com.embraiz.dataSource.core.InvoiceDs;
import com.embraiz.dataSource.core.InvoiceDs2;
import com.embraiz.dataSource.core.QuotationDs;
import com.embraiz.dataSource.core.QuotationFooterDs;
import com.embraiz.model.Invoice;
import com.embraiz.model.MessageMail;
import com.embraiz.model.Quotation;
import com.embraiz.model.QuotationFooter;
import com.embraiz.model.QuotationItem;
import com.embraiz.model.User;
import com.embraiz.model.WfJob;
import com.embraiz.model.WfJobDetail;
import com.embraiz.service.WorkflowService;
import com.embraiz.util.BeanTransformerAdapter;
import com.embraiz.util.SendMail;
import com.embraiz.util.StringFormat;

@Service
@Transactional
public class WorkflowServiceImpl implements WorkflowService {
	@Resource
	private BaseDao baseDao;

	@Override
	public String getStatus(String obj_id, String user_id) {
		// approved, rejected, pending, my_approval
		String response_output = "";
		String sql = "select current_approver, job_status from wf_job where module_id=:module_id ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("module_id", obj_id);
		Map<String, Object> result = baseDao.getObject(sql, params);

		String current_approver = result.get("current_approver").toString();
		String job_status = result.get("job_status").toString();
		if (current_approver.equals(user_id)) {
			response_output = "my_approval";
		} else {
			response_output = job_status;
		}
		return response_output;
	}

	@Override
	public int update(String obj_id) {
		int num = 0;
		String sql = " update wf_job_detail " + " inner join wf_job on wf_job.job_id = wf_job_detail.job_id "
				+ " set wf_job.job_status=:rejected,wf_job_detail.step_status=:rejected " + " where wf_job.module_id=:module_id ";
		Map<String, Object> params = new HashMap<>();
		params.put("rejected", "rejected");
		params.put("module_id", obj_id);
		params.put("pending", "pending");
		num = baseDao.updateBySql(sql, params);

		// Update Status
		String update = "update t_quotation set status=3 where quotation_id=:quotation_id";
		params.put("quotation_id", obj_id);
		baseDao.updateBySql(update, params);
		return num;
	}

	/**
	 * Quotation Submit
	 */
	@Override
	public int createJob(String obj_id, User user) {
		int JobId = 0;
		WfJob wfJob = new WfJob();
		wfJob.setModuleId(Integer.parseInt(obj_id));
		wfJob.setRouteId(1);// quotation:1
		wfJob.setJobStatus("pending");
		wfJob.setActionDate(new Date());
		wfJob.setSubmittedBy(user.getUserId());
		wfJob.setCurrentApprover(1);
		final WfJob wfJobadd = wfJob;
		JobId = (Integer) baseDao.save(wfJobadd);

		WfJobDetail wfJobDetail = new WfJobDetail();
		wfJobDetail.setStepStatus("pending");
		wfJobDetail.setUserId(user.getUserId());
		wfJobDetail.setStepId(1);
		wfJobDetail.setActionDate(new Date());
		wfJobDetail.setJobid(JobId);
		final WfJobDetail wfJobDetailadd = wfJobDetail;
		baseDao.save(wfJobDetailadd);

		// Update Status
		Map<String, Object> params = new HashMap<>();
		String update = "update t_quotation set status=2 where quotation_id=:quotation_id";
		params.put("quotation_id", obj_id);
		baseDao.updateBySql(update, params);

		return JobId;
	}

	/**
	 * Invoice Submit
	 */
	@Override
	public int createInvoiceJob(String obj_id, User user) {
		int JobId = 0;
		Map<String, Object> params = new HashMap<>();
		String sql = "select count(*) from t_invoice where invoice_id=:invoice_id";
		params.put("invoice_id", obj_id);
		int i = baseDao.getCountBySql(sql, params);
		if (i > 0) {
			WfJob wfJob = new WfJob();
			wfJob.setModuleId(Integer.parseInt(obj_id));
			wfJob.setRouteId(2);// invoice:2
			wfJob.setJobStatus("pending");
			wfJob.setActionDate(new Date());
			wfJob.setSubmittedBy(user.getUserId());
			wfJob.setCurrentApprover(1);
			final WfJob wfJobadd = wfJob;
			JobId = (Integer) baseDao.save(wfJobadd);

			WfJobDetail wfJobDetail = new WfJobDetail();
			wfJobDetail.setStepStatus("pending");
			wfJobDetail.setUserId(user.getUserId());
			wfJobDetail.setStepId(1);
			wfJobDetail.setActionDate(new Date());
			wfJobDetail.setJobid(JobId);
			final WfJobDetail wfJobDetailadd = wfJobDetail;
			baseDao.save(wfJobDetailadd);
			// Update Status
			String update = "update t_invoice set status=2 where invoice_id=:invoice_id";
			baseDao.updateBySql(update, params);
		}
		return JobId;
	}

	@SuppressWarnings("static-access")
	@Override
	public int updateApprover(String obj_id) throws UnknownHostException {
		// when user click approved
		int num = 0;
		Map<String, Object> params = new HashMap<>();
		String sqlselect = "select * from v_wf_job_list where module_id=:module_id and job_status=:job_status";
		params.put("module_id", obj_id);
		params.put("job_status", "pending");
		Map<String, Object> result = baseDao.getObject(sqlselect, params);
		if (result.size() > 0) {
			// first step
			if (result.get("step_id").toString().equals("1")) {
				String sql = " update wf_job_detail inner join wf_job on wf_job.job_id = wf_job_detail.job_id "
						+ " set wf_job.current_approver=:current_approver, wf_job_detail.step_id=:step_id " + " where wf_job.module_id=:module_id ";
				params.put("current_approver", 2);// 下个审核人userId
				params.put("step_id", 2);
				params.put("module_id", obj_id);
				num = baseDao.updateBySql(sql, params);

				// 下一个审核人资料
				StringBuffer mailContent = new StringBuffer();
				String user = "select  * from v_user_wf_job_detail_list where module_id=:module_id";
				params.put("module_id", obj_id);
				Map<String, Object> User = baseDao.getObject(user, params);

				// 审核内容
				StringBuffer content = new StringBuffer();
				content.append(" select * from wf_job ");
				if (result.get("route_id").equals(1)) {
					content.append(" LEFT JOIN t_quotation ON wf_job.module_id = t_quotation.quotation_id ");
				} else if (result.get("route_id").equals(2)) {
					content.append(" LEFT JOIN t_invoice ON wf_job.module_id = t_invoice.invoice_id ");
				}
				content.append(" where module_id=:module_id ");
				params.put("module_id", obj_id);
				Map<String, Object> resultcontent = baseDao.getObject(content.toString(), params);

				String url = "";
				mailContent.append("<strong>Dear " + User.get("login_name").toString() + ",</strong><br/><br/>");

				try {
					if (resultcontent.get("route_id").toString().equals("1")) {
						mailContent.append("Quotation:" + resultcontent.get("quotation_no"));
						url = this.printQuotationPDF(Integer.parseInt(obj_id));
					} else if (result.get("route_id").equals(2)) {
						mailContent.append("Invoice:" + resultcontent.get("invoice_no"));
						url = this.printInvoicePDF(Integer.parseInt(obj_id));
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				mailContent.append(" was approve,Please wait for the next step <br/><br/>Regards<br/><br/>Embraiz CRM System");

				MessageMail messageMail = new MessageMail();
				messageMail.setStatus("w");
				messageMail.setStatusOther("1");
				messageMail.setMailTo(User.get("email").toString());
				messageMail.setMailSubject("ERP Form Approve");
				messageMail.setMailContent(mailContent.toString());
				messageMail.setInsertDate(StringFormat.formatData(new Date()));
				int keyId = (Integer) baseDao.save(messageMail);

				SendMail sendmail = new SendMail();
				try {
					sendmail.sendMailcc(messageMail.getMailTo(), messageMail.getMailSubject(), messageMail.getMailContent(), url);
					String update = " update t_message_mail set status=:status where mail_id=:mail_id";
					Map<String, Object> params1 = new HashMap<>();
					params1.put("status", "S");
					params1.put("mail_id", keyId);
					baseDao.updateBySql(update, params1);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// second step
			} else if (result.get("step_id").toString().equals("2")) {
				String sql = " update wf_job_detail inner join wf_job on wf_job.job_id = wf_job_detail.job_id "
						+ " set wf_job.job_status=:job_status, wf_job_detail.step_status=:step_status " + " where wf_job.module_id=:module_id ";
				params.put("job_status", "approved");
				params.put("step_status", "approved");
				params.put("module_id", obj_id);
				num = baseDao.updateBySql(sql, params);

				// send mail
				StringBuffer mailContent = new StringBuffer();
				String submittedBy = "select * from v_user_wf_job_detail_list " + " where module_id=:module_id";
				Map<String, Object> map = new HashMap<>();
				map.put("module_id", obj_id);
				Map<String, Object> User = baseDao.getObject(submittedBy, params);

				if (User.get("route_id").toString().equals("1")) {// quotation
					// Update Status
					this.updateQuotation(obj_id);
				} else if (User.get("route_id").toString().equals("2")) {// invoice
					// Update invoice
					this.updateInvoice(obj_id);

				}

				String url = "";
				// mailContent
				mailContent.append("<strong>Dear " + User.get("login_name").toString() + ",</strong><br/><br/>");
				mailContent.append("" + User.get("route_name").toString() + ": ");
				try {

					if (User.get("route_name").toString().equals("Quotation")) {
						mailContent.append(User.get("quotation_no"));
						url = this.printQuotationPDF(Integer.parseInt(obj_id));
					} else if (User.get("route_name").toString().equals("Invoice")) {
						mailContent.append(User.get("invoice_no"));
						url = this.printInvoicePDF(Integer.parseInt(obj_id));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				mailContent.append(" was approve <br/><br/>Regards<br/><br/>Embraiz CRM System");
				MessageMail messageMail = new MessageMail();
				messageMail.setStatus("w");
				messageMail.setStatusOther("1");
				messageMail.setMailTo(User.get("email").toString());
				messageMail.setMailSubject("ERP Form Approve");
				messageMail.setMailContent(mailContent.toString());
				messageMail.setInsertDate(StringFormat.formatData(new Date()));
				int keyId = (Integer) baseDao.save(messageMail);

				SendMail sendmail = new SendMail();
				try {
					// send
					sendmail.sendMailcc(messageMail.getMailTo(), messageMail.getMailSubject(), messageMail.getMailContent(), url);
					String update = " update t_message_mail set status=:status where mail_id=:mail_id";
					Map<String, Object> params1 = new HashMap<>();
					params1.put("status", "S");
					params1.put("mail_id", keyId);
					baseDao.updateBySql(update, params1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return num;
	}

	public void updateQuotation(String obj_id) throws UnknownHostException {
		InetAddress address = InetAddress.getLocalHost();
		String ip = address.getHostAddress();
		Map<String, Object> params = new HashMap<>();

		String updateStatus = "update t_quotation set status=6,is_approval=1,approval_ip=:approval_ip,approval_date=:approval_date where quotation_id=:quotation_id";
		params.put("quotation_id", Integer.parseInt(obj_id));
		params.put("approval_ip", ip);
		params.put("approval_date", new Date());
		baseDao.updateBySql(updateStatus, params);

	}

	public void updateInvoice(String obj_id) throws UnknownHostException {
		InetAddress address = InetAddress.getLocalHost();
		String ip = address.getHostAddress();
		Map<String, Object> params = new HashMap<>();
		String updateStatus = "update t_invoice set status=6,is_approval=1,approval_ip=:approval_ip,approval_date=:approval_date where invoice_id=:invoice_id";
		params.put("invoice_id", Integer.parseInt(obj_id));
		params.put("approval_ip", ip);
		params.put("approval_date", new Date());
		baseDao.updateBySql(updateStatus, params);

	}

	/**
	 * QuotationPDF附件
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public String printQuotationPDF(@RequestParam(value = "obj_id") int quotationId) throws Exception {
		SimpleDateFormat dateFormat3 = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);
		DecimalFormat numFormat = new DecimalFormat("###,###,##0.00");

		Quotation quotation = null;
		List quotationItems = new ArrayList<>();
		List quotationFooters = new ArrayList<>();
		Quotation[] data = new Quotation[0];

		String sql = "select * from v_quotation where 1=1  and quotationId=:quotationId ";
		Map<String, Object> params = new HashMap<>();
		params.put("quotationId", quotationId);
		List<Object> quotationList = baseDao.getList(params, sql, Quotation.class);
		for (int i = 0; i < quotationList.size(); i++) {
			quotation = (Quotation) quotationList.get(i);
		}

		String sql1 = "select * from v_quotation_item where 1=1 and  parent_id=:parent_id order by sort";
		params.put("parent_id", quotationId);
		List<Object> list = baseDao.getList(params, sql1, QuotationItem.class);
		for (int i = 0; i < list.size(); i++) {
			QuotationItem qi = (QuotationItem) list.get(i);
			quotationItems.add(qi);
		}

		String sql2 = "select * from t_quotation_footer where 1=1 and  t_quotation_footer.quotation_id=:quotation_id ";
		params.put("quotation_id", quotationId);
		List<Object> quotationFooterList = baseDao.getList(params, sql2, QuotationFooter.class);
		for (int i = 0; i < quotationFooterList.size(); i++) {
			QuotationFooter qf = (QuotationFooter) quotationFooterList.get(i);
			quotationFooters.add(qf);
		}

		String root_path = System.getProperty("evan.webapp");
		String pdfurl = "";
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String fname = df.format(new Date()) + ".pdf";
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
			List<Map<String, Object>> result = baseDao.getList(sql3, params);
			if (result.get(0).get("cost").toString() != null && !"".equals(result.get(0).get("cost").toString())) {
				originalTotalHKD = new BigDecimal(result.get(0).get("cost").toString());
			} else {
				originalTotalHKD = new BigDecimal(0);
			}

			String sql4 = "select sum(v_quotation_item.sub_total) AS sub_total from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
			params.put("parent_id", quotationId);
			List<Map<String, Object>> result1 = baseDao.getList(sql4, params);
			if (result1.get(0).get("sub_total").toString() != null && !"".equals(result1.get(0).get("sub_total").toString())) {
				totalHKD = new BigDecimal(result1.get(0).get("sub_total").toString());
			} else {
				totalHKD = new BigDecimal(0);
			}

			String sql5 = "select sum(v_quotation_item.discount) AS discount  from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
			params.put("parent_id", quotationId);
			Map<String, Object> result2 = baseDao.getObject(sql5, params);
			if (result2.get("discount") != null && !"".equals(result2.get("discount"))) {
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
	 * InvoicePDF附件
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public String printInvoicePDF(@RequestParam("obj_id") int invoiceId) throws Exception {
		String pdfurl = "";
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);
		DecimalFormat numFormat = new DecimalFormat("###,###,##0.00");
		Invoice invoice = null;
		Invoice[] data = null;
		Map<String, Object> params = new HashMap<>();
		List<QuotationItem> quotationItems = new ArrayList<QuotationItem>();

		String sql = "select * from v_invoice  where invoiceId=:invoiceId";
		params.put("invoiceId", invoiceId);
		List<Object> invoiceList = baseDao.getList(params, sql, Invoice.class);
		for (int i = 0; i < invoiceList.size(); i++) {
			invoice = (Invoice) invoiceList.get(i);
		}

		StringBuffer quoationsNo = new StringBuffer();
		try {
			String sql1 = " select distinct v_quotation.* from v_quotation "
					+ " inner join t_obj_quotation_invoice on v_quotation.quotationId=t_obj_quotation_invoice.quotation_id "
					+ " and t_obj_quotation_invoice.invoice_id=:invoice_id and v_quotation.status<>4 ";
			params.put("invoice_id", invoiceId);
			Query query = baseDao.getSessionFactory().getCurrentSession().createSQLQuery(sql1)
					.setResultTransformer(new BeanTransformerAdapter(Quotation.class));
			if (params != null && params.size() > 0) {
				query.setProperties(params);
			}

			List<Quotation> quotationList = query.list();

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
					String sql2 = "select sum(v_quotation_item.cost) as cost from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
					params.put("parent_id", quotationList.get(i).getQuotationId());
					List<Map<String, Object>> result = baseDao.getList(sql2, params);
					if (result.get(0).get("cost").toString() != null && !"".equals(result.get(0).get("cost").toString())) {
						tempOriginal = new BigDecimal(result.get(0).get("cost").toString());
					} else {
						tempOriginal = new BigDecimal(0);
					}

					String sql3 = "select sum(v_quotation_item.cost) as cost from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
					params.put("parent_id", quotationList.get(i).getQuotationId());
					List<Map<String, Object>> costresult = baseDao.getList(sql3, params);
					if (costresult.get(0).get("cost").toString() != null && !"".equals(costresult.get(0).get("cost").toString())) {
						tempOriginal = new BigDecimal(costresult.get(0).get("cost").toString());
					} else {
						tempOriginal = new BigDecimal(0);
					}

					originalTotalHKD = originalTotalHKD.add(tempOriginal == null ? new BigDecimal(0.00) : tempOriginal);

					String sql4 = "select sum(v_quotation_item.discount) as discount from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
					params.put("parent_id", quotationList.get(i).getQuotationId());
					List<Map<String, Object>> discountresult = baseDao.getList(sql4, params);
					if (discountresult.get(0).get("discount").toString() != null && !"".equals(discountresult.get(0).get("discount").toString())) {
						tempDiscount = new BigDecimal(discountresult.get(0).get("discount").toString());
					} else {
						tempDiscount = new BigDecimal(0);
					}

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

					String sql5 = "select * from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id order by sort";
					params.put("parent_id", quotationList.get(i).getQuotationId());
					Query query1 = baseDao.getSessionFactory().getCurrentSession().createSQLQuery(sql5)
							.setResultTransformer(new BeanTransformerAdapter(QuotationItem.class));

					if (params != null && params.size() > 0) {
						query1.setProperties(params);
					}
					tempItems = query1.list();

					String sql6 = "select sum(v_quotation_item.sub_total) as sub_total from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
					params.put("parent_id", quotationList.get(i).getQuotationId());

					BigDecimal TotalCost = null;
					BigDecimal Payment = null;
					List<Map<String, Object>> subTotalresult = baseDao.getList(sql6, params);
					if (subTotalresult.get(0).get("sub_total").toString() != null && !"".equals(subTotalresult.get(0).get("sub_total").toString())) {
						TotalCost = new BigDecimal(subTotalresult.get(0).get("sub_total").toString());
					} else {
						TotalCost = new BigDecimal(0);
					}

					String sql7 = "select payment from t_obj_quotation_invoice where invoice_id=:invoice_id and quotation_id=:quotation_id ";
					params.put("invoice_id", invoiceId);
					params.put("quotation_id", quotationList.get(i).getQuotationId());
					List<Map<String, Object>> result1 = baseDao.getList(sql7, params);
					if (result1.get(0).get("payment").toString() != null && !"".equals(result1.get(0).get("payment").toString())) {
						Payment = new BigDecimal(result1.get(0).get("payment").toString());
					} else {
						Payment = new BigDecimal(0);
					}

					quotationList.get(i).setTotalCost(TotalCost);
					quotationList.get(i).setPayment(Payment);

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
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String fname = df.format(new Date()) + ".pdf";
			pdfurl = root_path + "\\mail_file\\" + fname;
			FileOutputStream ouputStream = new FileOutputStream(root_path + "\\mail_file\\" + fname + "");

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

		return pdfurl;

	}
}
