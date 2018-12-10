package com.embraiz.service;

import java.util.Map;

import com.embraiz.model.Page;
import com.embraiz.model.User;

public interface UserService {

	/**
	 * 登陆验证user信息
	 * 
	 * @param email
	 * @param password
	 * @return
	 */
	public User checkEmailAndPassword(String email, String password);

	public User checkEmail(String email);

	/**
	 * 查询email是否已注册
	 * 
	 * @param email
	 * @return
	 */
	public boolean ajaxValidateEmail(String email);

	/**
	 * 查询登录名是否已经被使用
	 * @param loginName  登录
	 * return boolean true表示已经未使用，false表示已使用
	 * @author Ada
	 */
	public boolean ajaxValidateLoginName(String loginName);
	/**
	 * 删除user
	 * 
	 * @param userIds
	 * @param updateUserId
	 * @return
	 */
	public int deleteUser(String userIds, int updateUserId);

	/**
	 * 根据userId查找user
	 * 
	 * @param userId
	 * @return
	 */
	public User getUserData(int userId, String lang);
	/**
	 * 根据userId查找user
	 */
	public User getUserById(int userId);
	/**
	 * 表格查询user
	 * 
	 * @param searchForm
	 * @param userId
	 * @param page
	 * @param sortBy
	 * @param sortOrder
	 * @return
	 */
	public Map<String, Object> getUserList(String searchForm, int userId, Page page, String sortBy, String sortOrder, String lang);

	/**
	 * insert user，并记录到log
	 * 
	 * @param user
	 * @param userId
	 * @return
	 */
	public int insertUser(User user, int userId,int parentId);

	/**
	 * 修改User的信息，并记录到log
	 * 
	 * @param user
	 * @param changeFields
	 * @param updateUserId
	 * @return
	 */
	public int updateUser(User user, String changeFields, int updateUserId);

	/**
	 * 第一次登录修改密码
	 * 
	 * @param userId
	 * @param password
	 * @return
	 */
	public int updatePSW(int userId, String password);

	/**
	 * 查询loginName是否已注册
	 * 
	 * @param loginName
	 * @return
	 */
	public boolean validateLoginName(String loginName);

	/**
	 * 修改用户语言
	 * 
	 * @param lang
	 * @param userId
	 * @return
	 */
	public int updateLang(String lang, Integer userId);

	/**
	 * 插入登录记录表
	 * @param user
	 */
	public void saveLoginLog(User user);

	
	/**
	 * 登出
	 * @param userForm
	 * @return 
	 */
	public int updateandsignOut(User userForm);
	
	
}
