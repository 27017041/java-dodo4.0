package com.embraiz.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.ConfLabel;
import com.embraiz.model.Field;
import com.embraiz.model.FormItem;
import com.embraiz.model.GridItem;
import com.embraiz.model.Module;
import com.embraiz.model.RefOption;
import com.embraiz.model.RefOptionType;
import com.embraiz.model.Relational;
import com.embraiz.model.RelationalItem;
import com.embraiz.model.SearchItem;
import com.embraiz.model.User;
import com.embraiz.service.BaseService;
import com.embraiz.service.CommonService;
import com.embraiz.service.ConfService;
import com.embraiz.util.MainUtil;

@Controller
@RequestMapping("/conf")
public class ConfController {
	
	@Resource
	private ConfService confService;
	@Resource
	private BaseService baseService;
	@Resource
	private CommonService commonService;
	
	
	/**
	 * 在新增模块栏位前，返回可以选择的labelOriginal
	 * @param moduleName
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getLabelOriginalListBySelect")
	public void getLabelOriginalListBySelect(
			@RequestParam("moduleName") String moduleName,
			HttpServletResponse response)
		throws IOException {
		
		JSONObject json = new JSONObject();
		
		List<Object> refOptionList = confService.getRefOptionListByModuleOptionKey(moduleName,"text");
		
		String ids = "";
		
		for(int i=0;i<refOptionList.size();i++){
			RefOption refOption = (RefOption)refOptionList.get(i);
			ids = ids + refOption.getOptionId() + ",";
		}
		
		if(!"".equals(ids)){
			ids = ids.substring(0, ids.length()-1);
		}
		
		json.put("labelOriginalList", confService.getLabelListByModuleLabelType(moduleName, ids));
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	

	/**
	 * 新增模块栏位
	 * @param fieldData
	 * @param confLabelData
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/saveField")
	public void saveField(
			@RequestParam("fieldData") String fieldData,
			@RequestParam("confLabelData") String confLabelData,
			HttpServletResponse response)
		throws IOException {
		
		JSONObject json = new JSONObject();
		boolean result = false;
		
		//需要配置core_field
		Field field = JSONObject.toJavaObject(JSONObject.parseObject(fieldData), Field.class);
		Integer fieldId = (Integer)baseService.save(field);
		
		if(fieldId!=null && fieldId>0){//field保存成功
			result = true;
			
			/*//需要配置conf_label
			List<ConfLabel> labelList =  JSONObject.parseArray(confLabelData, ConfLabel.class);
			for(ConfLabel confLabel : labelList){
				Integer labelId = (Integer)baseService.save(confLabel);
				if(labelId!=null && labelId>0){
					result = true;
				}else{
					result = false;
					break;
				}
			}*/
		}else{
			result = false;
		}
		
		json.put("result", result);
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
		
	}
	
	/**
	 * 返回新增模块时，需要选择的label_type选项
	 * @param moduleName
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getLabelType")
	public void getLabelType(
			@RequestParam("moduleName") String moduleName,
			HttpServletResponse response)
		throws IOException {
		JSONObject json = new JSONObject();
		
		json.put("labelTypeList", confService.getLabelType(moduleName,2));
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 返回新增模块时，field_type为select时，需要选择的下拉值
	 * @param moduleName
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getSelectOption")
	public void getSelectOption(
			@RequestParam("moduleName") String moduleName,
			HttpServletResponse response)
		throws IOException{
		JSONObject json = new JSONObject();
		
		json.put("selectOptionList", confService.getSelectOption(moduleName));
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	
	/**
	 * 根据roleId,获取对应可以被操作的模块列表
	 * @param session
	 * @param response
	 */
	@RequestMapping("/getModuleList")
	public void getModuleList(
			HttpSession session,
			HttpServletResponse response)
		throws IOException{
		
		User user = (User) session.getAttribute("user");
		String roleIds = user.getRoleId();
		
		String lang = (String) session.getAttribute("lang");
		
		if(lang==null){
			lang = "en";
		}
		
		String str = JSON.toJSON(confService.moduleList(lang, roleIds)).toString() ;
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(str);
		response.getWriter().flush();
	}
	
	
	/**
	 * 根据模块名字，获取配置详情
	 * @param session
	 * @param response
	 * @param moduleName
	 * @throws IOException
	 */
	@RequestMapping("/getModuleDetail")
	public void getModuleDetail(
			HttpSession session,
			HttpServletResponse response,
			@RequestParam("moduleName") String moduleName) throws IOException{
		JSONObject json = new JSONObject();
		if(moduleName!=null){
			Module module = confService.getModule(moduleName);
			if(module!=null){
				//模块信息
				json.put("module", module);
				
				String lang = (String) session.getAttribute("lang");
				
				if(lang==null){
					lang = "en";
				}
				
				//搜索区域的栏位列表
				json.put("searchFieldList", confService.getSearchField(lang, moduleName));
				//列表区域的栏位列表
				json.put("gridFieldList", confService.getGridField(lang, moduleName));
				//内页区域的栏位列表
				json.put("formFieldList", confService.getFormField(lang, moduleName));
				
				//该模块内的所有字段列表
				json.put("fieldList", confService.getFieldByModule(module.getModuleName()));
				
				
				//该模块下的关联信息列表(以及item列表)
				List<Object> ls = confService.getRelationalList(moduleName);
				if(ls!=null && ls.size()>0){
					json.put("relationalList", ls);
					json.put("relationalItemList",confService.getRelationalItemList(ls));
				}
				
				
			}
		}
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	
	/**
	 * 删除指定区域内的字段
	 * @param itemId
	 * @param viewName (search、grid、form)
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/deleteFieldInView")
	public void deleteFieldInView(
			@RequestParam("itemId")Integer itemId,
			@RequestParam("viewName")String viewName,
			HttpServletResponse response) throws Exception{
		JSONObject json = new JSONObject();
		if(itemId!=null && viewName!=null){
			String objectName = null;
			switch(viewName){
			case "search":
				objectName = "SearchItem";
				break;
			case "grid":
				objectName = "GridItem";
				break;
			case "form":
				objectName = "FormItem";
				break;
			default :
				objectName = null;
			}
			
			if(objectName!=null){
				Class cls = Class.forName("com.embraiz.model."+objectName);
				Object obj=cls.newInstance();
				Integer result = confService.deleteFieldInView(itemId, obj);
				json.put("result", result);
			}
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	
	/**
	 * 模块内指定区域增加field
	 * @param fieldId
	 * @param viewName
	 * @param sorting
	 * @param sortBy
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/saveFieldInView")
	public void saveFieldInView(
			@RequestParam("fieldId") Integer fieldId,
			@RequestParam("viewName")String viewName,
			@RequestParam(name="sortBy",required=false)String sortBy,
			HttpServletResponse response)
		throws Exception{
		
		JSONObject json = new JSONObject();
		boolean rtn = false;
		if(fieldId!=null && viewName!=null){
			
			SearchItem searchItem = null;
			GridItem gridItem = null;
			FormItem formItem = null;
			
			Field fieldDb = (Field)baseService.getObject(Field.class, fieldId);
			
				try{
					Map<String,Object> map = confService.getMaxSortInModule(fieldDb.getModuleName(), viewName);
					int sorting = -1;
					
					if(map!=null && map.get("maxSorting")!=null){
						sorting = Integer.parseInt(map.get("maxSorting").toString());
						sorting = sorting + 1;
					}else{
						sorting = 1;
					}
					
					if(viewName.equals("search")){
						searchItem = new SearchItem();
							searchItem.setFieldId(fieldId);
							searchItem.setSorting(sorting);
						baseService.save(searchItem);
					}
					else if(viewName.equals("grid")){
						gridItem = new GridItem();
							gridItem.setFieldId(fieldId);
							gridItem.setSorting(sorting);
						if(sortBy!=null && !("").equals(sortBy)){
							gridItem.setSortBy(sortBy);
						}
						baseService.save(gridItem);
					}
					else if(viewName.equals("form")){
						formItem = new FormItem();
							formItem.setFieldId(fieldId);
							formItem.setSorting(sorting);
						baseService.save(formItem);
					}
					
					rtn = true;
				}catch(Exception e){
					e.printStackTrace();
					rtn = false;
				}
			
		}
		
		json.put("result", rtn);
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
		
	}
	
	/**
	 * 修改模块内，指定区域的栏位（顺序）详情
	 * @param fieldIds
	 * @param viewName
	 * @param sortBy
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/updateFiledSortInView")
	public void updateFiledSortInView(
			@RequestParam("itemIds") String itemIds,
			@RequestParam("viewName")String viewName,
			@RequestParam(name="sortBy",required=false)String sortBy,
			HttpServletResponse response)
		throws IOException {
		JSONObject json = new JSONObject();
		boolean rtn = false;
		if(itemIds!=null && viewName!=null){
			
			String itemIdArray[] = itemIds.split(",");
			
			for(int i=0;i<itemIdArray.length;i++){
				Integer itemId = Integer.parseInt(itemIdArray[i]);
				
				try{
					if(viewName.equals("search")){
						SearchItem searchItemDb = (SearchItem)baseService.getObject(SearchItem.class, itemId);
							searchItemDb.setSorting(i);
						baseService.update(searchItemDb);
					}
					else if(viewName.equals("grid")){
						GridItem gridItemDb = (GridItem)baseService.getObject(GridItem.class, itemId);
							gridItemDb.setSorting(i);
							if(sortBy!=null && !("").equals(sortBy)){
								gridItemDb.setSortBy(sortBy);
							}
						baseService.update(gridItemDb);
					}
					else if(viewName.equals("form")){
						FormItem formItemDb = (FormItem)baseService.getObject(FormItem.class, itemId);
							formItemDb.setSorting(i);
						baseService.update(formItemDb);
					}
					
					rtn = true;
				}catch(Exception e){
					e.printStackTrace();
					rtn = false;
					break;
				}
			}
		}
		
		json.put("result", rtn);
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
		
	}
	
	/**
	 * 修改模块内，所有栏位的详情
	 * @param fieldList
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/updateFieldInModule")
	public void updateFieldInModule(
			@RequestParam("fieldData") String fieldData,
			HttpServletResponse response)
		throws IOException{
		
		Field field = JSONObject.toJavaObject(JSONObject.parseObject(fieldData), Field.class);
		boolean rtn = false;
		
		JSONObject json = new JSONObject();
			try{
				Field fieldDb = (Field)baseService.getObject(Field.class, field.getFieldId());
					fieldDb.setFieldType(field.getFieldType());
					fieldDb.setValidation(field.getValidation());
					fieldDb.setFieldLabel(field.getFieldLabel());
					fieldDb.setModuleName(field.getModuleName());
					fieldDb.setIdentifier(field.getIdentifier());
					fieldDb.setSelectTypeId(field.getSelectTypeId());
					fieldDb.setMinValue(field.getMinValue());
					fieldDb.setMaxValue(field.getMaxValue());
					fieldDb.setMinLengthValue(field.getMinLengthValue());
					fieldDb.setMaxLengthValue(field.getMaxLengthValue());
					fieldDb.setIsDisplay(field.getIsDisplay());
					fieldDb.setIsDisabled(field.getIsDisabled());
					fieldDb.setIsReadonly(field.getIsReadonly());
					fieldDb.setPattern(field.getPattern());
					fieldDb.setLinkageModule(field.getLinkageModule());
					fieldDb.setLinkageViews(field.getLinkageViews());
					fieldDb.setLinkageKey(field.getLinkageKey());
				baseService.update(fieldDb);
				
				rtn = true;
			}catch(Exception e){
				e.printStackTrace();
				rtn = false;
			}
		
		json.put("result", rtn);
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 根据fieldIds，进行栏位删除
	 * @param fieldIds
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/deleteFieldInModule")
	public void deleteFieldInModule(
			@RequestParam("fieldIds") String fieldIds,
			HttpServletResponse response)
		throws IOException{
		JSONObject json = new JSONObject();
		boolean rtn = false;
		
		if(fieldIds!=null && fieldIds.length()>0){
			String fieldIdArray[] = fieldIds.split(",");
			
			for(int i=0;i<fieldIdArray.length;i++){
				Integer fieldId = Integer.parseInt(fieldIdArray[i]);
				
				int formFieldCount = confService.getCountFormField(fieldId);
				int gridFieldCount = confService.getCountGridField(fieldId);
				int searchFieldCount = confService.getCountSearchField(fieldId);
				
				if(formFieldCount ==0 && gridFieldCount==0 && searchFieldCount==0){//该field没有被使用，可以删除
					Field fieldDb = (Field)baseService.getObject(Field.class, fieldId);
					int result = confService.deleteLabelByFieldLabel(fieldDb.getFieldLabel());
					if(result>0){
						confService.deleteFieldInModule(fieldId);
						rtn = true;
					}else{
						rtn = false;
						json.put("returnFieldId", fieldId);
						break;
					}
					
					
				}else{
					rtn = false;
					json.put("returnFieldId", fieldId);
					break;
				}
			}
		}
		
		json.put("result", rtn);
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 根据moduleName，获得所有栏位
	 * @param moduleName
	 * @param session
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getAllFieldInModule")
	public void getAllFieldInModule(
			@RequestParam("moduleName") String moduleName,
			HttpSession session,
			HttpServletResponse response)
		throws IOException{
		JSONObject json = new JSONObject();
		if(moduleName!=null){
			Module module = confService.getModule(moduleName);
			if(module!=null){
				String lang = (String) session.getAttribute("lang");
				if(lang==null){
					lang = "en";
				}
				
				//该模块内的所有字段列表
				json.put("fileldList", confService.getFieldByModule(module.getModuleName()));
			}
		}
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	
	/**
	 * 根据moduleName和viewName,获得指定模块下的指定区域栏位
	 * @param moduleName
	 * @param viewName
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/getFieldInModuleView")
	public void getFieldInModuleView(
			@RequestParam("moduleName") String moduleName,
			@RequestParam("viewName") String viewName,
			HttpServletResponse response,
			HttpSession session)
		throws IOException {
		JSONObject json = new JSONObject();
		
		String lang = (String) session.getAttribute("lang");
		if(lang==null){
			lang = "en";
		}
		
		if(viewName.equals("search")){
			//搜索区域的栏位列表
			json.put("searchFieldList", confService.getSearchField(lang, moduleName));
		}else if(viewName.equals("grid")){
			//列表区域的栏位列表
			json.put("gridFieldList", confService.getGridField(lang, moduleName));
		}else if(viewName.equals("form")){
			//内页区域的栏位列表
			json.put("formFieldList", confService.getFormField(lang, moduleName));
		}else if(viewName.equals("relational")){
			//关联模块
			List<Object> ls = confService.getRelationalList(moduleName);
			if(ls!=null && ls.size()>0){
				json.put("relationalList", ls);
				json.put("relationalItemList",confService.getRelationalItemList(ls));
			}
			
		}
		
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
		
	}
	
	
//----------------------------------------------以下 配置下拉选项-------------------------------------------------------
	
	/**
	 * 返回模块选择列表
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/getModuleListBySelect")
	public void getModuleListBySelect(
			HttpServletResponse response,
			HttpSession session)
		throws IOException {
		User user = (User) session.getAttribute("user");
		String roleId = user.getRoleId();
		
		String lang = (String) session.getAttribute("lang");
		if(lang==null){
			lang = "en";
		}
		
		JSONObject json = new JSONObject();
		json.put("moduleList", confService.getModuleList(roleId, lang));
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	

	/**
	 * 添加ref_option_type
	 * @param typeNameEn
	 * @param typeNameCn
	 * @param typeNameTc
	 * @param moduleName
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/saveRefOptionType")
	public void saveRefOptionType(
			@RequestParam("typeNameEn") String typeNameEn,
			@RequestParam("typeNameCn") String typeNameCn,
			@RequestParam("typeNameTc") String typeNameTc,
			@RequestParam("moduleName") String moduleName,
			HttpServletResponse response)
		throws IOException{
		
		JSONObject json = new JSONObject();
		
		if(typeNameEn!=null && typeNameCn!=null && typeNameTc!=null && moduleName!=null){
			RefOptionType refOptionType = new RefOptionType();
				refOptionType.setModuleName(moduleName);
				refOptionType.setTypeNameCn(typeNameCn);
				refOptionType.setTypeNameEn(typeNameEn);
				refOptionType.setTypeNameTc(typeNameTc);
			Integer typeId = (Integer)baseService.save(refOptionType);
			
			if(typeId!=null && typeId>0){
				json.put("result", true);
				json.put("typeId", typeId);
			}else{
				json.put("result", false);
			}
		}
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 修改ref_option_type
	 * @param typeNameEn
	 * @param typeNameCn
	 * @param typeNameTc
	 * @param typeId
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/updateRefOptionType")
	public void updateRefOptionType(
			@RequestParam("typeNameEn") String typeNameEn,
			@RequestParam("typeNameCn") String typeNameCn,
			@RequestParam("typeNameTc") String typeNameTc,
			@RequestParam("typeId") Integer typeId,
			HttpServletResponse response)
		throws IOException{
		
		RefOptionType refOptionTypeDb = (RefOptionType) baseService.getObject(RefOptionType.class, typeId);
		JSONObject json = new JSONObject();
		
		if(refOptionTypeDb!=null){
			refOptionTypeDb.setTypeNameCn(typeNameCn);
			refOptionTypeDb.setTypeNameEn(typeNameEn);
			refOptionTypeDb.setTypeNameTc(typeNameTc);
			
			try{
				baseService.update(refOptionTypeDb);
				json.put("result", true);
			}catch(Exception e){
				e.printStackTrace();
				json.put("result", false);
			}
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 添加ref_option
	 * @param refOption
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/saveRefOption")
	public void saveRefOption(
			@ModelAttribute RefOption refOption,
			HttpServletResponse response)
		throws IOException{
		
		JSONObject json = new JSONObject();
		
		refOption.setSort(confService.getMaxSortInRefOptionByTypeId(refOption.getTypeId()));
		
		Integer optionId  = (Integer)baseService.save(refOption);
		
		if(optionId!=null && optionId>0){
			json.put("result", true);
		}else{
			json.put("result", false);
		}
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 修改ref_option
	 * @param refOption
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/updateRefOption")
	public void updateRefOption(
			@ModelAttribute RefOption refOption,
			HttpServletResponse response)
		throws IOException{
		
		JSONObject json = new JSONObject();
		RefOption refOptionDb = (RefOption)baseService.getObject(RefOption.class, refOption.getOptionId());
		
		if(refOptionDb!=null){
			refOptionDb.setOptionNameCn(refOption.getOptionNameCn());
			refOptionDb.setOptionNameEn(refOption.getOptionNameEn());
			refOptionDb.setOptionNameTc(refOption.getOptionNameTc());
			
			try{
				baseService.update(refOptionDb);
				json.put("result", true);
			}catch(Exception e){
				e.printStackTrace();
				json.put("result", false);
			}
		}
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 返回ref_option_type表所有数据
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getRefOptionTypeList")
	public void getRefOptionTypeList(HttpServletResponse response)
		throws IOException{
		JSONObject json = new JSONObject();
		json.put("refOptionTypeList", baseService.getListByHql(RefOptionType.class));
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 根据typeId,返回RefOption列表
	 * @param typeId
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/searchRefOptionByTypeId")
	public void searchRefOptionByTypeId(
			@RequestParam(value="searchForm") String searchForm,
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam(value="length") int length,
			@RequestParam(value="start") int start,
			@RequestParam(value="order[0][column]",defaultValue="0") String sortCol,
			@RequestParam(value="order[0][dir]",defaultValue="asc") String sortOrder)
		throws IOException {
		
		JSONObject json = new JSONObject();
		
		//获取排序字段
		String sortBy = "option_key";
		if (request.getParameter("columns[" + sortCol + "][name]") != null && !request.getParameter("columns[" + sortCol + "][name]").equals("")) {
			sortBy = request.getParameter("columns[" + sortCol + "][name]");
		}
		
		if(searchForm!=null && !("").equals(searchForm)){
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			
			Map<String, Object> map = confService.getRefOptionListByTypeId(Integer.parseInt(searchJson.get("typeId").toString()),start,length,sortBy,sortOrder);
			json.put("recordsTotal", map.get("listCount"));
			json.put("recordsFiltered", map.get("listCount"));
			json.put("data", map.get("data"));
		}
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 根据optionId，获得RefOption详情
	 * @param optionId
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getRefOptionDetailByOptionId")
	public void getRefOptionDetailByOptionId(
			@RequestParam(value="optionId") Integer optionId,
			HttpServletResponse response)
					throws IOException {
		
		JSONObject json = new JSONObject();
		json.put("refOptionDetail", baseService.getObject(RefOption.class, optionId));
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 根据typeId获得RefOption列表（typeId可不传，默认值为2）
	 * @param typeId
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getRefOptionListByTypeId")
	public void getRefOptionListByTypeId(
			@RequestParam(value="typeId",required=false,defaultValue="2") Integer typeId,
			HttpServletResponse response)
					throws IOException {
		JSONObject json = new JSONObject();
		json.put("refOptionList", confService.getRefOptionListByTypeId(typeId));
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 表格查询，根据ref_option的option_id（conf_label的label_type）,查询conf_label表
	 * @param searchForm
	 * @param response
	 * @param request
	 * @param length
	 * @param start
	 * @param sortCol
	 * @param sortOrder
	 * @throws IOException
	 */
	@RequestMapping("/searchConfLabelByLabelType")
	public void searchConfLabelByLabelType(
			@RequestParam(value="searchForm") String searchForm,
			HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam(value="length") int length,
			@RequestParam(value="start") int start,
			@RequestParam(value="order[0][column]",defaultValue="0") String sortCol,
			@RequestParam(value="order[0][dir]",defaultValue="asc") String sortOrder)
		throws IOException{
		
		JSONObject json = new JSONObject();
		
		//获取排序字段
		String sortBy = "label_id";
		if (request.getParameter("columns[" + sortCol + "][name]") != null && !request.getParameter("columns[" + sortCol + "][name]").equals("")) {
			sortBy = request.getParameter("columns[" + sortCol + "][name]");
		}
		
		if(searchForm!=null && !("").equals(searchForm)){
			JSONObject searchJson = JSONObject.parseObject(searchForm);
			String labelText = searchJson.get("labelText") != null?searchJson.get("labelText").toString():null;
			String labelOrginal = searchJson.get("labelOrginal") != null?searchJson.get("labelOrginal").toString():null;
			
			Map<String, Object> map = confService.getConfLabelListByLabelType(Integer.parseInt(searchJson.get("optionId").toString()),
					labelText,labelOrginal, start, length, sortBy, sortOrder);
			
			json.put("recordsTotal", map.get("listCount"));
			json.put("recordsFiltered", map.get("listCount"));
			json.put("data", map.get("data"));
		}
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 新增3种语言的label
	 * @param confLabelData
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/saveConfLabel")
	public void saveConfLabel(
			@RequestParam("confLabelData") String confLabelData,
			HttpServletResponse response)
		throws IOException{
		
		boolean result = false;
		JSONObject json = new JSONObject();
		
		List<ConfLabel> labelList =  JSONObject.parseArray(confLabelData, ConfLabel.class);
		
		for(ConfLabel confLabel : labelList){
			Integer labelId = (Integer)baseService.save(confLabel);
			if(labelId!=null && labelId>0){
				result = true;
			}else{
				result = false;
				break;
			}
		}
		
		json.put("result", result);
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 修改label
	 * @param confLabelData
	 * @param response
	 */
	@RequestMapping("/updateConfLabel")
	public void updateConfLabel(
			@RequestParam("confLabelData") String confLabelData,
			HttpServletResponse response)
		throws IOException{
		
		boolean result = false;
		JSONObject json = new JSONObject();
		
		List<ConfLabel> labelList =  JSONObject.parseArray(confLabelData, ConfLabel.class);
		
		for(ConfLabel confLabel : labelList){
			ConfLabel confLabelDb = (ConfLabel)baseService.getObject(ConfLabel.class, confLabel.getLabelId());
				confLabelDb.setLabelText(confLabel.getLabelText());
			try{
				baseService.update(confLabelDb);
				result = true;
			}catch(Exception e){
				e.printStackTrace();
				result = false;
				break;
			}
		}
		
		json.put("result", result);
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 根据labelOriginal和labelType，获得3种语言的label详情
	 * @param labelOriginal
	 * @param labelType
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getConfLabelDetailList")
	public void getConfLabelDetailList(
			@RequestParam("labelOriginal") String labelOriginal,
			@RequestParam("labelType") Integer labelType,
			HttpServletResponse response)
		throws IOException{
		
		JSONObject json = new JSONObject();
		json.put("confLabelList", confService.getConfLabelDetailList(labelOriginal, labelType));
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
		
	}
	
//----------------------------------------------以下 配置关联列表-------------------------------------------------------
	
	/**
	 * 根据表名，获得该表所有字段
	 * @param tableName
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getTableFields")
	public void getTableFields(
			@RequestParam("tableName") String tableName,
			HttpServletResponse response) 
					throws IOException{
		MainUtil mainUtil = new MainUtil();
		mainUtil.sqlToJsonResponse(response, "tableFieldsList", commonService.getTableFields(tableName));
		
	}
	
	/**
	 * 添加Relational
	 * @param relationalData
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/saveRelational")
	public void saveRelational(
			@RequestParam("relationalData") String relationalData,
			HttpServletResponse response) 
					throws IOException{
		boolean result = false;
		JSONObject json = new JSONObject();
		
		Relational relational = JSONObject.toJavaObject(JSONObject.parseObject(relationalData), Relational.class);
		Integer id = (Integer)baseService.save(relational);
		
		if(id!=null && id>0){
			result = true;
		}
		json.put("result", result);
		MainUtil.reponseFlush(response, json.toJSONString());
		
	}
	
	/**
	 * 修改Relational
	 * @param relationalData
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/updateRelational")
	public void updateRelational(
			@RequestParam("relationalData") String relationalData,
			HttpServletResponse response) 
					throws IOException{
		
		JSONObject json = new JSONObject();
		boolean result = false;
		
		Relational relational = JSONObject.toJavaObject(JSONObject.parseObject(relationalData), Relational.class);
		
		Relational relationalDb = (Relational)baseService.getObject(Relational.class, relational.getId());
		if(relationalDb!=null){
			MainUtil mainUtil = new MainUtil();
			try{
				relationalDb = mainUtil.setValueByReflection(relationalDb, relational);
				baseService.update(relationalDb);
				result = true;
			}catch(Exception e){
				e.printStackTrace();
				result = false;
			}
		}
		
		json.put("result", result);
		MainUtil.reponseFlush(response,json.toJSONString());
	}
	
	/**
	 * 删除Relational
	 * @param ids
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/deleteRelational")
	public void deleteRelational(
			@RequestParam("ids") String ids,
			HttpServletResponse response) throws IOException{
		JSONObject json = new JSONObject();
		
		if(ids!=null && ids.length()>0){
			String sql = "delete from core_relational_item where relational_id in ("+ ids +")";
			baseService.deleteBySql(sql, null);
			
			sql = "delete from core_relational where id in ("+ ids +")";
			baseService.deleteBySql(sql, null);
			
			json.put("result", true);
		}
		MainUtil.reponseFlush(response,json.toJSONString());
	}
	
	/**
	 * 根据id获得Relational和RelationalItem详情
	 * @param id
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getRelationalById")
	public void getRelationalById(
			@RequestParam("id") Integer id,
			HttpServletResponse response) throws IOException{
		JSONObject json = new JSONObject();
		Relational relationalDB = (Relational)baseService.getObject(Relational.class, id);
		if(relationalDB!=null){
			json.put("relational", relationalDB);
			
			String hql = "from RelationalItem where relationalId = :relationalId order by sort";
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("relationalId", relationalDB.getId());
			json.put("relationalItemList", baseService.getListByHql(hql, params));
			
		}
		MainUtil.reponseFlush(response,json.toJSONString());
	}
	
	/**
	 * 添加RelationalItem
	 * @param relationalItemData
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("/saveRelationalItem")
	public void saveRelationalItem(
			@RequestParam("relationalItemData") String relationalItemData,
			HttpServletResponse response) throws IOException{
		boolean result = false;
		JSONObject json = new JSONObject();
		
		RelationalItem relationalItem = JSONObject.toJavaObject(JSONObject.parseObject(relationalItemData), RelationalItem.class);
		
		Integer sort = 0;
		Map<String,Object> map = confService.getMaxSortInRelationalItem(relationalItem.getRelationalId());
		if(map!=null && map.get("maxSort")!=null){
			sort = Integer.parseInt(map.get("maxSort").toString());
			sort = sort + 1;
		}else{
			sort = 1;
		}
		
		relationalItem.setSort(sort);
		
		Integer id = (Integer)baseService.save(relationalItem);
		
		if(id!=null && id>0){
			result = true;
		}
		json.put("result", result);
		MainUtil.reponseFlush(response, json.toJSONString());
	}
	
	/**
	 * 修改RelationalItem
	 * @param relationalItemData
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/updateRelationalItem")
	public void updateRelationalItem(
			@RequestParam("relationalItemData") String relationalItemData,
			HttpServletResponse response) throws IOException{
		JSONObject json = new JSONObject();
		boolean result = false;
		
		RelationalItem relationalItem = JSONObject.toJavaObject(JSONObject.parseObject(relationalItemData), RelationalItem.class);
		
		RelationalItem relationalItemDb = (RelationalItem)baseService.getObject(RelationalItem.class, relationalItem.getItemId());
		if(relationalItemDb!=null){
			MainUtil mainUtil = new MainUtil();
			try{
				relationalItemDb = mainUtil.setValueByReflection(relationalItemDb, relationalItem);
				baseService.update(relationalItemDb);
				result = true;
			}catch(Exception e){
				e.printStackTrace();
				result = false;
			}
		}
		
		json.put("result", result);
		MainUtil.reponseFlush(response,json.toJSONString());
	}
	
	/**
	 * 删除RelationalItem
	 * @param ids
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("/deleteRelationalItem")
	public void deleteRelationalItem(
			@RequestParam("ids") String ids,
			HttpServletResponse response) throws IOException{
		JSONObject json = new JSONObject();
		
		if(ids!=null && ids.length()>0){
			String sql = "delete from core_relational_item where item_id in ("+ ids +")";
			baseService.deleteBySql(sql, null);
			
			json.put("result", true);
		}
		MainUtil.reponseFlush(response,json.toJSONString());
	}
	
	/**
	 * 修改RelationalItem排序
	 * @param id
	 * @param sort
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/updateSortInRelationalItem")
	public void updateSortInRelationalItem(
			@RequestParam("itemIds") String itemIds,
			HttpServletResponse response) throws IOException{
		JSONObject json = new JSONObject();
		
		boolean rtn = false;
		if(itemIds!=null){
			String itemIdArray[] = itemIds.split(",");
			
			for(int i=0;i<itemIdArray.length;i++){
				Integer itemId = Integer.parseInt(itemIdArray[i]);
				RelationalItem relationalItem = (RelationalItem)baseService.getObject(RelationalItem.class, itemId);
				
				if(relationalItem!=null){
					relationalItem.setSort(i);
					try{
						baseService.update(relationalItem);
						rtn = true;
					}catch(Exception e){
						e.printStackTrace();
						rtn = false;
						break;
					}
					
				}
			}
			json.put("result", rtn);
		}
		
		MainUtil.reponseFlush(response,json.toJSONString());
	}
	
}
