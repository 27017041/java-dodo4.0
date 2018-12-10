package com.embraiz.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import org.springframework.web.multipart.MultipartFile;

import Decoder.BASE64Decoder;

public class ImageUtil {

	/**
	 * 保存图片(无压缩)
	 * @param files
	 * @return
	 */
	public String[] saveImage(String files,String path)throws Exception{
		//根据系统来判断在创建文件夹
    	String filePath=Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("/WEB-INF/classes/", "");
   	
		//定义图片存储路径
		//String path = "/image/original/";
		
		File file = new File(filePath+path);
	    if (!file.exists()&&!file.isDirectory()){     
	    	file.mkdirs(); 
	    }
	    
	    UUID uuid = UUID.randomUUID();
    	String newFileName = uuid.toString();
		String[] iamgeStr = files.split(",");
		
		String suffix = "jpg";
		if(iamgeStr[0].contains("image/png")){
			suffix = "png";
		}else if(iamgeStr[0].contains("image/bmp")){
			suffix = "bmp";
		}else if(iamgeStr[0].contains("image/gif")){
			suffix = "gif";
		}
		
		boolean saveStatus = GenerateImage(iamgeStr[1].replace(" ", "+"), filePath+path+newFileName+"."+suffix);
		if(saveStatus){
			String[] rs = new String[2];
			rs[0] = path+newFileName+"."+suffix;//返回图片访问目录
			rs[1] = filePath+path+newFileName+"."+suffix;//返回图片保存目录
			return rs;
	    }
	    return null;
	}
	
	
	/**
	 * 上传图片(文件)
	 * @param files
	 * @param path
	 * @return
	 */
	public String[] uploadImage(MultipartFile files,String path){
		// 根据系统来判断在创建文件夹
		String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("/WEB-INF/classes/", "");
		
		//文件访问路径
		String savePath [] = new String[2];
		
		// 文件不为空
		if (!files.isEmpty()) {
			//获取文件类型
			String fileName = files.getOriginalFilename();
			String fileType = fileName.substring(fileName.indexOf(".") + 1);
			
			//生成新的文件名字
			String newFileName = UUID.randomUUID().toString();

			// 创建文件夹
			File waterMark = new File(filePath + path, newFileName+"."+fileType);
			if (!waterMark.exists() && !waterMark.isDirectory()) {
				waterMark.mkdirs();
			}

			try {
				files.transferTo(waterMark);
				savePath[0] = path + newFileName +"."+fileType;
				savePath[1] = filePath + path + newFileName +"."+fileType;
			} catch (Exception e) {
				e.printStackTrace();
				savePath = null;
			}
		}
		return savePath;
	}
	
	
	/**
	 * 压缩图片
	 * @param file(原文件路径)
	 * @param fileName
	 * @param fileType
	 * @param type(需要压缩的类型字符串：thumbnail,medium,large)
	 */
	public boolean compressImage(String file,String fileName,String fileType,String type)throws Exception{
		
		Boolean rtn = false;
		
		//根据系统来判断在创建文件夹
    	String filePath=Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("/WEB-INF/classes/", "");
    	
    	//定义图片存储路径
    	String path = "/image/thumb/";
    	
    	File catalog = new File(filePath+path);
	    if (!catalog.exists()&&!catalog.isDirectory()){     
	    	catalog.mkdirs(); 
	    }
		
		if(type!=null && type.length()>0){
			//将类型分割成数组
			String typeArray[] = type.split(",");
			
			for(int i=0;i<typeArray.length;i++){
				 String typeName = typeArray[i];
				 ImageFormatSize imageFormat = new ImageFormatSize();
				 
				 if(typeName.equals("thumbnail")){
					 Map<String, BufferedImage> map = imageFormat.trim(file,"s");
					 
					//保存S规格图片
					 rtn = imageFormat.save(filePath+path+fileName+"100x100."+fileType, map.get("imageS"));
					 
				 }else if(typeName.equals("medium")){
					 Map<String, BufferedImage> map = imageFormat.trim(file,"m");
					 
					//保存M规格图片
					 rtn =  imageFormat.save(filePath+path+fileName+"257x257."+fileType, map.get("imageM"));
					 
				 }else if(typeName.equals("large")){
					 Map<String, BufferedImage> map = imageFormat.trim(file,"l");
					 
					//保存L规格图片
					 rtn =  imageFormat.save(filePath+path+fileName+"450x450."+fileType, map.get("imageL"));
					 
				 }
			}
		}
		return rtn;
		
	}
	
	
	/**
	 * 对字节数组字符串进行Base64解码并生成图片
	 * @param imgStr
	 * @param imgFilePath
	 * @return
	 * @throws Exception
	 */
	public boolean GenerateImage(String imgStr, String imgFilePath) throws Exception {
	    if (imgStr == null) // 图像数据为空
	      return false;
	    BASE64Decoder decoder = new BASE64Decoder();
	    try {
	      // Base64解码
	      byte[] bytes = decoder.decodeBuffer(imgStr);
	      for (int i = 0; i < bytes.length; ++i) {
	        if (bytes[i] < 0) {// 调整异常数据
	          bytes[i] += 256;
	        }
	      }
	      // 生成jpeg图片
	      OutputStream out = new FileOutputStream(imgFilePath);
	      out.write(bytes);
	      out.flush();
	      out.close();
	      return true;
	    } catch (Exception e) {
	       throw e;
	    }
	  }
	
