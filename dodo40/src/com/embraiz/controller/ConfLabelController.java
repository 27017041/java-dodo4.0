package com.embraiz.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.embraiz.helper.SystemHelper;
import com.embraiz.service.ConfLabelService;

@Controller
@RequestMapping("/label")
public class ConfLabelController {
	
	@Resource
	private ConfLabelService confLabelService;
	@Resource
	private SystemHelper systemHelper;
	
	/**
	 * 根据路由模块获取label
	 * @param module
	 * @param session
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getLabel")
	public void getLabel(@RequestParam(value="module") String module,
			HttpSession session,HttpServletResponse response) throws IOException{
		JSONObject json = new JSONObject();
		String lang = (String) session.getAttribute("lang");
		if(lang==null){
			lang = "en";
		}
		json.put("label", systemHelper.getLabelListByModule(lang, module));
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(JSON.toJSONString(json, SerializerFeature.DisableCircularReferenceDetect));
		response.getWriter().flush();
	}
	
	/**
	 * 获取全局模块获取label
	 * @param module
	 * @param session
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getGlobalLabel")
	public void getGlobalLabel(HttpSession session,
			HttpServletResponse response) throws IOException{
		JSONObject json = new JSONObject();
		String lang = (String) session.getAttribute("lang");
		if(lang==null){
			lang = "en";
		}
		json.put("lang", lang);
		
		String[] labelType = {"text","tips"};
		json.put("label", systemHelper.getLabelListByType(lang, labelType));
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
}
