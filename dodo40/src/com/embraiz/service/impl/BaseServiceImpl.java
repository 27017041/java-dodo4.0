package com.embraiz.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.service.BaseService;
import com.embraiz.vo.Page;
@Service
@Transactional
public class BaseServiceImpl implements BaseService {

	@Resource
	private BaseDao baseDao;
	
	@Override
	public Map<String, Object> getListAndPage(String sql,
			Map<String, Object> params, Page page) {
		return baseDao.getListAndPage(sql, params, page);
	}

	@Override
	public List<Map<String, Object>> getList(String sql, Map<String, Object> params) {
		return baseDao.getList(sql, params);
	}
	
	@Override
	public List<Object> getList(Map<String, Object> params, String sql,Class c) {
		return baseDao.getList(params,sql,c);
	}
	
	/**
	 * 查询所有记录
	 */
	public List<Object> getListByHql(Class c){
		return baseDao.getListByHql(c);
	}

	@Override
	public Object getObject(Class className, Serializable primaryKey) {
		return baseDao.getObject(className, primaryKey);
	}

	@Override
	public Integer getCountBySql(String sql, Map<String, Object> params) {
		return baseDao.getCountBySql(sql, params);
	}

	@Override
	public Serializable save(Object object) {
		return baseDao.save(object);
	}

	@Override
	public void update(Object object) {
		baseDao.update(object);
	}

	@Override
	public Integer deleteById(Class c,Serializable primaryKey) {
		return baseDao.deleteById(c, primaryKey);
	}

	@Override
	public Integer deleteBySql(String sql, Map<String, Object> params) {
		return baseDao.deleteBySql(sql, params);
	}

	@Override
	public Map<String, Object> getObject(String sql, Map<String, Object> params) {
		return baseDao.getObject(sql, params);
	}

	@Override
	public Map<String, Object> getListAndPage(Class className, Page page) {
		return baseDao.getListAndPage(className, page);
	}

	@Override
	public Integer updateBySql(String sql, Map<String, Object> params) {
		return baseDao.updateBySql(sql, params);
	}
	
	@Override
	public Integer updateBySqlInObjectMap(String sql, Map<Object, Object> params) {
		return baseDao.updateBySqlInObjectMap(sql, params);
	}
	
	@Override
	public Integer insertBySql(String sql, Map<Object, Object> params) {
		return baseDao.insertBySql(sql, params);
	}
	
	@Override
	public void updateByCall(String sql,Map<String, Object> params){
		baseDao.updateByCall(sql,params);
	}

	@Override
	public Object getObjectByHql(String hql, Map<String, Object> params) {
		return baseDao.getObjectByHql(hql, params);
	}

	@Override
	public List<Object> getListByHql(String hql, Map<String, Object> params) {
		return baseDao.getListByHql(hql, params);
	}

	@Override
	public Integer deleteById(Class c, Serializable[] primaryKey) {
		return baseDao.deleteById(c, primaryKey);
	}

	@Override
	public Map<String, Object> getListAndPageByHql(String hql,
			Map<String, Object> params, Page page) {
		return baseDao.getListAndPageByHql(hql, params, page);
	}

	@Override
	public void deleteByObject(Object object) {
		baseDao.deleteByObject(object);
	}

	

}
