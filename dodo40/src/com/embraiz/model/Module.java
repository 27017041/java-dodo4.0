package com.embraiz.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@DynamicInsert(true)
@DynamicUpdate(true)
@Table(name="core_module")
public class Module {

	@Id
	@Column(name="module_name")
	@GeneratedValue(generator="paymentableGenerator")
	@GenericGenerator(name="paymentableGenerator",strategy="assigned")
	private String moduleName;
	
	@Column(name="create_link")
	private String createLink;
	
	@Column(name="update_link")
	private String updateLink;
	
	@Column(name="read_link")
	private String readLink;
	
	@Column(name="delete_link")
	private String deleteLink;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getCreateLink() {
		return createLink;
	}

	public void setCreateLink(String createLink) {
		this.createLink = createLink;
	}

	public String getUpdateLink() {
		return updateLink;
	}

	public void setUpdateLink(String updateLink) {
		this.updateLink = updateLink;
	}

	public String getReadLink() {
		return readLink;
	}

	public void setReadLink(String readLink) {
		this.readLink = readLink;
	}

	public String getDeleteLink() {
		return deleteLink;
	}

	public void setDeleteLink(String deleteLink) {
		this.deleteLink = deleteLink;
	}
	
}
