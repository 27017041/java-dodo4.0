package com.embraiz.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.embraiz.helper.SystemHelper;
import com.embraiz.model.AuthToken;
import com.embraiz.model.Mail;
import com.embraiz.model.Obj;
import com.embraiz.model.Page;
import com.embraiz.model.Role;
import com.embraiz.model.User;
import com.embraiz.service.AuthTokenService;
import com.embraiz.service.CommonService;
import com.embraiz.service.ObjService;
import com.embraiz.service.RoleService;
import com.embraiz.service.SystemUtilService;
import com.embraiz.service.UserService;
import com.embraiz.util.DateUtil;
import com.embraiz.util.MD5Util;
import com.embraiz.util.MainUtil;
import com.embraiz.util.SendMail;
import com.embraiz.util.StringFormat;
import com.embraiz.util.TokenUtil;

@Controller
@RequestMapping("/user")
public class UserController {

	@Resource
	private UserService userService;
	@Resource
	private SystemUtilService systemUtilService;
	@Resource
	private ObjService objSerivce;
	@Resource
	private SystemHelper systemHelper;
	@Resource
	private CommonService commonService;
	@Resource
	private RoleService roleService;
	@Resource
	private AuthTokenService authTokenService;

	private static String checkMsg;

	/**
	 * 验证邮箱是否已注册
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public void ajaxValidateEmail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*
		 * 1. 获取Email
		 */
		String email = req.getParameter("email");
		/*
		 * 2. 通过service得到校验结果
		 */
		boolean b = userService.ajaxValidateEmail(email);
		/*
		 * 3. 发给客户端
		 */
		resp.getWriter().print(b);
	}

	/**
	 * 用户自己注册
	 * 
	 * @param userForm
	 * @param objParentId
	 *            用户归属
	 * @throws IOException
	 */
	@RequestMapping("/selfRegister")
	public void selfRegister(@ModelAttribute User userForm, @RequestParam(value = "objParentId") Integer objParentId, HttpServletResponse response)
			throws IOException {
		JSONObject json = new JSONObject();

		if (userForm != null) {
			json = deDecide(userForm);
			if (json.isEmpty()) {
				// 设置用户的权限
				userForm.setRoleId(5 + "");
				// 设置用户的状态为未审核
				userForm.setStatus(62);
				// 设置用户来源
				userForm.setSource(60);

				int id = userService.insertUser(userForm, 1, objParentId);
				if (id > 0) {
					objSerivce.updateObjOrder(id);
				}
				json.put("success", true);
				json.put("msg", "Register success");
			}
			doRes(response, json);
		} else {
			json.put("success", false);
			json.put("msg", "The params is error");
			doRes(response, json);
		}
	}

	/**
	 * 管理员建立用户
	 * 
	 * @param userForm
	 * @param objParentId
	 *            用户归属
	 * @throws IOException
	 */
	@RequestMapping("/adminRegister")
	public void adminRegister(@ModelAttribute User userForm, @RequestParam(value = "objParentId") Integer objParentId, HttpServletResponse response,
			HttpServletRequest request) throws IOException {
		JSONObject json = new JSONObject();
		User loginUser = (User) request.getSession().getAttribute("user");
		if (userForm != null) {
			json = deDecide(userForm);
			if (json.isEmpty() && (userForm.getRoleId() == null || userForm.getRoleId() == "")) {
				json.put("success", false);
				json.put("msg", "Administrators set up users to assign permissions");
			}
			if (json.isEmpty()) {
				// 设置用户的状态为未审核
				userForm.setStatus(13);
				// 设置用户来源
				userForm.setSource(61);
				userForm.setFirstLogin(1);
				String password = MainUtil.getPwd(5);
				MD5Util md5 = new MD5Util();
				userForm.setPassword(md5.setMD5(password));
				int id = userService.insertUser(userForm, loginUser.getUserId(), objParentId);
				if (id > 0) {
					objSerivce.updateObjOrder(id);
					// Mail mail=new Mail();
					SendMail sendMail = new SendMail();
					String emailcontent = "<html><body>Hi " + userForm.getFirstName() == null ? "" : userForm.getFirstName() + "  "
							+ userForm.getLastName() == null ? "" : userForm.getFirstName() + "  " + userForm.getLastName() + "<br><br>"
							+ "<p>Login Name  :  " + userForm.getLoginName() + "</p>" + "<p>Passrword  :  " + password + "</P><br/>"
							+ "</body></html>";
					sendMail.sendMail(userForm.getEmail(), "Create User", emailcontent);
				}
				json.put("success", true);
				json.put("msg", "Register success");
			}
			doRes(response, json);
		} else {
			json.put("success", false);
			json.put("msg", "The params is error");
			doRes(response, json);
		}
	}

	/**
	 * 管理员审核自己注册的用户,并更改用户的角色.
	 * 
	 * @param userId
	 *            用户Id
	 * @return
	 */
	@RequestMapping("/adminExamineUser")
	public void adminExamineUser(@RequestParam(value = "userId") Integer userId, @RequestParam(value = "roleId") Integer roleId,
			HttpServletResponse response, HttpServletRequest request) throws IOException {
		User loginUser = (User) request.getSession().getAttribute("user");
		JSONObject json = new JSONObject();

		// 判断用户角色参数
		if (json.isEmpty() && (roleId == null || roleId <= 0)) {
			json.put("success", false);
			json.put("msg", "Role parameters is error ");
		}
		// 判断用户Id的参数
		if (userId == null || userId <= 0) {
			json.put("success", false);
			json.put("msg", "User parameters is error");
		} else {
			// 根据id查找用户信息
			User user = userService.getUserById(userId);
			if (json.isEmpty() && user == null) {
				json.put("success", false);
				json.put("msg", "User does not exist");
			}

			if (json.isEmpty()) {
				user.setStatus(13);
				user.setRoleId(roleId + "");
				String changeFiloeds = "Status from '" + user.getStatus() + "' to '" + 13 + "',Role from '" + user.getRoleId() + "' to '" + 13 + "'";
				int status = userService.updateUser(user, changeFiloeds, loginUser.getUserId());
				if (status == 1) {
					json.put("success", true);
					json.put("msg", "Audit user success");
				} else {
					json.put("success", false);
					json.put("msg", "Audit user fail");
				}
			}
		}
		doRes(response, json);

	}

	private JSONObject deDecide(User userForm) {
		JSONObject json = new JSONObject();
		String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(userForm.getEmail());

		// 判断邮箱的格式
		if (!matcher.matches()) {
			json.put("success", false);
			json.put("msg", "Incorrect mail format");
			return json;
		}
		// 判断login_name是否已经使用
		boolean lNFlag = userService.ajaxValidateLoginName(userForm.getLoginName());
		if (!lNFlag) {
			json.put("success", false);
			json.put("msg", "The login name has been registered");
			return json;
		}
		// 判断邮箱是否已经注册
		boolean eFlag = userService.ajaxValidateEmail(userForm.getEmail());
		if (!eFlag) {
			json.put("success", false);
			json.put("msg", "The mailbox has been registered");
			return json;
		}
		return json;
	}

	private void doRes(HttpServletResponse response, JSONObject json) throws IOException {
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	@RequestMapping("/setLang")
	public void setLang(@RequestParam(value = "lang") String lang, HttpServletResponse response, HttpSession session) throws IOException {
		if ("en".equals(lang) || "cn".equals(lang) || "tc".equals(lang)) {
			session.setAttribute("lang", lang);
			response.getWriter().write("{\"success\":true}");
		} else {
			response.getWriter().write("{\"success\":false}");
		}
	}

	/**
	 * 获取user
	 * 
	 * @param userForm
	 * @param session
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/getUser")
	public void signIn(HttpServletResponse response, HttpSession session) throws IOException {
		User user = (User) session.getAttribute("user");
		JSONObject json = new JSONObject();

		if (user != null) {
			json.put("user", user);
		} else {
			json.put("user", false);
		}

		String lang = (String) session.getAttribute("lang");
		if (lang == null) {
			lang = "en";
		}
		json.put("lang", lang);

		String[] labelType = { "text", "tips" };
		json.put("label", systemHelper.getLabelListByType(lang, labelType));

		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 刷新检查session的user，并重新刷新session
	 * 
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/refreshUser")
	public void refreshUserSession(HttpServletResponse response, HttpSession session) throws IOException {
		User user = (User) session.getAttribute("user");
		String lang = (String) session.getAttribute("lang");
		if (user != null) {
			session.setAttribute("user", user);
			session.setAttribute("lang", lang == null ? "en" : lang);
			response.getWriter().write("{\"success\":true,\"status\":1}");
		} else {
			response.getWriter().write("{\"success\":true,\"status\":0}");
		}
	}

	/**
	 * 登陆
	 * 
	 * @param userForm
	 * @param session
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/signIn")
	public void signIn(@ModelAttribute User userForm, HttpServletResponse response, HttpSession session) throws IOException {
		MD5Util md5Util = new MD5Util();
		String password = md5Util.setMD5(userForm.getPassword());
		// User user = (User)
		// userService.checkEmailAndPassword(userForm.getEmail(),
		// userForm.getPassword());
		User user = (User) userService.checkEmailAndPassword(userForm.getEmail(), password);
		if (user == null) {
			response.getWriter().write("{\"success\":false}");
		} else {
			// 创建用户签名
			AuthToken authToken = new AuthToken();
			String uuid = TokenUtil.createUUID();
			String token = TokenUtil.getToken(uuid);
			Integer time = DateUtil.getDate();
			authToken.setUid(user.getUserId());
			authToken.setUuid(uuid);
			authToken.setToken(token);
			authToken.setOverdue(time.intValue() + 60 * 60 * 24 * 7);
			int count = authTokenService.getCountByUid(authToken.getUid());
			if (count > 0) {
				authTokenService.updateToken(authToken);
			} else {
				authTokenService.addToken(authToken);
			}
			if (user.getFirstLogin() == 1) {// 第一次登录修改密码
				response.getWriter().write(
						"{\"success\":true,\"status\":0,\"uid\":" + user.getUserId() + ",\"x-auth-token\":\"" + uuid + "\",\"x-lang\":\""
								+ user.getLang() + "\"}");
			} else {
				// 插入登錄表
				userService.saveLoginLog(user);

				// 把用户信息Id存入对象
				session.setAttribute("user", user);
				response.getWriter().write(
						"{\"success\":true,\"status\":1,\"uid\":" + user.getUserId() + ",\"x-auth-token\":\"" + uuid + "\",\"x-lang\":\""
								+ user.getLang() + "\"}");
			}
		}
		response.getWriter().flush();
	}

	/**
	 * 退出
	 * 
	 * @param session
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/updateAndsignOut")
	public void updateAndsignOut(HttpServletResponse response, HttpSession session) throws IOException {
		User user = (User) session.getAttribute("user");
		int i = userService.updateandsignOut(user);
		session.removeAttribute("user");
		response.setCharacterEncoding("utf-8");
		response.getWriter().write("{\"session\":false}");
		response.getWriter().flush();
	}

	/**
	 * session失效
	 * 
	 * @param url
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("toLogin")
	public void toLogin(@RequestParam(value = "url", required = false) String url, HttpServletResponse response, HttpSession session)
			throws IOException {
		// session失效修改登录记录
		User user = (User) session.getAttribute("user");
		int i = userService.updateandsignOut(user);
		response.getWriter().print("{\"session\":false,\"url\":\"" + url + "\"}");
		response.setCharacterEncoding("utf-8");
		response.getWriter().flush();
	}

	/**
	 * 注册
	 * 
	 * @param userForm
	 * @param session
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/signUp")
	public void signUp(@ModelAttribute User userForm, HttpServletResponse response) throws IOException {

		String emailRegex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(userForm.getEmail());
		// 邮箱是唯一的
		if (matcher.matches()) {
			User user = userService.checkEmail(userForm.getEmail());
			if (user == null) {
				response.getWriter().write("{\"success\":false}");
			} else {
				// 发送邮件
				response.getWriter().write("{\"success\":true}");
			}
		} else {
			response.getWriter().write("{\"success\":false}");
		}

	}

	/**
	 * 忘记密码，发送邮件
	 * 
	 * @param userForm
	 * @param session
	 */
	@RequestMapping("/forgetPassword")
	public void forgetPassword(@ModelAttribute User userForm, HttpSession session) {

	}

	/**
	 * 获取search form的所有下拉框数据
	 * 
	 * @param response
	 * @param request
	 * @throws IOException
	 */
	@RequestMapping("/getSearchStore")
	public void getSearchStoreData(HttpServletResponse response, HttpServletRequest request) throws IOException {
		User loginUser = (User) request.getSession().getAttribute("user");
		JSONObject json = new JSONObject();

		String lang = (String) request.getSession().getAttribute("lang");
		if (lang == null) {
			lang = "en";
		}

		// 存放所有下拉框的数据
		Map<String, Object> selectStore = new HashMap<String, Object>();

		if (loginUser != null) {
			// -------role栏位-------
			Obj userobj = objSerivce.getObjData(loginUser.getUserId());

			List<Role> roleList = new ArrayList<Role>();
			if (userobj.getObjParentId() != 0) {// 不是superadmin
				roleList = roleService.getRoleByEntity(userobj.getL0());
			} else {
				roleList = roleService.getAllRole();
			}

			selectStore.put("roleList", roleList);

			// -------status栏位-------
			selectStore.put("statusList", commonService.getSelectDataInModule(1, lang));
		}

		json.put("selectStore", selectStore);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 表格获取user的数据
	 * 
	 * @param response
	 * @param request
	 * @param searchForm
	 * @param length
	 * @param start
	 * @param sortCol
	 * @param sortOrder
	 * @throws IOException
	 */
	@RequestMapping("/getUserList")
	public void getUserList(HttpServletResponse response, HttpServletRequest request, @RequestParam(value = "searchForm") String searchForm,
			@RequestParam(value = "length") int length, @RequestParam(value = "start") int start,
			@RequestParam(value = "order[0][column]", defaultValue = "0") String sortCol,
			@RequestParam(value = "order[0][dir]", defaultValue = "asc") String sortOrder) throws IOException {
		User user = (User) request.getSession().getAttribute("user");
		int userId = 0;
		if (user != null) {
			userId = user.getUserId();
		}

		String lang = (String) request.getSession().getAttribute("lang");
		if (lang == null) {
			lang = "en";
		}

		// 获取排序字段
		String sortBy = "loginName";
		if (request.getParameter("columns[" + sortCol + "][name]") != null && !request.getParameter("columns[" + sortCol + "][name]").equals("")) {
			sortBy = request.getParameter("columns[" + sortCol + "][name]");
		}

		Page pageBo = new Page();
		pageBo.setCount(length);
		pageBo.setStart(start);

		Map<String, Object> map = userService.getUserList(searchForm, userId, pageBo, sortBy, sortOrder, lang);

		JSONObject json = new JSONObject();
		json.put("recordsTotal", map.get("recordsTotal"));
		json.put("recordsFiltered", map.get("recordsFiltered"));
		json.put("data", map.get("data"));

		System.out.println(json);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * user form获取user的数据，以及form所有下拉框的数据
	 * 
	 * @param response
	 * @param request
	 * @param userId
	 * @throws IOException
	 */
	@RequestMapping("/getUserData")
	public void getUserData(HttpServletResponse response, HttpServletRequest request, @RequestParam(value = "userId") int userId) throws IOException {
		User loginUser = (User) request.getSession().getAttribute("user");
		JSONObject json = new JSONObject();
		// 存放所有下拉框的数据
		Map<String, Object> selectStore = new HashMap<String, Object>();
		if (loginUser != null) {

			String lang = (String) request.getSession().getAttribute("lang");
			if (lang == null) {
				lang = "en";
			}

			// **********************??????*****************************
			User user = userService.getUserData(userId, lang);
			User userData = userService.getUserData(userId, lang);

			json.put("user", user);
			json.put("userData", userData);
			// **********************??????*****************************

			// 获取form的所有下拉框的数据

			// -------role------
			List<Role> roleList = new ArrayList<Role>();

			Obj userobj = objSerivce.getObjData(loginUser.getUserId());

			if (userobj.getObjParentId() != 0) {// 不是superadmin
				roleList = roleService.getRoleByEntity(userobj.getL0());
			} else {
				roleList = roleService.getAllRole();
			}

			selectStore.put("roleList", roleList);

			// -------status------
			selectStore.put("statusList", commonService.getSelectDataInModule(1, lang));

			// 获取teamData下拉框的数据
			String sqlWhere = "";
			if (userId == 0) {// insert
				sqlWhere = sqlWhere + " and accessRight=0 ";
			} else {// update
				sqlWhere = sqlWhere + " and accessRight<=1 ";
			}

			selectStore.put("teamList", commonService.getTeamData(loginUser.getUserId(), sqlWhere));
		}

		json.put("selectStore", selectStore);

		System.out.println(json);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * insert或update user
	 * 
	 * @param response
	 * @param request
	 * @param changeFields
	 * @param userJson
	 * @throws IOException
	 */
	@RequestMapping(value = "/updateUser", produces = "text/html;charset=UTF-8")
	public void updateUser(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(value = "changeFields", defaultValue = "") String changeFields, @RequestParam(value = "formData") String userJson)
			throws IOException {
		User nowUser = (User) request.getSession().getAttribute("user");
		JSONObject json = new JSONObject();
		int userId = 0;
		int status = 0;
		String msg = "";
		if (nowUser != null) {
			User user = JSONObject.toJavaObject(JSONObject.parseObject(userJson), User.class);

			boolean flag = check(user);

			if (flag) {
				// 多选去除[]
				String roleId = user.getRoleId();
				roleId = StringFormat.multSelectValueFormat(roleId);
				if (!roleId.equals("")) {
					user.setRoleId(roleId);
				}

				if (user.getUserId() != null) {// update
					userId = user.getUserId();
					status = userService.updateUser(user, changeFields, nowUser.getUserId());
					if (status == 1) {
						msg = "succUpdate";
					} else {
						msg = "failUpdate";
					}
				} else {// insert

					boolean rtn = userService.validateLoginName(user.getLoginName());
					if (rtn) {
						// 生成随机八位密码
						String pwd = MainUtil.getPwd(8);
						MD5Util md5Util = new MD5Util();
						user.setPassword(md5Util.setMD5(pwd));
						System.out.println(user.getPassword());
						int keyId = userService.insertUser(user, nowUser.getUserId(), 0);
						if (keyId > 0) {
							status = 1;
							userId = keyId;
							setUserEmail(user, pwd);// 发送用户登录邮件

							msg = "succAdd";
						} else {
							status = 0;
							msg = "failAdd";
						}
					} else {
						status = 0;
						msg = "failAdd";
					}
				}
			} else {
				status = 0;
				msg = this.checkMsg;
			}
		} else {
			msg = "userErr";
		}

		json.put("status", status);
		json.put("msg", msg);
		json.put("userId", userId);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * insert和update之前检查
	 * 
	 * @param user
	 * @return
	 */
	public boolean check(User user) {
		boolean flag = true;
		User check = new User();
		if (user.getUserId() != null) {// 已存在user不需要检查
			flag = true;
		} else {
			check = userService.checkEmail(user.getEmail());
		}

		if (check != null && check.getEmail()!=null) {
			if (user.getUserId() == null) {
				flag = false;
			} else {
				if (user.getUserId() != check.getUserId()) {
					flag = false;
				}
			}
		}
		if (!flag) {
			this.checkMsg = "emailExist";
		}
		return flag;
	}

	/**
	 * 发送用户登录信息邮件
	 * 
	 * @param user
	 * @param pwd
	 * @return
	 */
	public boolean setUserEmail(User user, String pwd) {
		StringBuffer content = new StringBuffer();
		content.append("<strong>Dear ").append(user.getLoginName()).append(",</strong> <br/><br/>");
		content.append("Thank you for your support. This email is being sent as confirmation of your registration:<br/><br/> ");
		content.append("Here is your login passWord:<strong>").append(pwd).append("</strong><br/><br/>");
		// content.append("passWord:<strong>").append(pwd).append("</strong><br/><br/> ");
		content.append("Best regards, <br/>");
		content.append("NakedCoach System<br/>");

		Mail mail = new Mail();
		mail.setSendTo(user.getEmail());
		mail.setSubject("Login information");
		mail.setContent(content.toString());

		return true;
		// return mailService.saveAndSendEmail(mail);
	}

	/**
	 * 删除user
	 * 
	 * @param response
	 * @param request
	 * @param userIds
	 * @throws IOException
	 */
	@RequestMapping("/deleteUser")
	public void deleteUser(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "userIds") String userIds) throws IOException {
		User nowUser = (User) session.getAttribute("user");
		JSONObject json = new JSONObject();
		int status = 0;
		String msg = "";
		if (nowUser != null) {
			if (!userIds.equals("")) {
				int flag = userService.deleteUser(userIds, nowUser.getUserId());
				if (flag > 0) {
					status = 1;
				}
			}

			if (status == 1) {
				msg = "succDel";
			} else {
				msg = "failDel";
			}
		} else {
			msg = "userErr";
		}

		json.put("status", status);
		json.put("msg", msg);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}

	/**
	 * 更新密码
	 * 
	 * @param password
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping("/updatePsw")
	public void updatePSW(@RequestParam(value = "password") String password, HttpServletResponse response, HttpSession session) throws IOException {
		JSONObject json = new JSONObject();
		User user = (User) session.getAttribute("user");
		MD5Util md5Util = new MD5Util();
		password = md5Util.setMD5(password);
		int status = 0;
		if (user != null) {
			status = userService.updatePSW(user.getUserId(), password);
		}
		if (status == 1) {
			session.removeAttribute("user");
		}
		json.put("status", status);
		response.setCharacterEncoding("utf-8");
		response.getWriter().print(json);
		response.getWriter().flush();
	}
}
