package com.embraiz.dataSource.core;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import com.embraiz.model.Quotation;
import com.embraiz.model.QuotationItem;

public class QuotationDs implements JRDataSource {

	private int index = -1;
	private Quotation[] data;
	private List<QuotationItem> quotationItems;
	DecimalFormat numformat = new DecimalFormat("###,###,##0.00");

	// 创建数据源时传入一个对象数组作为数据源
	public QuotationDs(Quotation[] data, List<QuotationItem> quotationItems) {
		if (data == null) {
			this.data = new Quotation[0];
		} else
			this.data = data;
		if (quotationItems == null) {
			this.quotationItems = new ArrayList<QuotationItem>();
		} else
			this.quotationItems = quotationItems;
	}

	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;

		String fieldName = field.getName();

		if ("id".equals(fieldName)) {
			value = index + 1;
		}
		if (index < data.length) {
			if (field.getName().equals("quotationNo")) {
				value = data[index].getQuotationNo();
				if (value == null)
					value = "";
			} else if (field.getName().equals("contactName")) {
				value = data[index].getContactName();
				if (value == null)
					value = "";
			} else if (field.getName().equals("companyName")) {
				value = data[index].getClientName();
				if (value == null)
					value = "";
			} else if (field.getName().equals("prepareBy")) {
				value = data[index].getPrepareBy();
				if (value == null)
					value = "";
			} else if (field.getName().equals("companyEmail")) {
				value = data[index].getClientName();
				if (value == null)
					value = "";
			} else if (field.getName().equals("title_1")) {
				value = data[index].getTitle();
				if (value == null)
					value = "";
			}
		}
		if (index < quotationItems.size()) {
			if (field.getName().equals("title")) {
				value = quotationItems.get(index).getTitle();
				if (value == null)
					value = "";
			} else if (field.getName().equals("description")) {
				value = quotationItems.get(index).getDescription();
				if (value == null)
					value = "";
			} else if (field.getName().equals("unitPriceStr")) {
				value = quotationItems.get(index).getUnitPrice();
				if (value == null)
					value = 0 + "";
				else
					value = (data[0].getCurrencyName() != null ? (data[0].getCurrencyName()) : "HK$") + numformat.format(value);
			} else if (field.getName().equals("subTotalStr")) {
				value = quotationItems.get(index).getSubTotal();
				if (value == null)
					value = new BigDecimal(0.0) + "";
				else
					value = numformat.format(value);
			} else if (field.getName().equals("qty")) {
				value = quotationItems.get(index).getQty();
				if (value == null)
					value = 0;
			} else if (field.getName().equals("discountStr")) {
				value = quotationItems.get(index).getDiscount();
				if (value == null)
					value = 0 + "";
				else
					value = numformat.format(value);
			}
		}
		return value;
	}

	public boolean next() throws JRException {
		index++;
		if (data.length >= quotationItems.size()) {
			return (index < data.length);
		} else {
			return (index < quotationItems.size());
		}
	}

}
