package com.embraiz.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.dao.BaseDao;
import com.embraiz.model.Lead;
import com.embraiz.model.Obj;
import com.embraiz.model.ObjLog;
import com.embraiz.model.RefList;
import com.embraiz.model.User;
import com.embraiz.service.LeadService;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.ObjService;
import com.embraiz.util.ReadExcel;

@Service
@Transactional
public class LeadServiceImpl implements LeadService {

	@Resource
	private BaseDao baseDao;
	@Resource
	private ObjLogService objLogService;
	@Resource
	private ObjService objService;

	@Override
	public int insert(Lead lead) {
		int leadId = 0;
		leadId = (Integer) baseDao.save(lead);
		return leadId;
	}

	@Override
	public int deleteData(String ids, User user) {
		int status = 0;
		String leadIdStr[] = ids.split(",");
		if (ids != null) {
			String newids = ids.substring(0, ids.length());
			for (int i = 0; i < leadIdStr.length; i++) {
				ObjLog objlog = new ObjLog();
				objlog.setObjId(Integer.parseInt(leadIdStr[i]));
				objlog.setUserId(user.getUserId());
				objlog.setDescription("Delete Lead");
				objlog.setModuleName("Lead");
				objlog.setConfId(0);
				objLogService.createLog(objlog);
			}
			String sql = "update t_lead set status=0  where lead_id in (" + newids + ")";
			String sqlobj = "update obj set status=0 where obj_id in (" + newids + ")";
			status = baseDao.updateBySql(sql, null);
			baseDao.updateBySql(sqlobj, null);
		}
		return status;
	}

	@Override
	public List<Map<String, Object>> getexportList(String searchForm) {
		String sql = "SELECT v.* FROM v_lead AS v WHERE 1 = 1 AND (find_in_set(v.creator, '1') OR find_in_set(v.assignTo, '1')) "
				+ "  AND (v.assignExpiryTime >= '2018-05-22'OR v.assignExpiryTime IS NULL)";
		if (searchForm != null && !"".equals(searchForm)) {
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			if (searchJson.get("leadName") != null && !"".equals(searchJson.get("leadName"))) {
				sql = sql + " and payment_no like '%" + searchJson.get("leadName").toString() + "%' ";
			}
			if (searchJson.get("clientName") != null && !"".equals(searchJson.get("clientName"))) {
				sql = sql + " and clientName like '%" + searchJson.get("leadName").toString() + "%' ";
			}
			if (searchJson.get("clientPhone") != null && !"".equals(searchJson.get("clientPhone"))) {
				sql = sql + " and clientPhone like '%" + searchJson.get("clientPhone").toString() + "%' ";
			}
			if (searchJson.get("clientMail") != null && !"".equals(searchJson.get("clientMail"))) {
				sql = sql + " and clientMail like '%" + searchJson.get("clientMail").toString() + "%' ";
			}
			if (searchJson.get("marketingCode") != null && !"".equals(searchJson.get("marketingCode"))) {
				sql = sql + " and marketingCode like '%" + searchJson.get("marketingCode").toString() + "%' ";
			}
		}
		return baseDao.getList(sql, null);
	}

	@Override
	public boolean insertexportFile(String name, MultipartFile file) {
		boolean b = false;
		int objkeyId = 0;

		// 创建处理EXCEL
		ReadExcel readExcel = new ReadExcel();
		// 解析excel，获取Lead信息集合。
		List<Lead> leadList = readExcel.getExcelInfo(name, file);

		if (leadList != null) {
			b = true;
		}
		// 迭代添加
		for (Lead lead : leadList) {
			// 插入OBJ表
			Obj obj = new Obj();
			obj.setObjTitle(lead.getLeadName());
			obj.setObjDesc(lead.getLeadName());
			obj.setStatus(1);
			obj.setKeyword(lead.getLeadName());
			obj.setObjTypeId(20);
			obj.setOwnerId(1);
			objkeyId = objService.insertObj(obj);

			lead.setLeadId(objkeyId);
			baseDao.save(lead);
		}
		return b;

	}

	@Override
	public Map<String, Object> getArea() {
		String sql = "SELECT list_id,list_name FROM t_ref_location ";
		String sqlCount = "SELECT count(*) FROM t_ref_location ";
		int listCount = baseDao.getCountBySql(sqlCount.toString(), null);
		List<Object> list = baseDao.getList(null, sql, RefList.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", list);
		return map;
	}

	@Override
	public Map<String, Object> getReturnMail() {
		String sql = "SELECT list_id,list_name FROM t_ref_return_mail ";
		String sqlCount = "SELECT count(*) FROM t_ref_return_mail ";
		int listCount = baseDao.getCountBySql(sqlCount.toString(), null);
		List<Object> list = baseDao.getList(null, sql, RefList.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", list);
		return map;
	}

	@Override
	public Map<String, Object> getOptout() {
		String sql = "SELECT list_id,list_name FROM t_ref_opt_out ";
		String sqlCount = "SELECT count(*) FROM t_ref_opt_out ";
		int listCount = baseDao.getCountBySql(sqlCount.toString(), null);
		List<Object> list = baseDao.getList(null, sql, RefList.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", list);
		return map;
	}

}
