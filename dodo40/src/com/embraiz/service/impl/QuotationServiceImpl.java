package com.embraiz.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.dao.BaseDao;
import com.embraiz.model.Currency;
import com.embraiz.model.ObjLog;
/*import com.embraiz.model.ObjLog;*/
import com.embraiz.model.Page;
import com.embraiz.model.Quotation;
import com.embraiz.model.QuotationFooter;
import com.embraiz.model.QuotationItem;
import com.embraiz.model.QuotationTemplate;
import com.embraiz.model.User;
import com.embraiz.service.ObjLogService;
import com.embraiz.service.QuotationService;
import com.embraiz.util.BeanTransformerAdapter;
import com.embraiz.util.StringFormat;

@Service
@Transactional
public class QuotationServiceImpl implements QuotationService {

	@Resource
	private BaseDao baseDao;
	@Resource
	private ObjLogService objLogService;

	@Override
	public int insert(Quotation quotation) {
		quotation.setCreateDate(new Date());
		int quotationId = 0;
		final Quotation quotationadd = quotation;
		quotationId = (Integer) baseDao.save(quotationadd);
		return quotationId;
	}

	@Override
	public int insertItem(QuotationItem quotationItem) {
		quotationItem.setCreateTime(new Date());
		int quotationItemId = 0;
		final QuotationItem quotationItemadd = quotationItem;
		quotationItemId = (Integer) baseDao.save(quotationItemadd);
		return quotationItemId;
	}

	@Override
	public int insertFooter(QuotationFooter quotationFooter) {
		int quotationFooterId = 0;
		quotationFooterId = (Integer) baseDao.save(quotationFooter);
		return quotationFooterId;
	}

	/**
	 * 全部修改Quotation状态改为0
	 */
	@Override
	public int deleteData(String ids, User user) {
		int status = 0;// 删除多少条数据
		String quotionIdStr[] = ids.split(",");
		if (ids != null) {
			for (int i = 0; i < quotionIdStr.length; i++) {
				ObjLog objlog = new ObjLog();
				objlog.setObjId(Integer.parseInt(quotionIdStr[i]));
				objlog.setUserId(user.getUserId());
				objlog.setDescription("Status from 1 to 0");
				objlog.setModuleName("Quotation");
				objlog.setConfId(0);
				objlog.setFieldName("status");
				objLogService.createLog(objlog);
			}
			String sql = "update t_quotation set status=0  where quotation_id in (" + ids + ")";
			String sqlobj = "update obj set status=0 where obj_id in (" + ids + ")";
			status = baseDao.updateBySql(sql, null);
			baseDao.updateBySql(sqlobj, null);
		}
		return status;
	}

	@Override
	public int deleteItem(String ids, User user) {
		int status = 0;// 删除多少条数据
		String quotionItemIdStr[] = ids.split(",");
		if (ids != null) {
			for (int i = 0; i < quotionItemIdStr.length; i++) {
				ObjLog objlog = new ObjLog();
				objlog.setObjId(Integer.parseInt(quotionItemIdStr[i]));
				objlog.setUserId(user.getUserId());
				objlog.setDescription("Delete QuotationItem");
				objlog.setModuleName("QuotationItem");
				objlog.setConfId(0);
				objlog.setFieldName("");
				objLogService.createLog(objlog);
			}
			String sql = "delete from t_obj_quotation_item  where title_id in (" + ids + ")";
			String sqlobj = "delete from obj  where obj_id in (" + ids + ")";
			status = baseDao.updateBySql(sql, null);
			baseDao.updateBySql(sqlobj, null);
		}
		return status;
	}

