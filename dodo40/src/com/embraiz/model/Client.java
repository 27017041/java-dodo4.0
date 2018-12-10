package com.embraiz.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@DynamicInsert(true)
@DynamicUpdate(true)
@Table(name = "obj_client")
public class Client {

	@Transient
	private Integer salesId;
	@Transient
	private String salesName;

	@Id
	@Column(name = "client_id")
	@GeneratedValue(generator = "paymentableGenerator")
	@GenericGenerator(name = "paymentableGenerator", strategy = "assigned")
	private Integer clientId;

	@Column(name = "client_name")
	private String clientName;
	
	@Column(name = "company_id")
	private Integer companyId;
	
	@Column(name = "company_name")
	private String companyName;

	@Column(name = "mobile_phone")
	private String mobilePhone;

	@Column(name = "general_phone")
	private String generalPhone;

	@Column(name = "floor_addr")
	private String floorAddr;

	@Column(name = "building_addr")
	private String buildingAddr;

	@Column(name = "street_addr")
	private String streetAddr;

	@Column(name = "block_addr")
	private String blockAddr;

	@Column(name = "district")
	private String district;

	@Column(name = "state")
	private Integer state;

	@Column(name = "email")
	private String email;

	@Column(name = "website")
	private String website;

	@Column(name = "fax")
	private String fax;

	@Column(name = "postal_code")
	private String postalCode;

	@Column(name = "birthday")
	private String birthday;

	@Column(name = "job")
	private String job;

	@Column(name = "status")
	private Integer status;

	@Column(name = "source_id")
	private Integer sourceId;
	
	@Column(name = "linkage_module")
	private String linkageModule;

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Integer getSalesId() {
		return salesId;
	}

	public void setSalesId(Integer salesId) {
		this.salesId = salesId;
	}

	public String getSalesName() {
		return salesName;
	}

	public void setSalesName(String salesName) {
		this.salesName = salesName;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getGeneralPhone() {
		return generalPhone;
	}

	public void setGeneralPhone(String generalPhone) {
		this.generalPhone = generalPhone;
	}

	public String getFloorAddr() {
		return floorAddr;
	}

	public void setFloorAddr(String floorAddr) {
		this.floorAddr = floorAddr;
	}

	public String getBlockAddr() {
		return blockAddr;
	}

	public void setBlockAddr(String blockAddr) {
		this.blockAddr = blockAddr;
	}

	public String getBuildingAddr() {
		return buildingAddr;
	}

	public void setBuildingAddr(String buildingAddr) {
		this.buildingAddr = buildingAddr;
	}

	public String getStreetAddr() {
		return streetAddr;
	}

	public void setStreetAddr(String streetAddr) {
		this.streetAddr = streetAddr;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public String getLinkageModule() {
		return linkageModule;
	}

	public void setLinkageModule(String linkageModule) {
		this.linkageModule = linkageModule;
	}

}
