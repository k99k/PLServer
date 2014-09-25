/**
 * 
 */
package com.k99k.plserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.DaoInterface;
import com.k99k.khunter.DaoManager;
import com.k99k.khunter.KObject;
import com.k99k.tools.IO;
import com.k99k.tools.StringUtil;

/**
 * @author Keel
 *
 */
public class LogTask extends Action {

	/**
	 * @param name
	 */
	public LogTask(String name) {
		super(name);
	}
	static DaoInterface dao;
	static final Logger log = Logger.getLogger(LogTask.class);
	
	private String unzipPath = "/usr/plserver/log/";

	@Override
	public ActionMsg act(ActionMsg msg) {
		byte[] iKey = (byte[]) msg.getData("iKey");
		String file = (String) msg.getData("file");
		String path = (String)msg.getData("path");
		String fullPath = path+"/"+file;
		File f = new File(fullPath);
		if (!f.exists()) {
			log.error(Err.ERR_LOG_FILE_NOT_FOUND+" file:"+fullPath);
			return super.act(msg);
		}
		//解压缩
		String tempPath = this.unzipPath +"/"+file;
		File dir = new File(tempPath);
		dir.mkdirs();
		if(!unzip(fullPath,tempPath)){
			log.error(Err.ERR_LOG_FILE_UNZIP+" file:"+fullPath);
			return super.act(msg);
		}
		//解密
		File[] logs = dir.listFiles();
		if (logs.length == 0) {
			log.error(Err.ERR_LOG_ZIP_NO_FILE+" fileDir:"+tempPath);
			return super.act(msg);
		}
		for (int i = 0; i < logs.length; i++) {
			try {
				String logTxt = IO.readTxt(logs[i].getAbsolutePath(), "utf-8");
				if (!StringUtil.isStringWithLen(logTxt, 1)) {
					log.error(Err.ERR_LOG_READ+" file:"+fullPath);
					continue;
				}
				//处理log内容
				String[] fArr = file.split("_");
				long uid = 0;
				if (fArr.length>1 && StringUtil.isDigits(fArr[0])) {
					uid = Long.parseLong(fArr[0]);
				}
				String[] logEnc = logTxt.split("\r\n");
				for (int j = 0; j < logEnc.length; j++) {
					String decTxt = AuthAction.decrypt(logEnc[j], iKey);
					readLogTxt(decTxt,uid);
				}
			} catch (IOException e) {
				e.printStackTrace();
				log.error(Err.ERR_LOG_READ+" file:"+fullPath,e);
				return super.act(msg);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(Err.ERR_LOG_DECRYPT+" file:"+fullPath,e);
				return super.act(msg);
			}
		}
		return super.act(msg);
	}
	static final int LEVEL_D = 0;
	static final int LEVEL_I = 1;
	static final int LEVEL_W = 2;
	static final int LEVEL_E = 3;
	static final int LEVEL_F = 4;
	private static final String SPLIT = "\\|\\|";
	private static final String NEWlINE = "\r\n";
	
	public static void main(String[] args) {
		String f = "d:/dsUnzip/0_1407685551832.zip/c_cache.dat";
		byte[] iKey  = new byte[16];
		String imeiKey = "QTAwMDAwMzg2Qjg1";
		for (int i = 0; i < 16; i++) {
			iKey[i] = (byte) imeiKey.charAt(i);
		}
		byte[] rootKey = {79, 13, 33, -66, -58, 103, 3, -34, -45, 53, 9, 45, 28, -124, 50, -2};
//		String file = "0_1407685551832.zip";
//		String[] fArr = file.split("_");
//		long uid = 0;
//		if (fArr.length>1 && StringUtil.isDigits(fArr[0])) {
//			uid = Long.parseLong(fArr[0]);
//		}
//		try {
//			String logTxt = IO.readTxt(f, "utf-8");
//			String[] logEnc = logTxt.split("\r\n");
//			for (int i = 0; i < logEnc.length; i++) {
//				String decTxt = AuthAction.decrypt(logEnc[i], iKey);
//				readLogTxt(decTxt,uid);
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
		try {
			/*
			String enc = "D5uv62.CfptXx0IJfE77Y35LSOUXJGGCFzaL..y7kELCv.8xi3LHaNu1p@90gIE5KAG0UuDNEcUbGmXsVULQYJIbkbg6W5DTYdi4lbE82jR_";
			//enc = "D5uv62.CfptXx0IJfE77Y7tBsLqvjCGtY2wpblL6QjzW.inOI3Dkv9QPDvwJBMTc00xy@Z.EIwkUxwCJvBTFfE80yWJlw9X4PLbOrV.qSjp_";
			String org = AuthAction.decrypt(enc, rootKey);
			System.out.println(org);
			*/
			/*
			f = "d:/download/c_cache.dat";
			f = "d:/download/3648_1411117239424/c_cache.dat";
			
			String logTxt = IO.readTxt(f, "utf-8").trim();
//			String decTxt = AuthAction.decrypt(logTxt, rootKey);
//			System.out.println(decTxt);
			String[] logEnc = logTxt.split("\r\n");
			for (int j = 0; j < logEnc.length; j++) {
				String decTxt = AuthAction.decrypt(logEnc[j], iKey);
//				readLogTxt(decTxt,2L);
				System.out.println(decTxt);
			}
			*/
			
			String s = null;
			s = s.substring(2);
			System.out.println("end:"+s);
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println(e.getLocalizedMessage());
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			System.out.println(sw.toString());
			String er = Arrays.toString(e.getStackTrace());
			System.out.println(er);
		}
		
	}
	
