/**
 * 
 */
package com.k99k.plserver;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.tools.StringUtil;
import com.k99k.tools.enc.Base64Coder;

/**
 * 验证http请求的v，并生成imeiKey
 * @author Keel
 *
 */
public class AuthAction extends Action {

	/**
	 * @param name
	 */
	public AuthAction(String name) {
		super(name);
	}
	static final Logger log = Logger.getLogger(AuthAction.class);
	
    private static byte[] rootkey = {79, 13, 33, -66, -58, 103, 3, -34, -45, 53, 9, 45, 28, -124, 50, -2};
    private static byte[] ivk = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    //private byte[] rkey = {43, 23, 13, -32, -58, 83, 3, -34, -87, 56, 19, 90, 28, -102, 15, 40};
    


	@Override
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
    	HttpServletRequest request =  httpmsg.getHttpReq();
    	String v = request.getHeader("v");
		msg = authV(v,msg);
		return super.act(msg);
	}

	/**
	 * 验证v，验证成功后则在msg中加入imeiKey(byte[])，否则加入MSG_ERR
	 * @param v
	 * @param msg
	 * @return
	 */
	public static final ActionMsg authV(String v,ActionMsg msg){
		int err = Err.ERR_AUTH;
		if (StringUtil.isStringWithLen(v, 5)) {
			try {
				String vv = decrypt(v, rootkey);
				if (vv  == null) {
					log.error("decrypt ERROR:"+v);
					return msg;
				}
				//TODO vparas第二个参数为时间，后期可以增加校验
				String[] vparas = vv.split("\\|\\|");
				String imei = vparas[0];
				String imeiKey = Base64Coder.encodeString(imei).substring(0, 16);
				byte[] ikey  = new byte[16];
//				StringBuilder sb1 = new StringBuilder();
				for (int i = 0; i < 16; i++) {
					ikey[i] = (byte) imeiKey.charAt(i);
//					sb1.append(ikey[i]).append(",");
				}
//				System.out.println("ikey:"+sb1.toString());
				msg.addData("imeiKey", ikey);
				return msg;
			} catch (Exception e1) {
				e1.printStackTrace();
				err = Err.ERR_AUTH_GET_IMEIKEY;
			}
		}
		msg.addData(ActionMsg.MSG_ERR, err);
		return msg;
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
	  			ex.printStackTrace();
	  			return null;
	  		}
	  	}
	
	
	
	

}
