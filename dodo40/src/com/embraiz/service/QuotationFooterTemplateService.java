package com.embraiz.service;

import java.util.Map;

import com.embraiz.model.Page;
import com.embraiz.model.QuotationFooterTemplate;
import com.embraiz.model.User;

public interface QuotationFooterTemplateService {

	public int insert(QuotationFooterTemplate quotationFooterTemplate);

	public int deleteData(String ids, User user);

	public Map<String, Object> getQuotationFooterTemplateList(String searchForm, String sortBy, String sortOrder, Page pageBo);

	public Map<String, Object> getDetail(Integer quotationFooterTempalteId);

}
