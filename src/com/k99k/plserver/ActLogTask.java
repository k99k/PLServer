/**
 * 
 */
package com.k99k.plserver;


import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.DaoInterface;
import com.k99k.khunter.DaoManager;
import com.k99k.khunter.KObject;
import com.k99k.tools.StringUtil;

/**
 * @author Keel
 *
 */
public class ActLogTask extends Action {

	/**
	 * @param name
	 */
	public ActLogTask(String name) {
		super(name);
	}
	static DaoInterface dao;
	static final Logger log = Logger.getLogger(ActLogTask.class);
	
	public static final int TYPE_TASK_DONE = 1;
	public static final int TYPE_DOWNLOAD_FILE = 2;
	public static final int TYPE_CLICK = 3;
	public static final int TYPE_INSTALL = 4;
	
	@Override
	public ActionMsg act(ActionMsg msg) {
		Object uid = msg.getData("uid");
		Object tid = msg.getData("tid");
		Object type = msg.getData("type");
		Object message = msg.getData("msg");
		//验证参数
		if (!StringUtil.isDigits(uid) || !StringUtil.isDigits(tid) || !StringUtil.isDigits(type)) {
			log.error(Err.ERR_ACTLOG_PARA+" tid:"+tid+" uid:"+uid+" type:"+type+" msg:"+message);
			return super.act(msg);
		}
		//记入数据库
		KObject logobj = new KObject();
		logobj.setId(dao.getIdm().nextId());
		logobj.setProp("uid", Long.parseLong(String.valueOf(uid)));
		logobj.setProp("tid", Long.parseLong(String.valueOf(tid)));
		logobj.setProp("type", Integer.parseInt(String.valueOf(type)));
		logobj.setProp("msg", String.valueOf(message));
		if(!dao.save(logobj)){
			log.error(Err.ERR_ACTLOG_SAVETODB+" tid:"+tid+" uid:"+uid+" type:"+type+" msg:"+message);
		}
		return super.act(msg);
	}
	@Override
	public void init() {
		dao = DaoManager.findDao("dsActLogDao");
		super.init();
	}
	

}
