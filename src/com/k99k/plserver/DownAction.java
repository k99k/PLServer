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

import org.apache.catalina.connector.ClientAbortException;
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
	private String redirectPath = "http://180.96.63.70:12370/plserver/down/";

	@Override
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
    	HttpServletRequest request =  httpmsg.getHttpReq();
    	HttpServletResponse response = httpmsg.getHttpResp();
    	String file = request.getParameter("f");
    	String uid = request.getParameter("u");
    	String tid = request.getParameter("t");
    	String message = request.getParameter("m");
    	
    	
//    	String v = request.getHeader("v");
    	boolean downOK = false;
    	if (StringUtil.isStringWithLen(file, 1) && StringUtil.isDigits(uid) && StringUtil.isDigits(tid) 
//    			&& StringUtil.isStringWithLen(v, 2)
    			) {
			//FIXME 解密v验证
    		
//    		try {
//    			response.sendRedirect(this.redirectPath+file);
//    			return super.act(msg);
//    		} catch (IOException e1) {
//    			e1.printStackTrace();
//    		}
    		String localPath = this.downloadLocalPath+file;
    		downOK = download(request, response, localPath, file, httpmsg);
    		
    		//进行日志记录
    		if (downOK) {
    			log.info("down OK. uid:"+uid+" file:"+file+" tid:"+tid+" msg:"+message);
    			//生成Task
    			ActionMsg atask1 = new ActionMsg("actLogTask");
    			//任务采用单队列的处理
    			atask1.addData(TaskManager.TASK_TYPE, TaskManager.TASK_TYPE_EXE_SINGLE);
    			atask1.addData("tid", StringUtil.objToNonNegativeInt(tid));
    			atask1.addData("uid", StringUtil.objToNonNegativeInt(uid));
    			atask1.addData("type", ActLogTask.TYPE_DOWNLOAD_FILE);
    			String msge = StringUtil.isStringWithLen(message, 1) ? "down@@"+file+"@@"+message: "down@@"+file;
    			atask1.addData("msg", msge);
    			TaskManager.makeNewTask("logTask:"+file+":"+System.currentTimeMillis(), atask1);
			}
		}else{
			JOut.err(404,Err.ERR_DOWN_PARA, httpmsg);
		}
		return super.act(msg);
	}
	
	/**
	 * 支持断点续传的文件下载
	 * @param request
	 * @param response
	 * @param file
	 * @param msg
	 * @return
	 */
	public static boolean download(HttpServletRequest request,HttpServletResponse response,String localFileFullPath,String file,ActionMsg msg){
		boolean downOK = false;
//		String localPath = localDir+file;
		File f = new File(localFileFullPath);
		if (f.exists()) {
			response.reset();
			response.setContentType("application/octet-stream");
			response.setHeader("Accept-Ranges", "bytes");
			response.addHeader("Content-Disposition", "attachment; filename=\"" + file  + "\"");
			int len = (int)f.length();
			
			if (len > 0) {
				if (request.getHeader("test") != null) {
					msg.addData(ActionMsg.MSG_PRINT, "");
					response.setContentLength(len);
					return false;
				}   
				try {   
					int fStart = 0;
					String range = request.getHeader("Range");
					if (range != null && range.length()>6) {
						int rIndex = range.indexOf("-");
						if (rIndex > 0) {
							String rangeStart = range.substring(6,range.indexOf("-"));
							if (StringUtil.isDigits(rangeStart)) {
								fStart = Integer.parseInt(rangeStart);
								len = len - fStart;
								log.info("file:"+file+" range:"+range+" fStart:"+fStart+" len:"+len);
								response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);//206   
								response.setHeader("Content-Range","bytes " + fStart + "-" + new Long(len -1).toString() + "/" + len);   
							}
						}
					}else{
						response.setStatus(HttpServletResponse.SC_OK);//200   
					}
					response.setContentLength(len);
					InputStream inStream = new FileInputStream(f);
					
					byte[] buf = new byte[BUFF_SIZE];
					ServletOutputStream servletOS = response.getOutputStream();
					int readLength;
					if (fStart>0) {
						inStream.skip(fStart);
					}
					while (((readLength = inStream.read(buf)) != -1)) {
						servletOS.write(buf, 0, readLength);
					}
					inStream.close();
					servletOS.flush();
					servletOS.close();
					msg.addData("ActionMsg.MSG_NONE", true);
					downOK = true;
				} catch (IOException e) {
					downOK = false;
					if (e.getClass().equals(ClientAbortException.class)) {
						//客户端取消下载
						response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);//200  
					}else{
						e.printStackTrace();
						log.error(Err.ERR_FILE_DOWN+" file:"+file);
					}
				}
			}
		}else{
			log.error(Err.ERR_DOWN_NOTFOUND+" file:"+file);
//			JOut.err(404,Err.ERR_DOWN_NOTFOUND, msg);
		}
		return downOK;
	}
	
	private static int BUFF_SIZE = 4096;
	
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

	public final String getRedirectPath() {
		return redirectPath;
	}

	public final void setRedirectPath(String redirectPath) {
		this.redirectPath = redirectPath;
	}
	
	

}
