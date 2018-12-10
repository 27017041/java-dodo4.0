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
@DynamicInsert(true)
@DynamicUpdate(true)
@Table(name="conf_grid")
public class ConfGird {
	@Id
	@Column(name="grid_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer gridId;
	
	@Column(name="field_name")
	private String fieldName;
	
	@Column(name="sorting")
	private Integer sorting;
	
	@Column(name="conf_id")
	private Integer confId;
	
	@Column(name="search_type")
	private String searchType;
	
	@Column(name="regular_expression")
	private String regularExpression;
	
	@Column(name="is_showin_grid")
	private int isShowinGrid;
	
	@Column(name="is_showin_search")
	private int isShowinSearch;
	
	@Column(name="is_default_grid")
	private int isDefaultGrid;
	
	@Column(name="module_name")
	private String moduleName;
	
	@Column(name="select_table")
	private String selectTable;
	
	@Column(name="select_is_obj_data")
	private int selectIsObjData;
	
	@Column(name="select_id_field")
	private String selectIdField;
	
	@Column(name="select_value_field")
	private String selectValueField;
	
	@Column(name="select_order_field")
	private String selectOrderField;
	
	@Column(name="select_from_field")
	private String selectFromField;
	
	@Column(name="sub_to")
	private Integer subTo;
	
	@Column(name="sub_from")
	private Integer subFrom;
	
	@Column(name="is_link")
	private int isLink;
	
	@Column(name="link_method")
	private String linkMethod;
	
	@Column(name="is_showin_sub_grid")
	private int isShowinSubGrid;
	
	@Column(name="select_type_field")
	private String selectTypeField;
	
	@Column(name="select_type_id")
	private Integer selectTypeId;
	
	
	/* labelText、labelType、langCode 应该是无用的字段,忽略该字段的映射*/
	@Transient
	private String labelText;
	@Transient
	private String labelType;
	@Transient
	private String langCode;
	
	public Integer getGridId() {
		return gridId;
	}
	public void setGridId(Integer gridId) {
		this.gridId = gridId;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Integer getSorting() {
		return sorting;
	}
	public void setSorting(Integer sorting) {
		this.sorting = sorting;
	}
	public Integer getConfId() {
		return confId;
	}
	public void setConfId(Integer confId) {
		this.confId = confId;
	}
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	public String getRegularExpression() {
		return regularExpression;
	}
	public void setRegularExpression(String regularExpression) {
		this.regularExpression = regularExpression;
	}
	public int getIsShowinGrid() {
		return isShowinGrid;
	}
	public void setIsShowinGrid(int isShowinGrid) {
		this.isShowinGrid = isShowinGrid;
	}
	public int getIsShowinSearch() {
		return isShowinSearch;
	}
	public void setIsShowinSearch(int isShowinSearch) {
		this.isShowinSearch = isShowinSearch;
	}
	public int getIsDefaultGrid() {
		return isDefaultGrid;
	}
	public void setIsDefaultGrid(int isDefaultGrid) {
		this.isDefaultGrid = isDefaultGrid;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getSelectTable() {
		return selectTable;
	}
	public void setSelectTable(String selectTable) {
		this.selectTable = selectTable;
	}
	public String getSelectIdField() {
		return selectIdField;
	}
	public void setSelectIdField(String selectIdField) {
		this.selectIdField = selectIdField;
	}
	public String getSelectValueField() {
		return selectValueField;
	}
	public void setSelectValueField(String selectValueField) {
		this.selectValueField = selectValueField;
	}
	public String getLabelText() {
		return labelText;
	}
	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}
	public String getLabelType() {
		return labelType;
	}
	public void setLabelType(String labelType) {
		this.labelType = labelType;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	public String getSelectOrderField() {
		return selectOrderField;
	}
	public void setSelectOrderField(String selectOrderField) {
		this.selectOrderField = selectOrderField;
	}
	public int getIsLink() {
		return isLink;
	}
	public void setIsLink(int isLink) {
		this.isLink = isLink;
	}
	public String getLinkMethod() {
		return linkMethod;
	}
	public void setLinkMethod(String linkMethod) {
		this.linkMethod = linkMethod;
	}
	public String getSelectFromField() {
		return selectFromField;
	}
	public void setSelectFromField(String selectFromField) {
		this.selectFromField = selectFromField;
	}
	public int getSelectIsObjData() {
		return selectIsObjData;
	}
	public void setSelectIsObjData(int selectIsObjData) {
		this.selectIsObjData = selectIsObjData;
	}
	public Integer getSubTo() {
		return subTo;
	}
	public void setSubTo(Integer subTo) {
		this.subTo = subTo;
	}
	public Integer getSubFrom() {
		return subFrom;
	}
	public void setSubFrom(Integer subFrom) {
		this.subFrom = subFrom;
	}
	public int getIsShowinSubGrid() {
		return isShowinSubGrid;
	}
	public void setIsShowinSubGrid(int isShowinSubGrid) {
		this.isShowinSubGrid = isShowinSubGrid;
	}
	public String getSelectTypeField() {
		return selectTypeField;
	}
	public void setSelectTypeField(String selectTypeField) {
		this.selectTypeField = selectTypeField;
	}
	public Integer getSelectTypeId() {
		return selectTypeId;
	}
	public void setSelectTypeId(Integer selectTypeId) {
		this.selectTypeId = selectTypeId;
	}
	
	
	
	
}
