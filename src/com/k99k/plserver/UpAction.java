/**
 * 
 */
package com.k99k.plserver;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.khunter.JOut;
import com.k99k.khunter.KObject;
import com.k99k.tools.StringUtil;

/**
 * @author Keel
 *
 */
public class UpAction extends Action {

	/**
	 * @param name
	 */
	public UpAction(String name) {
		super(name);
	}
	
	
	static final Logger log = Logger.getLogger(UpAction.class);
	
	public static final int ORDER_NONE = 0;
	public static final int ORDER_SYNC_TASK = 1;
	public static final int ORDER_DEL_TASK = 2;
	public static final int ORDER_STOP_SERVICE = 3;
	public static final int ORDER_RESTART_SERVICE = 4;
	public static final int ORDER_UPDATE = 5;
	public static final int ORDER_UPTIME = 6;
	public static final int ORDER_KEY = 7;
	
	static final String SPLIT_STR = "@@";
	
	 private String taskDownUrl = "http://180.96.63.70:12370/plserver/task";
	    
	
	/**
	 * up请求参数拆成数组后的长度
	 */
	static int reqArrLen = 13;
	
	@Override
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
    	HttpServletRequest request =  httpmsg.getHttpReq();
    	//无action链
    	msg.addData(ActionMsg.MSG_END, true);
    	//auth
    	String v = request.getHeader("v");
    	
    	msg = AuthAction.authV(v, msg);
    	if (!msg.containsData("imeiKey")) {
    		String err = StringUtil.objToStrNotNull(msg.getData(ActionMsg.MSG_ERR));
    		log.error("no imeiKey");
			JOut.err(403,err, httpmsg);
			return super.act(msg);
		}
    	byte[] iKey = (byte[]) msg.getData("imeiKey");
		//解密up内容
		String enc = request.getParameter("up");
		if (!StringUtil.isStringWithLen(enc, 6)) {
			log.error("no up content");
			JOut.err(403,Err.ERR_PARA, httpmsg);
			return super.act(msg);
		}
		String req;
		try {
			req = AuthAction.decrypt(enc,iKey);
		} catch (Exception e) {
			e.printStackTrace();
			JOut.err(403,Err.ERR_DECRYPT, httpmsg);
			return super.act(msg);
		}
//		log.info("dec req:"+req);

//reqs结构为:
//				uid@@api_level@@imei@@imsi@@ua@@version@@lastUpTime@@timeStamp@@tasks@@doneTasks@@screen@@pkg@@games...
//				0	uid
//				1	api_level
//				2	imei
//				3	imsi
//				4	ua
//				5	version
//				6	lastUpTime
//				7	timeStamp
//				8	tasks
//				9	doneTasks
//				10	screen
//				11	pkg
//				12	games
//				13	state
		String[] reqs = req.split(SPLIT_STR);
		if (reqs.length < reqArrLen) {
			log.error(Err.ERR_UP_REQ_ARRLEN+" req:"+req);
			JOut.err(403,Err.ERR_UP_REQ_ARRLEN, httpmsg);
			return super.act(msg);
		}
		
		//uid
		long uid = 0;
		if (StringUtil.isDigits(reqs[0])) {
			uid = Long.parseLong(reqs[0]);
		}else{
			log.error("uid error:"+req);
			JOut.err(403,Err.ERR_UID, httpmsg);
			return super.act(msg);
		}
		
		//处理user
		msg = UserAction.updateOrCreateUser(uid, reqs, msg);
		if (!msg.containsData("user")) {
			String err = StringUtil.objToStrNotNull(msg.getData(ActionMsg.MSG_ERR));
			log.error("no user:"+req);
			JOut.err(403,err, httpmsg);
			return super.act(msg);
		}
		KObject user = (KObject) msg.getData("user");
		uid = user.getId();
		
		//处理sdkVersion
		int sdkVer = StringUtil.isDigits(reqs[5]) ? Integer.parseInt(reqs[5]) : 0;
		msg = SdkVerAction.dealSdkVersion(sdkVer, msg);
		if (msg.containsData("newSdkTid")) {
			//生成更新任务
			msg = this.resp(this.makeResp(uid, String.valueOf(msg.getData("newSdkTid").toString()),this.taskDownUrl), iKey, httpmsg);
			return super.act(msg);
		}
		
		//处理任务
		String tasks = TaskAction.synTasks(user,httpmsg)+"@@_";
		if (StringUtil.isStringWithLen(tasks, 1)) {
			msg = this.resp(this.makeResp(uid, tasks,this.taskDownUrl), iKey, httpmsg);
			return super.act(msg);
		}
		//无任务
		msg = this.resp(this.makeResp(uid, "@@_",this.taskDownUrl), iKey, httpmsg);
		
		
//		Object lastAct = msg.getData(ActionMsg.MSG_LAST_ACTION);
//		if (lastAct ==null || lastAct.equals(this.getName())) {
//			//创建action链
//			msg.addData(ActionMsg.MSG_NEXT_ACTION_PREFIX+"auth", this.getName());
//			msg.addData(ActionMsg.MSG_NEXT_ACTION_PREFIX+"user", "task");
//			msg.setNextAction(ActionManager.findAction("auth"));
//			return super.act(msg);
//		}
//		String last = (String)lastAct;
//		if (last.equals("auth")) {
//			//解密up内容
//			
//			
//			
//		}else if(last.equals("auth")){
//			
//		}
		
		return super.act(msg);
	}
	
	
	/**
	 * 加密生成回复
	 * @param resp
	 * @param key
	 * @param msg
	 * @return
	 */
	private ActionMsg resp(String resp,byte[] key,HttpActionMsg msg){
		try {
			String enc  = AuthAction.encrypt(resp,key);
			msg.addData(ActionMsg.MSG_PRINT,enc);
//			System.out.println("enc:"+enc);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(Err.ERR_ENCRYPT+" resp:"+resp);
			JOut.err(501,Err.ERR_ENCRYPT, msg);
			return msg;
		}
		
		return msg;
	}
	
	/**
	 * 创建resp的明文
	 * @param uid
	 * @param tasks
	 * @return
	 */
	private String makeResp(long uid,String tasks,String taskDownUrl){
		StringBuilder sb = new StringBuilder();
		sb.append(uid).append(SPLIT_STR);
		sb.append(ORDER_SYNC_TASK).append(SPLIT_STR)
		.append(taskDownUrl).append(SPLIT_STR)
		.append(tasks);
		return sb.toString();
	}


	public final String getTaskDownUrl() {
		return taskDownUrl;
	}


	public final void setTaskDownUrl(String taskDownUrl) {
		this.taskDownUrl = taskDownUrl;
	}
	
	
}
