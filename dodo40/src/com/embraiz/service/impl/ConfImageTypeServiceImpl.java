package com.embraiz.service.impl;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.model.ConfImageType;
import com.embraiz.service.ConfImageTypeService;

@Service
@Transactional
public class ConfImageTypeServiceImpl implements ConfImageTypeService {

	@Resource
	private BaseDao baseDao;
	
	@Override
	public Serializable saveConfImageType(ConfImageType confImageType) {
		return baseDao.save(confImageType);
	}

	@Override
	public boolean deleteConfImageType(Integer confId) {
		int rtn = baseDao.deleteById(ConfImageType.class, confId);
		if(rtn>0){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void updateConfImageType(ConfImageType confImageType) {
		ConfImageType confImageTypeDb = (ConfImageType) baseDao.getObject(ConfImageType.class, confImageType.getConfId());
		if(confImageTypeDb!=null){
			confImageTypeDb.setConfName(confImageType.getConfName());
			confImageTypeDb.setHeight(confImageType.getHeight());
			confImageTypeDb.setWidth(confImageType.getWidth());
			confImageTypeDb.setQuality(confImageType.getQuality());
			
			baseDao.update(confImageTypeDb);
		}
	}

	@Override
	public List<Object> getList() {
		return baseDao.getListByHql(ConfImageType.class);
	}

}
