/**
 * 
 */
package com.k99k.plserver;

import javax.servlet.http.HttpServletRequest;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.khunter.JOut;
import com.k99k.tools.StringUtil;

/**
 * @author Keel
 *
 */
public class PLA extends Action {

	public PLA(String name) {
		super(name);
	}
    private String savePath = "/usr/plserver/logs/";
    
    
	@Override
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
    	HttpServletRequest request =  httpmsg.getHttpReq();
    	String file = request.getParameter("f");
		if (!StringUtil.isStringWithLen(file, 1)) {
			JOut.err(400,"err",httpmsg);
			return super.act(msg);
		}
		int size = Uploader.upload(request,this.savePath,file,file);
		if (size <= 0) {
			JOut.err(500,"err-upload",httpmsg);
			return super.act(msg);
		}
		msg.addData(ActionMsg.MSG_PRINT,"ok");
		return super.act(msg);
	}
	
	
	

}
