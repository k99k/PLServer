/**
 * 
 */
package com.k99k.khunter.acts;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.KFilter;
import com.k99k.khunter.ErrorCode;
import com.k99k.khunter.HTManager;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.khunter.KIoc;

/**
 * 编辑系统配置文件
 * @author keel
 *
 */
public class ConsoleEditIni extends Action {

	/**
	 * @param name
	 */
	public ConsoleEditIni(String name) {
		super(name);
	}
	
	public static final int ERR_CODE1 = 11;

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#act(com.k99k.khunter.ActionMsg)
	 */
	@Override
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
		//子命令
		String subact = KFilter.actPath(msg, 3, "load");//(pathArr.length == 4) ? "load" : pathArr[3];
		msg.addData("subact", subact);
		//载入
		String iniFile = HTManager.getIniFilePath();
		if (subact.equals("load")) {
			String iniFileName = httpmsg.getHttpReq().getParameter("ini");
			String ini = "";
			if (iniFileName == null || iniFileName.trim().length()<2) {
				iniFileName = "editIni";
				ini = KIoc.readTxtInUTF8(iniFile);
			}else{
				ini = KIoc.readTxtInUTF8(HTManager.getIniPath()+iniFileName+".json");
			}
			msg.addData("json", ini);
			msg.addData("ini", iniFileName);
		}
		//保存
		else if(subact.equals("save")){
			String iniFileName = httpmsg.getHttpReq().getParameter("ini");
			String json = httpmsg.getHttpReq().getParameter("json");
			int err = KIoc.saveJsonToFile(iniFileName, json);
			
			msg.addData("save", ErrorCode.getErrorInfo(KIoc.ERR_CODE1, err));
			
//			if (iniFileName == null || iniFileName.trim().length()<2) {
//				msg.addData("save", "ini not found.");
//			}else{
//				String json = httpmsg.getHttpReq().getParameter("json");
//				if (json == null || json.length() < 10) {
//					msg.addData("save", "no para");
//				}else {
//					//验证json格式
//					if (JSON.validateJsonString(json)) {
//						//保存
//						if(KIoc.writeTxtInUTF8(HTManager.getIniPath()+iniFileName+".json", json)){
//							msg.addData("save", "ok");
//						}else{
//							msg.addData("save", "save fail");
//						}
//					}else{
//						msg.addData("save", "validate fail");
//					}
//				}
//			}
			
		}
		return super.act(msg);
	}

	@Override
	public void exit() {
		
	}

	@Override
	public String getIniPath() {
		return null;
	}

	@Override
	public void init() {
	}


	
}
