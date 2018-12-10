package com.embraiz.service.impl;

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
import com.embraiz.model.Payment;
import com.embraiz.model.RefList;
import com.embraiz.service.PaymentService;
import com.embraiz.util.BeanTransformerAdapter;
import com.embraiz.util.StringFormat;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

	@Resource
	private BaseDao baseDao;

	@Override
	public int insert(Payment payment) {
		payment.setDate(new Date());
		int paymentId = 0;
		final Payment paymentadd = payment;
		paymentId = (Integer) baseDao.save(paymentadd);
		return paymentId;
	}

	@Override
	public String paymentNo() {
		String date = StringFormat.formatData(new Date());
		String pno = "";
		int no=0;
		String sql = "select max(right(right(payment_no,5),3))+1 as payment_no from t_payment";
		List<Map<String, Object>> result = baseDao.getList(sql, null);
		String no1 = "0000";
		if(result.get(0).get("payment_no")!=null){
			no1 = result.get(0).get("payment_no").toString();	
			no = Float.valueOf(no1).intValue();
		}
		if (no == 0) {
			no = 1;
		}
		if (no < 10) {
			pno += "00" + no;
		} else if (no >= 10 && no < 100) {
			pno += "0" + no;
		} else {
			pno += no;
		}
		
		String[] i = date.split("-");
		pno = "P" + i[0] + i[1] + pno;
		return pno;

	}

	@Override
	public Map<String, Object> getPaymentDetail(Integer paymentId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Payment payment = (Payment) baseDao.getObject(Payment.class, paymentId);
		map.put("data", payment);
		return map;
	}

	@Override
	public Map<String, Object> getDatasourse() {
		String sql = "SELECT list_id,list_name FROM ref_list INNER JOIN ref_item ON ref_item.item_id = ref_list.item_id WHERE(static_name = trim('Payment Method')OR item_name = trim('Payment Method'))ORDER BY list_name ASC ";
		String sqlCount = "SELECT count(*) FROM ref_list INNER JOIN ref_item ON ref_item.item_id = ref_list.item_id WHERE(static_name = trim('Payment Method')OR item_name = trim('Payment Method'))ORDER BY list_name ASC ";
		int listCount = baseDao.getCountBySql(sqlCount.toString(), null);
		List<Object> list = baseDao.getList(null, sql, RefList.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", list);
		return map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Payment> getpaymentById(String obj_id) {
		Map<String, Object> params = new HashMap<>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT t_payment.*, obj_user.login_name AS username,t_obj_invoice_payment.invoice_id,obj_company.company_name AS company_name, "
				+ " 	GROUP_CONCAT(DISTINCT t_quotation.quotation_no ORDER BY t_payment.payment_id SEPARATOR ' ') AS quotationNo, "
				+ " 	GROUP_CONCAT(DISTINCT t_invoice.invoice_no ORDER BY t_invoice.invoice_id SEPARATOR ' ') AS invoiceNo, "
				+ " obj_contact.contact_name AS contact_name,t_quotation.currency,t_currency.`name` AS currencyName,obj_company.company_id, "
				+ " 	t_quotation.quotation_id FROM t_payment "
				+ " LEFT JOIN obj_user ON t_payment.staff = obj_user.user_id "
				+ " LEFT JOIN t_obj_invoice_payment ON t_payment.payment_id = t_obj_invoice_payment.payment_id "
				+ " LEFT JOIN t_invoice ON t_invoice.invoice_id = t_obj_invoice_payment.invoice_id "
				+ " LEFT JOIN obj_company ON t_invoice.payer = obj_company.company_id "
				+ " LEFT JOIN t_obj_quotation_invoice ON t_obj_quotation_invoice.invoice_id = t_invoice.invoice_id "
				+ " LEFT JOIN t_quotation ON t_quotation.quotation_id = t_obj_quotation_invoice.quotation_id "
				+ " LEFT JOIN t_currency ON t_currency.id = t_quotation.currency "
				+ " LEFT JOIN obj_contact ON obj_contact.contact_id = t_quotation.contact_id "
				+ "WHERE t_payment.payment_id =:payment_id");
		sql.append(" group by t_payment.payment_id ");
		params.put("payment_id", obj_id);
		
		Query query = baseDao.getSessionFactory().getCurrentSession().createSQLQuery(sql.toString())
				.setResultTransformer(new BeanTransformerAdapter(Payment.class));

		if (params != null && params.size() > 0) {
			query.setProperties(params);
		}
		return query.list();
	}

	@Override
	public List<Map<String, Object>> getexportList(String searchForm) {
		String sql = "SELECT t_payment.*, obj_user.login_name AS username,t_invoice.invoice_id,obj_company.company_name AS companyName, "
				+ "  concat_ws(';',t_quotation.quotation_no) AS quotationNo,pm.list_name AS methodIdName,t_quotation.currency, "
				+ "  t_currency.name AS currencyName,obj_company.company_id, t_quotation.quotation_id FROM t_payment "
				+ " LEFT JOIN obj_user ON t_payment.staff = obj_user.user_id "
				+ " INNER JOIN t_obj_invoice_payment ON t_payment.payment_id = t_obj_invoice_payment.payment_id "
				+ " LEFT JOIN t_invoice ON t_invoice.invoice_id = t_obj_invoice_payment.invoice_id "
				+ " LEFT JOIN obj_company ON t_invoice.payer = obj_company.company_id "
				+ " LEFT JOIN ref_list AS pm ON pm.list_id = t_payment.method_id "
				+ " LEFT JOIN t_obj_quotation_invoice ON t_obj_quotation_invoice.invoice_id = t_invoice.invoice_id "
				+ " LEFT JOIN t_quotation ON t_quotation.quotation_id = t_obj_quotation_invoice.quotation_id "
				+ " LEFT JOIN t_currency ON t_currency.id = t_quotation.currency "
				+ " WHERE 1 = 1 GROUP BY t_payment.payment_id ORDER BY t_payment.payment_id DESC ";
		if (searchForm != null && !"".equals(searchForm)) {
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			if (searchJson.get("paymentNo") != null && !"".equals(searchJson.get("paymentNo"))) {
				sql = sql + " and payment_no like '%" + searchJson.get("paymentNo").toString() + "%' ";
			}
			if (searchJson.get("companyName") != null && !"".equals(searchJson.get("companyName"))) {
				sql = sql + " and companyName like '%" + searchJson.get("companyName").toString() + "%' ";
			}
			if (searchJson.get("totalamountFrom") != null && !"".equals(searchJson.get("totalamountFrom"))) {
				sql = sql + " and total_amount>=" + searchJson.get("totalamountFrom") + " ";
			}
			if (searchJson.get("totalamountTo") != null && !"".equals(searchJson.get("totalamountTo"))) {
				sql = sql + " and total_amount<=" + searchJson.get("totalamountTo") + " ";
			}
			if (searchJson.get("invoiceNo") != null && !"".equals(searchJson.get("invoiceNo"))) {
				sql = sql + " and invoiceNo like '%" + searchJson.get("invoiceNo").toString() + "%' ";
			}
			if (searchJson.get("dateFrom") != null && !"".equals(searchJson.get("dateFrom"))) {
				sql = sql + " and date>='" + searchJson.get("dateFrom") + "' ";
			}
			if (searchJson.get("dateTo") != null && !"".equals(searchJson.get("dateTo"))) {
				sql = sql + " and date<='" + searchJson.get("dateTo") + "' ";
			}			
		}
		return baseDao.getList(sql, null);		 
	}

}
