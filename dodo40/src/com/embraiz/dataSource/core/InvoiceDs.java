package com.embraiz.dataSource.core;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import com.embraiz.model.Invoice;
import com.embraiz.model.Quotation;

public class InvoiceDs implements JRDataSource {

	private int index = -1;
	private Invoice[] data;
	private List<Quotation> items;
	DecimalFormat numformat = new DecimalFormat("###,###,##0.00");

	// 创建数据源时传入一个对象数组作为数据源
	public InvoiceDs(Invoice[] data, List<Quotation> items) {
		if (data == null) {
			this.data = new Invoice[0];
		} else
			this.data = data;
		if (items == null) {
			this.items = new ArrayList<Quotation>();
		} else
			this.items = items;
	}

	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;

		String fieldName = field.getName();

		if ("id".equals(fieldName)) {
			value = index + 1;
		}
		if (index < data.length) {
			if (field.getName().equals("payername")) {
				value = data[index].getPayername();
				if (value == null)
					value = "";
			} else if (field.getName().equals("invoiceId")) {
				value = data[index].getInvoiceId();
				if (value == null)
					value = "";
			} else if (field.getName().equals("companyName")) {
				value = data[index].getCompanyName();
				if (value == null)
					value = "";
			} else if (field.getName().equals("remarks")) {
				String remarks = (data[index].getRemarks() == null || "".equals(data[index].getRemarks())) ? "" : data[index].getRemarks();
				value = " " + remarks;
				if (value == null)
					value = "";
			}
		}
		if (index < items.size()) {
			if (field.getName().equals("title")) {
				value = items.get(index).getTitle();
			} else if (field.getName().equals("quotationTotalStr")) {
				if (items.get(index).getTotalCost() != null)
					value = numformat.format(items.get(index).getTotalCost());
				else
					value = "";
			} else if (field.getName().equals("createDate")) {
				value = items.get(index).getCreateDate();
			} else if (field.getName().equals("paymentPercent")) {
				value = items.get(index).getPaymentPercent();
			}
		}
		return value;
	}

	public boolean next() throws JRException {	
		index++;
		if (data.length >= items.size()) {
			return (index < data.length);
		} else {
			return (index < items.size());
		}
	}

}
