package com.embraiz.service;

import java.util.Map;

import com.embraiz.model.ObjLog;
import com.embraiz.model.Page;

public interface ObjLogService {

	public void createLog(ObjLog objLog);

	public Map<String, Object> objLogList(String moduleName, String sortBy, String sortOrder, Page page);

	public Map<String, Object> getHistory(int objId,String moduleName);

}
