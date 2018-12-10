package com.embraiz.service;

import java.util.List;

import com.embraiz.model.ConfLabel;

public interface ConfLabelService {

	List<ConfLabel> getModuleLabelList(String lang, String typeName, String module);
	
	List<String> getLabelType(String lang, String module);
	
	List<ConfLabel> getGlobalLabelList(String lang, String typeName);
	
}
