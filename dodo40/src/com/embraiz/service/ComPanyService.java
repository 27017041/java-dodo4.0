package com.embraiz.service;

import java.util.Map;

import com.embraiz.model.Company;
import com.embraiz.model.User;

public interface ComPanyService {

	public int insert(Company company);

	public Map<String, Object> getCompanyDetail(Integer companyId);

	public Map<String, Object> getAllCompanyName();

	public int deleteData(String ids, User user);

}
