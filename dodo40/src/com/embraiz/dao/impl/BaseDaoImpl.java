package com.embraiz.dao.impl;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.jdbc.Work;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.util.BeanTransformerAdapter;
import com.embraiz.vo.Page;

/**
 * 数据库操作基类，供调用
 * @author andy
 *
 */

@Repository
public class BaseDaoImpl implements BaseDao{
	@Resource
	protected SessionFactory sessionFactory;
	
	/**
	 *根据主键删除 返回结果
	 */
	public Integer deleteById(Class c,Serializable primaryKey){
		Query query = sessionFactory.getCurrentSession().createQuery("delete from "+c.getSimpleName()+" where "+c.getDeclaredFields()[0].getName()+"=:id");
		query.setParameter("id", primaryKey);
		return query.executeUpdate();
	}
	
	/**
	 * 查询所有记录
	 */
	public List<Object> getListByHql(Class c){
		return sessionFactory.getCurrentSession().createQuery("From "+c.getSimpleName()).list();
	}
	
	/**
	 * 根据sql获取总行数
	 * @param sql
	 * @return
	 */
	public BigInteger getTotalBySql(String sql,Map<String, Object> params){
		//如果sql最后又一个group会影响sql结果,则进行如何判断
		if(sql.contains("group")||sql.contains("GROUP")){
			sql = "select count(*) from ("+sql+") a";
			Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
			if(params!=null){
				query.setProperties(params);
			}
			return (BigInteger) query.uniqueResult();
		}else{
			//获取from的位置
			sql = sql.replaceAll("from", "FROM").replaceAll("From", "FROM");
			int index = sql.indexOf("FROM");
			//获取from后面所有的字符串
			sql = sql.substring(index,sql.length());
			//sql前加入select count(*) 
			sql = "select count(*) "+sql;
			Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
			if(params!=null){
				query.setProperties(params);
			}
			return (BigInteger) query.uniqueResult();
		}
		
		
	}
	
	/**
	 * 根据hql获取总行数
	 * @param hql
	 * @return
	 */
	public BigInteger getTotalByHql(String hql,Map<String, Object> params){
		//获取from的位置
		hql = hql.replaceAll("from", "FROM").replaceAll("From", "FROM");
		int index = hql.indexOf("FROM");
		//获取from后面所有的字符串
		hql = hql.substring(index,hql.length());
		//sql前加入select count(*) 
		hql = "select count(*) "+hql;
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		if(params!=null&&params.size()>0){
			query.setProperties(params);
		}
		try {
			return (BigInteger) query.uniqueResult();
		} catch (Exception e) {
			Long total = (Long) query.uniqueResult();
			return new BigInteger(total.toString());
		}
	}

	@Override
	public Map<String, Object> getListAndPage(String sql,
			Map<String, Object> params, Page page) {
		//如果存在总条数则不查询
		if(page.getSize()==0){
			page.setSize(getTotalBySql(sql,params).intValue());
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		if(params!=null&&params.size()>0){
			query.setProperties(params);
		}
		query.setFirstResult(page.getStart());
		query.setMaxResults(page.getCount());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		map.put("list", query.list());
		return map;
	}

	@Override
	public List<Map<String, Object>> getList(String sql, Map<String, Object> params) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		if(params!=null&&params.size()>0){
			query.setProperties(params);
		}
		return query.list();
	}
	
