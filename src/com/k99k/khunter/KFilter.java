package com.k99k.khunter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * 过滤器，用于过滤非法请求，并处理参数
 */
public final class KFilter implements Filter {

    /**
     * Default constructor. 
     */
    public KFilter() {
    }
    
    private static boolean isStop = false;

	static final Logger log = Logger.getLogger(KFilter.class);
	
	private static String ini;
	
	private static int rootNum = 0;
	
	private static String staticPrefix = "";
	private static String actPrefix = "";
	
	/**
	 * 如果存在以*为name的Action，则此Action为默认Action,可匹配所有未匹配到的Action请求
	 */
	private boolean hasDefaultAction = false;
	
	/**
	 * 默认Action,名为*,可匹配所有未匹配到的Action请求
	 */
	private Action defaultAction = null;
	
	public static String getPrefix(){
		return actPrefix;
	}
    
	public static String getStaticPrefix(){
		return staticPrefix;
	}
    
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		HTManager.exit();
	}
	
	public static final int getRootNum(){
		return rootNum;
	}
	
    public static final String getIni(){
    	return ini;
    }
	
	public static final void stop(){
		isStop = true;
	}

	public static final void start(){
		isStop = false;
	}
	
	private static final HashMap<String,String> staticPath = new HashMap<String, String>();
	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		//全程使用utf-8
		setCharset("utf-8",req,resp);
		
		String requrl = req.getRequestURI();//+ req.getQueryString();
		//requrl = (requrl == null) ? "" : requrl.substring(1);
		String[] pathArr = requrl.split("\\/");
		int rn = rootNum+1;
		//[root]为默认Action
		String actName = (pathArr.length <= rn) ? ActionMsg.MSG_ROOT : pathArr[rn];
		//TODO 过滤静态请求,此处如果配合前端的web server过滤可省去
		if (staticPath.containsKey(actName)) {
//			String encoding = req.getHeader("Accept-Encoding");
//			if (encoding != null && encoding.indexOf("gzip") != -1){
//				HttpServletResponseWrapper wrapper = new GZIPServletResponseWrapper(resp);
//			    chain.doFilter(req, wrapper);
//			    wrapper.getOutputStream().close();
//			}else{
//				chain.doFilter(request, response);
//			}
			
			chain.doFilter(request, response);
			return;
		}
		
		
		if (isStop) {
			//FIXME 穿透控制,注意这里一定要加ip控制
			if (req.getParameter("keelcontrolsall") == null) {
				resp.setStatus(500);
				resp.getWriter().print("System under maintenance, please come back later.");
				return;
			}
		}
