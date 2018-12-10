package com.embraiz.service;

import java.util.List;

import com.embraiz.model.Obj;
import com.embraiz.model.ObjTypeRef;

public interface ObjService {
	
	public Obj getObjData(int objId);
	
	public int insertObj(Obj objfrom);
	
	public int searchObjType(String typeName);
	
	public int updateObj(Obj obj);
	
	public String getKeyword(String typeName,Object o) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException;
	
	public List<ObjTypeRef> getObjModule(String keyword,int userId);
	
	public List<Obj> getObjList(String keyword,int userId,int objTypeId);
	
	public void updateObjOrder(int objId);

}
