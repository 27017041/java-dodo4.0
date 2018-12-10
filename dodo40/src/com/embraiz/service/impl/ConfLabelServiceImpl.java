package com.embraiz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.model.ConfLabel;
import com.embraiz.service.ConfLabelService;

@Service
@Transactional
public class ConfLabelServiceImpl implements ConfLabelService {
	
	@Resource
	private BaseDao baseDao;

	@Override
	public List<ConfLabel> getModuleLabelList(String lang, String typeKey, String module) {
		String sql="select label_orginal,label_text from v_label_all where type_key=:typeKey and module=:module and lang_code=:lang";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("typeKey", typeKey);
		params.put("lang", lang);
		params.put("module", module);
		List<Object> objectList = baseDao.getList(params, sql, ConfLabel.class);
		List<ConfLabel> labelList = new ArrayList<ConfLabel>();
		for(int i=0;i<objectList.size();i++){
			ConfLabel label = (ConfLabel)objectList.get(i);
			labelList.add(label);
		}
		return labelList;
	}

	@Override
	public List<String> getLabelType(String lang, String module) {
		String sql="select type_key from v_label_all where module=:module and lang_code=:lang group by type_name";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("module", module);
		params.put("lang", lang);
		List<Object> objectList = baseDao.getList(params, sql, ConfLabel.class);
		List<String> typeList = new ArrayList<String>();
		for(int i=0;i<objectList.size();i++){
			ConfLabel label = (ConfLabel)objectList.get(i);
			typeList.add(label.getTypeKey());
		}
		return typeList;
	}

	@Override
	public List<ConfLabel> getGlobalLabelList(String lang, String typeName) {
		String sql="select label_orginal,label_text from v_label_all where type_key=:typeKey and module is null and lang_code=:lang";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("typeKey", typeName);
		params.put("lang", lang);
		List<Object> objectList = baseDao.getList(params, sql, ConfLabel.class);
		List<ConfLabel> labelList = new ArrayList<ConfLabel>();
		for(int i=0;i<objectList.size();i++){
			ConfLabel label = (ConfLabel)objectList.get(i);
			labelList.add(label);
		}
		return labelList;
	}

}
