/**
 * 
 */
package com.k99k.khunter;

import com.k99k.khunter.HttpActionMsg;

/**
 * 用于JSON方式输出结果,用于ajax请求
 * @author keel
 *
 */
public class JOut {

	public JOut() {
	}
	
	/**
	 * HttpActionMsg中打印输出错误码,同时将错误码置为http状态码
	 * @param errCode
	 * @param httpmsg
	 */
	public static final void err(int errState,HttpActionMsg httpmsg){
		httpmsg.getHttpResp().setStatus(errState);
		httpmsg.addData(ActionMsg.MSG_PRINT, "E"+String.valueOf(errState));
	}
	
	/**
	 * 将错误码置为http状态码,并返回错误消息
	 * @param errCode 错误码
	 * @param msg 错误消息
	 * @param httpmsg
	 */
	public static final void err(int errState,String msg,HttpActionMsg httpmsg){
		httpmsg.getHttpResp().setStatus(errState);
		httpmsg.addData(ActionMsg.MSG_PRINT, "E"+errState+msg);
		httpmsg.addData(ActionMsg.MSG_END, true);
	}
	
	public static final void err(int errState,int errCode,HttpActionMsg httpmsg){
		httpmsg.getHttpResp().setStatus(errState);
		httpmsg.addData(ActionMsg.MSG_PRINT, "E"+errState+errCode);
		httpmsg.addData(ActionMsg.MSG_END, true);
	}

	/**
	 * 直接输出String,状态码不设,为默认的200
	 * @param re
	 * @param httpmsg
	 */
	public static final void txtOut(String re,HttpActionMsg httpmsg){
		httpmsg.addData(ActionMsg.MSG_PRINT, re);
	}
	
	/**
	 * 模板化输出String,状态码不设,为默认的200
	 * @param reTemplet
	 * @param httpmsg
	 */
	public static final void txtOut(String reTemplet,String[] data,HttpActionMsg httpmsg){
		httpmsg.addData(ActionMsg.MSG_PRINT, templetOut(reTemplet,data));
	}
	
	/**
	 * 模板化输出String,状态码不设,为默认的200
	 * @param reTemplet
	 * @param httpmsg
	 */
	public static final void txtOut(String[] strArr,String[] data,HttpActionMsg httpmsg){
		httpmsg.addData(ActionMsg.MSG_PRINT, templetOut(strArr,data));
	}
	
	/**
	 * 模板化输出,用String[]中的数据填充模板中的"###"占位符.<br />
	 * 如:templetOut("aabb###ccx###nnn",new String[]{"22","zz"}) <br />
	 * 结果:"aabb22ccxzznnn" .占位符如果与data中的数量不符,则按最少的相符数据进行替换
	 * @param reTemplet String
	 * @param data String[]
	 * @return 格式化后输出String
	 */
	public static final String templetOut(String reTemplet,String[] data){
		String[] strArr = reTemplet.split("###");
		return templetOut(strArr,data);
	}
	
	/**
	 * 模板化输出,strArr为模板分解后的String[].<br />
	 * 如:templetOut("aabb###ccx###nnn",new String[]{"22","zz"}) <br />
	 * 结果:"aabb22ccxzznnn" .占位符如果与data中的数量不符,则按最少的相符数据进行替换
	 * @param strArr
	 * @param data
	 * @return
	 */
	public static final String templetOut(String[] strArr,String[] data){
		int dataLen = data.length;
		int temlLen = strArr.length;
		int max = 0;
		if (temlLen > dataLen) {
			max = dataLen;
		}else{
			max = temlLen;
		}
		StringBuilder sb = new StringBuilder(max);
		int i = 0;
		for (; i < max; i++) {
			sb.append(strArr[i]);
			sb.append(data[i]);
		}
		for (int j = i; j < temlLen; j++) {
			sb.append(strArr[j]);
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String s = "aabb###ccx###nnn";
		System.out.println(templetOut(s,new String[]{"22"}));
	}
}