	/**
	 * 按等比例和质量压缩图片
	 * @param file 原图片路径
	 * @param scale 缩放比例
	 * @param quality 压缩质量
	 * @return
	 */
	public String[] compressImageByQuality(String file,float scale,double quality){
		String rs[] = new String[2];
		
		//获取系统路径
		String filePath=Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("/WEB-INF/classes/", "");
    	//定义图片存储路径
    	String path = "/image/compress/";
    	//定义新的文件名字
    	String newFileName = UUID.randomUUID().toString() + ".jpg";
    	try {
			Thumbnails.of(file).scale(scale).outputQuality(quality).toFile(filePath + path + newFileName);
			rs [0] =  path + newFileName;
			rs [1] =  filePath + path + newFileName;
		} catch (Exception e) {
			e.printStackTrace();
			rs = null;
		}
    	return rs;
	}
	
	/**
	 * 裁剪指定宽高、指定位置的图像（可先按比例缩放）
	 * @param file 原图片路径
	 * @param scale 缩放比例
	 * @param width 需要裁剪的宽度
	 * @param height 需要裁剪的高度
	 * @param positions 需要裁剪的位置
	 * @return
	 */
	public String[] tailorImage(String file,float scale,int width,int height,Positions positions){
		String rs[] = new String[2];
		
		//获取系统路径
		String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("/WEB-INF/classes/", "");
		// 定义图片存储路径
		String path = "/image/compress/";
		// 定义新的文件名字
		String newFileName = UUID.randomUUID().toString() + ".jpg";
		
		try{
			if(scale==1f){//原尺寸进行切图
				Thumbnails.of(file).sourceRegion(positions, width, height).size(width,height).toFile(filePath + path + newFileName);
			}else{//需先进行缩放
				 BufferedImage bi = Thumbnails.of(file).scale(scale).asBufferedImage();
				 Thumbnails.of(bi).sourceRegion(positions, width, height).size(width,height).toFile(filePath + path + newFileName);
			}
			
			rs [0] =  path + newFileName;
			rs [1] =  filePath + path + newFileName;
		}catch(Exception e){
			e.printStackTrace();
			rs = null;
		}
		return rs;
		
	}
	
	/**
	 * 图片格式转换
	 * @param file 原图片路径
	 * @param scale 压缩比例
	 * @param type 转换成哪种类型
	 * @return
	 */
	public String[] transformFormat(String file,float scale,String type){
		String rs[] = new String[2];
		
		//获取系统路径
		String filePath = Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("/WEB-INF/classes/", "");
		// 定义图片存储路径
		String path = "/image/compress/";
		// 定义新的文件名字
		String newFileName = UUID.randomUUID().toString() + ".jpg";
		
		try{
			Thumbnails.of(file).scale(scale).outputFormat(type.toLowerCase()).toFile(filePath + path + newFileName);
			rs [0] =  path + newFileName;
			rs [1] =  filePath + path + newFileName;
		}catch(Exception e){
			e.printStackTrace();
			rs = null;
		}
		return rs;
		
	}
}
