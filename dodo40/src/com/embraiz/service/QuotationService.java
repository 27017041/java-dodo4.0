package com.embraiz.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.embraiz.model.Page;
import com.embraiz.model.Quotation;
import com.embraiz.model.QuotationFooter;
import com.embraiz.model.QuotationItem;
import com.embraiz.model.User;

public interface QuotationService {
	public int insert(Quotation quotation);

	public int insertFooter(QuotationFooter quotationFooterDb);

	public int insertItem(QuotationItem quotationItemDb);

	public int deleteData(String ids, User user);

	public Map<String, Object> seleteQuotationList(String searchForm, String sortBy, String sortOrder, Page page);

	public String quotationNo();

	public List<Object> getQuotationById(int id);

	public List<Object> getQuotationItemsByParentId(int id);

	public List<Object> getQuotationFootersByParentId(int id);

	public BigDecimal getOriginalTotalByQuotationId(Integer quotationId);

	public BigDecimal getSubTotalByQuotationId(Integer quotationId);

	public BigDecimal getDiscountByQuotationId(Integer quotationId);

	public Map<String, Object> getcurrency();

	public List<Quotation> getexportList(String searchForm);

	public void updateTotalCost(String obj_id);

	public int getStatus(String obj_id);

	public int deleteItem(String ids, User user);

	public Map<String, Object> getTemplateDatasourse();

	public int deleteFooter(String ids, User user);

	public Map<String, Object> getQuotationFooterList(String searchForm, String sortBy, String sortOrder, Page page);

	public Map<String, Object> getQuotationItemList(String searchForm, String sortBy, String sortOrder, Page page);

	public Map<String, Object> getDetail(Integer quotationId);

}
