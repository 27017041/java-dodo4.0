package com.embraiz.model;
@SuppressWarnings("unused")
public class Page {

    
    private int page; // 当前页
    private int count; // 每页多少行
    private int size; // 共多少行
	private int start;// 当前页起始行
    private int end;// 结束行
    private int pages; // 共多少页
    
	public int getPage() {
		return page+1;
	}
	public void setPage(int page) {
		this.page = page-1;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getStart() {
		return this.start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return page*count+count;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getPages() {
		if(size%count==0){
			return pages = size/count;
		}else{
			return pages = size/count+1;
		}
	}
	public void setPages(int pages) {
		this.pages = pages;
	}
    
    
    
    
}
