package com.embraiz.service.impl;

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
import com.embraiz.model.QuotationItemTemplate;
import com.embraiz.model.User;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.QuotationItemTemplateService;

@Service
@Transactional
public class QuotationItemTemplateServiceImpl implements QuotationItemTemplateService {

	@Resource
	private BaseDao baseDao;
	@Resource
	private ObjLogService objLogService;

	@Override
	public int insert(QuotationItemTemplate quotationItemTemplate) {
		int quotationItemTemplateId = 0;
		final QuotationItemTemplate quotationItemTemplateadd = quotationItemTemplate;
		quotationItemTemplateId = (Integer) baseDao.save(quotationItemTemplateadd);
		return quotationItemTemplateId;
	}

	@Override
	public int deleteData(String ids, User user) {
		int num = 0;// 删除多少条数据
		String quotionItemTemplateIdStr[] = ids.split(",");
		if (ids != null) {
			for (int i = 0; i < quotionItemTemplateIdStr.length; i++) {
				ObjLog objlog = new ObjLog();
				objlog.setObjId(Integer.parseInt(quotionItemTemplateIdStr[i]));
				objlog.setUserId(user.getUserId());
				objlog.setDescription("Delete QuotationItemTemplate");
				objlog.setModuleName("QuotationItemTemplate");
				objlog.setConfId(0);
				objlog.setFieldName("");
				objLogService.createLog(objlog);
			}
			String sql = "delete from t_quotation_item_template  where title_id in (" + ids + ")";
			num = baseDao.updateBySql(sql, null);
		}
		return num;
	}

	@Override
	public Map<String, Object> getQuotationItemTemplateList(String searchForm, String sortBy, String sortOrder, Page pageBo) {
		Map<String, Object> params = new HashMap<String, Object>();
		String sql = "select * from v_quotation_item_template where 1=1 ";
		String sqlCount = "select count(*) from v_quotation_item_template where 1=1 ";
		if (searchForm != null && !"".equals(searchForm)) {
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			if (searchJson.get("title") != null && !"".equals(searchJson.get("title"))) {
				sql = sql + " and title like '%" + searchJson.get("title").toString() + "%' ";
				sqlCount = sqlCount + " and title like '%" + searchJson.get("title").toString() + "%' ";
			}
		}
		// 排序
		if (!("").equals(sortBy) && !("").equals(sortOrder)) {
			sql = sql + " order by " + sortBy + " " + sortOrder;
		}

		// 分页
		sql = sql + " limit " + pageBo.getStart() + " , " + pageBo.getCount();
		// 获得行数
		int listCount = baseDao.getCountBySql(sqlCount.toString(), params);
		List<Object> data = baseDao.getList(params, sql, QuotationItemTemplate.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", data);

		return map;

	}

	@Override
	public Map<String, Object> getDetail(Integer quotationItemTempalteId) {
		Map<String, Object> params = new HashMap<>();
		String sql = "select * from v_quotation_item_template where title_id=:title_id ";
		params.put("title_id", quotationItemTempalteId);
		List<Object> data = baseDao.getList(params, sql, QuotationItemTemplate.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", data);
		return map;
	}

}
