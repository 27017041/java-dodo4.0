package com.embraiz.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageFormatSize {

	float trimWidthS = 100;
	float trimHeightS = 100;
	float trimWeidthM = 257;
	float trimHeightM = 257;
	float trimWeidthL = 450;
	float trimHeightL = 450;
	
	private Color color = new Color(240,240,240);
	
	private BufferedImage image;
	
	/**
	 * 生成三种图片规格
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Map<String, BufferedImage> trim(String filePath,String type) throws FileNotFoundException, IOException{
		image = ImageIO.read(new FileInputStream(filePath));
		
		Map<String, BufferedImage> map = new HashMap<String, BufferedImage>();
		
		if(type.equals("s")){
			BufferedImage imageS = trim(trimWidthS,trimHeightS);
			map.put("imageS", imageS);
		}else if(type.equals("m")){
			BufferedImage imageM = trim(trimWeidthM,trimHeightM);
			map.put("imageM", imageM);
		}else if(type.equals("l")){
			BufferedImage imageL = trim(trimWeidthL,trimHeightL);
			map.put("imageL", imageL);
		}
		
		
		return map;
	}
	/**
	 * 根据指定图片尺寸压缩并处理
	 * @param trimWidth
	 * @param trimHeight
	 * @return
	 */
	public BufferedImage trim(float trimWidth,float trimHeight) {
        //float width  = getTrimmedWidth();
        //float height = getTrimmedHeight();
        
		float width         = this.image.getWidth();
		float height        = this.image.getHeight();
        
        BufferedImage newImg=null;
        if(width>trimWidth&&trimWidth*(height/width)>trimHeight){
        	width=(int)(trimHeight*(width/height));
        	height=trimHeight;
        	newImg = new BufferedImage((int)(trimHeight*(width/height)), (int)trimHeight,
                    BufferedImage.TYPE_INT_RGB);
        }else if(width>trimWidth&&trimWidth*(height/width)<=trimHeight){
        	height=(int)(trimWidth*(height/width));
        	width=trimWidth;
        	newImg = new BufferedImage((int)trimWidth, (int)(trimWidth*(height/width)),
                    BufferedImage.TYPE_INT_RGB);
        }else if(height>trimHeight&&trimHeight*(width/height)>trimWidth){
        	height=(int)(trimWidth*(height/width));
        	width=trimWidth;
        	newImg = new BufferedImage((int)trimWidth, (int)(trimWidth*(height/width)),
                    BufferedImage.TYPE_INT_RGB);
        }else if(height>trimHeight&&trimHeight*(width/height)<=trimWidth){
        	width=(int)(trimHeight*(width/height));
        	height=trimHeight;
        	newImg = new BufferedImage((int)(trimHeight*(width/height)), (int)trimHeight,
                    BufferedImage.TYPE_INT_RGB);
        }else{
        	newImg = new BufferedImage((int)width, (int)height,
                    BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D g = newImg.createGraphics();
        newImg=g.getDeviceConfiguration().createCompatibleImage((int)width,(int)height,Transparency.TRANSLUCENT);
        g.dispose();
        g=newImg.createGraphics();
        Image from = image.getScaledInstance((int)width,(int)height, image.SCALE_AREA_AVERAGING); 
        g.drawImage( from, 0, 0,(int)width,(int)height, null );
        g.dispose();
        return imageFormat(newImg,trimWidth, trimHeight);//将图片进行填充背景色;
    }
	
	/**
	 * 填充图片背景色
	 * @param trimWidth
	 * @param trimHeight
	 * @return
	 */
	public BufferedImage imageFormat(BufferedImage newImg,float trimWidth,float trimHeight){
		BufferedImage bg=new BufferedImage((int)trimWidth,(int)trimHeight,BufferedImage.TYPE_INT_RGB);
    	Graphics2D g = bg.createGraphics();
    	g.setBackground(color);//设置背景色
    	g.clearRect(0, 0, (int)trimWidth, (int)trimHeight);//通过使用当前绘图表面的背景色进行填充来清除指定的矩形。
    	int x=0;
    	int y=0;
    	if(newImg.getWidth()<trimWidth){
    		x=((int)trimWidth-newImg.getWidth())/2;
    	}
    	if(newImg.getHeight()<trimHeight){
    		y=((int)trimHeight-newImg.getHeight())/2;
    	}
    	g.drawImage(newImg,x,y,newImg.getWidth(),newImg.getHeight(),null);
    	g.dispose();
    	return bg;
	}
	
	/**
     * 保存图片至指定目录
     * @param f
     * @param img
     * @return
     */
    public Boolean save(String saveFilePath,BufferedImage img) {
    	Boolean bool=false;
        try {
        	bool=ImageIO.write(img, "png", new File(saveFilePath));
        } catch (IOException e) {
            throw new RuntimeException( "Problem writing image", e );
        }
        return bool;
    }
	
	private int getTrimmedWidth() {
        int height       = this.image.getHeight();
        int width        = this.image.getWidth();
        int trimmedWidth = 0;

        for(int i = 0; i < height; i++) {
            for(int j = width - 1; j >= 0; j--) {
                if(image.getRGB(j, i) != Color.WHITE.getRGB() &&
                        j > trimmedWidth) {
                    trimmedWidth = j;
                    break;
                }
            }
        }

        return trimmedWidth;
    }

    private int getTrimmedHeight() {
        int width         = this.image.getWidth();
        int height        = this.image.getHeight();
        int trimmedHeight = 0;

        for(int i = 0; i < width; i++) {
            for(int j = height - 1; j >= 0; j--) {
                if(image.getRGB(i, j) != Color.WHITE.getRGB() &&
                        j > trimmedHeight) {
                    trimmedHeight = j;
                    break;
                }
            }
        }

        return trimmedHeight;
    }
}
