/**
 * 
 */
package com.k99k.plserver;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class DownAction extends Action {


	/**
	 * @param name
	 */
	public DownAction(String name) {
		super(name);
	}
	
	

	static final Logger log = Logger.getLogger(DownAction.class);

	
	private String downloadLocalPath = "d:/dats/";

	@Override
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
    	HttpServletRequest request =  httpmsg.getHttpReq();
    	HttpServletResponse response = httpmsg.getHttpResp();
    	String file = request.getParameter("f");
    	String uid = request.getParameter("u");
    	String tid = request.getParameter("t");
    	String message = request.getParameter("m");
    	String v = request.getHeader("v");
    	boolean downOK = false;
    	if (StringUtil.isStringWithLen(file, 1) && StringUtil.isDigits(uid) && StringUtil.isDigits(tid) && StringUtil.isStringWithLen(v, 2)) {
			//FIXME 解密v验证
    		
    		//下载任务文件
    		String localPath = this.downloadLocalPath+file;
    		File f = new File(localPath);
    		if (f.exists()) {
    			response.reset();
    			response.setContentType("application/x-msdownload");
    			response.addHeader("Content-Disposition", "attachment; filename=\"" + file  + "\"");
    			int len = (int)f.length();
    			response.setContentLength(len);
    			if (len > 0) {
    				response.addHeader("Content-Length", String.valueOf(len));
    				try {
    					InputStream inStream = new FileInputStream(f);
    					byte[] buf = new byte[4096];
    					ServletOutputStream servletOS = response.getOutputStream();
    					int readLength;
    					while (((readLength = inStream.read(buf)) != -1)) {
    						servletOS.write(buf, 0, readLength);
    					}
    					inStream.close();
    					servletOS.flush();
    					servletOS.close();
    					downOK = true;
    				} catch (IOException e) {
    					e.printStackTrace();
    					log.error(Err.ERR_FILE_DOWN+" file:"+file);
    				}
    			}
    			
    		}else{
    			log.error(Err.ERR_FILE_NOTFOUND+" file:"+file);
    			JOut.err(404,Err.ERR_FILE_NOTFOUND, httpmsg);
    		}
    		
    		//进行日志记录
    		if (downOK) {
    			//生成Task
    			ActionMsg atask1 = new ActionMsg("actLogTask");
    			//任务采用单队列的处理
    			atask1.addData(TaskManager.TASK_TYPE, TaskManager.TASK_TYPE_EXE_SINGLE);
    			atask1.addData("tid", StringUtil.objToNonNegativeInt(tid));
    			atask1.addData("uid", StringUtil.objToNonNegativeInt(uid));
    			atask1.addData("type", ActLogTask.TYPE_DOWNLOAD_FILE);
    			String msge = StringUtil.isStringWithLen(message, 1) ? "down@@"+file+"@@"+message: "down@@"+file;
    			atask1.addData("msg", msge);
    			TaskManager.makeNewTask("logTask:"+file, atask1);
			}
		}

		return super.act(msg);
	}
	
	@Override
	public void init() {
		super.init();
	}

	public final String getDownloadLocalPath() {
		return downloadLocalPath;
	}

	public final void setDownloadLocalPath(String downloadLocalPath) {
		this.downloadLocalPath = downloadLocalPath;
	}
	
	

}
