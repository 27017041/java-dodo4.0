package com.embraiz.service.impl;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.model.Contract;
import com.embraiz.model.ContractRenew;
import com.embraiz.model.ContractStatus;
import com.embraiz.model.ContractType;
import com.embraiz.model.ObjLog;
import com.embraiz.model.Quotation;
import com.embraiz.model.Reminder;
import com.embraiz.model.User;
import com.embraiz.service.ContractService;
import com.embraiz.service.ObjLogService;
import com.embraiz.util.SendMail;
import com.embraiz.util.StringFormat;
import com.embraiz.util.SynchronizingTask;

@Service
@Transactional
public class ContractServiceImpl implements ContractService {

	@Resource
	private BaseDao baseDao;
	@Resource
	private ObjLogService objLogService;

	@Override
	public int saveRenew(ContractRenew contractRenew) {
		int contractRenewId = 0;
		contractRenewId = (Integer) baseDao.save(contractRenew);
		return contractRenewId;
	}

	@Override
	public int insert(Contract contract, User user) {
		contract.setCreateBy(user.getUserId());
		contract.setCreateDate(new Date());
		int contractId = 0;
		contractId = (Integer) baseDao.save(contract);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		Date reminderSendDate = c.getTime();

		Reminder re = new Reminder();
		re.setUserId(user.getUserId());
		re.setEmail(user.getEmail());
		re.setReminderTime(reminderSendDate);
		re.setEndDate(contract.getEndDate());
		re.setModule("Contract");
		re.setModuleId(contract.getContractId());
		re.setReminderInterval(contract.getReminderInterval());
		re.setReminderType(contract.getReminderType());

		if (re.getReminderInterval() != null && re.getReminderType() != null) {
			re.setReminderStatus(1);
		} else {
			re.setReminderStatus(0);
		}
		re.setStatus(1);
		re.setEmailTempId(30);
		re.setClassObject(contract);

		String sql = "select * from t_mail_template where template_id=:template_id";
		Map<String, Object> params = new HashMap<>();
		params.put("template_id", re.getEmailTempId());
		Map<String, Object> result = baseDao.getObject(sql, params);

		String content = this.content(result, re);
		re.setReminderContent(content);
		re.setSubject(result.get("subject").toString());
		re.setCreateDate(new Date());
		baseDao.save(re);

		if (re.getReminderStatus() == 1) {
			final Reminder re1 = re;

			// 定时发送
			final Timer timer = new Timer();
			SynchronizingTask task = new SynchronizingTask(re1) {

				@Override
				public void run() {

					String sql = "select * from t_reminder where module_id =:module_id and status=1";
					Map<String, Object> params = new HashMap<>();
					params.put("module_id", re1.getModuleId());
					Map<String, Object> result = baseDao.getObject(sql, params);

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar calBegin = Calendar.getInstance();
					Date nowDate = calBegin.getTime();
					String reminderDate = sdf.format(result.get("end_date"));

					boolean flag = false;
					try {
						if (nowDate.before(sdf.parse(reminderDate))) {
							flag = true;
						} else {
							flag = false;
							// 过期修改合同status
							String update = " update t_contract SET status=3 WHERE contract_id=:contract_id ";
							params.put("contract_id", re1.getModuleId());
							baseDao.updateBySql(update, params);
						}
						if (result.get("reminder_status").toString().equals("0") || flag == false) {
							timer.cancel();
						} else {
							SendMail sendMail = new SendMail();
							sendMail.sendMail(result.get("email").toString(), result.get("subject").toString(), result.get("reminder_content")
									.toString());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			if (contract.getReminderType() == 1) {
				timer.schedule(task, 0, contract.getReminderInterval() * 24 * 60 * 60 * 1000);// Days
			} else if (contract.getReminderType() == 2) {
				timer.schedule(task, 0, contract.getReminderInterval() * 7 * 24 * 60 * 60 * 1000);// Week
			} else if (contract.getReminderType() == 3) {
				timer.schedule(task, 0, contract.getReminderInterval() * 30 * 24 * 60 * 60 * 1000);// month
			} else if (contract.getReminderType() == 4) {
				timer.schedule(task, 0, contract.getReminderInterval() * 4 * 30 * 24 * 60 * 60 * 1000);// Season
			} else if (contract.getReminderType() == 5) {
				timer.schedule(task, 0, contract.getReminderInterval() * 12 * 30 * 24 * 60 * 60 * 1000);// Year
			}

		}
		return contractId;
	}

	@Override
	public String contractNo() {
		String contractNo = "";
		String sql = "SELECT max(RIGHT(contract_no, 3)) + 1 as no FROM t_contract ";
		Map<String, Object> result = baseDao.getObject(sql, null);

		String no = "";
		if (result.get("no") == null || "".equals(result.get("no")) || "0".equals(result.get("no"))) {
			no = "1";
		} else {
			no = result.get("no").toString();
		}

		Number num = Float.parseFloat(no);
		contractNo = String.format("%03d", num.intValue());

		Date d = new Date();
		SimpleDateFormat mat = new SimpleDateFormat("yyyyMM");
		contractNo = "C" + mat.format(d) + contractNo;

		return contractNo;
	}

	public String content(Map<String, Object> temp, Reminder reminder) {
		String content = temp.get("content").toString();
		String[] name = temp.get("avaliable_key").toString().replaceAll("\\$", "").split(",");
		try {
			Object o = reminder.getClassObject();
			for (String s : name) {
				try {
					Field f = o.getClass().getDeclaredField(s);
					f.setAccessible(true);
					String value = StringFormat.formatData(f.get(o));

					content = content.replaceAll("\\$" + s, value);
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		}
		return content;
	}

	@Override
	public int deleteData(String ids, User user) {
		int status = 0;
		String contractIdStr[] = ids.split(",");
		if (ids != null) {
			for (int i = 0; i < contractIdStr.length; i++) {
				ObjLog objlog = new ObjLog();
				objlog.setObjId(Integer.parseInt(contractIdStr[i]));
				objlog.setUserId(user.getUserId());
				objlog.setDescription("Status From 1 to 0");
				objlog.setModuleName("Contract");
				objlog.setConfId(0);
				objlog.setFieldName("status");
				objLogService.createLog(objlog);
			}
			String sql = "update t_contract set status=0  where contract_id in (" + ids + ")";
			String sqlobj = "update obj set status=0 where obj_id in (" + ids + ")";
			status = baseDao.updateBySql(sql, null);
			baseDao.updateBySql(sqlobj, null);
		}
		return status;
	}

	@Override
	public int deleteRenew(String ids) {
		int status = 0;

		String sql = "delete from t_obj_contract_renew where renew_id in (" + ids + ")";
		status = baseDao.updateBySql(sql, null);

		return status;
	}

	@Override
	public Map<String, Object> getAllstatus() {
		String sql = "select list_id,list_name from t_ref_contract_status  ";
		String sqlCount = "select count(*) from t_ref_contract_status  ";

		int listCount = baseDao.getCountBySql(sqlCount.toString(), null);
		List<Object> list = baseDao.getList(null, sql, ContractStatus.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", list);
		return map;
	}

	@Override
	public Map<String, Object> getQuotationByCompanyId(int companyId) {
		Map<String, Object> params = new HashMap<>();
		String sql = "select quotationId,quotationName from v_quotation where companyId=:companyId and quotationId<>4  ";
		String sqlCount = "select count(*) from v_quotation where companyId=:companyId and quotationId<>4 ";
		params.put("companyId", companyId);
		int listCount = baseDao.getCountBySql(sqlCount.toString(), params);
		List<Object> list = baseDao.getList(params, sql, Quotation.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", list);
		return map;
	}

	@Override
	public Map<String, Object> getAllType() {
		String sql = "select list_id,list_name from t_ref_contract_type  ";
		String sqlCount = "select count(*) from t_ref_contract_type  ";

		int listCount = baseDao.getCountBySql(sqlCount.toString(), null);
		List<Object> list = baseDao.getList(null, sql, ContractType.class);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", list);
		return map;
	}

	@Override
	public Map<String, Object> getlastNo(Contract contract) {
		Map<String, Object> params = new HashMap<>();
		String sql = " SELECT no FROM v_contract_renew AS v WHERE 1 = 1 " + " AND v.contract_id = :contract_id  AND v.client_id = :client_id "
				+ " ORDER BY renew_id DESC LIMIT 0,1 ";
		params.put("contract_id", contract.getContractId());
		params.put("client_id", contract.getClientId());
		return baseDao.getObject(sql, params);
	}

}
