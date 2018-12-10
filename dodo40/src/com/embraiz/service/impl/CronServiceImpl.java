package com.embraiz.service.impl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.dao.BaseDao;
import com.embraiz.model.Cron;
import com.embraiz.model.Page;
import com.embraiz.service.CronService;
import com.embraiz.util.FileUtil;

@Service
@Transactional
public class CronServiceImpl implements CronService{

	@Resource
	private BaseDao baseDao;
	
	@Override
	public List<Map<String,Object>> checkCron(Calendar cal){
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA); 
		
		String sql = "SELECT * FROM `t_cron` WHERE t_cron.last_run <= :date "
				+ " AND (( month_day IS NULL AND week_day IS NULL ) OR ( month_day LIKE :dayMonth OR week_day LIKE :dayWeek )) "
				+ " AND hour_day LIKE :hourDay AND minute_hour LIKE :minuteHour ";
		Map<String,Object> params = new HashMap<String,Object>();
			params.put("date", format.format(cal.getTime()));
			params.put("dayMonth", "%"+cal.get(Calendar.DATE)+"%");
			params.put("dayWeek",  "%"+(cal.get(Calendar.DAY_OF_WEEK)-1)+"%");
			params.put("hourDay", "%"+cal.get(Calendar.HOUR_OF_DAY)+"%");
			params.put("minuteHour", "%"+cal.get(Calendar.MINUTE)+"%");
		List<Map<String,Object>> objectList = baseDao.getList(sql, params);
		
		return objectList;
	}
	
	@Override
	public Serializable saveCron(Cron cron){
		return baseDao.save(cron);
	}
	
	@Override
	public List<Object> getDelCronFileList(Integer cronId,String time){
		String hql =  "from CronLog where cronId = :cronId and startTime < :time and status = :status";
		Map<String,Object> params = new HashMap<String,Object>();
			params.put("cronId", cronId);
			params.put("time", time);
			params.put("status", "Success");
		List<Object> objectList = baseDao.getListByHql(hql, params);
		
		return objectList;
	}
	
	@Override
	public boolean delFile(String path){
		FileUtil fileUtil = new FileUtil();
		return fileUtil.deleteFile(path);
	}
	
	@Override
	public Map<String,Object> getCronList(String searchForm,String sortBy,String sortOrder,Page page){
		String sql = "select * from v_cron where 1=1 ";
		String sqlCount = "select count(*) from v_cron where 1=1 ";
		Map<String,Object> params = new HashMap<String,Object>();
		
		if(searchForm!=null && !"".equals(searchForm)){
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			
			if(searchJson.get("cronName")!=null && !"".equals(searchJson.get("cronName"))){
				sql = sql + " and cronName = :cronName ";
				sqlCount = sqlCount + " and cronName = :cronName ";
				params.put("cronName", searchJson.get("cronName"));
			}
		}
		
		//获得行数
		int listCount = baseDao.getCountBySql(sqlCount, params);
		
		if(!("").equals(sortBy) && !("").equals(sortOrder)){
			sql = sql + " order by "+sortBy+" "+sortOrder;
		}
		sql = sql + " limit "+page.getStart()+" , "+page.getCount();
		
		List<Map<String,Object>> ls = baseDao.getList(sql, params);
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", ls);
		
		return map;
	}
	
	@Override
	public boolean deleteCron(Integer cronId){
		int rtn = baseDao.deleteById(Cron.class, cronId);
		if(rtn>0){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public void updateCron(Cron cron){
		Cron cronDb = (Cron)baseDao.getObject(Cron.class, cron.getCronId());
		if(cronDb!=null){
			cronDb.setMonthDay(cron.getMonthDay());
			cronDb.setWeekDay(cron.getWeekDay());
			cronDb.setHourDay(cron.getHourDay());
			cronDb.setMinuteHour(cron.getMinuteHour());
			
			cronDb.setClassName(cron.getClassName());
			cronDb.setMethodName(cron.getMethodName());
			cronDb.setParamName(cron.getParamName());
			
			cronDb.setRetentionTime(cron.getRetentionTime());
			
			baseDao.update(cronDb);
		}
	}
	
	@Override
	public Map<String,Object> getCronLogList(String searchForm,String sortBy,String sortOrder,Page page){
		String sql = "select * from v_cron_log where 1=1 ";
		String sqlCount = "select count(*) from v_cron_log where 1=1 ";
		Map<String,Object> params = new HashMap<String,Object>();
		
		if(searchForm!=null && !"".equals(searchForm)){
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			
			if(searchJson.get("cronName")!=null && !"".equals(searchJson.get("cronName"))){
				sql = sql + " and cronName = :cronName ";
				sqlCount = sqlCount + " and cronName = :cronName ";
				params.put("cronName", searchJson.get("cronName"));
			}
			
			if(searchJson.get("frequency")!=null && !"".equals(searchJson.get("frequency"))){
				sql = sql + " and frequency = :frequency ";
				sqlCount = sqlCount + " and frequency = :frequency ";
				params.put("frequency", searchJson.get("frequency"));
			}
			
			if(searchJson.get("status")!=null && !"".equals(searchJson.get("status"))){
				sql = sql + " and status = :status ";
				sqlCount = sqlCount + " and status = :status ";
				params.put("status", searchJson.get("status"));
			}
		}
		
		//获得行数
		int listCount = baseDao.getCountBySql(sqlCount, params);
		
		if(!("").equals(sortBy) && !("").equals(sortOrder)){
			sql = sql + " order by "+sortBy+" "+sortOrder;
		}
		sql = sql + " limit "+page.getStart()+" , "+page.getCount();
		
		List<Map<String,Object>> ls = baseDao.getList(sql, params);
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("recordsTotal", listCount);
		map.put("recordsFiltered", listCount);
		map.put("data", ls);
		
		return map;
	}
	
}
