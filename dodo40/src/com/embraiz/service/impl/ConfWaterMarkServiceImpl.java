package com.embraiz.service.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.embraiz.dao.BaseDao;
import com.embraiz.model.WaterMarkTemp;
import com.embraiz.service.BaseService;
import com.embraiz.service.ConfWaterMarkService;
import com.embraiz.util.FileUtil;

@Service
@Transactional
public class ConfWaterMarkServiceImpl implements ConfWaterMarkService {
	
	@Resource
	private BaseDao baseDao;
	
	@Override
	public List<Map<String,Object>> getTempWaterMarkImage(){
		String sql = "select * from `conf_water_mark_image_temp`";
		return baseDao.getList(sql, null);
	}

	@Override
	public boolean deleteTempWaterMarkImage(){
		String sql ="select * from `conf_water_mark_image_temp`";
		Map<String,Object> params = new HashMap<String,Object>();
		List<Map<String,Object>> ls = baseDao.getList(sql, params);
		FileUtil fileUtil = new FileUtil();
		
		for(int i=0;i<ls.size();i++){
			if(ls.get(i)!=null && ls.get(i).get("conf_id")!=null){
				//删除文件
				fileUtil.deleteFile(ls.get(i).get("position").toString());
				
				//删除记录
				String sqlDelete = "delete from `conf_water_mark_image_temp` where conf_id = :confId";
				params.put("confId", ls.get(i).get("conf_id"));
				baseDao.deleteBySql(sqlDelete, params);
			}
			
		}
		
		return true;
	}
	
	@Override
	public Serializable saveTempWaterMarkImage(WaterMarkTemp waterMarkTemp){
		return baseDao.save(waterMarkTemp);
	}
	
	@Override
	public BufferedImage createBgImage(int width,int height,Color color){
		BufferedImage bi = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) bi.getGraphics();
		if(color!=null){
			g2.setBackground(color);
			g2.clearRect(0, 0, width, height);
		}else{
			//设置背景透明
			bi = g2.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		}
		g2.dispose();
		return bi;
	}
	
	@Override
	public boolean checkType(MultipartFile file){
		boolean isAllow = false;
		
		//定义允许上传的文件类型
		String typeArray[] = {"jpg","png","bmp"};
		
		//判断file数组不能为空并且长度大于0  
		if(file!=null){
			
			// 获取文件名作为保存到服务器的文件名称
			String fileName = file.getOriginalFilename();
			//判断上传的文件是否在允许类型内
			String fileType = fileName.substring(fileName.indexOf(".")+1);
			//转成小写
			fileType = fileType.toLowerCase();
			
			for (int n = 0; n< typeArray.length; n++) {
				if (!fileType.equals(typeArray[n])) {
					isAllow = false;
				}else{
					isAllow = true;
					break;
				}
			}
		}
		
		return isAllow;
	}
}
