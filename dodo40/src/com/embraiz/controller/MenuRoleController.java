package com.embraiz.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.MenuRole;
import com.embraiz.model.Obj;
import com.embraiz.model.Role;
import com.embraiz.model.User;
import com.embraiz.service.MenuRoleService;
import com.embraiz.service.ObjService;
import com.embraiz.service.RoleService;

@Controller
@RequestMapping("/menuRole")
public class MenuRoleController {
	
	@Resource
	private MenuRoleService menuRoleService;
	@Resource
	private RoleService roleService;
	@Resource
	private ObjService objSerivce;
	
	/**
	 * 根据user所属的entity,获取这个entity下所有role数据
	 * @param response
	 * @param request
	 * @throws IOException
	 */
	@RequestMapping("/getRoleSelectData")
	public void getRoleSelectData(
			HttpServletResponse response,
			HttpServletRequest request)
		throws IOException{
		User user = (User)request.getSession().getAttribute("user");
		JSONArray json = new JSONArray();
		if(user!=null){
			Obj obj  = objSerivce.getObjData(user.getUserId());
			
			if(obj!=null){
				List<Role> list = new ArrayList<Role>();
				if(obj.getObjParentId()==0){//不隶属任何entity的superadmin
					list = roleService.getAllRole();
				}else{
					list = roleService.getRoleByEntity(obj.getL0());
				}
				json.add(list);
			}
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	
	/**
	 * 获取role的所有菜单权限
	 * @param response
	 * @param request
	 * @param roleId
	 * @throws IOException
	 */
	@RequestMapping("/getRoleRightMap")
	public void getRoleRightMap(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam(value="roleId") int roleId) 
					throws IOException{
		String lang = (String) request.getSession().getAttribute("lang");
		if(lang==null){
			lang = "en";
		}
		List<MenuRole> list = menuRoleService.getRoleRightMap(roleId, lang);
		HashMap<String, Object> menuMap = new HashMap<String, Object>();
		if(list.size()>0){
			for(MenuRole menu:list){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("menuId", menu.getMenuId());
				map.put("read", menu.getRightRead()==1?true:false);
				map.put("insert", menu.getRightInsert()==1?true:false);
				map.put("update", menu.getRightUpdate()==1?true:false);
				map.put("delete", menu.getRightDelete()==1?true:false);
				if(menu.getRightRead()==1 && menu.getRightInsert()==1 && menu.getRightUpdate()==1 && menu.getRightDelete()==1){
					map.put("all", true);
				}
				menuMap.put("menu"+menu.getMenuId(), map);
			}
		}
		JSONObject json = new JSONObject();
		json.put("rightData", menuMap);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	
	/**
	 * 修改更新role的菜单权限
	 * @param response
	 * @param request
	 * @param roleId
	 * @param rightData
	 * @throws IOException
	 */
	@RequestMapping("/updateRoleRight")
	public void updateRoleRight(
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam(value="roleId") int roleId,
			@RequestParam(value="rightData") String rightData) 
					throws IOException{
		JSONObject json = JSONObject.parseObject(rightData);
		JSONObject jsonPut = new JSONObject();
		int status=1;
		String msg = "succSave";
		try{
			for(String str:json.keySet()){
				JSONObject json1 = json.getJSONObject(str);
				MenuRole mr = new MenuRole();
				if(json1.get("menuId")!=null){
					mr.setMenuId(Integer.parseInt(json1.get("menuId").toString()));
				}else{
					String menuId = str.substring(4, str.length());//menu1,截取出menuId是1
					mr.setMenuId(Integer.parseInt(menuId));
				}
				if(json1.get("read")!=null && json1.get("read").toString().equals("true")){
					mr.setRightRead(1);
				}else{
					mr.setRightRead(0);
				}
				if(json1.get("insert")!=null && json1.get("insert").toString().equals("true")){
					mr.setRightInsert(1);
				}else{
					mr.setRightInsert(0);
				}
				if(json1.get("update")!=null && json1.get("update").toString().equals("true")){
					mr.setRightUpdate(1);
				}else{
					mr.setRightUpdate(0);
				}
				if(json1.get("delete")!=null && json1.get("delete").toString().equals("true")){
					mr.setRightDelete(1);
				}else{
					mr.setRightDelete(0);
				}
				
				menuRoleService.updateRoleRight(roleId, mr);
				
			}
		}catch(Exception e){
			status=0;
			msg = "failSave";
		}
		jsonPut.put("status", status);
		jsonPut.put("msg", msg);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(jsonPut);
		response.getWriter().flush();
	}

}
