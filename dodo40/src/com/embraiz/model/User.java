package com.embraiz.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;


@Entity
@DynamicUpdate(true)
@DynamicInsert(true)
@Table(name="obj_user")
public class User implements Serializable{
	/**
	 *author george
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="user_id")
	@GeneratedValue(generator="paymentableGenerator")
	@GenericGenerator(name="paymentableGenerator",strategy="assigned")
	private Integer userId;
	
	@Column(name="login_name")
	private String loginName;
	
	@Column(name="email")
	private String email;
	
	@Column(name="mobile_phone")
	private String mobilePhone;
	
	@Column(name="direct_phone")
	private String directPhone;
	
	@Column(name="address1")
	private String address1;
	
	@Column(name="address2")
	private String address2;
	
	@Column(name="password")
	private String password;
	
	@Column(name="title")
	private String title;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="first_login")
	private int firstLogin;
	
	@Column(name="role_id")
	private String roleId;
	
	@Column(name="status")
	private int status;
	
	@Column(name="team_id")
	private Integer teamId;
	
	@Column(name="display_name")
	private String displayName;
	
	@Column(name="lang")
	private String lang;
	@Column(name="source")
	private Integer source;
	
	/* teamName、roleName、statusName 应该是无用的字段，忽略该字段的映射*/
	@Transient
	private String teamName;
	@Transient
	private String roleName;
	@Transient
	private String statusName;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getDirectPhone() {
		return directPhone;
	}

	public void setDirectPhone(String directPhone) {
		this.directPhone = directPhone;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getDisplayName() {
		if(firstName == null){
			firstName="";
		}
		if(lastName==null){
			lastName="";
		}
		return firstName+" "+lastName;
	}

	public int getFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(int firstLogin) {
		this.firstLogin = firstLogin;
	}

	@Override
	public String toString() {
		return "User [id=" + userId + ", loginName=" + loginName + ", email="
				+ email + ", mobilePhone=" + mobilePhone + ", directPhone="
				+ directPhone + ", address1=" + address1 + ", address2="
				+ address2 + ", password=" + password + ", title=" + title
				+ ", lastName=" + lastName + ", firstName=" + firstName
				+ ", firstLogin=" + firstLogin + "]";
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public Integer getTeamId() {
		return teamId;
	}

	public void setTeamId(Integer teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

}