//		chain.doFilter(request, response);
		

		try {
			//pathArr[0]为域名，pathArr[1]为第一个路径,后面继续为路径,最后面为参数
			//String[] pathArr = requrl.substring(requrl.indexOf("//")+2).split("\\/");
//			int rn = 1+rootNum;
//			String actName = (pathArr.length <= rn) ? "" : pathArr[rn];
			ActionMsg msg = new HttpActionMsg(actName, req, resp);
			Action action = ActionManager.findAction(actName);
			if (action == null) {
				//如果存在以*为name的Action，则此Action为默认Action,可匹配所有未匹配到的Action请求
				if (hasDefaultAction) {
					//加入actName,以便defaultAction使用
					msg.addData(ActionMsg.MSG_LAST_ACTION, actName);
					action = this.defaultAction;
				}else{
					resp.setStatus(404);
					resp.getWriter().print("404 - 2");
					return;
				}
			}
			msg.addData(ActionMsg.MSG_PATH_ARRAY, pathArr);
			//msg.addData("[prefix]", staticPrefix);
			//执行action
			msg = action.act(msg);
			//不处理
			if (msg.getData(ActionMsg.MSG_NONE)!=null) {
				return;
			}
			//是否打印
			else if (msg.getData(ActionMsg.MSG_PRINT) != null) {
				resp.getWriter().print(msg.getData(ActionMsg.MSG_PRINT));
				return;
			}
			//是否发向JSP
			else if (msg.getData(ActionMsg.MSG_JSP) != null) {
				String to = (String) msg.getData(ActionMsg.MSG_JSP);
//				Object o = msg.getData(ActionMsg.MSG_JSP_ATTR);
//				if (o != null) {
//					req.setAttribute(ActionMsg.MSG_JSP_ATTR, o);
//				}
				req.setAttribute(ActionMsg.MSG_JSP_ATTR, msg);
				RequestDispatcher rd = req.getRequestDispatcher(to);
				rd.forward(req, resp);
				return;
			}
			//是否跳转
			else if (msg.getData(ActionMsg.MSG_REDIRECT) != null) {
				String redirect = KFilter.getPrefix()+(String) msg.getData(ActionMsg.MSG_REDIRECT);
				resp.sendRedirect(redirect);
				return;
			}
			//是否直接跳转
			else if (msg.getData(ActionMsg.MSG_GOTO) != null) {
				String link = (String) msg.getData(ActionMsg.MSG_GOTO);
				resp.sendRedirect(link);
				return;
			}
			//继续走chain
//			else if(msg.getData("[chain]") != null){
//				chain.doFilter(request, response);
//				return;
//			}
			else{
				resp.setStatus(404);
				//resp.getWriter().print("404 - 3");
				return;
			}
		} catch (Exception e) {
			log.error("KFilter error!", e);
			resp.setStatus(404);
			resp.getWriter().print("500 - Error! please contact 15301588025@189.cn ");
			return;
		}

	}
	
	public static final boolean reStart(){
		KFilter.stop();
		HTManager.exit();
		boolean init = HTManager.init(KFilter.getIni());
		KFilter.start();
		return init;
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		ini = fConfig.getInitParameter("ini");
		rootNum = Integer.parseInt(fConfig.getInitParameter("rootNum"));
		staticPrefix = fConfig.getInitParameter("staticPrefix");
		actPrefix = fConfig.getInitParameter("actPrefix");
		//处理静态内容(非Action请求)的过滤
		String[] statics = fConfig.getInitParameter("statics").split(",");
		for (int i = 0; i < statics.length; i++) {
			staticPath.put(statics[i], statics[i]);
		}
		boolean initOK = HTManager.init(ini);
		if (!initOK) {
			log.error("---------KHunter init failed!!!------------");
		}else{
			defaultAction = ActionManager.findAction("*");
			this.hasDefaultAction = (defaultAction != null);
			log.info("defaultAction:"+hasDefaultAction);
		}
		
	}
	
	/**
	 * 设置输入输出的编码
	 * @param charset
	 * @param req
	 * @param resp
	 * @throws UnsupportedEncodingException
	 */
	public static final void setCharset(String charset,HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException{
		req.setCharacterEncoding(charset);
		resp.setCharacterEncoding(charset);
		resp.setHeader("Content-Encoding",charset);
		resp.setHeader("content-type","text/html; charset="+charset);
	}
	
	/**
	 * 从msg的[pathArr]中定位子Action的actName
	 * @param msg
	 * @param pathNum
	 * @param defaultStr
	 * @return subact 子Action的actName
	 */
	public static final String actPath(ActionMsg msg,int pathNum,String defaultStr){
		//FIXME 测试时多计算了Servlet
		pathNum = pathNum+rootNum;
		String[] pathArr = (String[]) msg.getData(ActionMsg.MSG_PATH_ARRAY);
		String subact = (pathArr.length <= pathNum) ? defaultStr : pathArr[pathNum];
		return subact;
	}
	
//	 class GZIPServletResponseWrapper extends HttpServletResponseWrapper {
//			private GZIPOutputStream gzipStream;
//			private ServletOutputStream servletOutputStream;
//			private PrintWriter printWriter;
//
//			GZIPServletResponseWrapper(HttpServletResponse resp) throws IOException {
//			    super(resp);
//			}
//			
//			public ServletOutputStream getOutputStream() throws IOException {
//			    if (servletOutputStream == null) {
//				servletOutputStream = createOutputStream();
//			    }
//			    return servletOutputStream;
//			}
//
//			public PrintWriter getWriter() throws IOException {
//			    if (printWriter == null) {
//				printWriter = new PrintWriter(new OutputStreamWriter(
//						getOutputStream(),
//						getCharacterEncoding())) {  // This is important for I18N
//		                    // Workaround for Tomcat bug where flush is NOT called when JSP output finished
//				    public void write(char[] cb, int off, int len) {
//					super.write(cb, off, len);
//					super.flush();
//				    }
//				};
//			    }
//			    return printWriter;
//			}
//
//			private ServletOutputStream createOutputStream() throws IOException {
//			    ServletResponse resp = this.getResponse();
//			    gzipStream = new GZIPOutputStream(resp.getOutputStream());
//			    addHeader("Content-Encoding", "gzip");
//			    addHeader("Vary", "Accept-Encoding");
//			    return new ServletOutputStream() {
//				/* The first three methods must be overwritten */
//				@Override
//				public void write(int b) throws IOException {
//				    gzipStream.write(b);		
//				}
//
//				@Override
//				public void flush() throws IOException {
//				    gzipStream.flush();
//				}
//
//				@Override
//				public void close() throws IOException {
//				    gzipStream.close();
//				}
//
//				/*
//				 * These two are not absolutely needed. They are here simply
//				 * because they were overriden by GZIPOutputStream.
//				 */
//				@Override
//				public void write(byte[] b) throws IOException {
//				    gzipStream.write(b);
//				}
//
//				@Override
//				public void write(byte[] b, int off, int len) throws IOException {
//				    gzipStream.write(b, off, len);
//				}
//			    };
//			}
//		    }
}
