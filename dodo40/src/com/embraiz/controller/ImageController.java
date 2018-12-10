package com.embraiz.controller;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Positions;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.ConfImageType;
import com.embraiz.model.Cron;
import com.embraiz.model.WaterMark;
import com.embraiz.model.WaterMarkTemp;
import com.embraiz.service.BaseService;
import com.embraiz.service.ConfImageTypeService;
import com.embraiz.service.ConfWaterMarkService;
import com.embraiz.util.FileUtil;
import com.embraiz.util.ImageUtil;
import com.embraiz.util.MainUtil;

@Controller
@RequestMapping("/image")
public class ImageController extends MainUtil{
	
	@Resource
	private BaseService baseService;
	@Resource
	private ConfImageTypeService confImageTypeService;
	@Resource
	private ConfWaterMarkService confWaterMarkService;

	@RequestMapping("/compressImage")
	public void compressImage(){
		ImageUtil imageUtil = new ImageUtil();
		
		String file = "D:/Tomcat/apache-tomcat-7.0.73/webapps/dodo40_master_new/image/original/Chrysanthemum.jpg";
		String fileName = "Chrysanthemum";
		String fileType = "jpg";
		String type = "thumbnail,large";
		
		try {
			boolean rtn = imageUtil.compressImage(file, fileName, fileType, type);
			System.out.println(rtn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 新增ConfImageType
	 * @param confImageType
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/saveConfImageType")
	public void saveConfImageType(
			@RequestParam("confImageTypeData") String confImageTypeData,
			HttpServletResponse response)
		throws IOException{
		
		JSONObject jsonObject = JSONObject.parseObject(confImageTypeData);
		ConfImageType confImageType = jsonObject.toJavaObject(jsonObject, ConfImageType.class);
		
		JSONObject json = new JSONObject();
			
		if (confImageType!=null) {
			Integer confId = (Integer) baseService.save(confImageType);
			if (confId > 0) {
				json.put("result", true);
			} else {
				json.put("result", false);
			}
		}
		
		
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 修改confImageType
	 * @param confImageType
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/updateConfImageType")
	public void updateConfImageType(
			@RequestParam("confImageTypeData") String confImageTypeData,
			HttpServletResponse response)
		throws IOException{
		
		JSONObject jsonObject = JSONObject.parseObject(confImageTypeData);
		ConfImageType confImageType = jsonObject.toJavaObject(jsonObject, ConfImageType.class);
		
		JSONObject json = new JSONObject();
		
		if(confImageType!=null){
			try{
				confImageTypeService.updateConfImageType(confImageType);
				json.put("result", true);
			}catch(Exception e){
				e.printStackTrace();
				json.put("result", false);
			}
		}
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 根据confId删除ConfImageType
	 * @param confId
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/deleteConfImageType")
	public void deleteConfImageType(
			@RequestParam(value="confIds") String confIds,
			HttpServletResponse response)
		throws IOException{
		JSONObject json = new JSONObject();
		
		if(confIds!=null && confIds.length()>0){
			String confIdStr [] = confIds.split(",");
			
			boolean rtn = false;
			
			for(int i=0;i<confIdStr.length;i++){
				rtn =  confImageTypeService.deleteConfImageType(Integer.parseInt(confIdStr[i]));
				if(!rtn){
					break;
				}
			}
			
			
			json.put("result", rtn);
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 获得ConfImageType列表
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getConfImageTypeList")
	public void getConfImageTypeList(
			HttpServletResponse response)
		throws IOException{
		
		List<Object> objectList = confImageTypeService.getList();
		
		JSONObject json = new JSONObject();
		json.put("confImageTypeList", objectList);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
		
	}
	
	/**
	 * 上传水印图片
	 * @param image
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/uploadWaterMarkImage")
	public void uploadWaterMarkImage(
			@RequestParam(value = "image") MultipartFile image,
			HttpServletResponse response)
		throws IOException{
		JSONObject json = new JSONObject();
		if(image!=null){
			
			//检测文件类型
			boolean rtn = confWaterMarkService.checkType(image);
			
			if(rtn){
				//删除之前所有的临时水印图片文件
				confWaterMarkService.deleteTempWaterMarkImage();
				
				//保存新的水印图片
				ImageUtil imageUtil = new ImageUtil();
				try {
					String savePath[] = imageUtil.uploadImage(image, "/image/water_mark_temp/");
					if(savePath!=null){
						WaterMarkTemp wmt = new WaterMarkTemp();
							wmt.setPosition(savePath[1]);
							wmt.setUrl(savePath[0]);
						confWaterMarkService.saveTempWaterMarkImage(wmt);
						
						json.put("result", true);
						json.put("msg", savePath[0]);
					}else{
						json.put("result", false);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					json.put("result", false);
				}
			}else{
				json.put("result", rtn);
				json.put("msg", "typeError");
			}
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 返回图片加上水印后的效果图
	 * @param response
	 * @param type (水印类型：image/text)
	 * @param text (作为水印的文字，非必填)
	 * @param position (水印的位置：Top Left、Top Right、Center、Bottom Left、Bottom Right，默认Center)
	 * @param transparency (透明度：0-100)
	 * @param width (宽度：默认400)
	 * @param height (长度：默认400)
	 * @param timeStamp (用于前端区分请求路径)
	 * @throws Exception
	 */
	@RequestMapping("/preview")
	public void preview(
			HttpServletResponse response,
			@RequestParam(name="confData")String confData,
			@RequestParam(name="timeStamp",required = false) String timeStamp
			)
		throws Exception{
		
		JSONObject json = new JSONObject();
		
		if(confData!=null && !("").equals(confData)){
			
			//定义一个可以用来生成白色背景图的对象
			BufferedImage bi = null;
			
			//定义一个需要进行水印处理的图片对象
			Builder<BufferedImage> bl = null;
			
			int defaultWidth = 400;
			int defaultHeight = 400;
			
			JSONObject confDataJson = JSONObject.parseObject(confData);
			
			if(confDataJson.get("width")!=null && confDataJson.get("height")!=null){
				bi = confWaterMarkService.createBgImage((int)confDataJson.get("width"),  (int)confDataJson.get("height"), Color.WHITE);
			}else{
				bi = confWaterMarkService.createBgImage(defaultWidth, defaultHeight, Color.WHITE);
			}
			
			bl = Thumbnails.of(bi).scale(1f).outputQuality(0.9d);
			
			//水印图片
			BufferedImage waterMark = null;
			
			String type = confDataJson.get("type").toString();
			
			if(type.equals("image")){
				List<Map<String,Object>> ls = confWaterMarkService.getTempWaterMarkImage();
				String tempPosition = "";
				for(int i=0;i<ls.size();i++){
					//获得临时水印图片的存储路径
					tempPosition = ls.get(i).get("position").toString();
				}
				
				waterMark = ImageIO.read(new File(tempPosition));
			}else{
				
				String text = confDataJson.get("text").toString();//文本内容
				
				String textFont = "黑体";//字体
				if(confDataJson.get("textFont")!=null){
					textFont = confDataJson.get("textFont").toString();
				}

				int textSize = 30;//字体大小
				if(confDataJson.get("textSize")!=null){
					textSize = (int)confDataJson.get("textSize");
				}
				
				Color textColor = Color.black;//字体颜色
				if(confDataJson.get("textColor")!=null){
					textColor = Color.getColor(confDataJson.get("textColor").toString());
				}
				
				int textMargin = 1;//字体间距
				if(confDataJson.get("textMargin")!=null){
					textMargin = (int)confDataJson.get("textMargin");
				}
				
				Color textBgColor = null;//文本背景颜色
				if(confDataJson.get("textBgColor")!=null){
					textBgColor = Color.getColor(confDataJson.get("textBgColor").toString());
				}
				
				Font font = new Font(textFont, Font.PLAIN, textSize);
				
				// 获取font的样式应用在str上的整个矩形
				Rectangle2D r = font.getStringBounds(text, new FontRenderContext(AffineTransform.getScaleInstance(1, 1), false, false));
				
				// 获取单个字符的高度
				int unitHeight = (int) Math.floor(r.getHeight());
				// 获取整个str用了font样式的宽度，这里用四舍五入后+1，保证宽度绝对能容纳这个字符串，作为图片的宽度
				int imageW = (int) Math.round(r.getWidth()) + 1;
				// 把单个字符的高度+3，保证高度绝对能容纳字符串，作为图片的高度
				int imageH = unitHeight + 3;
				
				// 创建图片
				//BufferedImage image = new BufferedImage(imageW, imageH,BufferedImage.TYPE_INT_BGR);
				BufferedImage image = confWaterMarkService.createBgImage(imageW, imageH, textBgColor);
				Graphics2D g2d = image.createGraphics();
				
				//设置背景透明
				/*Graphics2D g2d = image.createGraphics();
				image = g2d.getDeviceConfiguration().createCompatibleImage(imageW, imageH, Transparency.TRANSLUCENT);
				g2d.dispose();*/
				
				//居中显示
				int x= (int)(imageW/2 - textMargin * g2d.getFontMetrics().stringWidth(text)/2);  
			    int y= imageH/2 + g2d.getFontMetrics().getHeight()/3;
			    //设置字体间距
				changeFontMargin(text,x,y,(double)textMargin,g2d);
				
				//添加文字
				g2d = image.createGraphics();
				g2d.setColor(textColor);
				g2d.setStroke(new BasicStroke(10.0f));
				g2d.setFont(font);
				//g2d.drawString(text, 0, font.getSize());
				g2d.dispose();
				
				waterMark = image;
			}
			
			//透明度
			int transparency = 1;
			if(confDataJson.get("transparency")!=null){
				transparency = (int)confDataJson.get("transparency");
			}
			float transparencyF = transparency/100f;
			
			String position = "Center";
			if(confDataJson.get("position")!=null){
				position = confDataJson.get("position").toString();
			}
					
			switch(position){
			case "Top_Left":
				bl.watermark(Positions.TOP_LEFT,waterMark,transparencyF);
				break;
			case "Top_Right":
				bl.watermark(Positions.TOP_RIGHT,waterMark,transparencyF);
				break;
			case "Center":
				bl.watermark(Positions.CENTER,waterMark,transparencyF);
				break;
			case "Bottom_Left":
				bl.watermark(Positions.BOTTOM_LEFT,waterMark,transparencyF);
				break;
			case "Bottom_Right":
				bl.watermark(Positions.BOTTOM_RIGHT,waterMark,transparencyF);
				break;
			default:
				bl.watermark(Positions.CENTER,waterMark,transparencyF);
				break;
			}
			
			//获得项目的系统路径
			String filePath=Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("/WEB-INF/classes/", "");
			
			//图片保存名称
			String fileName = UUID.randomUUID().toString()+".jpg";
			//图片保存路径
			String savePath = "/image/previewImage/";
			
			File catalog = new File(filePath + savePath);
			if (!catalog.exists() && !catalog.isDirectory()) {
				catalog.mkdirs();
			}
			
			OutputStream os =  new FileOutputStream(filePath + savePath + fileName);   
			
			boolean rtn = ImageIO.write(bl.asBufferedImage(), "jpg", os);
			
			if(rtn){
				json.put("result", true);
				json.put("url", savePath+fileName);
			}else{
				json.put("result", false);
				json.put("url", null);
			}
			
		}
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
		
	}
	
	/**
	 * 保存水印图片和配置
	 * @param response
	 */
	@RequestMapping("/saveWaterMark")
	public void saveWaterMark(
			@RequestParam(name="confData")String confData,
			HttpServletResponse response)
		throws IOException {
		JSONObject json = new JSONObject();
		if(confData!=null && !("").equals(confData)){
			JSONObject jsonObject = JSONObject.parseObject(confData);
			
			WaterMark waterMark = JSONObject.toJavaObject(jsonObject, WaterMark.class);
			
			String sql = "select count(1) from conf_image_water_mark";
			int count = baseService.getCountBySql(sql, null);
			
			if(count==0){//save
				waterMark.setCreateDate(getTime());
				
				if(jsonObject.get("type").equals("image")){
					String hql = "from WaterMarkTemp";
					WaterMarkTemp waterMarkTemp = (WaterMarkTemp)baseService.getObjectByHql(hql, null);
					waterMark.setUrl(waterMarkTemp.getUrl());
					waterMark.setPath(waterMarkTemp.getPosition());
				}
				Integer confId = (Integer)baseService.save(waterMark);
				
				if(confId!=null && confId>0){
					json.put("result", true);
				}else{
					json.put("result", false);
				}
				
			}else{//update
				WaterMark waterMarkDb = (WaterMark)baseService.getObject(WaterMark.class, waterMark.getConfId());
				if(waterMarkDb!=null){
					if(jsonObject.get("type").equals("image")){
						String hql = "from WaterMarkTemp";
						WaterMarkTemp waterMarkTemp = (WaterMarkTemp)baseService.getObjectByHql(hql, null);
						waterMarkDb.setUrl(waterMarkTemp.getUrl());
						waterMarkDb.setPath(waterMarkTemp.getPosition());
						
						waterMarkDb.setType("image");
						waterMarkDb.setText(null);
						waterMarkDb.setTextFont(null);
						waterMarkDb.setTextSize(null);
						waterMarkDb.setTextColor(null);
						waterMarkDb.setTextMargin(null);
						waterMarkDb.setTextBgColor(null);
						
					}else if(jsonObject.get("type").equals("text")){
						
						waterMarkDb.setType("text");
						waterMarkDb.setText(waterMark.getText());
						waterMarkDb.setTextFont(waterMark.getTextFont());
						waterMarkDb.setTextSize(waterMark.getTextSize());
						waterMarkDb.setTextColor(waterMark.getTextColor());
						waterMarkDb.setTextMargin(waterMark.getTextMargin());
						waterMarkDb.setTextBgColor(waterMark.getTextBgColor());
						
						waterMarkDb.setUrl(null);
						waterMarkDb.setPath(null);
					}
					
					waterMarkDb.setPosition(waterMark.getPosition());
					waterMarkDb.setScaling(waterMark.getScaling());
					waterMarkDb.setSize(waterMark.getSize());
					waterMarkDb.setTransparency(waterMark.getTransparency());
					
					try{
						baseService.update(waterMarkDb);
						json.put("result", true);
					}catch(Exception e){
						e.printStackTrace();
						json.put("result", false);
					}
					
				}
				
			}
			
		}
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
		
	}
	
	/**
	 * 获取水印配置
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getWaterMark")
	public void getWaterMark(HttpServletResponse response)
		throws IOException{
		JSONObject json = new JSONObject();
		String hql = "from WaterMark";
		json.put("waterMark", baseService.getObjectByHql(hql, null));
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	public void action(){
		
		//获取需要处理的图片压缩配置
		List<Object> ls = confImageTypeService.getList();
		for(int i=0;i<ls.size();i++){
			ConfImageType confImageType = (ConfImageType)ls.get(i);
		}
		
		//压缩图片后，判断图片是否在添加水印条件范围内（only size）
		
		//添加水印时，判断是否需要缩小或放大
		
		//------单张图片测试后，进行多图测试-------
	}
	
	@RequestMapping("/testImage2")
	public void testImage2(HttpServletResponse response){
		try {
			Thumbnails.of("D://WorkSpace/demo_image/WebRoot/image/IMG_0549.JPG")
			.scale(1f)
			.outputQuality(0.9d)
			.watermark(Positions.CENTER,ImageIO.read(new File("D://WorkSpace/demo_image/WebRoot/image/logo.png")),0.9f)
			.toFile("D://WorkSpace/demo_image/WebRoot/image/Water_IMG_0549.JPG");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 改变字体间距
	 * @param str
	 * @param x
	 * @param y
	 * @param rate
	 * @param g
	 */
	private static void changeFontMargin(String str,int x,int y,double rate,Graphics2D g2d){
		String tempStr=new String();  
        int orgStringWight=g2d.getFontMetrics().stringWidth(str);  
        int orgStringLength=str.length();  
        int tempx=x;  
        int tempy=y;  
        while(str.length()>0)  
        {
            tempStr=str.substring(0, 1);  
            str=str.substring(1, str.length());  
            g2d.drawString(tempStr, tempx, tempy);  
            tempx=(int)(tempx+(double)orgStringWight/(double)orgStringLength*rate);  
        }  
	}
	
}
