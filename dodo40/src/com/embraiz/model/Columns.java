package com.embraiz.model;

public class Columns {
	private String data;
	private String name;
	private Boolean visible;
	private Boolean orderable;
	private String defaultContent;
	private String width;
	private String className;
	
	private String linkHtml;
	private int isLink;
	private String methodName;
	private String keyIdName;
	
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getOrderable() {
		return orderable;
	}
	public void setOrderable(Boolean orderable) {
		this.orderable = orderable;
	}
	public String getDefaultContent() {
		return defaultContent;
	}
	public void setDefaultContent(String defaultContent) {
		this.defaultContent = defaultContent;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	public String getLinkHtml() {
		return linkHtml;
	}
	public void setLinkHtml(String linkHtml) {
		this.linkHtml = linkHtml;
	}
	public int getIsLink() {
		return isLink;
	}
	public void setIsLink(int isLink) {
		this.isLink = isLink;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getKeyIdName() {
		return keyIdName;
	}
	public void setKeyIdName(String keyIdName) {
		this.keyIdName = keyIdName;
	}
	
}
