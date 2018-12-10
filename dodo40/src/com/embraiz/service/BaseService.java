package com.embraiz.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.embraiz.vo.Page;

public interface BaseService {
	



	/**
	 * 查询列表带分页
	 * @param sql
	 * @param params
	 * @param page
	 * @return
	 */
	public Map<String, Object> getListAndPage(String sql,Map<String, Object> params,Page page);
	
	/**
	 * 查询单条记录
	 * @param sql
	 * @param params
	 * @return
	 */
	public Map<String, Object> getObject(String sql,Map<String, Object> params);
	
	/**
	 * 查询列表
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> getList(String sql,Map<String, Object> params);
	
	
	/**
	 * 查询列表 
	 * 返回集合用实体类装载
	 */
	public List<Object> getList(Map<String, Object> params,String sql,Class c);
	
	
	
	/**
	 * 根据类名称和主键查询
	 * @param className
	 * @param id
	 * @return
	 */
	public Object getObject(Class className,Serializable primaryKey);
	
	/**
	 * 根据sql查询一条记录
	 * @param hql
	 * @return
	 */
	public Object getObjectByHql(String hql,Map<String, Object> params);
	
	/**
	 * 根据hql查询列表
	 * @param hql
	 * @param params
	 * @param page
	 * @return
	 */
	public Map<String, Object> getListAndPageByHql(String hql,Map<String, Object> params,Page page);
	
	/**
	 * 根据hql查询列表
	 * @param hql
	 * @return
	 */
	public List<Object> getListByHql(String hql,Map<String, Object> params);
	
	/**
	 * 查询所有记录
	 */
	public List<Object> getListByHql(Class c);
	
	/**
	 * 查询记录通过类名并分页
	 * @param className
	 * @param page
	 * @return
	 */
	public Map<String, Object> getListAndPage(Class className,Page page);
	
	/**
	 * 根据sql查询返回Integer
	 * @param sql
	 * @param params
	 * @return
	 */
	public Integer getCountBySql(String sql,Map<String, Object> params);
	
	/**
	 * 保存
	 * @param object
	 * @return
	 */
	public Serializable save(Object object);
	
	/**
	 * 更新
	 * @param object
	 * @return
	 */
	public void update(Object object);
	
	/**
	 * 通过sql更新
	 * @param sql
	 * @param params
	 * @return
	 */
	public Integer updateBySql(String sql,Map<String, Object> params);
	
	
	/**
	 * 通过sql更新
	 * @param sql
	 * @param params
	 * @return
	 */
	public Integer updateBySqlInObjectMap(String sql,Map<Object, Object> params);
	
	/**
	 * 通过sql插入
	 * @param sql
	 * @param params
	 * @return
	 */
	public Integer insertBySql(String sql,Map<Object, Object> params);
	
	/**
	 * 通过sql执行
	 * @param sql
	 * @param params
	 * @return
	 */
	public void updateByCall(String sql,Map<String, Object> params);
	
	/**
	 * 根据class名字和id删除
	 * @param c
	 * @param id
	 * @return
	 */
	public Integer deleteById(Class c,Serializable primaryKey);
	
	/**
	 * 根据class名字和id数组删除
	 * @param c
	 * @param id
	 * @return
	 */
	public Integer deleteById(Class c,Serializable[] primaryKey);
	
	
	/**
	 * 根据sql执行删除
	 * @param sql
	 * @param params
	 * @return
	 */
	public Integer deleteBySql(String sql,Map<String, Object> params);
	
	/**
	 * 根据object删除
	 * @param object
	 * @return
	 */
	public void deleteByObject(Object object);
}
