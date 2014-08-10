/**
 * 
 */
package com.k99k.plserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
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
				String decTxt = AuthAction.decrypt(logTxt, iKey);
				if (!StringUtil.isStringWithLen(decTxt, 1)) {
					log.error(Err.ERR_LOG_DECRYPT+" file:"+fullPath);
					continue;
				}
				//处理log内容
				
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
	

}
