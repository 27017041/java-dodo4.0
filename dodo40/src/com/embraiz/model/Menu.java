package com.embraiz.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate(true)
@DynamicInsert(true)
@Table(name="core_menu")
public class Menu {
	
	@Id
	@Column(name="menu_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer menuId;
	
	@Column(name="module_name")
	private String moduleName;
	
	@Column(name="menu_link")
	private String menuLink;
	
	@Column(name="menu_icon")
	private String menuIcon;
	
	@Column(name="parent_id")
	private Integer parentId;
	
	@Column(name="create_time")
	private Date creteTime;
	
	@Column(name="status")
	private Integer status;
	
	@Column(name="sort")
	private Integer sort;
	
	@Column(name="menu_level")
	private Integer menuLevel;
	
	@Column(name="has_childs")
	private Integer hasChilds;
	
	/*忽略labelText字段的映射*/
	@Transient
	private String labelText;

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	

	public String getMenuLink() {
		return menuLink;
	}

	public void setMenuLink(String menuLink) {
		this.menuLink = menuLink;
	}

	public String getMenuIcon() {
		return menuIcon;
	}

	public void setMenuIcon(String menuIcon) {
		this.menuIcon = menuIcon;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Date getCreteTime() {
		return creteTime;
	}

	public void setCreteTime(Date creteTime) {
		this.creteTime = creteTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getMenuLevel() {
		return menuLevel;
	}

	public void setMenuLevel(Integer menuLevel) {
		this.menuLevel = menuLevel;
	}

	public Integer getHasChilds() {
		return hasChilds;
	}

	public void setHasChilds(Integer hasChilds) {
		this.hasChilds = hasChilds;
	}

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	
	

}
