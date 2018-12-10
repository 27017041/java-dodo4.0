package com.embraiz.service;

import java.util.List;
import java.util.Map;

import com.embraiz.model.Contact;
import com.embraiz.model.Page;

public interface ContactService {
	
	
	public int insert(Contact contact);

	public int deleteData(String ids, Integer userId);

	public Map<String, Object> seleteContactList(String searchForm, String sortBy, String sortOrder, Page page);

	public Map<String, Object> getAllContactName();

	public Map<String, Object> getContactDetail(Integer contactId);

	public List<Map<String, Object>> getexportList(String searchForm);

}
