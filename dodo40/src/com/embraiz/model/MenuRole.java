package com.embraiz.model;

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
@Table(name="map_menu_role")
public class MenuRole {
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name="role_id")
	private Integer roleId;
	
	@Column(name="menu_id")
	private Integer menuId;
	
	@Column(name="right_insert")
	private int rightInsert;
	
	@Column(name="right_read")
	private int rightRead;
	
	@Column(name="right_update")
	private int rightUpdate;
	
	@Column(name="right_delete")
	private int rightDelete;
	
	/*menuOrginal应该是无用的字段，忽略该字段的映射*/
	@Transient
	private String menuOrginal;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public Integer getMenuId() {
		return menuId;
	}
	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}
	public int getRightInsert() {
		return rightInsert;
	}
	public void setRightInsert(int rightInsert) {
		this.rightInsert = rightInsert;
	}
	public int getRightUpdate() {
		return rightUpdate;
	}
	public void setRightUpdate(int rightUpdate) {
		this.rightUpdate = rightUpdate;
	}
	public int getRightDelete() {
		return rightDelete;
	}
	public void setRightDelete(int rightDelete) {
		this.rightDelete = rightDelete;
	}
	public String getMenuOrginal() {
		return menuOrginal;
	}
	public void setMenuOrginal(String menuOrginal) {
		this.menuOrginal = menuOrginal;
	}
	public int getRightRead() {
		return rightRead;
	}
	public void setRightRead(int rightRead) {
		this.rightRead = rightRead;
	}
}
