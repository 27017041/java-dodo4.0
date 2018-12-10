package com.embraiz.model;

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

@Entity
@DynamicInsert(true)
@DynamicUpdate(true)
@Table(name="conf_form_grid")
public class ConfFormGrid {
	@Id
	@Column(name="conf_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer confId;
	
	@Column(name="conf_grid_id")
	private Integer confGridId;
	
	@Column(name="button_show")
	private Boolean buttonShow;
	
	@Column(name="relation_field")
	private String relationField;
	
	
	/*以下字段，暂未找到使用的地方，忽略字段的映射*/
	@Transient
	private Conf gridConf;
	@Transient
	private String gridName;
	@Transient
	private String gridTitle;
	
	@Transient
	private List<Columns> columns;
	@Transient
	private String gridHeader;
	@Transient
	private String keyIdName;
	
	@Transient
	private List<ConfButton> buttonList;
	
	public Integer getConfId() {
		return confId;
	}
	public void setConfId(Integer confId) {
		this.confId = confId;
	}
	public Integer getConfGridId() {
		return confGridId;
	}
	public void setConfGridId(Integer confGridId) {
		this.confGridId = confGridId;
	}
	public Boolean getButtonShow() {
		return buttonShow;
	}
	public void setButtonShow(Boolean buttonShow) {
		this.buttonShow = buttonShow;
	}
	public String getRelationField() {
		return relationField;
	}
	public void setRelationField(String relationField) {
		this.relationField = relationField;
	}
	public String getGridHeader() {
		return gridHeader;
	}
	public void setGridHeader(String gridHeader) {
		this.gridHeader = gridHeader;
	}
	public String getKeyIdName() {
		return keyIdName;
	}
	public void setKeyIdName(String keyIdName) {
		this.keyIdName = keyIdName;
	}
	public List<ConfButton> getButtonList() {
		return buttonList;
	}
	public void setButtonList(List<ConfButton> buttonList) {
		this.buttonList = buttonList;
	}
	public List<Columns> getColumns() {
		return columns;
	}
	public void setColumns(List<Columns> columns) {
		this.columns = columns;
	}
	public Conf getGridConf() {
		return gridConf;
	}
	public void setGridConf(Conf gridConf) {
		this.gridConf = gridConf;
	}
	public String getGridName() {
		return gridName;
	}
	public void setGridName(String gridName) {
		this.gridName = gridName;
	}
	public String getGridTitle() {
		return gridTitle;
	}
	public void setGridTitle(String gridTitle) {
		this.gridTitle = gridTitle;
	}
}
