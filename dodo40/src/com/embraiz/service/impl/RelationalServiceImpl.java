package com.embraiz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.model.Page;
import com.embraiz.model.Relational;
import com.embraiz.model.RelationalItem;
import com.embraiz.service.RelationalService;
import com.embraiz.util.StringFormat;

@Service
@Transactional
public class RelationalServiceImpl implements RelationalService {

	@Resource
	private BaseDao baseDao;

	@Override
	public Map<String, Object> getRelational(Integer keyId, String moduleName, String relationalName, String sortBy, String sortOrder, Page pageBo,
			int start, int length) {

		Map<String, Object> params = new HashMap<>();
		Map<String, Object> map = new HashMap<String, Object>();

		// 获取field
		String sql = " SELECT core_relational_item.field_name FROM core_relational "
				+ " LEFT JOIN core_relational_item ON core_relational_item.relational_id = core_relational.id "
				+ " WHERE core_relational.relational_name=:relational_name ";
		params.put("relational_name", relationalName);
		List<Object> fieldNameList = baseDao.getList(params, sql, RelationalItem.class);
		map.put("fieldNameList", fieldNameList);

		sql = "select relational_name,table_name,table_field from core_relational WHERE module_name=:module_name ";
		params.put("module_name", moduleName);
		List<Map<String, Object>> result = baseDao.getList(sql, params);

		if (result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				if (relationalName.equals(result.get(i).get("relational_name").toString())) {
					String className = StringFormat.ConvertclassName(result.get(i).get("relational_name").toString());
					String tableName = result.get(i).get("table_name").toString();
					String tableField = result.get(i).get("table_field").toString();
					Page pageBo1 = new Page();
					pageBo1.setCount(length);
					pageBo1.setStart(start);

					if (sortBy.contains("_")) {
						sortBy = StringFormat.fieldNameConvert(sortBy);
					}
					// 获取Table数据
					sql = "select * from " + tableName + " where " + tableField + " =:tableField  order by " + sortBy + " " + sortOrder + "  limit "
							+ start + "," + length + "";
					String sqlcount = " select count(*) from " + tableName + " where " + tableField + " =:tableField   ";

					params.put("tableField", keyId);
					try {
						Class<?> module = Class.forName("com.embraiz.model." + className);
						Object o = module.newInstance();
						List<Object> data = baseDao.getList(params, sql, o.getClass());
						map.put("data", data);
						map.put("recordsTotal", baseDao.getCountBySql(sqlcount, params));
						map.put("recordsFiltered", baseDao.getCountBySql(sqlcount, params));
						map.put("pageBo", pageBo1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			List<Object> data = new ArrayList<Object>();
			map.put("data", data);
			map.put("recordsTotal", 0);
			map.put("recordsFiltered", 0);
			map.put("pageBo", null);
		}

		return map;
	}

	@Override
	public Map<String, Object> getCoreRelational(String moduleName) {
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> map = new HashMap<String, Object>();
		String sql = " select * from core_relational where 1=1";
		String sqlCount = " select count(*) from core_relational  where 1=1 ";

		if (moduleName != null) {
			sql += " and module_name=:module_name ";
			sqlCount += " and module_name=:module_name ";
			params.put("module_name", moduleName);
		}

		List<Object> data = baseDao.getList(params, sql, Relational.class);
		int listCount = baseDao.getCountBySql(sqlCount.toString(), params);

		String relationalId = "";
		List<Map<String, Object>> result = baseDao.getList(sql, params);
		for (int i = 0; i < result.size(); i++) {
			relationalId += result.get(i).get("id").toString() + ",";
		}

		if (!"".equals(relationalId)) {
			String sql1 = "select * from core_relational_item where relational_id in(" + relationalId.substring(0, relationalId.length() - 1) + ") ";
			List<Object> RelationalItem = baseDao.getList(params, sql1, RelationalItem.class);
			map.put("RelationalItem", RelationalItem);
		}

		map.put("data", data);
		map.put("listCount", listCount);

		return map;
	}

	@Override
	public Map<String, Object> getLinkageModuleList(String viewsName) {
		Map<String, Object> map = new HashMap<String, Object>();

		String sql = "";
		String sqlcount = "";
		if (viewsName != null) {
			sql = "select * from " + viewsName + " ";
			sqlcount = "select count(*) from " + viewsName + " ";
		}

		map.put("data", baseDao.getList(sql, null));
		map.put("listCount", baseDao.getCountBySql(sqlcount, null));

		return map;
	}

	@Override
	public Map<String, Object> getLinkageModuleListById(String viewsName, String keyName, Integer keyId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<>();
		String sql = "";
		String sqlcount = "";
		if (viewsName != null) {
			sql = "select * from " + viewsName + " where 1=1";
			sqlcount = "select count(*) from " + viewsName + " where 1=1";

			if (keyId != null) {
				sql = sql + " and " + keyName + " = :keyId";
				sqlcount = sqlcount + " and " + keyName + " = :keyId";
				params.put("keyId", keyId);
			}

			map.put("data", baseDao.getList(sql, params));
			map.put("listCount", baseDao.getCountBySql(sqlcount, params));
		}

		return map;
	}

}
