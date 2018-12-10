package com.embraiz.service.impl;

import java.lang.reflect.Field;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.embraiz.dao.BaseDao;
import com.embraiz.model.Obj;
import com.embraiz.model.ObjTypeRef;
import com.embraiz.model.Page;
import com.embraiz.service.ObjService;
import com.embraiz.util.StringFormat;

@Service
@Transactional
public class ObjServiceImpl implements ObjService{

	@Resource
	private BaseDao baseDao;
	
	@Override
	public Obj getObjData(int objId){
		return (Obj)baseDao.getObject(Obj.class, objId);
	}
	
	@Override
	public int insertObj(Obj objfrom){
		int keyId = 0;
		final Obj obj = objfrom;
		String sql = "{call obj_insert(?,?,?,?,?,?,?)}";
		Map<String,Object> params = new HashMap<String,Object>();
			params.put("1", obj.getObjParentId());
			params.put("2", obj.getObjTitle());
			params.put("3", obj.getObjDesc());
			params.put("4", obj.getObjTypeId());
			params.put("5", obj.getKeyword());
			params.put("6", obj.getOwnerId());
			params.put("7", Types.INTEGER);
		keyId = baseDao.insertByCall(sql, params);
		return keyId;
	}
	@Override
	public void updateObjOrder(int objId){
		int keyId=0;
		String sql2= "{call obj_order(?)}";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("1", objId);
		baseDao.updateAndReturnByCall(sql2, param);
		
	}
	
	@Override
	public int searchObjType(String typeName){
		String sql = "select ifnull(obj_type_id,0) as obj_type_id from obj_type_ref where obj_title_title = :typeName ";
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("typeName", typeName);
		
		Map<String,Object> map = baseDao.getObject(sql, params);
		return Integer.parseInt(map.get("obj_type_id").toString());
	}
	
	@Override
	public int updateObj(Obj obj){
		String sql = "update obj set obj_title = :objTitle,obj_desc = :objDesc,keyword = :keyword,update_date = :updateDate where obj_id = :objId ";
		Map<String,Object> params = new HashMap<String,Object>();
			params.put("objTitle", obj.getObjTitle());
			params.put("objDesc", obj.getObjDesc());
			params.put("keyword", obj.getKeyword());
			params.put("updateDate",new Date());
			params.put("objId", obj.getObjId());
		return baseDao.updateBySql(sql, params);		
	}
	
	
	@Override
	public String getKeyword(String typeName,Object o) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		String hql = "from ObjTypeRef where objTitleTitle = :typeName ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("typeName", typeName);
		ObjTypeRef type=(ObjTypeRef)baseDao.getObjectByHql(hql, params);
		String keywordField=type.getKeywordField();
		String fieldKeyword="";
		if(keywordField!=null && !keywordField.equals("")){
			String[] keywordFields=keywordField.split(",");
			for(int i=0;i<keywordFields.length;i++){
				String field=keywordFields[i];
				field=StringFormat.classNameConvert(field);
				Field f=o.getClass().getDeclaredField(field);  //拿到field对应module里面的字段名
				f.setAccessible(true);
				String c=f.getType().getName();
				Object s=f.get(o);
				String value="";
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				if(s!=null){
					if (c.equals("java.lang.String")) {
						value=s.toString();
					}else if(c.equals("java.util.Date")) {
						value=format.format(s);				
					}else{
						value=s.toString();
					}
				}
				if(fieldKeyword.equals("")){
					fieldKeyword+=value;
				}else{
					fieldKeyword+=","+value;
				}
			}
		}
		return fieldKeyword;
	}
	
	
	//get到所有有keyword内容的objTypeRef记录
	@Override
	public List<ObjTypeRef> getObjModule(String keyword,int userId){
		String sql="select ref.obj_type_id,ref.obj_title_title,ref.keyword_field,ref.icon,ref.display_name,ref.module_name,ob.count as module_count from obj_type_ref ref inner join"+
			" (select obj.obj_type_id,count(obj_id) as count from obj where keyword like :keyword and status=1 group by obj_type_id) as ob"+
			" on ref.obj_type_id=ob.obj_type_id where ref.is_searchable=1 ";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("keyword", "%"+keyword+"%");
		List<Object> o = baseDao.getList(params, sql, ObjTypeRef.class);
		List<ObjTypeRef> types=new ArrayList<ObjTypeRef>();
		if(o.size()>0){
			for(int i=0;i<o.size();i++){
				types.add((ObjTypeRef)o.get(i));
			}
		}
		return types;
	}
	
	
	//get到所有有keyword内容的obj记录
	@Override
	public List<Obj> getObjList(String keyword,int userId,int objTypeId){
		String hql="select obj.obj_title as objTitle,obj.obj_desc as objDesc, "+
					" obj.keyword,obj.obj_type_id as objTypeId,obj_type_ref.obj_title_title as objTypeName "+
					" from obj join obj_type_ref on obj.obj_type_id=obj_type_ref.obj_type_id where keyword like :keyword and obj_type_id= :objTypeId and status=1";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("keyword", "%"+keyword+"%");
		params.put("objTypeId", objTypeId);
		List<Object> o=baseDao.getListByHql(hql,params);
		List<Obj> objs=new ArrayList<Obj>();
		if(o.size()>0){
			for(int i=0;i<o.size();i++){
				objs.add((Obj)o.get(i));
			}
		}
		return objs;
	}
	
 
}
