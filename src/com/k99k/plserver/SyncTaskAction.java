/**
 * 
 */
package com.k99k.plserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.khunter.JOut;
import com.k99k.khunter.KFilter;
import com.k99k.tools.IO;
import com.k99k.tools.JSON;
import com.k99k.tools.StringUtil;

/**
 * 同步手机端文件和exv及emv任务
 * @author Keel
 *
 */
public class SyncTaskAction extends Action {

	/**
	 * @param name
	 */
	public SyncTaskAction(String name) {
		super(name);
	}
	
	static final Logger log = Logger.getLogger(SyncTaskAction.class);

	private String synFilePath = "";
	private String sync = "";
	private HashMap<String,Object> syncMap = null;
	private int ver;
	private String[] downPres;
	private int preCount = 0;
	private String[] sycCacheStr;
	
	/*
协议：
{
ver:1,   //每更新一次加1,客户端可据此判断是否有变化
downPre:["http://","http://2"],     //每次由server提供一个
emPath:"",   //em文件名,serEmp用
exVer:0,      //直接更新
emUrl:"",
exUrl:"",
sd:{
     "cache_01":"#",   //保留
     "c_cache.dat":"#",   //保留
     "update":{
          "file1":"fileSize", //用于判断是否需要断点续传
          "file2":""
     },
     "pics":{
          
     }
},
data:{
     "file":"downPath"
}

}
	 */
	

	@Override
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
		HttpServletRequest request =  httpmsg.getHttpReq();
		msg.addData(ActionMsg.MSG_END, true);
		
		String subact = KFilter.actPath(msg, 2, "");
		if (subact.equals("syn")) {
			
			String v = request.getHeader("v");
			msg = AuthAction.authV(v, msg);
	    	if (!msg.containsData("imeiKey")) {
	    		String err = StringUtil.objToStrNotNull(msg.getData(ActionMsg.MSG_ERR));
	    		log.error("no imeiKey");
				JOut.err(403,err, httpmsg);
				return super.act(msg);
			}
	    	byte[] iKey = (byte[]) msg.getData("imeiKey");
			//解密up内容
			String enc = request.getParameter("sy");
			String req; //这里加密内容是ver版本号
			int clinetVer = 0;
			try {
				req = AuthAction.decrypt(enc,iKey);
			} catch (Exception e) {
				e.printStackTrace();
				JOut.err(403,Err.ERR_DECRYPT, httpmsg);
				return super.act(msg);
			}
			if (StringUtil.isDigits(req)) {
				clinetVer = Integer.parseInt(req);
			}else{
				JOut.err(403,Err.ERR_DECRYPT, httpmsg);
				return super.act(msg);
			}
			if (this.ver > clinetVer) {
				//需要更新
			}
			
			//输出加密的内容
			String out = "";
			try {
				//轮循pre
				String json = this.sycCacheStr[this.preCount];
				this.preCount++;
				if (preCount >= this.downPres.length) {
					this.preCount = 0;
				}
//				out = json;
				out = AuthAction.encrypt(json,iKey);
			} catch (Exception e) {
				JOut.err(500,Err.ERR_ENCRYPT, httpmsg);
				return super.act(msg);
			}
			msg.addData(ActionMsg.MSG_PRINT, out);
			
		}else if(subact.equals("reinit-keel")){
			this.init();
			msg.addData(ActionMsg.MSG_PRINT, this.sync);
		}else{
			JOut.err(404, httpmsg);
		}
		
		return super.act(msg);
	}
	
	
	public static void main(String[] args) {
		try {
			String s  = IO.readTxt("d:/plserver/dats/syn.json", "utf-8");
			//System.out.println(s);
			HashMap<String,Object> root = (HashMap<String, Object>) JSON.read(s);
			if (root != null) {
				System.out.println(root.get("ver"));
			}else{
				System.out.println("root is null.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
		try {
			String s = IO.readTxt(this.synFilePath, "utf-8");
			if (StringUtil.isStringWithLen(s, 10)) {
				this.sync = s;
				HashMap<String,Object> root = (HashMap<String, Object>) JSON.read(this.sync);
				if (root !=null && root.containsKey("ver") && root.containsKey("downPre")) {
					this.ver = Integer.parseInt(root.get("ver").toString());
					ArrayList<String> pres = (ArrayList<String>) root.get("downPre");
					this.downPres = new String[pres.size()];
					Iterator<String> it = pres.iterator();
					for (int i=0; it.hasNext();i++) {
						String url = it.next();
						this.downPres[i] = url;
					}
					this.syncMap = root;
					this.preCount = 0;
					this.sycCacheStr = new String[this.downPres.length];
					//生成不同downPre的cache
					for (int i = 0; i < this.downPres.length; i++) {
						this.syncMap.put("downPre", this.downPres[i]);
						String c = JSON.write(this.syncMap);
						this.sycCacheStr[i] = c;
					}
				}else{
					log.error("sync file read failed:"+this.synFilePath);
				}
			}else{
				log.error("sync file read failed:"+this.synFilePath);
			}
		} catch (IOException e) {
			log.error("sync file read failed:"+this.synFilePath);
			e.printStackTrace();
		}
		super.init();
	}

	public final String getSynFilePath() {
		return synFilePath;
	}

	public final void setSynFilePath(String synFilePath) {
		this.synFilePath = synFilePath;
	}
	

}
