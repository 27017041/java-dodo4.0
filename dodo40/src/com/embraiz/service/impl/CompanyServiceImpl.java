package com.embraiz.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.model.Company;
import com.embraiz.model.ObjLog;
import com.embraiz.model.User;
import com.embraiz.service.ComPanyService;
import com.embraiz.service.ObjLogService;

@Service
@Transactional
public class CompanyServiceImpl implements ComPanyService {

	@Resource
	private BaseDao baseDao;
	@Resource
	private ObjLogService objLogService;

	@Override
	public int insert(Company company) {
		int companyId = 0;
		companyId = (Integer) baseDao.save(company);
		return companyId;
	}

	@Override
	public int deleteData(String ids, User user) {
		int status = 0;
		String companyIdStr[] = ids.split(",");
		if (ids != null) {
			String newids = ids.substring(0, ids.length());
			for (int i = 0; i < companyIdStr.length; i++) {
				ObjLog objlog = new ObjLog();
				objlog.setObjId(Integer.parseInt(companyIdStr[i]));
				objlog.setUserId(user.getUserId());
				objlog.setDescription("Delete Company");
				objlog.setModuleName("Company");
				objlog.setConfId(0);
				objLogService.createLog(objlog);
			}
			String sql = "update obj_company set status=0  where company_id in (" + newids + ")";
			String sqlobj = "update obj set status=0 where obj_id in (" + newids + ")";
			status = baseDao.updateBySql(sql, null);
			baseDao.updateBySql(sqlobj, null);
		}
		return status;
	}

	@Override
	public Map<String, Object> getCompanyDetail(Integer companyId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Company company = (Company) baseDao.getObject(Company.class, companyId);
		map.put("data", company);
		return map;
	}

	
	@Override
	public Map<String, Object> getAllCompanyName() {
		String sql = "select company_id,company_name from obj_company where status=1 ";
		String sqlCount = "select count(*) from obj_company where status=1 ";

		int listCount = baseDao.getCountBySql(sqlCount.toString(), null);
		List<Object> list = baseDao.getList(null, sql, Company.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", list);
		return map;
	}

}
