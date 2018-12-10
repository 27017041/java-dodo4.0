package com.embraiz.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.embraiz.model.ConfLabel;
import com.embraiz.service.ConfLabelService;

@Component
public class SystemHelperImpl implements SystemHelper {
	
	@Resource
	ConfLabelService confLabelService;
	
	@Override
	public Map<String, Map<String, String>> getLabelListByModule(String lang, String module) {
		Map<String, Map<String, String>> labelMap = new HashMap<String, Map<String,String>>();
		List<String> typeList = confLabelService.getLabelType(lang, module);
		if(typeList!=null){
			for(String typeKey:typeList){
				List<ConfLabel> labelList = confLabelService.getModuleLabelList(lang, typeKey, module);
				Map<String, String> labelTextMap = new HashMap<String, String>();
				if(labelList!=null){
					for(ConfLabel label: labelList){
						labelTextMap.put(label.getLabelOrginal(), label.getLabelText());
					}
				}
				if(labelTextMap!=null){
					labelMap.put(typeKey, labelTextMap);
				}
			}
		}
		return labelMap;
	}
	
	@Override
	public Map<String, Map<String, String>> getLabelListByType(String lang, String[] typeName) {
		Map<String, Map<String, String>> labelMap = new HashMap<String, Map<String,String>>();
		for(int i=0;i<typeName.length;i++){
			List<ConfLabel> labelList = confLabelService.getGlobalLabelList(lang, typeName[i]);
			Map<String, String> labelTextMap = new HashMap<String, String>();
			if(labelList!=null){
				for(ConfLabel label: labelList){
					labelTextMap.put(label.getLabelOrginal(), label.getLabelText());
				}
			}
			if(labelTextMap!=null){
				labelMap.put(typeName[i], labelTextMap);
			}
		}
		return labelMap;
	}
	

}
