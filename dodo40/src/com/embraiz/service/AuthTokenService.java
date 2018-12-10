package com.embraiz.service;

import com.embraiz.model.AuthToken;

public interface AuthTokenService {
	
	/**
	 * 查询token
	 * @param uuid
	 * @return
	 */
	AuthToken getToken(String uuid);
	
	/**
	 * 添加token
	 * @param authToken
	 * @return
	 */
	int addToken(AuthToken authToken);
	
	/**
	 * 更新token
	 * @param authToken
	 * @return
	 */
	int updateToken(AuthToken authToken);
	
	/**
	 * 根据uid查询token个数
	 * @param uid
	 * @return
	 */
	int getCountByUid(Integer uid);
	
	/**
	 * 删掉过期的token
	 * @return
	 */
	int deleteToken();
}
