package com.embraiz.service;

import java.net.UnknownHostException;

import com.embraiz.model.User;

public interface WorkflowService {
	public String getStatus(String obj_id, String user_id);

	public int update(String obj_id);

	public int createJob(String obj_id, User user);

	public int updateApprover(String obj_id) throws UnknownHostException;

	public int createInvoiceJob(String obj_id, User user);

}