	@Override
	public List<Object> getList(Map<String, Object> params, String sql,Class c) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(new BeanTransformerAdapter(c));
		if(params!=null&&params.size()>0){
			query.setProperties(params);
		}
		return query.list();
	}
	

	@Override
	public Integer getCountBySql(String sql, Map<String, Object> params) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
		if(params!=null&&params.size()>0){
			query.setProperties(params);
		}
		try {
			BigInteger count = (BigInteger) query.uniqueResult();
			return count.intValue();
		} catch (Exception e) {
			return 0;
		}
		
	}

	@Override
	public Serializable save(Object object) {
		return sessionFactory.getCurrentSession().save(object);
	}

	@Override
	public void update(Object object) {
		sessionFactory.getCurrentSession().update(object);
	}

	@Override
	public Object getObject(Class className, Serializable primaryKey) {
		return sessionFactory.getCurrentSession().get(className, primaryKey);
	}

	@Override
	public Integer deleteBySql(String sql, Map<String, Object> params) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
		if(params!=null&&params.size()>0){
			query.setProperties(params);
		}
		return query.executeUpdate();
	}

	
	@Transactional
	@Override
	public Map<String, Object> getObject(String sql, Map<String, Object> params) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		if(params!=null&&params.size()>0){
			query.setProperties(params);
		}
		return (Map<String, Object>) query.uniqueResult();
	}

	@Override
	public Map<String, Object> getListAndPage(Class className, Page page) {
		if(page.getSize()==0){
			page.setSize(getTotalByHql("from "+className.getSimpleName(),null).intValue());
		}
		Query query = sessionFactory.getCurrentSession().createQuery("from "+className.getSimpleName());
		query.setFirstResult(page.getStart());
		query.setMaxResults(page.getCount());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		map.put("list", query.list());
		return map;
	}

	@Transactional
	@Override
	public Integer updateBySql(String sql, Map<String, Object> params) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
		if(params!=null&&params.size()>0){
			query.setProperties(params);
		}
		return query.executeUpdate();
	}
	
	@Override
	public Integer updateBySqlInObjectMap(String sql, Map<Object, Object> params) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
		
		if(params!=null&&params.size()>0){
			Iterator iter = params.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry) iter.next();
				int key = Integer.parseInt(entry.getKey().toString());
				query.setString(key, entry.getValue().toString());
			}
		}
		return query.executeUpdate();
	}
	
	
	@Override
	public Integer insertBySql(String sql, Map<Object, Object> params) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
		if(params!=null&&params.size()>0){
			Iterator iter = params.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry) iter.next();
				int key = Integer.parseInt(entry.getKey().toString());
				query.setString(key, entry.getValue().toString());
			}
			
		}
		return query.executeUpdate();
	}
	
	@Override
	public void updateByCall(String sql,Map<String, Object> params){
		//Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
		 Session session = sessionFactory.getCurrentSession();
		 Connection conn = ((SessionImplementor)session).connection();
		 try {
			CallableStatement call = conn.prepareCall(sql);
			 call.execute();
			 call.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	@Override
	public Integer insertByCall(String sql,Map<String,Object> params){
		 Session session = sessionFactory.getCurrentSession();
		 Connection conn = ((SessionImplementor)session).connection();
		 Integer keyId = 0;
		 try {
				CallableStatement call = conn.prepareCall(sql);
				
				 if(params!=null && params.size()>0){
					 call.setInt(1, Integer.parseInt(params.get("1").toString()));
					 call.setString(2, params.get("2").toString());
					 call.setString(3, params.get("3").toString());
					 call.setInt(4, Integer.parseInt(params.get("4").toString()));
					 call.setString(5, params.get("5").toString());
					 call.setInt(6, Integer.parseInt(params.get("6").toString()));
					 call.registerOutParameter(7, Integer.parseInt(params.get("7").toString()));
				 }
				 
				 call.execute();
				 
				 keyId = call.getInt(7);//获取输出参数的值
				 
				 call.close();
				 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return keyId;
	}
	
	@Override
	public void updateAndReturnByCall(String sql,Map<String,Object> params){
		 Session session = sessionFactory.getCurrentSession();
		 Connection conn = ((SessionImplementor)session).connection();
		 Integer keyId = 0;
		 try {
				CallableStatement call = conn.prepareCall(sql);
				
				 if(params!=null && params.size()>0){
					 call.setInt(1, Integer.parseInt(params.get("1").toString()));
				 }
				 call.execute();
				 
				 
				 call.close();
				 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	@Override
	public Object getObjectByHql(String hql, Map<String, Object> params) {
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		if(params!=null&&params.size()>0){
			query.setProperties(params);
		}
		return query.uniqueResult();
	}

	@Override
	public List<Object> getListByHql(String hql, Map<String, Object> params) {
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		if(params!=null&&params.size()>0){
			query.setProperties(params);
		}
		return query.list();
	}

	@Override
	public Integer deleteById(Class c, Serializable[] primaryKey) {
		Query query = sessionFactory.getCurrentSession().createQuery("delete from "+c.getSimpleName()+" where "+c.getDeclaredFields()[0].getName()+" in (:id)");
		query.setParameterList("id", primaryKey);
		return query.executeUpdate();
	}

	@Override
	public Map<String, Object> getListAndPageByHql(String hql,
			Map<String, Object> params, Page page) {
		if(page.getSize()==0){
			page.setSize(getTotalByHql(hql,params).intValue());
		}
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setFirstResult(page.getStart());
		query.setMaxResults(page.getCount());
		if(params!=null&&params.size()>0){
			query.setProperties(params);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		map.put("list", query.list());
		return map;
	}

	@Override
	public void deleteByObject(Object object) {
		sessionFactory.getCurrentSession().delete(object);
	}
	
	public void excel(){
		   Session session = sessionFactory.getCurrentSession();
	        session.doWork(new Work() {
	            @Override
	            public void execute(Connection connection) throws SQLException {
	                CallableStatement cs = connection.prepareCall("");
	                cs.executeUpdate();
	            }
	        });
	}

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
