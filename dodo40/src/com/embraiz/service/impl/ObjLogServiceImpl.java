package com.embraiz.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.model.ContractType;
import com.embraiz.model.ObjLog;
import com.embraiz.model.Page;
import com.embraiz.service.ObjLogService;

@Service
@Transactional
public class ObjLogServiceImpl implements ObjLogService {

	@Resource
	private BaseDao baseDao;

	@Override
	public void createLog(ObjLog objLog) {
		objLog.setActionDate(new Date());
		baseDao.save(objLog);
	}

	@Override
	public Map<String, Object> objLogList(String moduleName, String sortBy, String sortOrder, Page page) {
		Map<String, Object> params = new HashMap<String, Object>();
		String sql = "select * from obj_log where 1=1 ";
		String sqlCount = "select count(*) from obj_log where 1=1 ";

		if (moduleName != null && !"".equals(moduleName)) {
			sql = sql + "and module_name='" + moduleName + "' ";
			sqlCount = sqlCount + "and module_name='" + moduleName + "' ";
			params.put("module_name", moduleName);
		}

		// 排序
		if (!("").equals(sortBy) && !("").equals(sortOrder)) {
			sql = sql + " order by " + sortBy + " " + sortOrder;
		}
		// 分页
		sql = sql + " limit " + page.getStart() + " , " + page.getCount();

		// 获得行数
		int listCount = baseDao.getCountBySql(sqlCount.toString(), params);

		List<Map<String, Object>> data = baseDao.getList(sql, params);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", data);

		return map;
	}

	@Override
	public Map<String, Object> getHistory(int objId, String moduleName) {
		Map<String, Object> params = new HashMap<>();

		String sql = "SELECT user_name,description,action_date,module_name,field_name FROM v_obj_log WHERE obj_id=:obj_id ";
		String sqlCount = "select count(*) from v_obj_log WHERE obj_id=:obj_id ";
		params.put("obj_id", objId);
		if (moduleName != null && !"".equals(moduleName)) {
			sql += "and module_name=:module_name ";
			sqlCount += "and module_name=:module_name ";
			params.put("module_name", moduleName);
		}
		List<Object> list = baseDao.getList(params, sql, ContractType.class);
		int listCount = baseDao.getCountBySql(sqlCount.toString(), params);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", list);

		return map;
	}

}
