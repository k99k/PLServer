/**
 * 
 */
package com.k99k.khunter.acts;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionManager;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.KFilter;
import com.k99k.khunter.DaoManager;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.khunter.KObjManager;
import com.k99k.tools.StringUtil;

/**
 * 控制重新启动的Action
 * @author keel
 *
 */
public class ConsoleReloadAction extends Action {

	/**
	 * @param name
	 */
	public ConsoleReloadAction(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#act(com.k99k.khunter.ActionMsg)
	 */
	@Override
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
		//子命令
//		String subact = httpmsg.getHttpReq().getParameter("subact");
//		if (subact == null || subact.trim().length() <3) {
//			subact = "show";
//		}
		
		String subact = KFilter.actPath(msg, 3, "show");//(pathArr.length == 4) ? "show" : pathArr[3];
		msg.addData("subact", subact);
		String re = "err";
		//显示重启菜单
		if (subact.equals("show")) {
			return super.act(msg);
		}
		//显示重启确认
		if (subact.equals("confirm")) {
			String sub = StringUtil.toStrNotNull(httpmsg.getHttpReq().getParameter("sub"),"");
			String re_name = StringUtil.toStrNotNull(httpmsg.getHttpReq().getParameter("re_name"),"");
			msg.addData("sub", sub);
			msg.addData("re_name", re_name);
			return super.act(msg);
		}
		//重启所有action
		else if (subact.equals("allactions")) {
			KFilter.stop();
			boolean init = ActionManager.reloadAllActions();
			if (init) {
				KFilter.start();
				re = "ok";
			}
		}
		//重启所有KObj
		else if (subact.equals("allkobjs")) {
			KFilter.stop();
			boolean init = KObjManager.reInit(null);
			if (init) {
				KFilter.start();
				re = "ok";
			}
		}
		//重启所有dao
		else if (subact.equals("alldaos")) {
			KFilter.stop();
			boolean init = DaoManager.reInit(null, null);
			if (init) {
				KFilter.start();
				re = "ok";
			}
		}
		//重启指定action
		else if (subact.equals("action")) {
			String actname = StringUtil.toStrNotNull(httpmsg.getHttpReq().getParameter("reload_name"),"");
			if (actname.equals("")) {
				msg.addData("re", "action not exsit.");
				return super.act(msg);
			}
			KFilter.stop();
			boolean init = ActionManager.reLoadAction(actname);
			if (init) {
				KFilter.start();
				re = "ok";
			}else{
				msg.addData("re", "reLoadAction error:"+actname);
				return super.act(msg);
			}
		}
		//重启指定dao
		else if (subact.equals("dao")) {
			String name = StringUtil.toStrNotNull(httpmsg.getHttpReq().getParameter("reload_name"),"");
			if (name.equals("")) {
				msg.addData("re", "dao not exsit.");
				return super.act(msg);
			}
			KFilter.stop();
			boolean init = DaoManager.reLoadDao(name);
			if (init) {
				KFilter.start();
				re = "ok";
			}else{
				msg.addData("re", "reLoadDao error:"+name);
				return super.act(msg);
			}
		}
		//重启指定kobj
		else if (subact.equals("kobj")) {
			String name = StringUtil.toStrNotNull(httpmsg.getHttpReq().getParameter("reload_name"),"");
			if (name.equals("")) {
				msg.addData("re", "kobj not exsit.");
				return super.act(msg);
			}
			KFilter.stop();
			boolean init = KObjManager.reload(name);
			if (init) {
				KFilter.start();
				re = "ok";
			}else{
				msg.addData("re", "reload kobj error:"+name);
				return super.act(msg);
			}
		}
		//拒绝所有请求,注意拒绝后需要加上控制参数穿透进来启用接受
		else if (subact.equals("stop")) {
			KFilter.stop();
			re = "ok";
		}
		//接受所有请求,注意需要加上控制参数穿透进来启用接受
		else if (subact.equals("start")) {
			KFilter.start();
			re = "ok";
		}
		//重启整个系统
		else if (subact.equals("system")) {
			boolean init = KFilter.reStart();
			if (init) {
				re = "ok";
			}else{
				re = "failed";
			}
		}
		//新数据库生成页
		else if (subact.equals("buildNewDB")) {
			msg.addData("subact", "buildNewDB");
			return super.act(msg);
		}
		//新数据库表重新生成页
		else if (subact.equals("buildDBTable")) {
			msg.addData("subact", "buildDBTable");
			return super.act(msg);
		}
		//生成新的数据库结构
		else if (subact.equals("newdb")) {
			String ds = httpmsg.getHttpReq().getParameter("ds");
			re = "failed";
			if (StringUtil.isStringWithLen(ds, 2)) {
				boolean res = KObjManager.buildAllNewData(ds);
				if (res) {
					re = "ok";
				}
			}
		}
		//生成新的数据库结构
		else if (subact.equals("retable")) {
			String tb = httpmsg.getHttpReq().getParameter("tb");
			re = "failed";
			if (StringUtil.isStringWithLen(tb, 2)) {
				boolean res = KObjManager.reBuildNewData(tb);
				if (res) {
					re = "ok";
				}
			}
		}
		msg.addData("re", re);
		return super.act(msg);
	}

	@Override
	public void exit() {
		
	}

	@Override
	public String getIniPath() {
		return null;
	}

	@Override
	public void init() {
		
	}

}
