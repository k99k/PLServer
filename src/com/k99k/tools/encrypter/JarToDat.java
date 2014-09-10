package com.k99k.tools.encrypter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class JarToDat {

	public JarToDat() {
	}
	
    private static byte[] rootkey = {79, 13, 33, -66, -58, 103, 3, -34, -45, 53, 9, 45, 28, -124, 50, -2};
    private static byte[] ivk = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    
	public static void main(String[] args) {
		if (args != null && args.length>=1) {
			File currentDir = new File("");
			try {
				String file = currentDir.getCanonicalPath()+File.separator+args[0]+".jar";
				String newFile = currentDir.getCanonicalPath()+File.separator+args[0]+".dat";
				System.out.println("JAR:"+file);
				File f = new File(file);
				if (f.isFile()) {
					BufferedInputStream in = new BufferedInputStream(
							new FileInputStream(file));
					ByteArrayOutputStream dout = new ByteArrayOutputStream(4096);
					byte[] temp = new byte[4096];
					int size = 0;
					while ((size = in.read(temp)) != -1) {
						dout.write(temp, 0, size);
					}
					in.close();
					byte[] content = dout.toByteArray();
					byte[] enc = encrypt(content, rootkey);
					dout.close();
					//写入文件
					FileOutputStream out = new FileOutputStream(newFile);  
					out.write(enc);
			        out.close();  
			        //检查
			        
				}else{
					System.out.println("file is not exsit:"+file);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}else{
			System.out.println("args is empty.");
		}
	}

	public static final byte[] encrypt(byte[] srcBytes,byte[] key) throws Exception {
//  		byte[] srcBytes = sSrc.getBytes("utf-8");
  		Cipher cipher = null;
  		srcBytes = zeroPadding(srcBytes);
//  		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
  		cipher = Cipher.getInstance("AES/CBC/NoPadding");
  		
  		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
  		IvParameterSpec iv =  new IvParameterSpec(ivk);//new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
  		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
  		byte[] encrypted = cipher.doFinal(srcBytes);
  		return encrypted;
  	}
	
	// 加密
	  	public static final String encryptWithBase(String sSrc,byte[] key) throws Exception {
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
	
	public static final byte[] zeroPadding(byte[] bs){
		try {
//			byte[] bs = in.getBytes("utf-8");
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
		} catch (Exception e) {
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
