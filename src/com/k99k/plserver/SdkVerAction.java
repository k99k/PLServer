/**
 * 
 */
package com.k99k.plserver;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.DaoInterface;
import com.k99k.khunter.DaoManager;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.khunter.JOut;
import com.k99k.tools.StringUtil;

/**
 * @author Keel
 *
 */
public class SdkVerAction extends Action {

	/**
	 * @param name
	 */
	public SdkVerAction(String name) {
		super(name);
	}
	static final Logger log = Logger.getLogger(SdkVerAction.class);
	
	static DaoInterface dao;
	
	static int currentSdkVer = 5;
	static int currentSdkVerTid = 15;
	static HashMap<String,Object> field_tid = new HashMap<String, Object>(2);
	static HashMap<String,Object> sort_id_desc = new HashMap<String, Object>(2);

	static{
		field_tid.put("tid", 1);
		sort_id_desc.put("_id", -1);
	}
	@Override
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
    	HttpServletRequest request =  httpmsg.getHttpReq();
    	//TODO 这里需要验证权限
    	
    	
    	//设置最新版本为dsVerion表中的最大id
    	String newVer = request.getParameter("newVer");
		if (StringUtil.isStringWithLen(newVer, 1)) {
			checkSdkVer();
			msg.addData(ActionMsg.MSG_PRINT, currentSdkVer);
		}else{
			JOut.err(403,Err.ERR_NEW_VER_PARA, httpmsg);
		}
		return super.act(msg);
	}
	
	
	
	/**
	 * 从数据库获取最新的sdkVer并更新，同时返回对应的tid,如果无更新则返回-1
	 * @return
	 */
	private void checkSdkVer(){
		ArrayList<HashMap<String,Object>> ls = dao.query(UserAction.q_state, field_tid, sort_id_desc, 0, 1, null);
		if (ls.isEmpty()) {
			return;
		}
		HashMap<String,Object> map = ls.get(0);
		currentSdkVer = Integer.parseInt(map.get("_id").toString());
		currentSdkVerTid = StringUtil.objToNonNegativeInt(map.get("tid"));
	}
	
	
	public static final ActionMsg dealSdkVersion(int sdkVer,ActionMsg msg){
		//与当前版本比较，如果小则生成一个升级任务
		if (sdkVer < currentSdkVer && currentSdkVerTid > 0) {
			msg.addData("newSdkTid", currentSdkVerTid);
		}
		return msg;
	}
	
	
	

	@Override
	public void init() {
		dao = DaoManager.findDao("dsVersionDao");
		super.init();
	}
	

}
