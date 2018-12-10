package com.embraiz.service;

import java.util.Map;

import com.embraiz.model.Page;

public interface RelationalService {

	Map<String, Object> getRelational(Integer keyId, String moduleName, String relationalName, String sortBy, String sortOrder, Page pageBo,
			int start, int length);

	Map<String, Object> getCoreRelational(String moduleName);

	Map<String, Object> getLinkageModuleList(String viewsName);

	Map<String, Object> getLinkageModuleListById(String viewsName, String keyName, Integer keyId);

}
