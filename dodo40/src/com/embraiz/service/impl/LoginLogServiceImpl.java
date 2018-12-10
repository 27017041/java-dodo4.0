package com.embraiz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.service.LoginLogService;

@Service
@Transactional
public class LoginLogServiceImpl implements LoginLogService {
	@Resource
	private BaseDao baseDao;

	@Override
	public int selectnum() {
		String sql = "select count(*) from t_login_log where login_status=1";
		int i = baseDao.getCountBySql(sql, null);
		return i;

	}

}
