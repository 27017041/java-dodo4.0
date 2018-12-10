package com.embraiz.service;

import java.io.Serializable;
import java.util.List;

import com.embraiz.model.ConfImageType;

public interface ConfImageTypeService {

	/**
	 * 增加ConfImageType
	 * @param confImageType
	 * @return
	 */
	public Serializable saveConfImageType(ConfImageType confImageType);
	
	/**
	 * 根据confId删除ConfImageType
	 * @param conf_id
	 * @return
	 */
	public boolean deleteConfImageType(Integer confId);
	
	/**
	 * 修改ConfImageType
	 * @param confImageType
	 */
	public void updateConfImageType(ConfImageType confImageType);
	
	/**
	 * 得到ConfImageType列表
	 * @return
	 */
	public List<Object> getList();
}
