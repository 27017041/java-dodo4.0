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
@Table(name="core_grid_item")
public class GridItem {

	@Id
	@Column(name="grid_item_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer gridItemId;
	
	@Column(name="field_id")
	private Integer fieldId;
	
	@Column(name="sorting")
	private int sorting;
	
	@Column(name="sort_by")
	private String sortBy;

	public Integer getGridItemId() {
		return gridItemId;
	}

	public void setGridItemId(Integer gridItemId) {
		this.gridItemId = gridItemId;
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

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
	
	
}
