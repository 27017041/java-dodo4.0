package com.embraiz.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.embraiz.model.Lead;
import com.embraiz.model.User;

public interface LeadService {

	public int insert(Lead lead);

	public int deleteData(String ids, User user);

	public List<Map<String, Object>> getexportList(String searchForm);

	public boolean insertexportFile(String name, MultipartFile file);

	public Map<String, Object> getArea();

	public Map<String, Object> getReturnMail();

	public Map<String, Object> getOptout();

}
