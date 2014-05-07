package com.k99k.plserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.k99k.tools.StringUtil;
import com.k99k.tools.encrypter.Encrypter;

/**
 * Servlet implementation class PS
 */
public class PS extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PS() {
        super();
    }
    
    
	
	public static final int ORDER_NONE = 0;
	public static final int ORDER_SYNC_TASK = 1;
	public static final int ORDER_DEL_TASK = 2;
	public static final int ORDER_STOP_SERVICE = 3;
	public static final int ORDER_RESTART_SERVICE = 4;
	public static final int ORDER_UPDATE = 5;
	public static final int ORDER_UPTIME = 6;
	public static final int ORDER_KEY = 7;

    public static final String ERR_PARA = "e01";
    public static final String ERR_DECRYPT = "e02";
    public static final String ERR_KEY_EXPIRED = "e03";
    public static final String ERR_DECRYPT_CLIENT = "e04";
    
    
    private static final String SPLIT_STR = "@@";
    private String taskDownUrl = "http://127.0.0.1";
    private String updateDownUrl = "http://127.0.0.1";
    private final static String downloadType = "application/x-msdownload";

    private String downloadLocalPath = "/usr/local/PLServer/dats/";
    
    private int currentKeyVersion = 1;
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		setCharset("utf-8",request,response	);
		String tid = request.getParameter("id");
		String kVer = request.getHeader("v");
		if (!StringUtil.isDigits(tid) || !StringUtil.isDigits(kVer)) {
			response.setStatus(404);
			response.getWriter().println(ERR_PARA);
			return;
		}
		//TODO 这里最好验证一下imsi和imei，确定用户权限
		
		
		//下载任务文件
		String localPath = this.downloadLocalPath+"/"+tid+".dat";
		File f = new File(localPath);
		if (f.exists()) {
			response.reset();
			response.setContentType(downloadType);
			response.addHeader("Content-Disposition", "attachment; filename=\"" + tid  + ".dat\"");
			int len = (int)f.length();
			response.setContentLength(len);
			if (len > 0) {
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
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}else{
			response.setStatus(404);
			response.getWriter().println(ERR_PARA);
			return;
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		setCharset("utf-8",request,response	);
		String enc = request.getParameter("up");
		String kVer = request.getHeader("v");
		if (!StringUtil.isStringWithLen(enc, 6) || !StringUtil.isDigits(kVer)) {
			response.getWriter().println(ERR_PARA);
			return;
		}
		int keyVersion = (Integer.parseInt(kVer)-17)/27;
		String req = null;
		try {
			//TODO 注意这里解密先需要确定KEY
			if (keyVersion != this.currentKeyVersion) {
				response.getWriter().println(ERR_KEY_EXPIRED);
				return;
			}
			req = Encrypter.getInstance().decrypt(enc);
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().println(ERR_DECRYPT);
			return;
		}
		String[] reqs = req.split(SPLIT_STR);
		//TODO 获取用户信息和taskList，根据用户具体情况修改taskList返回
		
		
		//实现ORDER_SYNC_TASK
		StringBuilder sb = new StringBuilder();
		sb.append(ORDER_SYNC_TASK).append(SPLIT_STR)
		.append(this.taskDownUrl).append(SPLIT_STR)
		//这里仅使用两个测试任务ID,1为toast,2为下载view数据
		.append("1"+SPLIT_STR+"2").append(SPLIT_STR)
		.append(this.currentKeyVersion);
		String resp = null;
		try {
			resp = Encrypter.getInstance().encrypt(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.getWriter().println(resp);
	}
	/**
	 * 设置输入输出的编码
	 * @param charset
	 * @param req
	 * @param resp
	 * @throws UnsupportedEncodingException
	 */
	public static final void setCharset(String charset,HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException{
		req.setCharacterEncoding(charset);
		resp.setCharacterEncoding(charset);
		resp.setHeader("Content-Encoding",charset);
		resp.setHeader("content-type","text/html; charset="+charset);
	}

	/**
	 * @return the currentKeyVersion
	 */
	public final int getCurrentKeyVersion() {
		return currentKeyVersion;
	}

	/**
	 * @param currentKeyVersion the currentKeyVersion to set
	 */
	public final void setCurrentKeyVersion(int currentKeyVersion) {
		this.currentKeyVersion = currentKeyVersion;
	}

	/**
	 * @return the taskDownUrl
	 */
	public final String getTaskDownUrl() {
		return taskDownUrl;
	}

	/**
	 * @param taskDownUrl the taskDownUrl to set
	 */
	public final void setTaskDownUrl(String taskDownUrl) {
		this.taskDownUrl = taskDownUrl;
	}

	/**
	 * @return the updateDownUrl
	 */
	public final String getUpdateDownUrl() {
		return updateDownUrl;
	}

	/**
	 * @param updateDownUrl the updateDownUrl to set
	 */
	public final void setUpdateDownUrl(String updateDownUrl) {
		this.updateDownUrl = updateDownUrl;
	}
	
	
	
	
}
