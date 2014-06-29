package com.k99k.plserver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.k99k.tools.StringUtil;

/**
 */
public class PL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PL() {
        super();
    }
    
    private String savePath = "d:/";//"/usr/plserver/logs/";
    
   
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		setCharset("utf-8",request,response	);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		setCharset("utf-8",request,response	);
		String file = request.getParameter("f");
		if (!StringUtil.isStringWithLen(file, 1)) {
			response.setStatus(400);
			response.getWriter().println("err");
			return;
		}
		int size = Uploader.upload(request,this.savePath,file,file);
		if (size <= 0) {
			response.setStatus(500);
			response.getWriter().println("err");
			return;
		}
		response.getWriter().println("ok");
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
	 * @return the savePath
	 */
	public final String getSavePath() {
		return savePath;
	}

	/**
	 * @param savePath the savePath to set
	 */
	public final void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	
	
	
}
