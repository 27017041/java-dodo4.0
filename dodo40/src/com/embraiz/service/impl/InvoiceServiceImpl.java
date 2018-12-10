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
import com.embraiz.model.Company;
import com.embraiz.model.Invoice;
import com.embraiz.model.ObjLog;
import com.embraiz.model.Page;
import com.embraiz.model.Quotation;
import com.embraiz.model.QuotationItem;
import com.embraiz.model.User;
import com.embraiz.service.InvoiceService;
import com.embraiz.service.ObjLogService;
import com.embraiz.util.BeanTransformerAdapter;
import com.embraiz.util.StringFormat;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

	@Resource
	private BaseDao baseDao;
	@Resource
	private ObjLogService objLogService;

	@Override
	public String invoiceNo() {
		String date = StringFormat.formatData(new Date());
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		String invoiceno = "";
		String inNo = "I" + format.format(new Date());
		String sql = "select invoice_id,right(invoice_no,4) as invoice_no from t_invoice" + " where left(invoice_no,7)='" + inNo
				+ "' order by invoice_id desc limit 0,1";
		List<Map<String, Object>> result = baseDao.getList(sql, null);
		String no1 = "0000";
		if (result.size() > 0) {
			no1 = result.get(0).get("invoice_no").toString();
		}
		int no = Integer.valueOf(no1).intValue() + 1;
		if (no == 0) {
			no = 1;
		}
		if (no < 10) {
			invoiceno += "000" + no;
		} else if (no >= 10 && no < 100) {
			invoiceno += "00" + no;
		} else if (no >= 100 && no < 1000) {
			invoiceno += "0" + no;
		} else {
			invoiceno += no;
		}

		String[] i = date.split("-");
		invoiceno = "I" + i[0] + i[1] + invoiceno;
		return invoiceno;

	}

	@Override
	public int insert(Invoice invoice) {
		invoice.setCreateDate(new Date());
		int invoiceId = 0;
		final Invoice Invoiceadd = invoice;
		invoiceId = (Integer) baseDao.save(Invoiceadd);
		return invoiceId;
	}

	@Override
	public List<Object> createInovice(int quotationId) {
		Map<String, Object> params = new HashMap<>();
		String sql = "select * from v_quotation where 1=1  and quotation_id=:quotation_id ";
		params.put("quotation_id", quotationId);
		return baseDao.getList(params, sql, Quotation.class);
	}

	@Override
	public int voidInvoice(String id, User user, String voidRemark) {
		int status = 0;
		if (id != null) {
			String sql = "";
			Map<String, Object> params = new HashMap<>();
			ObjLog objlog = new ObjLog();
			objlog.setObjId(Integer.parseInt(id));
			objlog.setUserId(user.getUserId());
			objlog.setDescription("Status from 1 to 0");
			objlog.setModuleName("Invoice");
			objlog.setConfId(0);
			objlog.setFieldName("status");
			objLogService.createLog(objlog);
			params.put("id", id);
			if (voidRemark != null && !"".equals(voidRemark)) {
				sql = "update t_invoice set status=0,void_remark=" + voidRemark + "  where quotation_id=:id";
			} else {
				sql = "update t_invoice set status=0  where quotation_id=:id";
			}
			String sqlobj = "update t_invoice set status=0 where obj_id=:id";
			status = baseDao.updateBySql(sql, params);
			baseDao.updateBySql(sqlobj, params);
		}
		return status;
	}

	@Override
	public Map<String, Object> selectInvoice(String searchForm, String sortBy, String sortOrder, Page page) {
		Map<String, Object> params = new HashMap<>();
		String sql = "SELECT DISTINCT t_invoice.*, t_invoice.*, ref_list.list_name AS statusName,obj_company.company_name AS companyName,obj_company.company_id,t_quotation.quotation_no "
				+ " FROM t_invoice LEFT JOIN obj_company ON obj_company.company_id = t_invoice.payer LEFT JOIN t_quotation ON t_invoice.invoice_id = t_quotation.invoice_id  LEFT JOIN ref_list ON ref_list.list_id = t_invoice. STATUS ";

		String sqlCount = "SELECT DISTINCT t_invoice.*, t_invoice.*, ref_list.list_name AS statusName,obj_company.company_name AS companyName,obj_company.company_id,t_quotation.quotation_no "
				+ " FROM t_invoice LEFT JOIN obj_company ON obj_company.company_id = t_invoice.payer LEFT JOIN t_quotation ON t_invoice.invoice_id = t_quotation.invoice_id  LEFT JOIN ref_list ON ref_list.list_id = t_invoice. STATUS ";

		if (searchForm != null && !"".equals(searchForm)) {
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			if (searchJson.get("invoiceNo") != null && !"".equals(searchJson.get("invoiceNo"))) {
				sql = sql + " and invoice_no like '%" + searchJson.get("invoiceNo").toString() + "%' ";
				sqlCount = sqlCount + " and invoice_no like '%" + searchJson.get("invoiceNo").toString() + "%' ";
			}
			if (searchJson.get("DateFrom") != null && !"".equals(searchJson.get("DateFrom"))) {
				sql = sql + " and create_date>='" + searchJson.get("DateFrom").toString() + "' ";
				sqlCount = sqlCount + " and create_date>='" + searchJson.get("DateFrom").toString() + "' ";
			}
			if (searchJson.get("DateTo") != null && !"".equals(searchJson.get("DateTo"))) {
				sql = sql + " and create_date<='" + searchJson.get("DateTo").toString() + "' ";
				sqlCount = sqlCount + " and create_date<='" + searchJson.get("DateTo").toString() + "' ";
			}
			if (searchJson.get("quotationNo") != null && !"".equals(searchJson.get("quotationNo"))) {
				sql = sql + " and quotation_no like '%" + searchJson.get("quotationNo").toString() + "%' ";
				sqlCount = sqlCount + " and quotation_no like '%" + searchJson.get("quotationNo").toString() + "%' ";
			}
			if (searchJson.get("companyName") != null && !"".equals(searchJson.get("companyName"))) {
				sql = sql + " and company_name like '%" + searchJson.get("companyName").toString() + "%' ";
				sqlCount = sqlCount + " and company_name like '%" + searchJson.get("companyName").toString() + "%' ";
			}
			if (searchJson.get("AmountFrom") != null && !"".equals(searchJson.get("AmountFrom"))) {
				sql = sql + " and amount>=" + searchJson.get("AmountFrom").toString() + " ";
				sqlCount = sqlCount + " and amount>=" + searchJson.get("AmountFrom").toString() + " ";
			}
			if (searchJson.get("AmountTo") != null && !"".equals(searchJson.get("AmountTo"))) {
				sql = sql + " and amount<='" + searchJson.get("AmountTo").toString() + "' ";
				sqlCount = sqlCount + " and amount<='" + searchJson.get("AmountTo").toString() + "' ";
			}
			if (searchJson.get("Status") != null && !"".equals(searchJson.get("Status"))) {
				sql = sql + " and status='" + searchJson.get("Status").toString() + "' ";
				sqlCount = sqlCount + " and amount='" + searchJson.get("Status").toString() + "' ";
			}

		}
		sql = sql + " GROUP BY t_invoice.invoice_id ";
		sqlCount = sqlCount + " GROUP BY t_invoice.invoice_id ";

		// 排序
		if (!("").equals(sortBy) && !("").equals(sortOrder)) {

			sql = sql + " order by " + StringFormat.fieldNameConvertWithLine(sortBy) + " " + sortOrder;
		}

		// 分页
		sql = sql + " limit " + page.getStart() + " , " + page.getCount();
		// 获得行数
		int listCount = baseDao.getCountBySql(sqlCount.toString(), params);
		List<Object> list = baseDao.getList(params, sql, Company.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", list);
		return map;
	}

	@Override
	public int deleteData(String ids, User user) {
		int status = 0;// 删除多少条数据
		String invoiceIdStr[] = ids.split(",");
		if (ids != null) {
			for (int i = 0; i < invoiceIdStr.length; i++) {
				ObjLog objlog = new ObjLog();
				objlog.setObjId(Integer.parseInt(invoiceIdStr[i]));
				objlog.setUserId(user.getUserId());
				objlog.setDescription("Delete Invoice");
				objlog.setModuleName("Invoice");
				objlog.setConfId(0);
				objLogService.createLog(objlog);
			}
			String sql = "update t_invoice set status=0  where quotation_id in (" + ids + ")";
			String sqlobj = "update obj set status=0 where obj_id in (" + ids + ")";
			status = baseDao.updateBySql(sql, null);
			baseDao.updateBySql(sqlobj, null);
		}
		return status;
	}

	@Override
	public List<Object> getinvoiceById(int invoiceId) {
		String sql = "select * from v_invoice  where invoiceId=:invoiceId";
		Map<String, Object> params = new HashMap<>();
		params.put("invoiceId", invoiceId);
		return baseDao.getList(params, sql, Invoice.class);
	}

	@Override
	public List<Quotation> getByInvoicId(int invoiceId) {
		String sql = " select distinct v_quotation.* from v_quotation "
				+ " inner join t_obj_quotation_invoice on v_quotation.quotationId=t_obj_quotation_invoice.quotation_id "
				+ " and t_obj_quotation_invoice.invoice_id=:invoice_id and v_quotation.status<>4 ";
		Map<String, Object> params = new HashMap<>();
		params.put("invoice_id", invoiceId);

		Query query = baseDao.getSessionFactory().getCurrentSession().createSQLQuery(sql)
				.setResultTransformer(new BeanTransformerAdapter(Quotation.class));

		if (params != null && params.size() > 0) {
			query.setProperties(params);
		}
		return query.list();

	}

	@Override
	public BigDecimal getOriginalTotalByQuotationId(Integer quotationId) {
		String sql = "select sum(v_quotation_item.cost) as cost from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
		Map<String, Object> params = new HashMap<>();
		params.put("parent_id", quotationId);
		List<Map<String, Object>> result = baseDao.getList(sql, params);
		if (result.get(0).get("cost").toString() != null && !"".equals(result.get(0).get("cost").toString())) {
			return new BigDecimal(result.get(0).get("cost").toString());
		} else {
			return new BigDecimal(0);
		}
	}

	@Override
	public BigDecimal getDiscountByQuotationId(Integer quotationId) {
		String sql = "select sum(v_quotation_item.discount) as discount from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
		Map<String, Object> params = new HashMap<>();
		params.put("parent_id", quotationId);
		List<Map<String, Object>> result = baseDao.getList(sql, params);
		if (result.get(0).get("discount").toString() != null && !"".equals(result.get(0).get("discount").toString())) {
			return new BigDecimal(result.get(0).get("discount").toString());
		} else {
			return new BigDecimal(0);
		}
	}

	@Override
	public List<QuotationItem> getQuotationItemsByParentId(String invoiceId) {
		String sql = "select * from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id order by sort";
		Map<String, Object> params = new HashMap<>();
		params.put("parent_id", invoiceId);

		Query query = baseDao.getSessionFactory().getCurrentSession().createSQLQuery(sql)
				.setResultTransformer(new BeanTransformerAdapter(QuotationItem.class));

		if (params != null && params.size() > 0) {
			query.setProperties(params);
		}
		return query.list();
	}

	@Override
	public BigDecimal getSubTotalByQuotationId(Integer quotationId) {
		String sql = "select sum(v_quotation_item.sub_total) as sub_total from v_quotation_item where 1=1 and  v_quotation_item.parent_id=:parent_id and v_quotation_item.optional=0 ";
		Map<String, Object> params = new HashMap<>();
		params.put("parent_id", quotationId);
		List<Map<String, Object>> result = baseDao.getList(sql, params);
		if (result.get(0).get("sub_total").toString() != null && !"".equals(result.get(0).get("sub_total").toString())) {
			return new BigDecimal(result.get(0).get("sub_total").toString());
		} else {
			return new BigDecimal(0);
		}
	}

	@Override
	public BigDecimal getPayment(int invoiceId, String quotationId) {
		String sql = "select payment from t_obj_quotation_invoice where invoice_id=:invoice_id and quotation_id=:quotation_id ";
		Map<String, Object> params = new HashMap<>();
		params.put("invoice_id", invoiceId);
		params.put("quotation_id", quotationId);
		List<Map<String, Object>> result = baseDao.getList(sql, params);
		if (result.get(0).get("payment").toString() != null && !"".equals(result.get(0).get("payment").toString())) {
			return new BigDecimal(result.get(0).get("payment").toString());
		} else {
			return new BigDecimal(0);
		}
	}

	@Override
	public List<Invoice> getexportList(String searchForm) {
		Map<String, Object> params = new HashMap<>();
		String sql = " SELECT DISTINCT t_invoice.*, ref_list.list_name AS statusName,obj_company.company_name AS companyName,obj_company.company_id,t_quotation.quotation_no FROM t_invoice "
				+ " LEFT JOIN obj_company ON obj_company.company_id = t_invoice.payer "
				+ " LEFT JOIN t_quotation ON t_invoice.invoice_id = t_quotation.invoice_id "
				+ " LEFT JOIN ref_list ON ref_list.list_id = t_invoice. STATUS " + " WHERE 1 = 1 ";

		if (searchForm != null && !"".equals(searchForm)) {
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			if (searchJson.get("invoiceNo") != null && !"".equals(searchJson.get("invoiceNo"))) {
				sql = sql + " and invoice_no like '%" + searchJson.get("invoiceNo").toString() + "%' ";
			}
			if (searchJson.get("quotationNo") != null && !"".equals(searchJson.get("quotationNo"))) {
				sql = sql + " and quotation_no like '%" + searchJson.get("quotation_no").toString() + "%' ";
			}
			if (searchJson.get("amountFrom") != null && !"".equals(searchJson.get("amountFrom"))) {
				sql = sql + " and amount>=" + searchJson.get("amountFrom") + " ";
			}
			if (searchJson.get("amountTo") != null && !"".equals(searchJson.get("amountTo"))) {
				sql = sql + " and amount<=" + searchJson.get("amountTo") + " ";
			}
			if (searchJson.get("dateFrom") != null && !"".equals(searchJson.get("dateFrom"))) {
				sql = sql + " and t_invoice.create_date>='" + searchJson.get("dateFrom") + "' ";
			}
			if (searchJson.get("dateTo") != null && !"".equals(searchJson.get("dateTo"))) {
				sql = sql + " and t_invoice.create_date<='" + searchJson.get("dateTo") + "' ";
			}
			if (searchJson.get("client") != null && !"".equals(searchJson.get("client"))) {
				sql = sql + " and payer=" + searchJson.get("client") + " ";
			}
			if (searchJson.get("status") != null && !"".equals(searchJson.get("status"))) {
				sql = sql + " and status=" + searchJson.get("status") + " ";
			}

		}

		sql = sql + " GROUP BY t_invoice.invoice_id ";
		sql = sql + " ORDER BY t_invoice.invoice_id DESC ";
		Query query = baseDao.getSessionFactory().getCurrentSession().createSQLQuery(sql)
				.setResultTransformer(new BeanTransformerAdapter(Invoice.class));

		if (params != null && params.size() > 0) {
			query.setProperties(params);
		}
		return query.list();
	}

	@Override
	public String getQuotationAmount(Integer invoiceId) {
		String quotationAmount = "";
		Map<String, Object> params = new HashMap<>();
		String sql = "select (sum(if(item.optional=0,if(item.sub_total is not null,item.sub_total,0),0))-if(v_quotation.discount is not null,v_quotation.discount,0)) as quotationAmount from v_quotation  "
				+ "   inner join t_obj_quotation_invoice on t_obj_quotation_invoice.quotation_id=v_quotation.quotation_id   "
				+ "   left join v_quotation_item item on item.parent_id=v_quotation.quotation_id  "
				+ "   where t_obj_quotation_invoice.invoice_id=:invoice_id ";
		params.put("invoice_id", invoiceId);
		Map<String, Object> result = baseDao.getObject(sql, params);

		if (result.get("quotationAmount") != null) {
			quotationAmount = result.get("quotationAmount").toString();
		} else {
			quotationAmount = "0.00";
		}
		return quotationAmount;
	}

	@Override
	public Map<String, Object> getInvoiceDetail(Integer invoiceId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Invoice invoice = (Invoice) baseDao.getObject(Invoice.class, invoiceId);
		map.put("data", invoice);
		return map;
	}

}
