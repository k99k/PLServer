/**
 * 
 */
package com.k99k.plserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传动作类
 * @author keel
 *
 */
public class Uploader {

	/**
	 * @param name
	 */
	public Uploader() {
		super();
	}
	
	
	

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#act(com.k99k.khunter.ActionMsg)
	
	@Override
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
		HttpServletRequest req = httpmsg.getHttpReq();
		String file = req.getParameter("f");
		if (!StringUtil.isStringWithLen(file, 1)) {
			JOut.err(400, httpmsg);
			return super.act(msg);
		}
		String re = upload(req,this.savePath,file,true);
		msg.addData(ActionMsg.MSG_PRINT, re);
		return super.act(msg);
	}
	 */
	
	/**
	 * 在文件路径中增加后缀,如aaa.txt增加为aaa_1.txt,其中_1为tail
	 * @param tail
	 * @param filePath
	 * @return
	 */
	public static final String addFileTail(String tail,String filePath){
		int po = filePath.lastIndexOf(".");
		po = (po<0)?0:po;
		StringBuilder sb = new StringBuilder(filePath.substring(0, po));
		sb.append(tail);
		sb.append(filePath.substring(po));
		return sb.toString();
	}

	
	/**
	 * 接收上传文件,如有同名文件则覆盖
	 * @param request HttpServletRequest
	 * @param savePath 保存文件的路径,注意末尾要加上/符
	 * @param fileName 原上传文件名
	 * @param newName 上传后的文件名
	 * @return 上传后的文件名
	 */
	public final static int upload(HttpServletRequest request,String savePath,String fileName,String newName){
		FileOutputStream fos = null;
        ServletInputStream  sis = null;
        int size = Integer.parseInt(request.getHeader("Content-Length"));
		try {
			 sis = request.getInputStream();
			 (new File(savePath)).mkdirs();
			 String toFile = savePath+newName;
//			 System.out.println("toFile:"+toFile);
            fos = new FileOutputStream(new File(toFile),false);
            byte[] bt = new byte[2048];  
            int count,inSize=0;  
            while ((count = sis.read(bt)) > 0) {  
                fos.write(bt, 0, count); 
                inSize+=count;
            }  
//            System.out.println("size:"+inSize);
            if (inSize == size) {
				return size;
			}
			return -1;
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}finally {
            try {
                fos.close();
                sis.close();
            } catch (IOException ignored) {
            }
        }
	}
	


}
