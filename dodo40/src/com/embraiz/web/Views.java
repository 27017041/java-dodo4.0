package com.embraiz.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.util.StringFormat;


@Controller
public class Views {

     @RequestMapping("/error/{errorCode}")
     public void page(@PathVariable String errorCode,
    		 HttpServletResponse response,
    		 HttpServletRequest request) throws IOException{
    	JSONObject json = new JSONObject();
 		json.put("error", errorCode);
    	response.setCharacterEncoding("utf-8");
 		response.getWriter().print(json);
 		response.getWriter().flush();
     }
     
}