	/**
	 * 解密后的log数据按>>号拆分成单条处理
	 * @param decTxt
	 */
	private static void readLogTxt(String decTxt,long uid){
		String[] lines = decTxt.split(NEWlINE);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.startsWith(">>")) {
				if (sb.length()>0) {
					logToDB(sb.toString(),uid);
				}
				sb = new StringBuilder();
			}else{
				sb.append("\r\n");
			}
			sb.append(line);
		}
		if (sb.length()>0) {
			logToDB(sb.toString(),uid);
		}
	}
	
	
	
	/**
	 * 将一条数据存入数据库
	 * @param line
	 */
	private static void logToDB(String line,long uid){
//		System.out.println("["+txt+"]");
		String[] arr = line.split(SPLIT);
		//字段为:
//		>>timeStamp
//		||level
//		||tag
//		||act
//		||pkg
//		||msg : gid_cid_msg
		//长度验证,只可能大于6(不保证msg里面没有||号和\r\n)
		if (arr.length < 6) {
			log.error(Err.ERR_LOG_LINE+" log line:"+line);
			return;
		}
		KObject logobj = new KObject();
		logobj.setId(dao.getIdm().nextId());
		String time = arr[0].substring(2);
		if (StringUtil.isDigits(time)) {
			logobj.setCreateTime(Long.parseLong(time));
		}else{
			log.error(Err.ERR_LOG_TIME+" log line:"+line);
			return;
		}
		if (StringUtil.isDigits(arr[1])) {
			logobj.setLevel(Integer.parseInt(arr[1]));
		}else{
			log.error(Err.ERR_LOG_TIME+" log line:"+line);
			return;
		}
		if (StringUtil.isStringWithLen(arr[2], 1)) {
			logobj.setProp("logTag", arr[2]);
		}else{
			log.error(Err.ERR_LOG_TAG+" log line:"+line);
			return;
		}
		if (StringUtil.isDigits(arr[3])) {
			logobj.setProp("act", Integer.parseInt(arr[3]));
		}else{
			log.error(Err.ERR_LOG_ACT+" log line:"+line);
			return;
		}
		logobj.setProp("pkg",arr[4]);
		if (StringUtil.isStringWithLen(arr[5],1)) {
			String[] mArr = arr[5].split("_");
			if (mArr.length<2 || !StringUtil.isDigits(mArr[0]) || !StringUtil.isDigits(mArr[1])) {
				log.error(Err.ERR_LOG_MSG+" log line:"+line);
				return;
			}
			logobj.setProp("gid", mArr[0]);
			logobj.setProp("cid", mArr[1]);
			if (mArr.length>2) {
				StringBuilder sb = new StringBuilder();
				for (int i = 2; i < mArr.length; i++) {
					sb.append(mArr[i]);
				}
				logobj.setProp("msg", sb.toString());
			}else{
				logobj.setProp("msg", "");
			}
		}else{
			log.error(Err.ERR_LOG_MSG+" log line:"+line);
			return;
		}
		logobj.setProp("uid", uid);
		if(!dao.save(logobj)){
			log.error(Err.ERR_LOG_SAVETODB+" log line:"+line);
			return;
		}
	}
	
	private static final int IO_BUFFER_SIZE = 1024 * 4;
	
	public static boolean unzip(String file,String outputDirectory){
		boolean re = false;
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			ZipEntry zipEntry = null;
			File dest = new File(outputDirectory);
			dest.mkdirs();
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				String entryName = zipEntry.getName();
				InputStream in = null;
				FileOutputStream out = null;
				try {
					if (zipEntry.isDirectory()) {
						String name = zipEntry.getName();
						name = name.substring(0, name.length() - 1);
						File f = new File(outputDirectory + File.separator
								+ name);
						f.mkdirs();
					} else {
						int index = entryName.lastIndexOf("\\");
						if (index != -1) {
							File df = new File(outputDirectory + File.separator
									+ entryName.substring(0, index));
							df.mkdirs();
						}
						index = entryName.lastIndexOf("/");
						if (index != -1) {
							File df = new File(outputDirectory + File.separator
									+ entryName.substring(0, index));
							df.mkdirs();
						}
						File f = new File(outputDirectory + File.separator
								+ zipEntry.getName());
						in = zipFile.getInputStream(zipEntry);
						out = new FileOutputStream(f);
						int c;
						byte[] by = new byte[IO_BUFFER_SIZE];
						while ((c = in.read(by)) != -1) {
							out.write(by, 0, c);
						}
						out.flush();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException ex) {
						}
					}
					if (out != null) {
						try {
							out.close();
						} catch (IOException ex) {
						}
					}
				}
			}
			re = true;
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException ex) {
				}
			}
		}

		return re;
	}

	public final String getUnzipPath() {
		return unzipPath;
	}

	public final void setUnzipPath(String unzipPath) {
		this.unzipPath = unzipPath;
	}


	@Override
	public void init() {
		dao = DaoManager.findDao("dsLogDao");
		super.init();
	}
	

}
