package com.embraiz.helper;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.ConfLabel;
import com.embraiz.service.ConfLabelService;

public interface SystemHelper {
	
	/**
	 * 根据lang和路由模块获取label列表
	 * @param lang
	 * @param module
	 * @return
	 */
	Map<String, Map<String, String>> getLabelListByModule(String lang, String module);
	
	/**
	 * 根据lang和label类型获取label列表
	 * @param lang
	 * @param typeName
	 * @return
	 */
	Map<String, Map<String, String>> getLabelListByType(String lang, String[] typeName);
}
