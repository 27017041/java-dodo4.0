package com.embraiz.util;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.embraiz.model.Invoice;
import com.embraiz.model.Quotation;

public class ExcelExport {
	public void createExcelAllWfJob(List<Map<String, Object>> exportList, HttpServletResponse response) throws Exception {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("export");
		XSSFRow row = sheet.createRow((int) 0);
		XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		sheet.setDefaultColumnWidth(25);
		Font ztFont = wb.createFont();
		ztFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		ztFont.setFontHeightInPoints((short) 14);
		style.setFont(ztFont);

		XSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("Route Name");
		cell.setCellStyle(style);

		cell = row.createCell((short) 1);
		cell.setCellValue("Status");
		cell.setCellStyle(style);

		cell = row.createCell((short) 2);
		cell.setCellValue("Step");
		cell.setCellStyle(style);

		cell = row.createCell((short) 3);
		cell.setCellValue("Time");
		cell.setCellStyle(style);

		cell = row.createCell((short) 4);
		cell.setCellValue("Name");
		cell.setCellStyle(style);

		for (int i = 0; i < exportList.size(); i++) {
			row = sheet.createRow((int) i + 1);
			Map<String, Object> export = exportList.get(i);

			String routeName = "";
			if (export.get("route_name") != null) {
				routeName = export.get("route_name").toString();
			}
			row.createCell((short) 0).setCellValue(routeName);

			String jobStatus = "";
			if (export.get("job_status") != null) {
				jobStatus = export.get("job_status").toString();
			}
			row.createCell((short) 1).setCellValue(jobStatus);

			String stepId = "";
			if (export.get("step_id") != null) {
				stepId = export.get("step_id").toString();
			}
			row.createCell((short) 2).setCellValue(stepId);

			String actionTime = "";
			if (export.get("action_time") != null) {
				actionTime = StringFormat.formatData(export.get("action_time").toString());
			}
			row.createCell((short) 3).setCellValue(actionTime);

			row.createCell((short) 4).setCellValue(export.get("submitted_by").toString());

		}

		try {
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
			String fileName = df.format(date);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment;filename=WfJobList" + fileName + ".xlsx");
			OutputStream out = response.getOutputStream();
			out.flush();
			wb.write(out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
	 * Invoice
	 */
	public void createExcelAlInvoice(List<Invoice> exportList, HttpServletResponse response) throws Exception {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("invoice");
		XSSFRow row = sheet.createRow((int) 0);
		XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		sheet.setDefaultColumnWidth(25);
		Font ztFont = wb.createFont();
		ztFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		ztFont.setFontHeightInPoints((short) 14);
		style.setFont(ztFont);

		XSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("Invoice No");
		cell.setCellStyle(style);

		cell = row.createCell((short) 1);
		cell.setCellValue("Quotation No");
		cell.setCellStyle(style);

		cell = row.createCell((short) 2);
		cell.setCellValue("Date");
		cell.setCellStyle(style);

		cell = row.createCell((short) 3);
		cell.setCellValue("Console Amounnt");
		cell.setCellStyle(style);

		cell = row.createCell((short) 4);
		cell.setCellValue("Invoice Amount");
		cell.setCellStyle(style);

		cell = row.createCell((short) 5);
		cell.setCellValue("Balance");
		cell.setCellStyle(style);

		cell = row.createCell((short) 6);
		cell.setCellValue("payment");
		cell.setCellStyle(style);

		cell = row.createCell((short) 7);
		cell.setCellValue("Company");
		cell.setCellStyle(style);

		cell = row.createCell((short) 8);
		cell.setCellValue("Quotation Amount");
		cell.setCellStyle(style);

		cell = row.createCell((short) 9);
		cell.setCellValue("Status");
		cell.setCellStyle(style);

		for (int i = 0; i < exportList.size(); i++) {
			row = sheet.createRow((int) i + 1);
			Invoice export = exportList.get(i);

			String invoiceNo = "";
			if (export.getInvoiceNo() != null) {
				invoiceNo = export.getInvoiceNo();
			}
			row.createCell((short) 0).setCellValue(invoiceNo);

			String quotationNo = "";
			if (export.getQuotationNo() != null) {
				quotationNo = export.getQuotationNo();
			}
			row.createCell((short) 1).setCellValue(quotationNo);

			String Date = "";
			if (export.getCreateDate() != null) {
				Date = export.getCreateDate().toString();
			}
			row.createCell((short) 2).setCellValue(Date);

			String conseleAmount = "";
			if (export.getConsoleAmount() != null) {
				conseleAmount = export.getConsoleAmount().toString();
			}
			row.createCell((short) 3).setCellValue("$" + ("".equals(conseleAmount) ? "0.00" : conseleAmount));

			String invoiceAmount = "";
			if (export.getAmount() != null) {
				invoiceAmount = export.getAmount().toString();
			}
			row.createCell((short) 4).setCellValue("$" + ("".equals(invoiceAmount) ? "0.00" : invoiceAmount));

			String balance = "";
			if (export.getBalance() != null) {
				balance = export.getBalance().toString();
			}
			row.createCell((short) 5).setCellValue("$" + ("".equals(balance) ? "0.00" : balance));

			String payment = "";
			if (export.getPayment() != null) {
				payment = export.getPayment().toString();
			}
			row.createCell((short) 6).setCellValue("$" + ("".equals(payment) ? "0.00" : payment));

			String companyName = "";
			if (export.getCompanyName() != null) {
				companyName = export.getCompanyName().toString();
			}
			row.createCell((short) 7).setCellValue(companyName);

			String quotationAmount = "";
			if (export.getQuotationAmount() != null) {
				quotationAmount = export.getQuotationAmount().toString();
			}
			row.createCell((short) 8).setCellValue("$" + quotationAmount);

			String statusName = "";
			if (export.getStatusName() != null) {
				statusName = export.getStatusName().toString();
			}
			row.createCell((short) 9).setCellValue(statusName);

		}

		try {
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
			String fileName = df.format(date);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment;filename=InvoiceList" + fileName + ".xlsx");
			OutputStream out = response.getOutputStream();
			out.flush();
			wb.write(out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	/**
	 * Quotation
	 */
	public void createExcelByQuotation(List<Quotation> exportList, HttpServletResponse response) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("quotation");
		XSSFRow row = sheet.createRow((int) 0);
		XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		sheet.setDefaultColumnWidth(25);
		Font ztFont = wb.createFont();
		ztFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		ztFont.setFontHeightInPoints((short) 14);
		style.setFont(ztFont);

		XSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("Quotation No");
		cell.setCellStyle(style);

		cell = row.createCell((short) 1);
		cell.setCellValue("Title");
		cell.setCellStyle(style);

		cell = row.createCell((short) 2);
		cell.setCellValue("Company Name");
		cell.setCellStyle(style);

		cell = row.createCell((short) 3);
		cell.setCellValue("Contact Name");
		cell.setCellStyle(style);

		cell = row.createCell((short) 4);
		cell.setCellValue("Create Date");
		cell.setCellStyle(style);

		cell = row.createCell((short) 5);
		cell.setCellValue("Total Cost");
		cell.setCellStyle(style);

		cell = row.createCell((short) 6);
		cell.setCellValue("Balance");
		cell.setCellStyle(style);

		cell = row.createCell((short) 7);
		cell.setCellValue("Payment");
		cell.setCellStyle(style);

		for (int i = 0; i < exportList.size(); i++) {
			row = sheet.createRow((int) i + 1);
			Quotation export = exportList.get(i);

			String quotationNo = "";
			if (export.getQuotationNo() != null) {
				quotationNo = export.getQuotationNo();
			}
			row.createCell((short) 0).setCellValue(quotationNo);

			String title = "";
			if (export.getTitle() != null) {
				title = export.getTitle();
			}
			row.createCell((short) 1).setCellValue(title);

			String companyName = "";
			if (export.getClientName() != null) {
				companyName = export.getClientName();
			}
			row.createCell((short) 2).setCellValue(companyName);

			String contactName = "";
			if (export.getContactName() != null) {
				contactName = export.getContactName();
			}
			row.createCell((short) 3).setCellValue(contactName);

			String createDate = "";
			if (export.getCreateDate() != null) {
				createDate = export.getCreateDate().toString();
			}
			row.createCell((short) 4).setCellValue(createDate);

			String totalCost = "";
			if (export.getTotalCost() != null) {
				totalCost = export.getTotalCost().toString();
			}
			row.createCell((short) 5).setCellValue(totalCost);

			String balance = "";
			if (export.getBalance() != null) {
				balance = export.getBalance().toString();
			}
			row.createCell((short) 6).setCellValue(balance);

			String payment = "";
			if (export.getPayment() != null) {
				payment = export.getPayment().toString();
			}
			row.createCell((short) 7).setCellValue(payment);

		}
		try {
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
			String fileName = df.format(date);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment;filename=QuotationList" + fileName + ".xlsx");
			OutputStream out = response.getOutputStream();
			out.flush();
			wb.write(out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	/**
	 * Contact
	 */
	public void createExcelByContact(List<Map<String, Object>> exportList, HttpServletResponse response) {

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("export");
		XSSFRow row = sheet.createRow((int) 0);
		XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		sheet.setDefaultColumnWidth(25);
		Font ztFont = wb.createFont();
		ztFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		ztFont.setFontHeightInPoints((short) 14);
		style.setFont(ztFont);

		XSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("Contact");
		cell.setCellStyle(style);

		cell = row.createCell((short) 1);
		cell.setCellValue("Job Position");
		cell.setCellStyle(style);

		cell = row.createCell((short) 2);
		cell.setCellValue("Mobile No");
		cell.setCellStyle(style);

		cell = row.createCell((short) 3);
		cell.setCellValue("Direct Line");
		cell.setCellStyle(style);

		cell = row.createCell((short) 4);
		cell.setCellValue("Contact Email");
		cell.setCellStyle(style);

		cell = row.createCell((short) 5);
		cell.setCellValue("Create Date");
		cell.setCellStyle(style);

		cell = row.createCell((short) 6);
		cell.setCellValue("Creator");
		cell.setCellStyle(style);

		for (int i = 0; i < exportList.size(); i++) {
			row = sheet.createRow((int) i + 1);
			Map<String, Object> export = exportList.get(i);

			String contact = "";
			if (export.get("contactName") != null) {
				contact = export.get("contactName").toString();
			}
			row.createCell((short) 0).setCellValue(contact);

			String JobPosition = "";
			if (export.get("jobTitle") != null) {
				JobPosition = export.get("jobTitle").toString();
			}
			row.createCell((short) 1).setCellValue(JobPosition);

			String Mobile = "";
			if (export.get("mobile") != null) {
				Mobile = export.get("mobile").toString();
			}
			row.createCell((short) 2).setCellValue(Mobile);

			String DirectLine = "";
			if (export.get("directLine") != null) {
				DirectLine = export.get("directLine").toString();
			}
			row.createCell((short) 3).setCellValue(DirectLine);

			String ContactEmail = "";
			if (export.get("email") != null) {
				ContactEmail = export.get("email").toString();
			}
			row.createCell((short) 4).setCellValue(ContactEmail);

			String CreateDate = "";
			if (export.get("createDate") != null) {
				CreateDate = export.get("createDate").toString();
			}
			row.createCell((short) 5).setCellValue(CreateDate);

			String Creator = "";
			if (export.get("loginName") != null) {
				Creator = export.get("loginName").toString();
			}
			row.createCell((short) 6).setCellValue(Creator);
		}
		try {
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
			String fileName = df.format(date);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment;filename=ContactList" + fileName + ".xlsx");
			OutputStream out = response.getOutputStream();
			out.flush();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createExcelByPayment(List<Map<String, Object>> exportList, HttpServletResponse response) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("export");
		XSSFRow row = sheet.createRow((int) 0);
		XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		sheet.setDefaultColumnWidth(30);
		Font ztFont = wb.createFont();
		ztFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		ztFont.setFontHeightInPoints((short) 14);
		style.setFont(ztFont);

		XSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("Payment No");
		cell.setCellStyle(style);

		cell = row.createCell((short) 1);
		cell.setCellValue("Date");
		cell.setCellStyle(style);

		cell = row.createCell((short) 2);
		cell.setCellValue("Company");
		cell.setCellStyle(style);

		cell = row.createCell((short) 3);
		cell.setCellValue("Method");
		cell.setCellStyle(style);

		cell = row.createCell((short) 4);
		cell.setCellValue("Currency");
		cell.setCellStyle(style);

		cell = row.createCell((short) 5);
		cell.setCellValue("Total Amount");
		cell.setCellStyle(style);

		cell = row.createCell((short) 6);
		cell.setCellValue("Banked");
		cell.setCellStyle(style);

		cell = row.createCell((short) 7);
		cell.setCellValue("Remarks");
		cell.setCellStyle(style);

		cell = row.createCell((short) 8);
		cell.setCellValue("Staff");
		cell.setCellStyle(style);

		for (int i = 0; i < exportList.size(); i++) {
			row = sheet.createRow((int) i + 1);
			Map<String, Object> export = exportList.get(i);

			String paymentNo = "";
			if (export.get("payment_no") != null) {
				paymentNo = export.get("payment_no").toString();
			}
			row.createCell((short) 0).setCellValue(paymentNo);

			String Date = "";
			if (export.get("date") != null) {
				Date = export.get("date").toString();
			}
			row.createCell((short) 1).setCellValue(Date);

			String Company = "";
			if (export.get("companyName") != null) {
				Company = export.get("companyName").toString();
			}
			row.createCell((short) 2).setCellValue(Company);

			String Method = "";
			if (export.get("methodIdName") != null) {
				Method = export.get("methodIdName").toString();
			}
			row.createCell((short) 3).setCellValue(Method);

			String Currency = "";
			if (export.get("currencyName") != null) {
				Currency = export.get("currencyName").toString();
			}
			row.createCell((short) 4).setCellValue(Currency);

			String TotalAmount = "";
			if (export.get("total_amount") != null) {
				TotalAmount = export.get("total_amount").toString();
			}
			row.createCell((short) 5).setCellValue(TotalAmount);

			String Banked = "";
			if (export.get("banked") != null) {
				Banked = export.get("banked").toString();
			}
			row.createCell((short) 6).setCellValue(Banked);

			String remark = "";
			if (export.get("remarks") != null) {
				remark = export.get("remarks").toString();
			}
			row.createCell((short) 7).setCellValue(remark);

			String staff = "";
			if (export.get("username") != null) {
				staff = export.get("username").toString();
			}
			row.createCell((short) 8).setCellValue(staff);

		}

		try {
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
			String fileName = df.format(date);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment;filename=PaymentList" + fileName + ".xlsx");
			OutputStream out = response.getOutputStream();
			out.flush();
			wb.write(out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public void createExcelByLead(List<Map<String, Object>> exportList, HttpServletResponse response) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("export");
		XSSFRow row = sheet.createRow((int) 0);
		XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		sheet.setDefaultColumnWidth(30);
		Font ztFont = wb.createFont();
		ztFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		ztFont.setFontHeightInPoints((short) 14);
		style.setFont(ztFont);

		XSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("Lead  Name");
		cell.setCellStyle(style);

		cell = row.createCell((short) 1);
		cell.setCellValue("Client");
		cell.setCellStyle(style);

		cell = row.createCell((short) 2);
		cell.setCellValue("Client Phone");
		cell.setCellStyle(style);

		cell = row.createCell((short) 3);
		cell.setCellValue("Building Name");
		cell.setCellStyle(style);

		cell = row.createCell((short) 4);
		cell.setCellValue("District");
		cell.setCellStyle(style);

		cell = row.createCell((short) 5);
		cell.setCellValue("Client Website Address");
		cell.setCellStyle(style);

		cell = row.createCell((short) 6);
		cell.setCellValue("Marketing  Code");
		cell.setCellStyle(style);

		cell = row.createCell((short) 7);
		cell.setCellValue("Client Email Address");
		cell.setCellStyle(style);

		cell = row.createCell((short) 8);
		cell.setCellValue("Notes");
		cell.setCellStyle(style);

		cell = row.createCell((short) 8);
		cell.setCellValue("Creator");
		cell.setCellStyle(style);

		for (int i = 0; i < exportList.size(); i++) {
			row = sheet.createRow((int) i + 1);
			Map<String, Object> export = exportList.get(i);

			String LeadName = "";
			if (export.get("leadName") != null) {
				LeadName = export.get("leadName").toString();
			}
			row.createCell((short) 0).setCellValue(LeadName);

			String Client = "";
			if (export.get("clientName") != null) {
				Client = export.get("clientName").toString();
			}
			row.createCell((short) 1).setCellValue(Client);

			String ClientPhone = "";
			if (export.get("clientPhone") != null) {
				ClientPhone = export.get("clientPhone").toString();
			}
			row.createCell((short) 2).setCellValue(ClientPhone);

			String BuildingName = "";
			if (export.get("address1") != null) {
				BuildingName = export.get("address1").toString();
			}
			row.createCell((short) 3).setCellValue(BuildingName);

			String District = "";
			if (export.get("district") != null) {
				District = export.get("district").toString();
			}
			row.createCell((short) 4).setCellValue(District);

			String Website = "";
			if (export.get("website") != null) {
				Website = export.get("website").toString();
			}
			row.createCell((short) 5).setCellValue(Website);

			String MarketingCode = "";
			if (export.get("marketingCode") != null) {
				MarketingCode = export.get("marketingCode").toString();
			}
			row.createCell((short) 6).setCellValue(MarketingCode);

			String ClientEmailAddress = "";
			if (export.get("clientEmail") != null) {
				ClientEmailAddress = export.get("clientEmail").toString();
			}
			row.createCell((short) 7).setCellValue(ClientEmailAddress);

			String notes = "";
			if (export.get("notes") != null) {
				notes = export.get("notes").toString();
			}
			row.createCell((short) 8).setCellValue(notes);

			String creator = "";
			if (export.get("creatorName") != null) {
				creator = export.get("creatorName").toString();
			}
			row.createCell((short) 9).setCellValue(creator);
		}
		try {
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
			String fileName = df.format(date);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment;filename=PaymentList" + fileName + ".xlsx");
			OutputStream out = response.getOutputStream();
			out.flush();
			wb.write(out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
