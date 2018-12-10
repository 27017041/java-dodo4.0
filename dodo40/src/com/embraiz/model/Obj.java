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
@Table(name = "obj")
public class Obj {

	@Id
	@Column(name = "obj_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer objId;

	@Column(name = "obj_title")
	private String objTitle;

	@Column(name = "obj_desc")
	private String objDesc;

	@Column(name = "status")
	private int status;

	@Column(name = "keyword")
	private String keyword;

	@Column(name = "obj_parent_id")
	private Integer objParentId;

	@Column(name = "l0")
	private Integer l0;

	@Column(name = "l1")
	private Integer l1;

	@Column(name = "l2")
	private Integer l2;

	@Column(name = "l3")
	private Integer l3;

	@Column(name = "l4")
	private Integer l4;

	@Column(name = "l5")
	private Integer l5;

	@Column(name = "l6")
	private Integer l6;

	@Column(name = "l7")
	private Integer l7;

	@Column(name = "l8")
	private Integer l8;

	@Column(name = "l9")
	private Integer l9;

	@Column(name = "obj_level")
	private int objLevel;

	@Column(name = "obj_type_id")
	private int objTypeId;

	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "owner_id")
	private Integer ownerId;

	@Column(name = "update_date")
	private Date updateDate;

	@Transient
	private Integer leadId;

	@Column(name = "is_dirty_lock")
	private Integer isDirtylock;

	public Integer getObjId() {
		return objId;
	}

	public void setObjId(Integer objId) {
		this.objId = objId;
	}

	public String getObjTitle() {
		return objTitle;
	}

	public void setObjTitle(String objTitle) {
		this.objTitle = objTitle;
	}

	public String getObjDesc() {
		return objDesc;
	}

	public void setObjDesc(String objDesc) {
		this.objDesc = objDesc;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getObjParentId() {
		return objParentId;
	}

	public void setObjParentId(Integer objParentId) {
		this.objParentId = objParentId;
	}

	public Integer getL0() {
		return l0;
	}

	public void setL0(Integer l0) {
		this.l0 = l0;
	}

	public Integer getL1() {
		return l1;
	}

	public void setL1(Integer l1) {
		this.l1 = l1;
	}

	public Integer getL2() {
		return l2;
	}

	public void setL2(Integer l2) {
		this.l2 = l2;
	}

	public Integer getL3() {
		return l3;
	}

	public void setL3(Integer l3) {
		this.l3 = l3;
	}

	public Integer getL4() {
		return l4;
	}

	public void setL4(Integer l4) {
		this.l4 = l4;
	}

	public Integer getL5() {
		return l5;
	}

	public void setL5(Integer l5) {
		this.l5 = l5;
	}

	public Integer getL6() {
		return l6;
	}

	public void setL6(Integer l6) {
		this.l6 = l6;
	}

	public Integer getL7() {
		return l7;
	}

	public void setL7(Integer l7) {
		this.l7 = l7;
	}

	public Integer getL8() {
		return l8;
	}

	public void setL8(Integer l8) {
		this.l8 = l8;
	}

	public Integer getL9() {
		return l9;
	}

	public void setL9(Integer l9) {
		this.l9 = l9;
	}

	public int getObjLevel() {
		return objLevel;
	}

	public void setObjLevel(int objLevel) {
		this.objLevel = objLevel;
	}

	public int getObjTypeId() {
		return objTypeId;
	}

	public void setObjTypeId(int objTypeId) {
		this.objTypeId = objTypeId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getLeadId() {
		return leadId;
	}

	public void setLeadId(Integer leadId) {
		this.leadId = leadId;
	}

	public Integer getIsDirtylock() {
		return isDirtylock;
	}

	public void setIsDirtylock(Integer isDirtylock) {
		this.isDirtylock = isDirtylock;
	}

}
