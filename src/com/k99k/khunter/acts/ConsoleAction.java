/**
 * 
 */
package com.k99k.khunter.acts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.KFilter;
import com.k99k.khunter.HTManager;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.khunter.KIoc;
import com.k99k.tools.JSON;

/**
 * 控制台总Action,同时管理其他Console 用到的action
 * @author keel
 *
 */
public class ConsoleAction extends Action {

	/**
	 * @param name
	 */
	public ConsoleAction(String name) {
		super(name);
	}
	static final Logger log = Logger.getLogger(ConsoleAction.class);
	
	
	/**
	 * 存储Action的Map,初始化大小为100
	 */
	private static final Map<String, Action> consoleActMap = new HashMap<String, Action>(50);
	
	/**
	 * 管理员map
	 */
	private static final Map<String, Map<String,Object>> adminMap = new HashMap<String, Map<String,Object>>(50);
	
	
	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#act(com.k99k.khunter.ActionMsg)
	 */
	@Override
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
		
//		String right = "state";
		
//		if (httpmsg.getHttpReq().getParameter("right") != null) {
//			right = httpmsg.getHttpReq().getParameter("right");
//		}
		
		//1级Action定位
		String right = KFilter.actPath(msg, 2, "state");//(pathArr.length == 3) ? "state" : pathArr[2];
		msg.addData(ActionMsg.MSG_JSP, "/WEB-INF/to/console.jsp");
		HttpSession se = httpmsg.getHttpReq().getSession();
		if (right.equals("login")) {
			//处理登录
			String name = httpmsg.getHttpReq().getParameter("form_name");
			String pwd = httpmsg.getHttpReq().getParameter("form_pwd");
			if (name != null && pwd != null && adminMap.containsKey(name) && pwd.equals(adminMap.get(name).get("pwd"))) {
				//ok
				se.setAttribute("admin", name);
				//msg.addData("right", "state");
				//msg.addData("jspAttr", msg);
				msg.removeData(ActionMsg.MSG_JSP);
				msg.addData(ActionMsg.MSG_REDIRECT, "/console/state");
			}else{
				//error
				msg.addData(ActionMsg.MSG_JSP, "/WEB-INF/to/login.jsp");
			}
		}
		
		//退出
		else if (right.equals("exit")) {
			se.removeAttribute("admin");
			se.invalidate();
			msg.addData(ActionMsg.MSG_JSP, "/WEB-INF/to/login.jsp");
		}
		else{
			//验证登录
			
			if (se == null || se.getAttribute("admin")==null ) {
				msg.addData(ActionMsg.MSG_JSP, "/WEB-INF/to/login.jsp");
			}else{
				//处理其他管理动作
				msg.addData("right", right);
				//调用相关的ConsoleAction
				Action a = consoleActMap.get(right);
				if (a !=null) {
					a.act(httpmsg);
				}
				//没有action则作为调用其他right部分的jsp用
//				else{
//					log.error("Can't find in consoleActMap:"+right);
//				}
				//msg.addData(ActionMsg.MSG_JSP_ATTR, msg);
				msg.setNextAction(null);
			}
		}

		return super.act(msg);
	}

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#init()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		try {
			String ini = KIoc.readTxtInUTF8(HTManager.getIniPath()+getIniPath());
			Map<String,?> root = (Map<String,?>) JSON.read(ini);
			//先定位到json的actions属性
			Map<String, ?> actionsMap = (Map<String, ?>) root.get("actions");
			//循环加入Action
			int i = 0;
			for (Iterator<String> iter = actionsMap.keySet().iterator(); iter.hasNext();) {
				String actionName = iter.next();
				Map<String, ?> m = (Map<String, ?>) actionsMap.get(actionName);
				//读取必要的属性，如果少则报错并继续下一个
				if (m.containsKey("_class")) {
					String _class = (String) m.get("_class");
					
					/*
					//type默认为normal //--直接在属性中加入
					//String _type = (m.containsKey("_type"))?"normal":(String) m.get("_type");
					*/
					
					Object o = KIoc.loadClassInstance("file:/"+HTManager.getClassPath(), _class, new Object[]{actionName});
					if (o == null) {
						log.error("loadClassInstance error! _class:"+_class+" _name:"+actionName);
						continue;
					}
					Action action = (Action)o;
					
					HTManager.fetchProps(action, m);
					//加入Action,无论是否已存在,以action的name作为jsp输出的默认right
					consoleActMap.put(action.getName(), action);
					log.info("- ConsoleAction added: "+action.getName());
					try {
						//Action初始化
						action.init();
					} catch (Exception e) {
						log.error("Action init Error:"+action.getName(), e);
						continue;
					}
				}else{
					log.error("ConsoleAction init Error! miss one or more key props. Position:"+i);
					continue;
				}
				i++;
			}
		} catch (Exception e) {
			log.error("ConsoleAction init Error!", e);
		}
	}
	
	/**
	 * 设置所有管理员
	 * @param admins 多个管理员
	 */
	@SuppressWarnings("unchecked")
	public final void setAdmin(Object admins){
		ArrayList<Map<String,Object>> as = (ArrayList<Map<String,Object>> )admins;
		for (Iterator<Map<String,Object>> it = as.iterator(); it.hasNext();) {
			Map<String, Object> admin =  it.next();
			adminMap.put(admin.get("name").toString(), admin);
		}
	}

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#getIniPath()
	 */
	@Override
	public String getIniPath() {
		return "console.json";
	}

	@Override
	public void exit() {
		adminMap.clear();
		consoleActMap.clear();
	}

}
