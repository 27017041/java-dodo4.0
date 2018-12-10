package com.embraiz.service.impl;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.embraiz.dao.BaseDao;
import com.embraiz.model.LoginLog;
import com.embraiz.model.Obj;
import com.embraiz.model.ObjLog;
import com.embraiz.model.Page;
import com.embraiz.model.User;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.ObjService;
import com.embraiz.service.SystemUtilService;
import com.embraiz.service.UserService;
import com.embraiz.util.StringFormat;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	@Resource
	private BaseDao baseDao;
	@Resource
	private ObjLogService objLogService;
	@Resource
	private SystemUtilService systemUtilService;
	@Resource
	private ObjService objService;

	@Override
	public User checkEmailAndPassword(String email, String password) {

		String sql = "SELECT option_id from ref_option where type_id = 3 and option_key = 'active'";// 获取status对应的option_id

		String hql = "from User where email = :email and password = :password  and status = :status ";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email", email);
		params.put("password", password);
		params.put("status", baseDao.getObject(sql, null).get("option_id"));

		User user = (User) baseDao.getObjectByHql(hql, params);
		return user;
	}

	@Override
	public User checkEmail(String email) {
		String hql = "from User where email = :email";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email", email);

		User user = (User) baseDao.getObjectByHql(hql, params);

		return user;
	}

	@Override
	public boolean ajaxValidateEmail(String email) {
		String sql = "select count(1) from obj_user where email = :email";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email", email);

		Integer number = baseDao.getCountBySql(sql, params);

		return number == 0;
	}

	public boolean ajaxValidateLoginName(String loginName) {
		String sql = "select count(1) from obj_user where login_name = :loginName";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginName", loginName);

		Integer number = baseDao.getCountBySql(sql, params);

		return number == 0;
	}

	@Override
	public int deleteUser(String userIds, int updateUserId) {
		int flag = 0;
		/*
		 * String sql = "delete from obj_user where user_id in ("+userIds+")";
		 * status = jdbcTemplate.update(sql);
		 */

		String sql = "update obj set status=0 where obj_id = :delId ";

		Map<String, Object> params = new HashMap<String, Object>();

		String[] userIdArray = userIds.split(",");
		for (int i = 0; i < userIdArray.length; i++) {
			int status = 0;
			String delId = userIdArray[i];
			params.put("delId", delId);

			status = baseDao.updateBySql(sql, params);

			if (status > 0) {
				flag++;

				ObjLog objLog = new ObjLog();
				objLog.setObjId(Integer.parseInt(delId));
				objLog.setUserId(updateUserId);
				objLog.setDescription("Delete User");
				objLog.setModuleName("User");
				objLog.setConfId(0);
				objLog.setFieldName("status");
				objLogService.createLog(objLog);
			}
		}
		return flag;
	}

	@Override
	public User getUserData(int userId, String lang) {
		String sql = "select v_obj_user.* , ref_option.option_name_" + lang
				+ " as statusName from v_obj_user JOIN ref_option on ref_option.option_id = v_obj_user.`status` where userId = :userId ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);

		User user = new User();

		List<Object> objectList = baseDao.getList(params, sql, User.class);

		if (objectList.size() > 0) {
			user = (User) objectList.get(0);
		}

		return user;
	}

	public User getUserById(int userId) {
		return (User) baseDao.getObject(User.class, userId);
	}

	@Override
	public Map<String, Object> getUserList(String searchForm, int userId, Page page, String sortBy, String sortOrder, String lang) {
		String sql = "select v_obj_user.* , ref_option.option_name_" + lang
				+ " as statusName from v_obj_user JOIN ref_option on ref_option.option_id = v_obj_user.`status` where 1=1 ";
		String totalSql = "select count(1) from v_obj_user where 1=1 ";

		if (searchForm != null && !"".equals(searchForm)) {
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			if (searchJson.get("loginName") != null && !searchJson.get("loginName").equals("")) {
				sql = sql + " and loginName like '%" + searchJson.get("loginName") + "%'";
				totalSql = totalSql + " and loginName like '%" + searchJson.get("loginName") + "%'";
			}
			if (searchJson.get("email") != null && !searchJson.get("email").equals("")) {
				sql = sql + " and email like '%" + searchJson.get("email").toString() + "%'";
				totalSql = totalSql + " and email like '%" + searchJson.get("email").toString() + "%'";
			}
			if (searchJson.get("status") != null && !searchJson.get("status").equals("")) {
				sql = sql + " and status = " + searchJson.get("status").toString();
				totalSql = totalSql + " and status = " + searchJson.get("status").toString();
			}
			if (searchJson.get("roleId") != null && !searchJson.get("roleId").equals("")) {
				String roleId = searchJson.get("roleId").toString();
				sql = sql + " and (roleId = '" + roleId + "' or roleId like '" + roleId + ",%' or roleId like '%," + roleId
						+ ",%' or roleId like '%," + roleId + "') ";
				totalSql = totalSql + " and (roleId = '" + roleId + "' or roleId like '" + roleId + ",%' or roleId like '%," + roleId
						+ ",%' or roleId like '%," + roleId + "') ";
			}
		}

		// obj_share_map,加入userId的条件，查询这个user可看的信息
		sql = sql + " and shareUserId=" + userId;
		totalSql = totalSql + " and shareUserId=" + userId;

		if (!sortBy.equals("") && !sortOrder.equals("")) {
			sql = sql + " order by " + sortBy + " " + sortOrder;
		}
		sql = sql + " limit " + page.getStart() + " , " + page.getCount();

		int size = 0;

		Map<String, Object> params = new HashMap<String, Object>();

		List<Object> objectList = baseDao.getList(params, sql, User.class);
		size = baseDao.getCountBySql(totalSql, params);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", size);
		map.put("recordsFiltered", size);
		map.put("data", objectList);
		return map;

	}

	@Override
	public int insertUser(User user, int doUserId, int parentId) {
		int id = 0;

		Obj obj = new Obj();
		int objTypeId = objService.searchObjType("user");

		if (objTypeId != 0) {
			// insert into obj
			// int parentId = user.getTeamId();
			String titleValue = user.getLoginName();

			obj.setObjTypeId(objTypeId);
			obj.setObjTitle(titleValue);
			obj.setObjDesc(titleValue);
			obj.setKeyword(titleValue);
			obj.setObjParentId(parentId);
			obj.setOwnerId(doUserId);
			obj.setCreateDate(new Date());
			id = objService.insertObj(obj);

			if (id > 0) {

				// insert into user
				user.setUserId(id);

				Integer status = (Integer) baseDao.save(user);

				if (status > 0) {
					ObjLog objLog = new ObjLog();
					objLog.setObjId(0);
					objLog.setUserId(doUserId);
					objLog.setDescription("Create User");
					objLog.setModuleName("User");
					objLog.setConfId(0);
					objLogService.createLog(objLog);
				}
			}
		}

		return id;
	}

	@Override
	public int updateUser(User user, String changeFields, int updateUserId) {
		int status = 0;
		Obj userObj = objService.getObjData(user.getUserId());

		if (user.getTeamId() != userObj.getObjParentId()) {// 父id，team修改了，做一系列处理
			// 修改obj的parentId
		}

		// 更新user表
		baseDao.update(user);

		if (!changeFields.equals("")) {
			changeFields = StringFormat.encodeStr(changeFields);// 解决中文乱码
			String newchangeFields = "[" + changeFields + "]";// 把changeFields变成jsonArray形式
			JSONArray jsonArray = JSONArray.parseArray(newchangeFields);// 转成JSonArrays
			status = 1;// 因为这里的update方法没有返回值，默认是修改成功的
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				ObjLog objLog = new ObjLog();
				objLog.setObjId(user.getUserId());
				objLog.setUserId(updateUserId);
				objLog.setDescription(jsonObject.get("description").toString());
				objLog.setModuleName("User");
				objLog.setConfId(0);
				objLog.setFieldName(jsonObject.get("fieldName").toString());
				objLogService.createLog(objLog);
			}

		}
		return status;
	}

	@Override
	public int updatePSW(int userId, String password) {
		int status = 0;
		String sql = "update obj_user set password = :password ,first_login = 0 where user_id = :userId ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("password", password);
		params.put("userId", userId);

		status = baseDao.updateBySql(sql, params);
		if (status > 0) {
			ObjLog objLog = new ObjLog();
			objLog.setObjId(userId);
			objLog.setUserId(userId);
			objLog.setDescription("Frist Login change Psw");
			objLog.setModuleName("User");
			objLog.setConfId(0);
			objLog.setFieldName("password");
			objLogService.createLog(objLog);
		}
		return status;
	}

	@Override
	public boolean validateLoginName(String loginName) {
		String sql = "select count(1) from obj_user where login_name = :loginName";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginName", loginName);

		Integer number = baseDao.getCountBySql(sql, params);

		return number == 0;
	}

	@Override
	public int updateLang(String lang, Integer userId) {
		String sql = "update obj_user set lang=:lang where user_id=:userId";
		Query query = baseDao.getSessionFactory().getCurrentSession().createSQLQuery(sql);
		query.setString("lang", lang);
		query.setInteger("userId", userId);
		return query.executeUpdate();
	}

	@Override
	public void saveLoginLog(User user) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
			InetAddress addr = InetAddress.getLocalHost();
			String ip = addr.getHostAddress();
			LoginLog loginLog = new LoginLog();
			loginLog.setUserId(user.getUserId());
			loginLog.setLoginStatus("1");
			loginLog.setIpAddress(ip);
			loginLog.setLoginTime(sdf.parse(sdf.format(new Date())));
			baseDao.save(loginLog);
		} catch (Exception e) {

		}

	}

	@Override
	public int updateandsignOut(User user) {
		Map<String, Object> params = new HashMap<String, Object>();
		String sql = "update t_login_log set logout_time = :logoutTime,login_status = :status where user_id = :userId and login_status= :Status and logout_time is null ";
		params.put("logoutTime", new Date());
		params.put("status", "0");
		params.put("userId", user.getUserId());
		params.put("Status", "1");

		return baseDao.updateBySql(sql, params);

	}

}
