/**
 * 
 */
package com.k99k.khunter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


/**
 * @author keel
 *
 */
public class WebTool {

	static final Logger log = Logger.getLogger(WebTool.class);
	
	/**
	 * cookie保存时间,初始化时由json配置 
	 */
	private static int cookieTime = 60 * 60 * 24;//默认为1天

	/**
	 * 
	 */
	public WebTool() {
	}
	
	public static final void setCookieTime(int cTime){
		cookieTime = cTime;
	}
	
	/**
	 * 遍历cookie[]组,获取匹配值名对的cookie
	 * @param cookies
	 * @param cookieName
	 * @param defaultValue
	 * @return
	 */
	public final static String getCookieValue(Cookie[] cookies, String cookieName,
			String defaultValue) {
		if (cookies == null) {
			return defaultValue;
		}
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if (cookieName.equals(cookie.getName()))
				return (cookie.getValue());
		}
		return (defaultValue);
	}
	
	/**
	 * 设置cookie
	 * @param cookieName
	 * @param cookieVal
	 * @param resp
	 */
	public final static void setCookie(String cookieName,String cookieVal,HttpServletResponse resp){
		setCookie(cookieName,cookieVal,cookieTime,resp);
	}
	
	/**
	 * 设置cookie
	 * @param cookieName
	 * @param cookieVal
	 * @param cookieTime cookie保持时间
	 * @param resp
	 */
	public final static void setCookie(String cookieName,String cookieVal,int cookieTime,HttpServletResponse resp){
		Cookie c = new Cookie(cookieName, cookieVal);// cookie值名对
		c.setMaxAge(cookieTime);// 有效期一年
		c.setPath("/"); // 路径
		//c.setDomain("192.168.0.115");// 域名
		resp.addCookie(c); // 在本地硬盘上产生文件
	}
	
	/**
	 * 删除cookie
	 * @param cookieName
	 * @param resp
	 */
	public final static void removeCookie(String cookieName,HttpServletResponse resp){
		Cookie c = new Cookie(cookieName, "");
		c.setMaxAge(0);// 有效时间为0则系统会自动删除过期的cookie
		c.setPath("/");
		resp.addCookie(c);
	}

}
