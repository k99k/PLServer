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
import com.k99k.khunter.TaskManager;
import com.k99k.tools.StringUtil;

/**
 * @author Keel
 *
 */
public class LogAction extends Action {

	/**
	 * @param name
	 */
	public LogAction(String name) {
		super(name);
	}
	static final Logger log = Logger.getLogger(LogAction.class);
	
	
	/**
	 * 日志文件保存路径
	 */
	private String savePath = "/usr/plserver/logs/";
    

	@Override
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
    	HttpServletRequest request =  httpmsg.getHttpReq();
    	//无action链
    	msg.addData(ActionMsg.MSG_END, true);
    	String v = request.getHeader("v");
    	msg  = AuthAction.authV(v, httpmsg);
    	if (!msg.containsData("imeiKey")) {
    		String err = StringUtil.objToStrNotNull(msg.getData(ActionMsg.MSG_ERR));
			JOut.err(403,err, httpmsg);
			return super.act(msg);
		}
    	byte[] iKey = (byte[]) msg.getData("imeiKey");
    	String file = request.getParameter("f");
		if (!StringUtil.isStringWithLen(file, 1)) {
			JOut.err(403,Err.ERR_LOG_F,httpmsg);
			return super.act(msg);
		}
		
		int size = Uploader.upload(request,this.savePath,file,file);
		if (size <= 0) {
			JOut.err(501,Err.ERR_LOG_UPLOAD,httpmsg);
			return super.act(msg);
		}
		msg.addData(ActionMsg.MSG_PRINT,"ok");
		
		//生成Task
		ActionMsg atask1 = new ActionMsg("logTask");
		//任务采用池的处理
		atask1.addData(TaskManager.TASK_TYPE, TaskManager.TASK_TYPE_EXE_POOL);
		atask1.addData("iKey", iKey);
		atask1.addData("file", file);
		atask1.addData("path", this.savePath);
		TaskManager.makeNewTask("logTask:"+file, atask1);
	
		log.info("log upload ok:"+file);
		
		return super.act(msg);
	}


	public final String getSavePath() {
		return savePath;
	}


	public final void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	
	
	
}
