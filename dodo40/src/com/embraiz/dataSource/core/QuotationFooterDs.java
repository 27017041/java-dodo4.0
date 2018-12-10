package com.embraiz.dataSource.core;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import com.embraiz.model.QuotationFooter;

public class QuotationFooterDs implements JRDataSource {

	private int index = -1;

	private List<QuotationFooter> quotationFooters;

	// 创建数据源时传入一个对象数组作为数据源
	public QuotationFooterDs(List<QuotationFooter> quotationFooters) {

		if (quotationFooters == null) {
			this.quotationFooters = new ArrayList<QuotationFooter>();
		} else
			this.quotationFooters = quotationFooters;
	}

	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;

		String fieldName = field.getName();

		if (field.getName().equals("title_1")) {
			value = quotationFooters.get(index).getTitle();
			if (value == null)
				value = "";
		} else if (field.getName().equals("description_1")) {
			value = quotationFooters.get(index).getDescription();
			if (value == null)
				value = "";
		}
		return value;
	}

	public boolean next() throws JRException {
		index++;
		return (index < quotationFooters.size());
	}

}
