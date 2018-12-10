package com.embraiz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.dao.BaseDao;
import com.embraiz.model.ObjLog;
import com.embraiz.model.Page;
import com.embraiz.model.Role;
import com.embraiz.model.User;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.RoleService;
import com.embraiz.util.StringFormat;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

	@Resource
	private BaseDao baseDao;
	@Resource
	private ObjLogService objLogService;

	@Override
	public List<Role> getAllRole() {
		String hql = "from Role where status = 1";
		Map<String, Object> params = new HashMap<String, Object>();

		List<Role> roleList = new ArrayList<Role>();

		List<Object> objectList = baseDao.getListByHql(hql, params);
		for (int i = 0; i < objectList.size(); i++) {
			Role role = (Role) objectList.get(i);
			roleList.add(role);
		}

		return roleList;
	}

	@Override
	public List<Role> getRoleByEntity(int entityId) {
		String hql = "from Role where status = 1 and entityId = :entityId ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("entityId", entityId);

		List<Role> roleList = new ArrayList<Role>();

		List<Object> objectList = baseDao.getListByHql(hql, params);
		for (int i = 0; i < objectList.size(); i++) {
			Role role = (Role) objectList.get(i);
			roleList.add(role);
		}
		return roleList;
	}

	@Override
	public int delete(String roleIds, User user) {
		int status = 0;
		String checkSql = "select count(*) from obj_role inner join obj_user on find_in_set(obj_role.role_id,obj_user.role_id) where obj_role.role_id in ("
				+ roleIds + ")";

		Map<String, Object> params = new HashMap<String, Object>();
		int num = baseDao.getCountBySql(checkSql, params);

		if (num > 0) {
			status = -1;
		} else {
			String sql = "delete from obj_role where role_id in (" + roleIds + ")";
			status = baseDao.deleteBySql(sql, params);
			try {
				if (roleIds != null && !"".equals(roleIds)) {
					String[] roleIdArrays = roleIds.split(",");
					for (int i = 0; i < roleIdArrays.length; i++) {
						int keyId = Integer.parseInt(roleIdArrays[i]);
						ObjLog objLog = new ObjLog();
						objLog.setObjId(keyId);
						objLog.setUserId(user.getUserId());
						objLog.setDescription("Delete Role");
						objLog.setModuleName("Role");
						objLog.setConfId(0);
						objLogService.createLog(objLog);
					}
				}

			} catch (Exception e) {
			}
		}

		return status;
	}

	@Override
	public int insert(Role role) {
		int roleId = 0;
		final Role roleadd = role;
		roleId = (Integer) baseDao.save(roleadd);
		return roleId;
	}

	@Override
	public int update(Role role) {
		int status = 0;
		String sql = "update obj_role set role_name = :roleName, status = :status where role_id = :roleId ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleName", role.getRoleName());
		params.put("status", role.getStatus());
		params.put("roleId", role.getRoleId());
		status = baseDao.updateBySql(sql, params);
		return status;
	}

	@Override
	public Role checkRole(String roleName) {
		String hql = "from Role where roleName = :roleName ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleName", roleName);
		Role role = (Role) baseDao.getObjectByHql(hql, params);
		return role;
	}

	@Override
	public Role getRoleData(int roleId) {
		Role role = (Role) baseDao.getObject(Role.class, roleId);

		if (role.getStatus() == 1) {
			role.setStatusValue("Active");
		} else {
			role.setStatusValue("Inactive");
		}

		return role;
	}

	@Override
	public Map<String, Object> getRoleList(Role searchRole, Page page, String sortBy, String sortOrder) {
		String sql = "select * from obj_role where 1=1";
		String totalSql = "select count(1) from obj_role where 1=1";

		if (searchRole.getRoleName() != null && !searchRole.getRoleName().equals("")) {
			sql = sql + " and role_name like '%" + searchRole.getRoleName() + "%'";
			totalSql = totalSql + " and role_name like '%" + searchRole.getRoleName() + "%'";
		}
		if (searchRole.getStatus() != null) {
			sql = sql + " and status = " + searchRole.getStatus();
			totalSql = totalSql + " and status = " + searchRole.getStatus();
		}
		if (!sortBy.equals("") && !sortOrder.equals("")) {
			sql = sql + " order by " + sortBy + " " + sortOrder;
		}
		sql = sql + " limit " + page.getStart() + " , " + page.getCount();

		Map<String, Object> params = new HashMap<String, Object>();
		List<Role> list = new ArrayList<Role>();
		int size = 0;

		List<Object> objectList = baseDao.getList(params, sql, Role.class);
		for (int i = 0; i < objectList.size(); i++) {
			Role role = (Role) objectList.get(i);

			if (role.getStatus() == 1) {
				role.setStatusValue("Active");
			} else {
				role.setStatusValue("Inactive");
			}

			list.add(role);
		}
		size = baseDao.getCountBySql(totalSql, params);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", size);
		map.put("recordsFiltered", size);
		map.put("data", list);
		return map;
	}

	@Override
	public Map<String, Object> getList(String searchForm, Page page, String sortBy, String sortOrder, String sqlWhere, String lang) {
		List<Object> list = null;
		int size = 0;

		String sql = " select obj_role.role_id AS roleId, obj_role.role_name AS roleName, obj_role.entity_id AS entityId, obj_role.`status`, "
				+ " ref.option_name_" + lang + " AS `statusValue` "
				+ " from obj_role LEFT JOIN ref_option ref on ref.option_id = obj_role.`status` where 1=1  ";
		String totalSql = "select count(1) from obj_role LEFT JOIN ref_option ref on ref.option_id = obj_role.`status` where 1=1  ";

		if (searchForm != null && !"".equals(searchForm)) {
			JSONObject searchJson = JSONObject.parseObject(searchForm);

			if (searchJson.get("roleName") != null && !"".equals(searchJson.get("roleName"))) {
				sql = sql + " and obj_role.role_name like '%" + searchJson.get("roleName").toString() + "%' ";
				totalSql = totalSql + " and obj_role.role_name like '%" + searchJson.get("roleName").toString() + "%' ";
			}

			if (searchJson.get("status") != null && !"".equals(searchJson.get("status"))) {
				sql = sql + " and obj_role.status = " + searchJson.get("status").toString();
				totalSql = totalSql + " and obj_role.status = " + searchJson.get("status").toString();
			}
		}

		if (sqlWhere != null && !sqlWhere.equals("")) {
			sql = sql + sqlWhere;
			totalSql = totalSql + sqlWhere;
		}

		if (!sortBy.equals("") && !sortOrder.equals("")) {
			sortBy = StringFormat.fieldNameConvertWithLine(sortBy);
			sql = sql + " order by " + sortBy + " " + sortOrder;
		}

		sql = sql + " limit " + page.getStart() + " , " + page.getCount();

		list = baseDao.getList(null, sql, Role.class);
		size = baseDao.getCountBySql(totalSql, null);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", size);
		map.put("recordsFiltered", size);
		map.put("data", list);
		return map;
	}
}
