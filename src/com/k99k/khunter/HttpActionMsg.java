/**
 * 
 */
package com.k99k.khunter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.k99k.tools.JSON;

/**
 * Http方式的ActionMsg
 * @author keel
 *
 */
public class HttpActionMsg extends ActionMsg {

	
	/**
	 * @param actionName
	 * @param httpReq HttpServletRequest不能为null
	 * @param resp HttpServletResponse不能为null 
	 */
	public HttpActionMsg(String actionName, HttpServletRequest httpReq,HttpServletResponse httpResp) {
		super(actionName);
		this.httpReq = httpReq;
		this.httpResp = httpResp;
		this.ip = httpReq.getRemoteAddr();
		this.url = httpReq.getRequestURL().toString();
	}


	private String ip;
	
	private String url;
	
	/**
	 * HttpServletRequest
	 */
	private HttpServletRequest httpReq;
	
	private HttpServletResponse httpResp;
	/**
	 * 增加ip,url,paraMap到json中
	 * @see com.k99k.khunter.ActionMsg#addToJson(java.lang.StringBuilder)
	 */
	@Override
	StringBuilder addToJson(StringBuilder sb) {
		sb.append(",\"ip\":\"").append(this.ip).append("\",");
		sb.append("\"url\":\"").append(this.url).append("\",");
		sb.append("\"paraMap\":").append(JSON.write(this.httpReq.getParameterMap()));
		return sb;
	}


	/**
	 * @return the ip
	 */
	public final String getIp() {
		return ip;
	}


	/**
	 * @return the url
	 */
	public final String getUrl() {
		return url;
	}


	/**
	 * @return the httpReq
	 */
	public final HttpServletRequest getHttpReq() {
		return httpReq;
	}


	/**
	 * @return the httpResp
	 */
	public final HttpServletResponse getHttpResp() {
		return httpResp;
	}
	
	
	
}
