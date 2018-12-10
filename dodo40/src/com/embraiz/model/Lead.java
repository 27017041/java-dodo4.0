package com.embraiz.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate(true)
@DynamicInsert(true)
@Table(name = "t_lead")
public class Lead {

	@Id
	@Column(name = "lead_id")
	private Integer leadId;

	@Column(name = "lead_name")
	private String leadName;

	@Column(name = "source_id")
	private Integer sourceId;

	@Column(name = "notes")
	private String notes;

	@Column(name = "assign_to")
	private Integer assignTo;

	@Column(name = "assign_expiry_time")
	private Date assignExpiryTime;

	@Column(name = "client")
	private String client;

	@Column(name = "doing_business_other_name")
	private String doingBusinessOtherName;

	@Column(name = "client_phone")
	private String clientPhone;

	@Column(name = "client_fax")
	private String clientFax;

	@Column(name = "address1")
	private String address1;

	@Column(name = "address2")
	private String address2;

	@Column(name = "district_id")
	private Integer districtId;

	@Column(name = "location_id")
	private Integer locationId;

	@Column(name = "website")
	private String website;

	@Column(name = "client_email")
	private String clientEmail;

	@Column(name = "natureofbusiness_id")
	private Integer natureofbusinessId;

	@Column(name = "contact")
	private String contact;

	@Column(name = "contact_title")
	private String contactTitle;

	@Column(name = "contact_direct_line")
	private String contactDirectLine;

	@Column(name = "contact_direct_fax")
	private String contactDirectFax;

	@Column(name = "contact_mobile")
	private String contactMobile;

	@Column(name = "contact_email")
	private String contactEmail;

	@Column(name = "converted")
	private Boolean converted;

	@Column(name = "opt_out_time")
	private Date optOutTime;

	@Column(name = "marketing_code")
	private String marketingCode;

	@Column(name = "shop_flat_room_no")
	private String shopFlatRoomNo;

	@Column(name = "floor")
	private String floor;

	@Column(name = "block")
	private String block;

	@Column(name = "estate")
	private String estate;

	@Column(name = "street_no")
	private String streetNo;

	@Column(name = "postal_code")
	private String postalCode;

	@Column(name = "opt_out")
	private String optOut;

	@Column(name = "return_mail")
	private String returnMail;

	@Column(name = "create_date")
	private String createDate;

	public Integer getLeadId() {
		return leadId;
	}

	public void setLeadId(Integer leadId) {
		this.leadId = leadId;
	}

	public String getLeadName() {
		return leadName;
	}

	public void setLeadName(String leadName) {
		this.leadName = leadName;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Integer getAssignTo() {
		return assignTo;
	}

	public void setAssignTo(Integer assignTo) {
		this.assignTo = assignTo;
	}

	public Date getAssignExpiryTime() {
		return assignExpiryTime;
	}

	public void setAssignExpiryTime(Date assignExpiryTime) {
		this.assignExpiryTime = assignExpiryTime;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getDoingBusinessOtherName() {
		return doingBusinessOtherName;
	}

	public void setDoingBusinessOtherName(String doingBusinessOtherName) {
		this.doingBusinessOtherName = doingBusinessOtherName;
	}

	public String getClientPhone() {
		return clientPhone;
	}

	public void setClientPhone(String clientPhone) {
		this.clientPhone = clientPhone;
	}

	public String getClientFax() {
		return clientFax;
	}

	public void setClientFax(String clientFax) {
		this.clientFax = clientFax;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public Integer getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Integer districtId) {
		this.districtId = districtId;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	public Integer getNatureofbusinessId() {
		return natureofbusinessId;
	}

	public void setNatureofbusinessId(Integer natureofbusinessId) {
		this.natureofbusinessId = natureofbusinessId;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getContactTitle() {
		return contactTitle;
	}

	public void setContactTitle(String contactTitle) {
		this.contactTitle = contactTitle;
	}

	public String getContactDirectLine() {
		return contactDirectLine;
	}

	public void setContactDirectLine(String contactDirectLine) {
		this.contactDirectLine = contactDirectLine;
	}

	public String getContactDirectFax() {
		return contactDirectFax;
	}

	public void setContactDirectFax(String contactDirectFax) {
		this.contactDirectFax = contactDirectFax;
	}

	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public Boolean getConverted() {
		return converted;
	}

	public void setConverted(Boolean converted) {
		this.converted = converted;
	}

	public Date getOptOutTime() {
		return optOutTime;
	}

	public void setOptOutTime(Date optOutTime) {
		this.optOutTime = optOutTime;
	}

	public String getMarketingCode() {
		return marketingCode;
	}

	public void setMarketingCode(String marketingCode) {
		this.marketingCode = marketingCode;
	}

	public String getShopFlatRoomNo() {
		return shopFlatRoomNo;
	}

	public void setShopFlatRoomNo(String shopFlatRoomNo) {
		this.shopFlatRoomNo = shopFlatRoomNo;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	public String getEstate() {
		return estate;
	}

	public void setEstate(String estate) {
		this.estate = estate;
	}

	public String getStreetNo() {
		return streetNo;
	}

	public void setStreetNo(String streetNo) {
		this.streetNo = streetNo;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getOptOut() {
		return optOut;
	}

	public void setOptOut(String optOut) {
		this.optOut = optOut;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getReturnMail() {
		return returnMail;
	}

	public void setReturnMail(String returnMail) {
		this.returnMail = returnMail;
	}

}
