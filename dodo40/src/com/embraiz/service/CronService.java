package com.embraiz.service;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.embraiz.model.Cron;
import com.embraiz.model.Page;

public interface CronService {

	/**
	 * 查询符合条件的Cron任务
	 * @param date
	 * @return
	 */
	public List<Map<String,Object>> checkCron(Calendar cal);
	
	/**
	 * 创建Cron任务
	 * @param cron
	 */
	public Serializable saveCron(Cron cron);
	
	/**
	 * 根据cronId和time得到需要被清理的文件列表
	 * @param cronId
	 * @param time
	 * @return
	 */
	public List<Object> getDelCronFileList(Integer cronId,String time);
	
	/**
	 * 删除文件
	 * @param path
	 */
	public boolean delFile(String path);
	
	/**
	 * 获得分页列表
	 * @param cronName
	 * @param page
	 * @return
	 */
	public Map<String,Object> getCronList(String searchForm,String sortBy,String sortOrder,Page page);
	
	/**
	 * 根据cronId进行删除
	 * @param cronId
	 * @return
	 */
	public boolean deleteCron(Integer cronId);
	
	/**
	 * 修改Cron
	 * @param cron
	 */
	public void updateCron(Cron cron);
	
	/**
	 * 获得日志的分页列表
	 * @param searchForm
	 * @param sortBy
	 * @param sortOrder
	 * @param page
	 * @return
	 */
	public Map<String,Object> getCronLogList(String searchForm,String sortBy,String sortOrder,Page page);
}
