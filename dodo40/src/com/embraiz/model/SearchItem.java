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
@Table(name="core_search_item")
public class SearchItem {

	@Id
	@Column(name="search_item_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer searchItemId;
	
	@Column(name="field_id")
	private Integer fieldId;
	
	@Column(name="sorting")
	private int sorting;

	public Integer getSearchItemId() {
		return searchItemId;
	}

	public void setSearchItemId(Integer searchItemId) {
		this.searchItemId = searchItemId;
	}

	public Integer getFieldId() {
		return fieldId;
	}

	public void setFieldId(Integer fieldId) {
		this.fieldId = fieldId;
	}

	public int getSorting() {
		return sorting;
	}

	public void setSorting(int sorting) {
		this.sorting = sorting;
	}
	
	
	
}
