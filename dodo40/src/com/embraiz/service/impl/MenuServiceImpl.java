package com.embraiz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.model.Menu;
import com.embraiz.service.MenuService;

@Service
@Transactional
public class MenuServiceImpl implements MenuService{
	
	@Resource
	private BaseDao baseDao;
	
	@Override
	public List<Menu> menuList(int parentId,String lang,String roleIds){
		StringBuffer sql = new StringBuffer();
		Map<String,Object> params = new HashMap<String,Object>();
		
		sql.append("select DISTINCT menu_id,parent_id,menu_orginal,menu_link,menu_icon,has_childs,menu_level,label_text as menu_name");
		sql.append(" from v_menu_role where parent_id= :parentId and status=1 and lang_code= :lang ");
		sql.append(" and right_read=1 and role_id in ( :roleIds )");
		sql.append(" order by sort");
		
		params.put("parentId", parentId);
		params.put("lang", lang);
		params.put("roleIds", roleIds);
		
		//定义一个需要返回的数据类型
		List<Menu> menuList = new ArrayList<Menu>();
		
		List<Map<String,Object>> objectList = baseDao.getList(sql.toString(), params);
		
		//将objectList的值放入menuList
		for(int i=0;i<objectList.size();i++){
			Map<String,Object> map = objectList.get(i);
			
			Menu menu = new Menu();
			
				if(map.get("menu_id")!=null){
					menu.setMenuId(Integer.parseInt(map.get("menu_id").toString()));
				}
				if(map.get("parent_id")!=null){
					menu.setParentId(Integer.parseInt(map.get("parent_id").toString()));
				}
				/*if(map.get("menu_orginal")!=null){
					menu.setMenuOrginal(map.get("menu_orginal").toString());
				}*/
				if(map.get("menu_link")!=null){
					menu.setMenuLink(map.get("menu_link").toString());
				}
				if(map.get("menu_icon")!=null){
					menu.setMenuIcon(map.get("menu_icon").toString());
				}
				if(map.get("has_childs")!=null){
					menu.setHasChilds(Integer.parseInt(map.get("has_childs").toString()));
				}
				if(map.get("menu_level")!=null){
					menu.setMenuLevel(Integer.parseInt(map.get("menu_level").toString()));
				}
				if(map.get("label_text")!=null){
					menu.setLabelText(map.get("label_text").toString());
				}
				
			menuList.add(menu);
		}
		
		return menuList;
		
		
	}
	
	@Override
	public List<Menu> getMenuMap(String lang){
		StringBuffer sql = new StringBuffer();
		Map<String,Object> params = new HashMap<String,Object>();
		
		sql.append("select menuId , moduleName , labelText , menuLevel , hasChilds, parentId, sort ");
		sql.append(" from v_menu_map");
		sql.append(" where langCode= :lang and status=1");
		sql.append(" order by sort asc");
			
		params.put("lang", lang);
			
		//定义一个需要返回的数据类型
		List<Menu> menuList = new ArrayList<Menu>();
		
		List<Object> objectList = baseDao.getList(params, sql.toString(), Menu.class);
		
		//将objectList的值放入menuList
		for (int i = 0; i < objectList.size(); i++) {
			Menu menu = (Menu)objectList.get(i);
			menuList.add(menu);
		}
		return menuList;
	}
	
	
	@Override
	public  List<Menu> menuList(String lang,String roleIds){
		String sql = " select DISTINCT menuId, parentId, moduleName, menuLink, menuIcon, hasChilds, menuLevel, labelText "
					+ " from v_menu_role where status=1 and langCode= :lang "
					+ " and rightRead=1 and roleId in ( :roleIds ) "
					+ "  order by sort ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("lang", lang);
		params.put("roleIds", roleIds);

		List<Object> objectList = baseDao.getList(params, sql, Menu.class);
		List<Menu> menuList = new ArrayList<Menu>();
		for(int i=0;i<objectList.size();i++){
			Menu menu = (Menu)objectList.get(i);
			menuList.add(menu);
		}
		return menuList;
	}
}
