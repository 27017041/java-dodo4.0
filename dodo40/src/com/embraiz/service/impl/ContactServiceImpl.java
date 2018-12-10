package com.embraiz.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.dao.BaseDao;
import com.embraiz.model.Company;
import com.embraiz.model.Contact;
import com.embraiz.model.ObjLog;
import com.embraiz.model.Page;
import com.embraiz.service.ContactService;
import com.embraiz.service.ObjLogService;

@Service
@Transactional
public class ContactServiceImpl implements ContactService {

	@Resource
	private BaseDao baseDao;
	@Resource
	private ObjLogService objLogService;

	// 新增
	@Override
	public int insert(Contact contact) {
		contact.setStatus(1);
		contact.setCreateDate(new Date());
		int contactId = 0;
		final Contact contactadd = contact;
		contactId = (Integer) baseDao.save(contactadd);
		return contactId;
	}

	// delete
	@Override
	public int deleteData(String ids, Integer userId) {
		int status = 0;// 删除多少条数据
		String contactIdStr[] = ids.split(",");
		if (ids != null) {
			for (int i = 0; i < contactIdStr.length; i++) {
				ObjLog objlog = new ObjLog();
				objlog.setObjId(Integer.parseInt(contactIdStr[i]));
				objlog.setUserId(userId);
				objlog.setDescription("Delete Contact");
				objlog.setModuleName("Contact");
				objlog.setConfId(0);
				objLogService.createLog(objlog);
			}					
		}	
		String sql = "update obj_contact set status=0  where contact_id in ("+ids+")";
		String sqlobj = "update obj set status=0 where obj_id in ("+ids+")";		
		status = baseDao.updateBySql(sql, null);
		baseDao.updateBySql(sqlobj, null);
		return status;
	}

	/**
	 * 查询
	 */
	@Override
	public Map<String, Object> seleteContactList(String searchForm, String sortBy, String sortOrder, Page page) {
		Map<String, Object> params = new HashMap<String, Object>();
		String sql = "select * from obj_contact where 1=1 ";
		String sqlCount = "select count(*) from obj_contact where 1=1 ";

		if (searchForm != null && !"".equals(searchForm)) {
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			if (searchJson.get("contactName") != null && !"".equals(searchJson.get("contactName"))) {
				sql = sql + " and contact_name like '%" + searchJson.get("contactName").toString() + "%' ";
				sqlCount = sqlCount + " and contact_name like '%" + searchJson.get("contactName").toString() + "%' ";
			}
			if (searchJson.get("generalPhone") != null && !"".equals(searchJson.get("generalPhone"))) {
				sql = sql + "and general_phone like '%" + searchJson.get("generalPhone").toString() + "%' ";
				sqlCount = sqlCount + "and general_phone like '%" + searchJson.get("generalPhone").toString() + "%' ";
			}
			if (searchJson.get("Area") != null && !"".equals(searchJson.get("Area"))) {
				sql = sql + "and area like '%" + searchJson.get("Area").toString() + "%' ";
				sqlCount = sqlCount + "and area like '%" + searchJson.get("Area").toString() + "%' ";
			}
		}
		// 排序
		if (!("").equals(sortBy) && !("").equals(sortOrder)) {
			sql = sql + " order by " + sortBy + " " + sortOrder;
		}
		// 分页
		sql = sql + " limit " + page.getStart() + " , " + page.getCount();

		// 获得行数
		int listCount = baseDao.getCountBySql(sqlCount.toString(), params);
		List<Object> data = baseDao.getList(params, sql, Contact.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", data);
		return map;
	}

	@Override
	public Map<String, Object> getAllContactName() {
		String sql = "select contact_id,contact_name from obj_contact where status=1 ";
		String sqlCount = "select ccontact_id,contact_name from obj_contact where status=1 ";

		int listCount = baseDao.getCountBySql(sqlCount.toString(), null);
		List<Object> list = baseDao.getList(null, sql, Contact.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", list);
		return map;
	}

	@Override
	public Map<String, Object> getContactDetail(Integer contactId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Contact contact = (Contact) baseDao.getObject(Contact.class, contactId);
		map.put("data", contact);
		return map;
	}

	@Override
	public List<Map<String, Object>> getexportList(String searchForm) {
		String sql = "select  v.*   from v_obj_contact as v  where 1=1 ";

		if (searchForm != null && !"".equals(searchForm)) {
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			if (searchJson.get("contact") != null && !"".equals(searchJson.get("contact"))) {
				sql = sql + " and contactName like '%" + searchJson.get("contact").toString() + "%' ";
			}
			if (searchJson.get("email") != null && !"".equals(searchJson.get("email"))) {
				sql = sql + " and email like '%" + searchJson.get("email").toString() + "%' ";
			}
			if (searchJson.get("creator") != null && !"".equals(searchJson.get("creator"))) {
				sql = sql + " and createBy like '%" + searchJson.get("creator").toString() + "%' ";
			}
		}
		return baseDao.getList(sql, null);

	}
}
