package com.embraiz.model;

//用来处理json对象
public class Data{
	//默认状态是fail
	private String status = "fail";
	private String mssage = "";
	private Object data = new Object();

	@Override
	public String toString() {
		return "Data [status=" + status + ", mssage=" + mssage + ", data="
				+ data + "]";
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMssage() {
		return mssage;
	}

	public void setMssage(String mssage) {
		this.mssage = mssage;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
