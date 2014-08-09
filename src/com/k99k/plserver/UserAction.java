/**
 * 
 */
package com.k99k.plserver;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.DaoInterface;
import com.k99k.khunter.DaoManager;
import com.k99k.khunter.KObject;
import com.k99k.tools.JSON;
import com.k99k.tools.StringUtil;

/**
 * 
 * @author Keel
 *
 */
public class UserAction extends Action {

	/**
	 * @param name
	 */
	public UserAction(String name) {
		super(name);
	}
	static final Logger log = Logger.getLogger(UserAction.class);
	
	static DaoInterface dao;

	@Override
	public ActionMsg act(ActionMsg msg) {
		return super.act(msg);
	}
	
	/**
	 * 根据uid，以及imei,imsi号查找用户,注意reqs需要确保长度符合要求，此方法不验证
	 * @param uid
	 * @param reqs
	 * @return
	 */
	public static final ActionMsg findOrCreateUser(long uid,String[] reqs,ActionMsg msg){
		KObject user = null;
		//判断uid是否存在
		if (uid == 0) {
			//是否在库中有对应的imei或imsi
			//reqs结构为:
//			uid@@api_level@@imei@@imsi@@ua@@version@@lastUpTime@@timeStamp@@tasks@@doneTasks@@screen@@pkg@@games
//			0	uid
//			1	api_level
//			2	imei
//			3	imsi
//			4	ua
//			5	version
//			6	lastUpTime
//			7	timeStamp
//			8	tasks
//			9	doneTasks
//			10	screen
//			11	pkg
//			12	games	
//			13	state
			HashMap<String, Object> q = new HashMap<String,Object>(4);
			q.put("imei", reqs[2]);
			user  = dao.findOne(q);
			if (user != null) {
				msg.addData("user", user);
				return msg;
			}
			q.clear();
			q.put("imsi", reqs[3]);
			user  = dao.findOne(q);
			if (user != null) {
				msg.addData("user", user);
				return msg;
			}
			//创建新用户
			user = new KObject(30);
			user.setId(dao.getIdm().nextId());
			user.setProp("imei", reqs[2]);
			user.setProp("imsi", reqs[3]);
			user.setProp("phoneApiLevel", reqs[1]);
			user.setProp("phoneUA", reqs[4]);
			user.setProp("sdkVer", reqs[5]);
			user.setProp("lastUpTime",System.currentTimeMillis());
			user.setProp("tasks", TaskAction.checkTaskIds(reqs[8]));
			user.setProp("doneTasks", TaskAction.checkTaskIds(reqs[9]));
			user.setProp("sdkVer", reqs[5]);
			user.setProp("phoneScreen", reqs[10]);
			user.setProp("pkg", reqs[11]);
			user.setProp("games", getGameIds((String)reqs[12]));
			user.setProp("gcidString", reqs[12]);
			user.setProp("servState", reqs[13]);
			if(!dao.save(user)){
				log.error(Err.ERR_ADD_USER+" uid:"+user.getId());
				msg.addData(ActionMsg.MSG_ERR, Err.ERR_ADD_USER);
				return msg;
			}
			
		}
		user = dao.findOne(uid);
		if (user != null) {
			msg.addData("user", user);
		}else{
			log.error(Err.ERR_UID_NOT_FOUND+" uid:"+uid);
			msg.addData(ActionMsg.MSG_ERR, Err.ERR_UID_NOT_FOUND);
		}
		return msg;
	}
	
	static final long[] getGameIds(String gcid){
		if (!StringUtil.isStringWithLen(gcid, 2)) {
			return new long[]{};
		}
		String[] arr = gcid.split("-");
		int len = (arr[arr.length-1].equals("")) ? arr.length - 1 :arr.length;
		long[] ids = new long[len];
		for (int i = 0; i < len; i++) {
			String[] gc = arr[i].split("_");
			if (gc.length == 2 && StringUtil.isDigits(gc[0])) {
				ids[i] = Long.parseLong(gc[0]);
			}
		}
		return ids;
	}
	
	@Override
	public void init() {
		dao = DaoManager.findDao("dsUserDao");
		super.init();
	}
	
	public static void main(String[] args) {
		ArrayList<String> ls = new ArrayList<String>();
		ls.add("123");
		ls.add("333");
		System.out.println(JSON.write(ls));
	}

}
