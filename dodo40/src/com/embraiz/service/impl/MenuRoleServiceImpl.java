package com.embraiz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.model.MenuRole;
import com.embraiz.service.MenuRoleService;

@Service
@Transactional
public class MenuRoleServiceImpl implements MenuRoleService{
	
	@Resource
	private BaseDao baseDao;
	
	@Override
	public MenuRole getMenuRoleData(String menuOrginal,int userId){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("select v_menu_role.menu_id,v_menu_role.menu_orginal,");
		sql.append(" case when group_concat(distinct v_menu_role.right_read )='0'  then 0 when group_concat(distinct v_menu_role.right_read) is null then null else 1 end as right_read,");
		sql.append(" case when group_concat(distinct v_menu_role.right_insert )='0'  then 0 when group_concat(distinct v_menu_role.right_insert) is null then null else 1 end as right_insert,");
		sql.append(" case when group_concat(distinct v_menu_role.right_update )='0'  then 0 when group_concat(distinct v_menu_role.right_update) is null then null else 1 end as right_update,");
		sql.append(" case when group_concat(distinct v_menu_role.right_delete )='0'  then 0 when group_concat(distinct v_menu_role.right_delete) is null then null else 1 end as right_delete");
		sql.append(" from v_menu_role ");
		sql.append(" INNER JOIN obj_user on FIND_IN_SET(v_menu_role.role_id,obj_user.role_id) ");
		sql.append(" where obj_user.user_id = :userId and v_menu_role.menu_orginal = :menuOrginal ");
		sql.append(" group by v_menu_role.menu_id ");
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("menuOrginal", menuOrginal);
		
		Map<String, Object> map = baseDao.getObject(sql.toString(),params);
		
		MenuRole menuRole = new MenuRole();
			if(map.get("menu_id")!=null){
				menuRole.setMenuId(Integer.parseInt(map.get("menu_id").toString()));
			}
			if(map.get("menu_orginal")!=null){
				menuRole.setMenuOrginal(map.get("menu_orginal").toString());
			}
			if(map.get("right_read")!=null){
				menuRole.setRightRead(Integer.parseInt(map.get("right_read").toString()));
			}
			if(map.get("right_insert")!=null){
				menuRole.setRightInsert(Integer.parseInt(map.get("right_insert").toString()));
			}
			if(map.get("right_update")!=null){
				menuRole.setRightUpdate(Integer.parseInt(map.get("right_update").toString()));
			}
			if(map.get("right_delete")!=null){
				menuRole.setRightDelete(Integer.parseInt(map.get("right_delete").toString()));
			}
		
		return menuRole;
	}
	
	@Override
	public List<MenuRole> getRoleRightMap(int roleId,String lang){
		StringBuffer sql = new StringBuffer();
		sql.append("select menuId,roleId,rightRead,rightInsert,rightUpdate,rightDelete");
		sql.append(" from v_menu_role");
		sql.append(" where roleId= :roleId and langCode= :lang and status=1");
		sql.append(" order by sort asc");
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("roleId", roleId);
		params.put("lang", lang);
		
		//定义一个需要返回的数据类型
		List<MenuRole> menuRoleList = new ArrayList<MenuRole>();

		List<Object> objectList = baseDao.getList(params, sql.toString(), MenuRole.class);
		
		//将objectList的值放入menuRoleList
		for(int i=0;i<objectList.size();i++){
			MenuRole menuRole = (MenuRole)objectList.get(i);
			menuRoleList.add(menuRole);
				
		}
		
		return menuRoleList;
		
	}
	
	@Override
	public void updateRoleRight(int roleId,MenuRole mr){
		String sql = "select * from map_menu_role where role_id= :roleId  and menu_id= :menuId ";
		Map<String,Object> params = new HashMap<String,Object>();
		
		params.put("roleId", roleId);
		params.put("menuId", mr.getMenuId());
		
		Map<String, Object> map = baseDao.getObject(sql, params);
		
		if(map!=null && map.size()>0){
			//存在记录，则进行修改
			mr.setId(Integer.parseInt(map.get("id").toString()));
			mr.setRoleId(roleId);
			baseDao.update(mr);
		}else{
			//不存在记录，则保存记录
			mr.setRoleId(roleId);
			baseDao.save(mr);
		}
	}

}
