package com.embraiz.service;

import java.util.Map;

import com.embraiz.model.Page;
import com.embraiz.model.QuotationItemTemplate;
import com.embraiz.model.User;

public interface QuotationItemTemplateService {

	public int insert(QuotationItemTemplate quotationItemTemplate);

	public int deleteData(String ids, User user);

	public Map<String, Object> getQuotationItemTemplateList(String searchForm, String sortBy, String sortOrder, Page pageBo);

	public Map<String, Object> getDetail(Integer quotationItemTempalteId);

}