	@Override
	public Map<String, Object> seleteQuotationList(String searchForm, String sortBy, String sortOrder, Page page) {
		Map<String, Object> params = new HashMap<String, Object>();
		String sql = "select * from v_quotation where 1=1 ";
		String sqlCount = "select count(*) from v_quotation where 1=1 ";
		if (searchForm != null && !"".equals(searchForm)) {
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			if (searchJson.get("clientId") != null && !"".equals(searchJson.get("clientId"))) {
				sql = sql + "and clientId=:clientId ";
				sqlCount = sqlCount + "and clientId=:clientId";
				params.put("clientId", searchJson.get("clientId"));
			}
			if (searchJson.get("contactId") != null && !"".equals(searchJson.get("contactId"))) {
				sql = sql + "and contactId=:contactId ";
				sqlCount = sqlCount + "and contactId=:contactId";
				params.put("contactId", searchJson.get("contactId"));
			}
			if (searchJson.get("status") != null && !"".equals(searchJson.get("status"))) {
				sql = sql + "and status=:status ";
				sqlCount = sqlCount + "and status=:status";
				params.put("status", searchJson.get("status"));
			}
			if (searchJson.get("totalCost") != null && !"".equals(searchJson.get("totalCost"))) {
				sql = sql + "and totalCost=:totalCost ";
				sqlCount = sqlCount + "and totalCost=:totalCost";
				params.put("totalCost", searchJson.get("totalCost"));
			}
			if (searchJson.get("discount") != null && !"".equals(searchJson.get("discount"))) {
				sql = sql + "and discount=:discount ";
				sqlCount = sqlCount + "and discount=:discount";
				params.put("discount", searchJson.get("discount"));
			}
			if (searchJson.get("expiryDate") != null && !"".equals(searchJson.get("expiryDate"))) {
				sql = sql + "and expiryDate=:expiryDate ";
				sqlCount = sqlCount + "and expiryDate=:expiryDate";
				params.put("expiryDate", searchJson.get("expiryDate"));
			}
			if (searchJson.get("title") != null && !"".equals(searchJson.get("title"))) {
				sql = sql + " and title like '%" + searchJson.get("title").toString() + "%' ";
				sqlCount = sqlCount + " and title like '%" + searchJson.get("title").toString() + "%' ";
			}
			if (searchJson.get("currency") != null && !"".equals(searchJson.get("currency"))) {
				sql = sql + "and currency=:currency ";
				sqlCount = sqlCount + "and currency=:currency";
				params.put("currency", searchJson.get("currency"));
			}
			if (searchJson.get("exchangeRate") != null && !"".equals(searchJson.get("exchangeRate"))) {
				sql = sql + "and exchangeRate=:exchangeRate ";
				sqlCount = sqlCount + "and exchangeRate=:exchangeRate";
				params.put("exchangeRate", searchJson.get("exchangeRate"));
			}
			if (searchJson.get("localCurrency") != null && !"".equals(searchJson.get("localCurrency"))) {
				sql = sql + "and localCurrency=:localCurrency ";
				sqlCount = sqlCount + "and localCurrency=:localCurrency";
				params.put("localCurrency", searchJson.get("localCurrency"));
			}
			if (searchJson.get("createDateForm") != null && !"".equals(searchJson.get("createDateForm"))) {
				sql = sql + "and createDate>='" + searchJson.get("createDateForm") + "'";
				sqlCount = sqlCount + "and >='" + searchJson.get("createDateForm") + "'";
			}
			if (searchJson.get("createDateTo") != null && !"".equals(searchJson.get("createDateTo"))) {
				sql = sql + "and createDate<='" + searchJson.get("createDateTo") + "'";
				sqlCount = sqlCount + "and <='" + searchJson.get("createDateTo") + "'";
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
		List<Object> data = baseDao.getList(params, sql, Quotation.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", data);
		return map;
	}

	@Override
	public Map<String, Object> getQuotationFooterList(String searchForm, String sortBy, String sortOrder, Page page) {
		Map<String, Object> params = new HashMap<>();
		String sql = "select * from v_quotation_footer where 1=1 ";
		String sqlCount = "select count(*) from v_quotation_footer where 1=1";
		if (searchForm != null && !"".equals(searchForm)) {
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			if (searchJson.get("objId") != null && !"".equals(searchJson.get("objId"))) {
				sql = sql + "and quotation_id=:quotation_id ";
				sqlCount = sqlCount + "and quotation_id=:quotation_id";
				params.put("quotation_id", searchJson.get("objId"));
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
		List<Object> data = baseDao.getList(params, sql, QuotationFooter.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", data);

		return map;
	}

	@Override
	public Map<String, Object> getQuotationItemList(String searchForm, String sortBy, String sortOrder, Page page) {
		Map<String, Object> params = new HashMap<>();
		String sql = "select * from v_quotation_item where parentId=:parentId ";
		String sqlCount = "select count(*) from v_quotation_item where parentId=:parentId";

		if (searchForm != null && !"".equals(searchForm)) {
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			if (searchJson.get("parentId") != null && !"".equals(searchJson.get("parentId"))) {
				sql = sql + "and parentId=:parentId ";
				sqlCount = sqlCount + "and parentId=:parentId";
				params.put("parentId", searchJson.get("parentId"));
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
		List<Object> data = baseDao.getList(params, sql, QuotationItem.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", data);

		return map;
	}

	/**
	 * 生成一个QuotationNo 规则:Q+YYYY+MM+NO
	 * 
	 * @return
	 */
	@Override
	public String quotationNo() {
		String date = StringFormat.formatData(new Date());
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		String quotationno = "";
		String quNo = "Q" + format.format(new Date());

		String sql = "select quotation_id,right(quotation_no,4) as quotation_no from t_quotation " + "where left(quotation_no,7)='" + quNo
				+ "' order by quotation_id desc limit 0,1";
		List<Map<String, Object>> quot = baseDao.getList(sql, null);

		String no1 = "0000";
		if (quot.size() > 0) {
			no1 = quot.get(0).get("quotation_no").toString();
		}
		int no = Integer.valueOf(no1).intValue() + 1;
		if (no == 0) {
			no = 1;
		}
		if (no < 10) {
			quotationno += "000" + no;
		} else if (no >= 10 && no < 100) {
			quotationno += "00" + no;
		} else if (no >= 100 && no < 1000) {
			quotationno += "0" + no;
		} else {
			quotationno += no;
		}
		String[] i = date.split("-");
		quotationno = "Q" + i[0] + i[1] + quotationno;
		return quotationno;
	}

	@Override
	public List<Object> getQuotationById(int quotationId) {
		String sql = "select * from v_quotation where 1=1  and quotationId=:quotationId ";
		Map<String, Object> params = new HashMap<>();
		params.put("quotationId", quotationId);
		return baseDao.getList(params, sql, Quotation.class);
	}

	@Override
	public List<Object> getQuotationItemsByParentId(int quotationId) {
		Map<String, Object> params = new HashMap<>();
		String sql = "select * from v_quotation_item where 1=1 and  parent_id=:parent_id order by sort";
		params.put("parent_id", quotationId);
		return baseDao.getList(params, sql, QuotationItem.class);
	}

	@Override
	public List<Object> getQuotationFootersByParentId(int quotationId) {
		Map<String, Object> params = new HashMap<>();
		String sql = "select * from t_quotation_footer where 1=1 and  t_quotation_footer.quotation_id=:quotation_id ";
		params.put("quotation_id", quotationId);
		return baseDao.getList(params, sql, QuotationFooter.class);
	}

	@Override
	public BigDecimal getOriginalTotalByQuotationId(Integer quotationId) {
		Map<String, Object> params = new HashMap<>();
		String sql = "select sum(v_quotation_item.cost) AS cost from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
		params.put("parent_id", quotationId);
		List<Map<String, Object>> result = baseDao.getList(sql, params);
		if (result.get(0).get("cost").toString() != null && !"".equals(result.get(0).get("cost").toString())) {
			return new BigDecimal(result.get(0).get("cost").toString());
		} else {
			return new BigDecimal(0);
		}
	}

	@Override
	public BigDecimal getSubTotalByQuotationId(Integer quotationId) {
		Map<String, Object> params = new HashMap<>();
		String sql = "select sum(v_quotation_item.sub_total) AS sub_total from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
		params.put("parent_id", quotationId);
		List<Map<String, Object>> result = baseDao.getList(sql, params);
		if (result.get(0).get("sub_total").toString() != null && !"".equals(result.get(0).get("sub_total").toString())) {
			return new BigDecimal(result.get(0).get("sub_total").toString());
		} else {
			return new BigDecimal(0);
		}

	}

	@Override
	public BigDecimal getDiscountByQuotationId(Integer quotationId) {
		Map<String, Object> params = new HashMap<>();
		String sql = "select sum(v_quotation_item.discount) AS discount  from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
		params.put("parent_id", quotationId);
		Map<String, Object> result = baseDao.getObject(sql, params);
		if (result.get("discount") != null && !"".equals(result.get("discount"))) {
			return new BigDecimal(result.get("discount").toString());
		} else {
			return new BigDecimal(0);
		}
	}

	@Override
	public Map<String, Object> getcurrency() {
		String sql = "select id,name from t_currency order by sort asc ";
		String sqlCount = "select count(*) from t_currency ";
		int listCount = baseDao.getCountBySql(sqlCount.toString(), null);
		List<Object> list = baseDao.getList(null, sql, Currency.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", list);
		return map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Quotation> getexportList(String searchForm) {
		Map<String, Object> params = new HashMap<>();
		String sql = " SELECT v_quotation.* FROM v_quotation WHERE 1 = 1 " + " AND (v_quotation. STATUS >= 6 AND v_quotation. STATUS <= 9) "
				+ " AND (v_quotation.balance IS NULL OR v_quotation.balance <> 0) ";
		if (searchForm != null && !"".equals(searchForm)) {
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			if (searchJson.get("title") != null && !"".equals(searchJson.get("title"))) {
				sql = sql + " and title like '%" + searchJson.get("title").toString() + "%' ";
			}
			if (searchJson.get("companyId") != null && !"".equals(searchJson.get("companyId"))) {
				sql = sql + " and companyId=" + searchJson.get("companyId") + " ";
			}
			if (searchJson.get("dateFrom") != null && !"".equals(searchJson.get("dateFrom"))) {
				sql = sql + " and createDate>='" + searchJson.get("dateFrom") + "' ";
			}
			if (searchJson.get("dateTo") != null && !"".equals(searchJson.get("dateTo"))) {
				sql = sql + " and createDate<='" + searchJson.get("dateTo") + "' ";
			}
		}

		sql = sql + " ORDER BY quotationId DESC ";

		Query query = baseDao.getSessionFactory().getCurrentSession().createSQLQuery(sql)
				.setResultTransformer(new BeanTransformerAdapter(Quotation.class));

		if (params != null && params.size() > 0) {
			query.setProperties(params);
		}
		return query.list();
	}

	@Override
	public void updateTotalCost(String obj_id) {
		Map<String, Object> params = new HashMap<>();
		BigDecimal totalCost = null;
		String sql = "";
		sql = "select sum(sub_total) as sub_total from v_quotation_item where  optional=0 and parent_id=:parent_id";
		params.put("parent_id", obj_id);
		Map<String, Object> sub_total = baseDao.getObject(sql, params);

		sql = "select discount from t_quotation where quotation_id=:quotation_id ";
		params.put("quotation_id", obj_id);
		Map<String, Object> quotation = baseDao.getObject(sql, params);

		if (sub_total.get("sub_total") != null && quotation.get("discount") != null) {
			totalCost = new BigDecimal(sub_total.get("sub_total").toString()).setScale(2);
			totalCost = totalCost.subtract(new BigDecimal(quotation.get("discount").toString()));
		} else {
			totalCost = new BigDecimal(sub_total.get("sub_total").toString()).setScale(2);
		}

		sql = "update t_quotation set total_cost=:total_cost where quotation_id=:quotation_id ";
		params.put("total_cost", totalCost);
		baseDao.updateBySql(sql, params);

	}

	@Override
	public int getStatus(String obj_id) {
		String sql = "";
		Map<String, Object> params = new HashMap<>();
		sql = "select is,appstatus from t_quotation where quotation_id=:quotation_id ";
		params.put("quotation_id", obj_id);
		int status = baseDao.getCountBySql(sql, params);
		return status;
	}

	@Override
	public Map<String, Object> getTemplateDatasourse() {
		String sql = "SELECT v.title_id,v.title FROM v_quotation_template AS v WHERE 1 = 1 ";
		String sqlCount = "select count(*) from v_quotation_template ";

		int listCount = baseDao.getCountBySql(sqlCount.toString(), null);
		List<Object> list = baseDao.getList(null, sql, QuotationTemplate.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", list);
		return map;

		// return null;
	}

	@Override
	public int deleteFooter(String ids, User user) {
		int status = 0;// 删除多少条数据
		String quotionItemIdStr[] = ids.split(",");
		if (ids != null) {
			for (int i = 0; i < quotionItemIdStr.length; i++) {
				ObjLog objlog = new ObjLog();
				objlog.setObjId(Integer.parseInt(quotionItemIdStr[i]));
				objlog.setUserId(user.getUserId());
				objlog.setDescription("Delete QuotationFooter");
				objlog.setModuleName("QuotationFooter");
				objlog.setConfId(0);
				objlog.setFieldName("");
				objLogService.createLog(objlog);
			}
			String sql = "delete from t_quotation_footer  where title_id in (" + ids + ")";
			String sqlobj = "delete from obj  where obj_id in (" + ids + ")";
			status = baseDao.updateBySql(sql, null);
			baseDao.updateBySql(sqlobj, null);
		}
		return status;
	}

	@Override
	public Map<String, Object> getDetail(Integer quotationId) {
		Map<String, Object> params = new HashMap<>();
		String sql = "select * from v_quotation where quotationId=:quotationId ";
		params.put("quotationId", quotationId);
		List<Object> data = baseDao.getList(params, sql, Quotation.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", data);
		return map;
	}

}
