package com.embraiz.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.CommonService;
import com.embraiz.service.WorkflowService;
import com.embraiz.util.ExcelExport;
import com.embraiz.util.MainUtil;

@Controller
@RequestMapping("/workflow")
public class WorkflowController {

	@Resource
	private BaseService baseService;
	@Resource
	private WorkflowService workflowService;
	@Resource
	private CommonService commonService;

	// display the approval list on top of an object
	@RequestMapping("/getApprovalList")
	public void getApprovalList(@RequestParam("obj_id") String obj_id, HttpServletResponse response, HttpSession session) throws IOException {
		String sql = "select display_name, action_time, v_wf_job_list.step_status from v_wf_job_list inner join obj_user on obj_user.user_id = v_wf_job_list.user_id  where  module_id=:module_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("module_id", obj_id);
		MainUtil mainUtil = new MainUtil();
		mainUtil.sqlToJsonResponse(response, baseService.getList(sql, params));

	}

	// get the current status upon the users
	@RequestMapping("/getStatus")
	public void getStatus(@RequestParam("obj_id") String obj_id, HttpServletResponse response, HttpSession session) throws IOException {
		// approved, rejected, pending, my_approval
		User user = (User) session.getAttribute("user");
		String response_output = workflowService.getStatus(obj_id, user.getUserId().toString());
		MainUtil.reponseFlush(response, response_output);

	}

	// when user push the approve and reject button
	@RequestMapping("/TakeAction")
	public void TakeAction(@RequestParam("action") String action, @RequestParam("obj_id") String obj_id, HttpServletResponse response,
			HttpSession session) throws IOException {
		// check if the action is for the current approver

		JSONObject json = new JSONObject();
		User user = (User) session.getAttribute("user");
		user.setUserId(2);
		String response_output = workflowService.getStatus(obj_id, user.getUserId().toString());
		if (!"my_approval".equals(response_output)) {
			MainUtil.reponseFlush(response, "not_my_turn");
		} else {
			if (action.equals("approved")) {
				// if user click approved, move to the next approver or send the
				// whole job to complete
				int flag = (Integer) workflowService.updateApprover(obj_id);
				if (flag > 0) {
					json.put("Msg", "Approved Success");
				} else {
					json.put("Msg", "Approved Error");
				}
			} else if (action.equals("reject")) {
				// if user click reject, reset everything and start all again
				int flag = workflowService.update(obj_id);// update
				if (flag > 0) {
					json.put("Msg", "Reject Success");
				} else {
					json.put("Msg", "Reject Error");
				}
			}
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(json);
			response.getWriter().flush();
		}
	}

	@ResponseBody
	@RequestMapping("exportFileByWfJob")
	public void exportFile(HttpServletResponse response) {
		try {
			String sql = "select * from v_wf_job_report where job_status=:job_status";
			Map<String, Object> params = new HashMap<>();
			params.put("job_status", "pending");
			List<Map<String, Object>> exportList = baseService.getList(sql, params);
			ExcelExport excel = new ExcelExport();
			excel.createExcelAllWfJob(exportList, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
