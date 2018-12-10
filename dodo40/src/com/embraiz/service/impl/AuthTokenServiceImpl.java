package com.embraiz.service.impl;

import java.math.BigInteger;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.model.AuthToken;
import com.embraiz.service.AuthTokenService;
import com.embraiz.util.DateUtil;

@Service
@Transactional
public class AuthTokenServiceImpl implements AuthTokenService {
	
	@Resource
	private BaseDao baseDao;

	@Override
	public AuthToken getToken(String uuid) {
		String hql = "from AuthToken where uuid=:uuid";
		Query query = baseDao.getSessionFactory().getCurrentSession().createQuery(hql);
		query.setString("uuid", uuid);
		return (AuthToken) query.uniqueResult();
	}

	@Override
	public int addToken(AuthToken authToken) {
		return (Integer) baseDao.save(authToken);
	}

	@Override
	public int deleteToken() {
		String sql = "delete from t_auth_token where overdue<:time";
		Query query = baseDao.getSessionFactory().getCurrentSession().createSQLQuery(sql);
		query.setInteger("time", DateUtil.getDate());
		return query.executeUpdate();
	}

	@Override
	public int getCountByUid(Integer uid) {
		String sql = "select count(*) from t_auth_token where uid=:uid";
		Query query = baseDao.getSessionFactory().getCurrentSession().createSQLQuery(sql);
		query.setInteger("uid", uid);
		BigInteger rs = (BigInteger) query.uniqueResult();
		return rs.intValue();
	}

	@Override
	public int updateToken(AuthToken authToken) {
		String sql = "update t_auth_token set uuid=:uuid,token=:token,overdue=:overdue where uid=:uid";
		Query query = baseDao.getSessionFactory().getCurrentSession().createSQLQuery(sql);
		query.setString("uuid", authToken.getUuid());
		query.setString("token", authToken.getToken());
		query.setInteger("overdue", authToken.getOverdue());
		query.setInteger("uid", authToken.getUid());
		return query.executeUpdate();
	}

}
