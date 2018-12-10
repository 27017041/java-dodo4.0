package com.embraiz.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate(true)
@DynamicInsert(true)
@Table(name = "obj_contact")
public class Contact {

	@Id
	@Column(name = "contact_id")
	private Integer contactId;

	@Column(name = "company_id")
	private Integer companyId;
	
	@Column(name = "company_Name")
	private String companyName;

	@Column(name = "client_id")
	private Integer clientId;
	
	@Column(name = "client_Name")
	private String clientName;
	
	@Column(name = "contact_name")
	private String contactName;

	@Column(name = "general_phone")
	private String generalPhone;

	@Column(name = "shop_flat_room_addr")
	private String shopFlatRoomAddr;

	@Column(name = "floor_addr")
	private String floorAddr;

	@Column(name = "block_addr")
	private String blockAddr;

	@Column(name = "building_addr")
	private String buildingAddr;

	@Column(name = "street_no_addr")
	private String streetNoAddr;

	@Column(name = "tree_name_addr")
	private String treeNameAddr;

	@Column(name = "district")
	private String district;

	@Column(name = "area")
	private String area;

	@Column(name = "email")
	private String email;

	@Column(name = "website")
	private String website;

	@Column(name = "fax")
	private String fax;

	@Column(name = "name_in_local_language")
	private String nameInLocalLanguage;

	@Column(name = "postal_code")
	private String postalCode;

	@Column(name = "br_code")
	private String brCode;

	@Column(name = "direct_line")
	private String directLine;

	@Column(name = "mobile")
	private String mobile;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "job_title")
	private String jobTitle;

	@Column(name = "mr_or_miss")
	private String mrOrMiss;

	@Column(name = "status")
	private Integer status;

	@Column(name = "create_date")
	private Date createDate;
	
	@Column(name = "linkage_module")
	private String linkageModule;

	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
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

	public String getGeneralPhone() {
		return generalPhone;
	}

	public void setGeneralPhone(String generalPhone) {
		this.generalPhone = generalPhone;
	}

	public String getShopFlatRoomAddr() {
		return shopFlatRoomAddr;
	}

	public void setShopFlatRoomAddr(String shopFlatRoomAddr) {
		this.shopFlatRoomAddr = shopFlatRoomAddr;
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

	public String getStreetNoAddr() {
		return streetNoAddr;
	}

	public void setStreetNoAddr(String streetNoAddr) {
		this.streetNoAddr = streetNoAddr;
	}

	public String getTreeNameAddr() {
		return treeNameAddr;
	}

	public void setTreeNameAddr(String treeNameAddr) {
		this.treeNameAddr = treeNameAddr;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
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

	public String getNameInLocalLanguage() {
		return nameInLocalLanguage;
	}

	public void setNameInLocalLanguage(String nameInLocalLanguage) {
		this.nameInLocalLanguage = nameInLocalLanguage;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getBrCode() {
		return brCode;
	}

	public void setBrCode(String brCode) {
		this.brCode = brCode;
	}

	public String getDirectLine() {
		return directLine;
	}

	public void setDirectLine(String directLine) {
		this.directLine = directLine;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getMrOrMiss() {
		return mrOrMiss;
	}

	public void setMrOrMiss(String mrOrMiss) {
		this.mrOrMiss = mrOrMiss;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getLinkageModule() {
		return linkageModule;
	}

	public void setLinkageModule(String linkageModule) {
		this.linkageModule = linkageModule;
	}

}
