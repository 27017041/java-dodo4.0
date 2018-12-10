package com.embraiz.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate(true)
@DynamicInsert(true)
@Table(name="conf_image_water_mark")
public class WaterMark {

	@Id
	@Column(name="conf_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer confId;
	
	@Column(name="create_date")
	private String createDate;
	
	@Column(name="type")
	private String type;
	
	@Column(name="position")
	private String position;
	
	@Column(name="scaling")
	private String scaling;
	
	@Column(name="size")
	private Integer size;
	
	@Column(name="transparency")
	private Integer transparency;
	
	@Column(name="url")
	private String url;
	
	@Column(name="path")
	private String path;
	
	@Column(name="text")
	private String text;
	
	@Column(name="text_font")
	private String textFont;
	
	@Column(name="text_size")
	private Integer textSize;
	
	@Column(name="text_color")
	private String textColor;
	
	@Column(name="text_margin")
	private Integer textMargin;
	
	@Column(name="text_bg_color")
	private String textBgColor;

	public Integer getConfId() {
		return confId;
	}

	public void setConfId(Integer confId) {
		this.confId = confId;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getScaling() {
		return scaling;
	}

	public void setScaling(String scaling) {
		this.scaling = scaling;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getTransparency() {
		return transparency;
	}

	public void setTransparency(Integer transparency) {
		this.transparency = transparency;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTextFont() {
		return textFont;
	}

	public void setTextFont(String textFont) {
		this.textFont = textFont;
	}

	public Integer getTextSize() {
		return textSize;
	}

	public void setTextSize(Integer textSize) {
		this.textSize = textSize;
	}

	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	public Integer getTextMargin() {
		return textMargin;
	}

	public void setTextMargin(Integer textMargin) {
		this.textMargin = textMargin;
	}

	public String getTextBgColor() {
		return textBgColor;
	}

	public void setTextBgColor(String textBgColor) {
		this.textBgColor = textBgColor;
	}
	
	
	
}
