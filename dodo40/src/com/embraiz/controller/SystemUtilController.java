package com.embraiz.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.embraiz.model.Columns;
import com.embraiz.model.Conf;
import com.embraiz.model.ConfButton;
import com.embraiz.model.ConfForm;
import com.embraiz.model.ConfFormGrid;
import com.embraiz.model.ConfGird;
import com.embraiz.model.Page;
import com.embraiz.model.StoreData;
import com.embraiz.model.User;
import com.embraiz.service.SystemUtilService;
import com.embraiz.util.StringFormat;

@Controller
@RequestMapping("/system")
public class SystemUtilController {
	@Resource
	private SystemUtilService systemUtilSerivce;
	
	@RequestMapping("/defaultConf")
	public void getDefaultConf(HttpServletResponse response,HttpServletRequest request,@RequestParam(value="moduleName") String moduleName) throws IOException{
		int confId = 0;
		
		Conf conf = systemUtilSerivce.getDefaultConf(moduleName);
		/*if(conf!=null){
			confId = conf.getConfId();
		}*/
		
		JSONObject json = new JSONObject();
		json.put("conf", conf);
		
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 获取module的searchForm和grid配置、以及searchForm的下拉框数据
	 * @param response
	 * @param request
	 * @param confId
	 * @throws Exception
	 */
	@RequestMapping("/moduleList")
	public void moduleList(HttpServletResponse response,HttpServletRequest request,@RequestParam(value="confId") int confId) throws Exception{
		String lang = (String) request.getSession().getAttribute("lang");
		User user = (User)request.getSession().getAttribute("user");
		if(lang==null){
			lang = "en";
		}
		JSONObject json = new JSONObject();
		
		Conf conf = systemUtilSerivce.getConfData(confId);
		
		if(user!=null){
			//搜索框form的field数据
			List<ConfGird> searchList = systemUtilSerivce.moduleSearch(confId, lang);
			Map<String,Object> searchForm = new HashMap<String, Object>();//初始一个searchForm的Bo，搜索的时候传搜索值到后台
			Map<String, Object> selectStore = new HashMap<String, Object>();//搜索框中的下拉框数据
			
			//处理search form的field
			if(searchList.size()>0){
				for(ConfGird cg:searchList){
					searchForm.put(cg.getFieldName(), "");
					if(cg.getSearchType().equals("select") || cg.getSearchType().equals("multiSelect") || cg.getSearchType().equals("filterSelect")){//搜索下拉框数据
						List<StoreData> storeList = new ArrayList<StoreData>();
						if(cg.getSubFrom()==null){//该select没有被其他select关联，否则不要查找数据
							if(cg.getSelectIsObjData()==1){//搜索的数据是obj数据，有权限控制
								storeList = systemUtilSerivce.storeObjDataList(cg.getSelectTable(),cg.getSelectIdField(),cg.getSelectValueField(),cg.getSelectOrderField(),user.getUserId(),"");
							}else{//搜索的数据是普通表
								String sqlWhere = null;
								if(cg.getSelectTypeField()!=null && cg.getSelectTypeId()!=null){
									sqlWhere = " and "+cg.getSelectTypeField() + " = "+ cg.getSelectTypeId();
								}
								
								String valueField = cg.getSelectValueField();
								if(lang.equals("cn")){
									valueField = valueField + "_cn";
								}else if(lang.equals("en")){
									valueField = valueField + "_en";
								}else{
									valueField = valueField + "_tc";
								}
								storeList = systemUtilSerivce.storeDataList(cg.getSelectTable(),cg.getSelectIdField(),valueField,cg.getSelectOrderField(),sqlWhere);
							}
						}
						selectStore.put(cg.getFieldName()+"Store", storeList);
					}
				}
			}
			json.put("searchList", searchList);
			json.put("searchForm", searchForm);
			json.put("selectStore", selectStore);
			
			//表格
			List<ConfGird> gridList = systemUtilSerivce.moduleGird(confId, lang);
			String gridHeader = ",id";
			List<Columns> colsList = new ArrayList<Columns>();
			//判断表格是否有checkBox
			if(conf.getHasCheckbox()==1){//有checkBox
				gridHeader = ",id";
				Columns column = new Columns();
				column.setDefaultContent("");
				column.setClassName("select-checkbox");
				column.setOrderable(false);
				column.setWidth("5%");
				colsList.add(column);
			}else{
				gridHeader = "id";
			}
			//设置id列
			Columns colId = new Columns();
			colId.setData(StringFormat.fieldNameConvert(conf.getKeyField()));
			colId.setVisible(false);
			colId.setOrderable(false);
			colsList.add(colId);
			//遍历处理表格的列配置
			if(gridList.size()>0){
				for(ConfGird cgg:gridList){
					gridHeader = gridHeader+","+cgg.getLabelText();
					Columns column = new Columns();
					if(cgg.getFieldName()!=null){
						column.setData(StringFormat.fieldNameConvert(cgg.getFieldName()));
						if(cgg.getSearchType().equals("selectValue")){//该项是下拉框的值
							column.setName(cgg.getSelectFromField());//设置name为来源控制项
						}else{
							column.setName(cgg.getFieldName());
						}
						column.setDefaultContent("");//为null时的默认值
						if(cgg.getIsLink()==1){//有链接
							column.setIsLink(1);
							column.setKeyIdName(StringFormat.fieldNameConvert(conf.getKeyField()));
							if(cgg.getLinkMethod()!=null && !"".equals(cgg.getLinkMethod())){
								column.setMethodName(cgg.getLinkMethod());
							}else{
								column.setMethodName("linkDetail");
							}
						}
					}
					colsList.add(column);
				}
			}
			json.put("columns", colsList);
			json.put("gridHeader", gridHeader);
			json.put("keyIdName", StringFormat.fieldNameConvert(conf.getKeyField()));
			
			//表格按钮
			List<ConfButton> buttonList = systemUtilSerivce.moduleButtons(confId, lang);
			json.put("buttonList", buttonList);
		}
		
		System.out.println(json);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	
	/**
	 * 搜索框(search from)的联动下拉数据查询
	 * @param response
	 * @param request
	 * @param confGridId
	 * @param changeId
	 * @throws IOException
	 */
	@RequestMapping("/subSearchStoreData")
	public void subSearchStoreData(HttpServletResponse response,HttpServletRequest request,
			@RequestParam(value="confGirdId") int confGridId,@RequestParam(value="changeId",defaultValue="0") String changeId) throws IOException{
		JSONObject json = new JSONObject();
		ConfGird fromCg = systemUtilSerivce.getSearchConfGird(confGridId);
		User user = (User)request.getSession().getAttribute("user");
		if(user!=null && fromCg!=null){
			ConfGird toCg = systemUtilSerivce.getSearchConfGird(fromCg.getSubTo());
			String sqlWhere = "";
			if(fromCg.getSearchType().equals("multiSelect")){//源下拉框是多选
				sqlWhere = " and "+fromCg.getFieldName()+" in("+changeId+")";
			}else{
				sqlWhere = " and "+fromCg.getFieldName()+" = "+changeId;
			}
			if(toCg!=null){
				json.put("subToConfig", toCg);
				List<StoreData> storeList = new ArrayList<StoreData>();
				if(toCg.getSelectIsObjData()==1){//搜索的数据是obj数据，有权限控制
					storeList = systemUtilSerivce.storeObjDataList(toCg.getSelectTable(),toCg.getSelectIdField(),toCg.getSelectValueField(),toCg.getSelectOrderField(),user.getUserId(),sqlWhere);
				}else{//搜索的数据是普通表
					storeList = systemUtilSerivce.storeDataList(toCg.getSelectTable(),toCg.getSelectIdField(),toCg.getSelectValueField(),toCg.getSelectOrderField(),sqlWhere);
				}
				json.put("storeData", storeList);
			}
		}
		
		System.out.println(json);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	
	/**
	 * form jsp获取form配置
	 * @param request
	 * @param confId
	 * @return
	 */
	public List<ConfForm> moduleFormCofig(HttpServletRequest request,int confId){
		String lang = (String) request.getAttribute("lang");
		if(lang==null){
			lang = "en";
		}
		List<ConfForm> list = systemUtilSerivce.moduleForm(confId, lang);
		return list;
	}
	
	/**
	 * 获取这module的form配置，并查询数据
	 * @param response
	 * @param request
	 * @param confId
	 * @param keyId
	 * @throws IOException
	 */
	@RequestMapping("/moduleForm")
	public void moduleForm(HttpServletResponse response,HttpServletRequest request,
			@RequestParam(value="confId") int confId,
			@RequestParam(value="keyId",defaultValue="0") int keyId) throws IOException{
		String lang = (String) request.getSession().getAttribute("lang");
		User user = (User)request.getSession().getAttribute("user");
		if(lang==null){
			lang = "en";
		}
		JSONObject json = new JSONObject();
		
		Conf conf = systemUtilSerivce.getConfData(confId);
		
		if(user!=null){
			try {
				Class<?> module = Class.forName("com.embraiz.model."+ conf.getModuleName());
				Object o = module.newInstance();
				
				//获取这个detail form的数据
				Object data = systemUtilSerivce.getViewData(conf, keyId, o);//存储form数据值作为form的数据绑定
				Object oldData = systemUtilSerivce.getViewData(conf, keyId, o);//存储form数据值作为原始值update的时候做对比
				
				List<ConfForm> list = systemUtilSerivce.moduleForm(confId, lang);
				//添加keyId隐藏到form
				ConfForm idCf = new ConfForm();
				idCf.setDisplay(false);
				idCf.setFieldName(StringFormat.fieldNameConvert(conf.getKeyField()));
				idCf.setFieldType("input");
				list.add(idCf);
				
				//存放所有下拉框的数据
				Map<String, Object> selectStore = new HashMap<String, Object>();
				
				//处理所有form的field选项，select的获取options数据，有默认值的设置默认值
				if(list.size()>0){
					for(ConfForm cf:list){
						if(cf.getFieldType()!=null && (cf.getFieldType().equals("select") || cf.getFieldType().equals("multiSelect") || cf.getFieldType().equals("filterSelect"))){//搜索下拉框数据
							List<StoreData> storeList = new ArrayList<StoreData>();
							String sqlWhere = "";
							if(cf.getSubFrom()!=null){//该select被其他select关联
								ConfForm fromCf = systemUtilSerivce.getFormConfField(cf.getSubFrom());
								String fromValue = "";
								String first = fromCf.getFieldName().substring(0, 1).toUpperCase();
								String fieldName = first+StringFormat.fieldNameConvert(fromCf.getFieldName()).substring(1);
								try {
									Method m = data.getClass().getMethod("get"+fieldName, new Class[]{});
									fromValue= m.invoke(data, new Object[]{})+"";
								} catch (Exception e) {
									e.printStackTrace();
								}
								if(!fromValue.equals("")){
									if(fromCf.getFieldType().equals("multiSelect")){//源下拉框是多选
										sqlWhere = sqlWhere+" and "+fromCf.getFieldName()+" in("+fromValue+")";
									}else{
										sqlWhere = sqlWhere+" and "+fromCf.getFieldName()+" = "+fromValue;
									}
								}
							}
							if(cf.getSelectIsObjData()==1){//搜索的数据是obj数据，有权限控制
								if(keyId==0){//insert
									sqlWhere = sqlWhere+" and access_right=0 ";
								}else{//update
									sqlWhere = sqlWhere+" and access_right<=1 ";
								}
								storeList = systemUtilSerivce.storeObjDataList(cf.getSelectTable(),cf.getSelectIdField(),cf.getSelectValueField(),cf.getSelectOrderField(),user.getUserId(),sqlWhere);
							}else{

								if(cf.getSelectTypeField()!=null && cf.getSelectTypeId()!=null){
									sqlWhere = sqlWhere + " and "+cf.getSelectTypeField() + " = "+ cf.getSelectTypeId();
								}
								
								String valueField = cf.getSelectValueField();
								if(lang.equals("cn")){
									valueField = valueField + "_cn";
								}else if(lang.equals("en")){
									valueField = valueField + "_en";
								}else{
									valueField = valueField + "_tc";
								}
								storeList = systemUtilSerivce.storeDataList(cf.getSelectTable(),cf.getSelectIdField(),valueField,cf.getSelectOrderField(),sqlWhere);
							}
							selectStore.put(cf.getFieldName()+"Store", storeList);	
						}
						//有默认值,且是insert的时候
						/*if(cf.getDefaultValue()!=null && !"".equals(cf.getDefaultValue()) && keyId==0){
							try {
								String fieldName = StringFormat.fieldNameConvert(cf.getFieldName());
								Field f = data.getClass().getDeclaredField(fieldName);
								f.setAccessible(true);
								f.set(fieldName, cf.getDefaultValue());
								Method m =  data.getClass().getMethod("get"+fieldName, new Class[]{});
								String className = m.getReturnType().toString().substring(6);//截取className，例如：class java.lang.String截取java.lang.String
								System.out.println(className);
								Class fieldClass = Class.forName(className);
								Method s = data.getClass().getMethod("set"+fieldName, fieldClass);
								Object v = (Object)cf.getDefaultValue();
								s.invoke(data, v);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}*/
					}
				}
				
				json.put("formData", data);
				json.put("formOldData", oldData);
				json.put("formConfig", list);
				json.put("formSelectStore", selectStore);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println(json);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * form联动下拉数据查询
	 * @param response
	 * @param request
	 * @param confFormId
	 * @param changeId
	 * @param formType
	 * @throws IOException
	 */
	@RequestMapping("/subFormStoreData")
	public void subFormStoreData(HttpServletResponse response,HttpServletRequest request,
			@RequestParam(value="confFormId") int confFormId,@RequestParam(value="changeId",defaultValue="0") String changeId,
			@RequestParam(value="formType") String formType) throws IOException{
		JSONObject json = new JSONObject();
		ConfForm fromCf = systemUtilSerivce.getFormConfField(confFormId);
		User user = (User)request.getSession().getAttribute("user");
		if(user!=null && fromCf!=null){
			ConfForm toCf = systemUtilSerivce.getFormConfField(fromCf.getSubTo());
			String sqlWhere = "";
			if(fromCf.getFieldType().equals("multiSelect")){//源下拉框是多选
				sqlWhere = " and "+fromCf.getFieldName()+" in("+changeId+")";
			}else{
				sqlWhere = " and "+fromCf.getFieldName()+" = "+changeId;
			}
			if(toCf!=null){
				json.put("subToConfig", toCf);
				List<StoreData> storeList = new ArrayList<StoreData>();
				if(toCf.getSelectIsObjData()==1){//搜索的数据是obj数据，有权限控制
					if(formType.equals("insert")){//insert
						sqlWhere = sqlWhere + " and access_right=0 ";
					}else{//update
						sqlWhere = sqlWhere + " and access_right<=1 ";
					}
					storeList = systemUtilSerivce.storeObjDataList(toCf.getSelectTable(),toCf.getSelectIdField(),toCf.getSelectValueField(),toCf.getSelectOrderField(),user.getUserId(),sqlWhere);
				}else{//搜索的是普通表的数据
					storeList = systemUtilSerivce.storeDataList(toCf.getSelectTable(),toCf.getSelectIdField(),toCf.getSelectValueField(),toCf.getSelectOrderField(),sqlWhere);
				}
				json.put("storeData", storeList);
			}
		}
		
		System.out.println(json);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	
	/**
	 * 获取module form的grid的配置
	 * @param response
	 * @param request
	 * @param formGridJson
	 * @throws IOException 
	 */
	@RequestMapping("/formGridConfig")
	public void formGridConfig(HttpServletResponse response,HttpServletRequest request,
			@RequestParam(value="confId") int confId) throws IOException{
		String lang = (String) request.getAttribute("lang");
		if(lang==null){
			lang = "en";
		}
		JSONObject json = new JSONObject();
		
		List<ConfFormGrid> formGridsConfig = new ArrayList<ConfFormGrid>();
		
		//获取所有关联grid
		List<ConfFormGrid> formGridList = systemUtilSerivce.formGrids(confId);
		if(formGridList.size()>0){
			for(ConfFormGrid cfg:formGridList){
				Conf gridConf = systemUtilSerivce.getConfData(cfg.getConfGridId());
				
				cfg.setGridConf(gridConf);
				cfg.setGridName(gridConf.getModuleName()+"FormGrid");
				cfg.setGridTitle(gridConf.getModuleName()+" List");
				
				List<ConfGird> gridList = systemUtilSerivce.moduleGird(cfg.getConfGridId(), lang);
				String gridHeader = ",id";
				List<Columns> colsList = new ArrayList<Columns>();
				//判断表格是否有checkBox
				if(cfg.getButtonShow()==true){//按钮显示
					gridHeader = ",id";
					Columns column = new Columns();
					column.setDefaultContent("");
					column.setClassName("select-checkbox");
					column.setOrderable(false);
					column.setWidth("5%");
					colsList.add(column);
				}else{
					gridHeader = "id";
				}
				//设置id列
				Columns colId = new Columns();
				colId.setData(StringFormat.fieldNameConvert(gridConf.getKeyField()));
				colId.setVisible(false);
				colId.setOrderable(false);
				colsList.add(colId);
				if(gridList.size()>0){
					for(ConfGird cgg:gridList){
						gridHeader = gridHeader+","+cgg.getLabelText();
						Columns column = new Columns();
						if(cgg.getFieldName()!=null){
							column.setData(StringFormat.fieldNameConvert(cgg.getFieldName()));
							if(cgg.getSearchType().equals("selectValue")){//下拉框的值
								column.setName(cgg.getSelectFromField());
							}else{
								column.setName(cgg.getFieldName());
							}
							column.setDefaultContent("");
							if(cgg.getIsLink()==1 && cgg.getLinkMethod()!=null && !"".equals(cgg.getLinkMethod())){//有链接
								column.setIsLink(1);
								column.setKeyIdName(StringFormat.fieldNameConvert(gridConf.getKeyField()));
								column.setMethodName(cgg.getLinkMethod());
							}
						}
						colsList.add(column);
					}
				}
				cfg.setColumns(colsList);
				cfg.setGridHeader(gridHeader);
				cfg.setKeyIdName(StringFormat.fieldNameConvert(gridConf.getKeyField()));
				
				//表格按钮
				if(cfg.getButtonShow()==true){
					List<ConfButton> buttonList = systemUtilSerivce.moduleButtons(cfg.getConfGridId(), lang);
					cfg.setButtonList(buttonList);
				}
				
				formGridsConfig.add(cfg);
			}
		}
		json.put("formGridsConfig", formGridsConfig);
		
		System.out.println(json);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 查询表格数据
	 * @param response
	 * @param request
	 * @param model
	 * @param searchForm
	 * @param confId
	 * @param length
	 * @param start
	 * @param sortCol
	 * @param sortOrder
	 * @throws Exception
	 */
	@RequestMapping("/gridData")
	public void gridData(HttpServletResponse response,HttpServletRequest request,
			@RequestParam(value="model") String model,
			@RequestParam(value="searchForm") String searchForm,
			@RequestParam(value="confId") int confId,
			@RequestParam(value="length") int length,@RequestParam(value="start") int start,
			@RequestParam(value="order[0][column]",defaultValue="1") String sortCol,
			@RequestParam(value="order[0][dir]",defaultValue="asc") String sortOrder) throws Exception{
		try{
			User user = (User)request.getSession().getAttribute("user");
			String lang = (String) request.getSession().getAttribute("lang");
			if(lang==null){
				lang = "en";
			}
			
			int userId = 0;
			if(user!=null){
				userId = user.getUserId();
			}
			Class<?> module = Class.forName("com.embraiz.model."+ model);
			Object o = module.newInstance();
			
			//获取排序字段
			String sortBy = "";
			if(request.getParameter("columns["+sortCol+"][name]")!=null && !request.getParameter("columns["+sortCol+"][name]").equals("")){
				sortBy = request.getParameter("columns["+sortCol+"][name]");
			}
			
			Page pageBo = new Page();
			pageBo.setCount(length);
			pageBo.setStart(start);
			
			Map<String, Object> map = systemUtilSerivce.getList(searchForm, pageBo, sortBy, sortOrder, o,userId,confId ,null,lang);
			
			JSONObject json = new JSONObject();
			json.put("recordsTotal", map.get("recordsTotal"));
			json.put("recordsFiltered", map.get("recordsFiltered"));
			json.put("data", map.get("data"));
			
			System.out.println(json);
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(json);
			response.getWriter().flush();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * form grid分页查询数据
	 * @param response
	 * @param request
	 * @param model
	 * @param confId
	 * @param relationId 关联id
	 * @param relationField 关联id的field
	 * @param length
	 * @param start
	 * @param sortCol
	 * @param sortOrder
	 * @throws Exception
	 */
	@RequestMapping("/formGridData")
	public void formGridData(HttpServletResponse response,HttpServletRequest request,
			@RequestParam(value="model") String model,
			@RequestParam(value="confId") int confId,
			@RequestParam(value="relationId") int relationId,
			@RequestParam(value="relationField") String relationField,
			@RequestParam(value="length") int length,@RequestParam(value="start") int start,
			@RequestParam(value="order[0][column]",defaultValue="1") String sortCol,
			@RequestParam(value="order[0][dir]",defaultValue="asc") String sortOrder) throws Exception{
		try{
			User user = (User)request.getSession().getAttribute("user");
			int userId = 0;
			if(user!=null){
				userId = user.getUserId();
			}
			Class<?> module = Class.forName("com.embraiz.model."+ model);
			Object o = module.newInstance();
			
			//获取排序字段
			String sortBy = "";
			if(request.getParameter("columns["+sortCol+"][name]")!=null && !request.getParameter("columns["+sortCol+"][name]").equals("")){
				sortBy = request.getParameter("columns["+sortCol+"][name]");
			}
			
			Page pageBo = new Page();
			pageBo.setCount(length);
			pageBo.setStart(start);
			
			Map<String, Object> map = systemUtilSerivce.getformGridList(relationId,relationField, pageBo, sortBy, sortOrder, o, confId,userId);
			
			JSONObject json = new JSONObject();
			json.put("recordsTotal", map.get("recordsTotal"));
			json.put("recordsFiltered", map.get("recordsFiltered"));
			json.put("data", map.get("data"));
			
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(json);
			response.getWriter().flush();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加或更新数据
	 * @param response
	 * @param request
	 * @param formData
	 * @param confId
	 * @throws IOException
	 */
	@RequestMapping("/updateData")
	public void updateData(HttpServletResponse response,HttpServletRequest request,
			@RequestParam(value="formData") String formData,
			@RequestParam(value="changeFields",defaultValue="") String changeFields,
			@RequestParam(value="confId") int confId) throws IOException{
		JSONObject json = new JSONObject();
		int status = 0;
		int keyId = 0;
		String msg = "";
		User user = (User)request.getSession().getAttribute("user");
		if(user!=null){
			Conf conf = systemUtilSerivce.getConfData(confId);
			
			if(formData!=null && !formData.equals("")){
				formData = StringFormat.encodeStr(formData);//解决中文乱码
				changeFields = StringFormat.encodeStr(formData);//解决中文乱码
				String newchangeFields="["+changeFields+"]";//改成jsonArrays形式的字符串
				JSONObject data = JSONObject.parseObject(formData);
				
				if(data.get(StringFormat.fieldNameConvert(conf.getKeyField()))==null || data.get(StringFormat.fieldNameConvert(conf.getKeyField()))=="0"){//insert
					keyId = systemUtilSerivce.insertData(conf,user,data);
					if(keyId>0){
						status=1;
						msg = "succAdd";
					}else if(keyId<0){//在conf中配置obj的信息有误
						status=0;
						msg = "confErr";
					}else{
						status=0;
						msg = "failAdd";
					}
				}else{//update
					keyId = Integer.parseInt(data.get(StringFormat.fieldNameConvert(conf.getKeyField())).toString());
					status = systemUtilSerivce.updateData(conf, data, keyId ,user.getUserId(),newchangeFields);
					if(status==1){
						msg = "succUpdate";
					}else{
						msg = "failUpdate";
					}
				}
			}else{
				msg = "dataErr";
			}
		}else{
			msg = "userErr";
		}
		
		json.put("status",status);
		json.put("msg",msg);
		json.put("keyId",keyId);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 删除数据
	 * @param response
	 * @param request
	 * @param ids
	 * @param confId
	 * @throws IOException
	 */
	@RequestMapping("/deleteData")
	public void deleteData(HttpServletResponse response,HttpServletRequest request,@RequestParam(value="ids") String ids,@RequestParam(value="confId") int confId) throws IOException{
		JSONObject json = new JSONObject();
		User nowUser = (User)request.getSession().getAttribute("user");
		int status = 0;
		String msg = "";
		
		Conf conf = systemUtilSerivce.getConfData(confId);
		
		if(nowUser!=null){
			if(!ids.equals("")){
				int flag = systemUtilSerivce.deleteData(conf, ids,nowUser.getUserId());
				if(flag>0){
					status=1;
				}
			}
			
			if(status==1){
				msg="succDel";
			}else{
				msg="failDel";
			}
		}else{
			msg="failDel";
		}
		
		json.put("status",status);
		json.put("msg",msg);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	/**
	 * 获取一个表格的数据组成下拉框数据
	 * @param response
	 * @param request
	 * @param tableName
	 * @param idField
	 * @param valueField
	 * @param orderField
	 * @throws IOException
	 */
	@RequestMapping("/storeData")
	public void getStoreData(HttpServletResponse response,HttpServletRequest request,
			@RequestParam(value="tableName") String tableName,@RequestParam(value="idField") String idField,
			@RequestParam(value="valueField") String valueField,@RequestParam(value="orderField",defaultValue="") String orderField) throws IOException{
		List<StoreData> list = new ArrayList<StoreData>();
		
		if(tableName!=null && idField!=null && valueField!=null){
			list = systemUtilSerivce.storeDataList(tableName, idField, valueField, orderField,null);
		}
		
		JSONArray json = new JSONArray();
		json.add(list);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
	
	public List<ConfForm> changeJsonToConfData(String[] formConfigString){
		List<ConfForm> formConfig = new ArrayList<ConfForm>();
		if(formConfigString!=null && !formConfigString.equals("")){
			for(int i=0;i<formConfigString.length;i++){
				String value = formConfigString[i];
				ConfForm cf = JSONObject.toJavaObject(JSONObject.parseObject(value), ConfForm.class);
				formConfig.add(cf);
			}
		}
		return formConfig;
	}
}
