package com.embraiz.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate(true)
@DynamicInsert(true)
@Table(name="t_message_template")
public class MessageTemplate {

	@Id
	@Column(name="template_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer templateId;
	
	@Column(name="template_name")
	private String templateName;
	
	@Column(name="content_email")
	private String contentEmail;
	
	@Column(name="content_mobile")
	private String contentMobile;
	
	@Column(name="language")
	private String language;
	
	@Column(name="content_site")
	private String contentSite;

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getContentEmail() {
		return contentEmail;
	}

	public void setContentEmail(String contentEmail) {
		this.contentEmail = contentEmail;
	}

	public String getContentMobile() {
		return contentMobile;
	}

	public void setContentMobile(String contentMobile) {
		this.contentMobile = contentMobile;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getContentSite() {
		return contentSite;
	}

	public void setContentSite(String contentSite) {
		this.contentSite = contentSite;
	}
	
	
}
