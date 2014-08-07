package com.k99k.khunter;

import org.apache.log4j.Logger;

/**
 * TODO 输入输出通道管理器
 * @author keel
 *
 */
public final class IOManager {

	static final Logger log = Logger.getLogger(IOManager.class);
	
	/**
	 * 初始化
	 * @param iniFile
	 * @param classPath
	 * @return
	 */
	public static final boolean init(String iniFile,String classPath){
		
		return true;
	}
	
	/**
	 * 查找IOInterface
	 * @param ioName
	 * @return
	 */
	public static final IOInterface findIO(String ioName){
		
		return null;
	}
	
	/**
	 * 退出IOManager时的操作
	 */
	public static final void exit(){
		log.info("IOManager exited.");
	}
}
