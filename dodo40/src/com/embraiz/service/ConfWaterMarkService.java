package com.embraiz.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.embraiz.model.WaterMarkTemp;

public interface ConfWaterMarkService {
	
	/**
	 * 获得临时水印图片数据
	 * @return
	 */
	public List<Map<String,Object>> getTempWaterMarkImage();

	/**
	 * 删除所有的临时水印图片
	 * @return
	 */
	public boolean deleteTempWaterMarkImage();
	
	/**
	 * 保存临时水印
	 * @param waterMarkTemp
	 * @return
	 */
	public Serializable saveTempWaterMarkImage(WaterMarkTemp waterMarkTemp);
	
	/**
	 * 创建指定大小和颜色的图片
	 * @param width
	 * @param height
	 * @param color
	 * @return
	 */
	public BufferedImage createBgImage(int width,int height,Color color);
	
	/**
	 * 检测文件类型
	 * @param file
	 * @return
	 */
	public boolean checkType(MultipartFile file);
}
