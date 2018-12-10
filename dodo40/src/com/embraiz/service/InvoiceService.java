package com.embraiz.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.embraiz.model.Invoice;
import com.embraiz.model.Page;
import com.embraiz.model.Quotation;
import com.embraiz.model.QuotationItem;
import com.embraiz.model.User;

public interface InvoiceService {

	public String invoiceNo();

	public int insert(Invoice invoice);

	public List<Object> createInovice(int quotationId);

	public int voidInvoice(String id, User user, String voidRemark);

	public Map<String, Object> selectInvoice(String searchForm, String sortBy, String sortOrder, Page pageBo);

	public int deleteData(String ids, User user);

	public List<Object> getinvoiceById(int invoiceId);

	public List<Quotation> getByInvoicId(int invoiceId);

	public BigDecimal getOriginalTotalByQuotationId(Integer quotationId);

	public BigDecimal getDiscountByQuotationId(Integer quotationId);

	public List<QuotationItem> getQuotationItemsByParentId(String quotationId);

	public BigDecimal getSubTotalByQuotationId(Integer quotationId);

	public BigDecimal getPayment(int invoiceId, String quotationId);

	public List<Invoice> getexportList(String searchForm);

	public String getQuotationAmount(Integer invoiceId);

	public Map<String, Object> getInvoiceDetail(Integer invoiceId);

}
