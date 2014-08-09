/**
 * 
 */
package com.k99k.khunter.acts;

//import java.awt.geom.AffineTransform;
//import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.tools.StringUtil;
import com.k99k.khunter.JOut;
import com.sun.image.codec.jpeg.*;

/**
 * 文件上传动作类
 * @author keel
 *
 */
public class Uploader extends Action {

	/**
	 * @param name
	 */
	public Uploader(String name) {
		super(name);
	}
	
	
	/**
	 * 保存文件的路径
	 */
	private String savePath = "";
	
	static final Logger log = Logger.getLogger(Uploader.class);

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#act(com.k99k.khunter.ActionMsg)
	 */
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
	 * 接收上传文件
	 * @param request HttpServletRequest
	 * @param savePath 保存文件的路径,注意末尾要加上/符
	 * @param fileName 原上传文件名
	 * @param saveRandomName 是否生成随机的文件名("毫秒数__fileName")
	 * @return 上传后的文件名
	 */
	public final static String upload(HttpServletRequest request,String savePath,String fileName,boolean saveRandomName){
		String newName = (saveRandomName)?System.currentTimeMillis()+"__"+fileName:fileName;
    	//newName = UUID.randomUUID().toString()+"__"+fileName;
		return upload(request, savePath, fileName, newName);
	}
	
	
	/**
	 * 接收上传文件,如有同名文件则覆盖
	 * @param request HttpServletRequest
	 * @param savePath 保存文件的路径,注意末尾要加上/符
	 * @param fileName 原上传文件名
	 * @param newName 上传后的文件名
	 * @return 上传后的文件名
	 */
	public final static String upload(HttpServletRequest request,String savePath,String fileName,String newName){
		FileOutputStream fos = null;
        ServletInputStream  sis = null;
		try {
			 sis = request.getInputStream();
			 String toFile = savePath+newName;
		 
            fos = new FileOutputStream(new File(toFile),false);
            
            byte[] bt = new byte[2048];  
            int count;  
            //因为按行读取最后一行会多读一个换行符,所以使用tmpb保存上一行尾部多余一个或两个byte,在新一行读取时根据情况写入
            byte[] tmpb = new byte[2];
            //尾部字节是否是一个,false表示有两个字节
            boolean one = false;
            //读第一行得到分隔标识
            sis.readLine(bt,0,bt.length);
            String firstLine = new String(bt).trim();
            int firstLineLen = firstLine.length();
          	//向后读6行,此为http协议中文件的head部分，
            for(int j = 0;j<7;j++){
            	sis.readLine(bt,0,bt.length);
            }
          	//文件从第二行开始接上一行尾部字节,appendLastTail为是否接上一行尾部byte
          	boolean appendLastTail = false;
            while ((count = sis.readLine(bt,0,bt.length)) > 0) { 
            	//第7行开始读,前几行均为文件头信息
                //文件中发现5个-(45的String就是-)时,判断是否是文件末尾
            	if((bt[0]==45)&&(bt[1]==45)&&(bt[2]==45)&&(bt[3]==45)&&(bt[4]==45)) {
            		String line = new String(bt);
            		if(line.length()>=firstLineLen){
            			line = line.substring(0,firstLineLen);
            			//当与firstLine分隔标识相同时，表示为文件末尾，跳出
            			if(line.equals(firstLine)){
            				break;
            			}
            		}
            	}
            	//从第8行开始写上一行的尾部字节
            	if(appendLastTail){
            		if(one){
                		fos.write(tmpb[0]);
                	}else{
                		fos.write(tmpb); 
                	}
            	}
            	if(count == 1){
            		//本行仅有一个字节,到下一行再作为尾部字节写入
            		tmpb[0] = bt[0];
            		one = true;
            	}else{
            		//写入除尾部字节外的本行字节
                	fos.write(bt, 0, count-2);
            		//保存尾部字节
                	tmpb[0] = bt[count-2];
                	tmpb[1] = bt[count-1];
                	one = false;
            	}
                appendLastTail = true;
            }  
	        //System.out.println("toFile:"+toFile); 
	        //System.out.println("basePath+filename:"+basePath+filename); 
			//out.clear();	
			//out.print(basePath+"/images/upload/temppic/"+filename);
			return newName;
		} catch (Exception ex) {
			log.error("upload Error!", ex);
			return null;
		}finally {
            try {
                fos.close();
                sis.close();
            } catch (IOException ignored) {
            }
        }
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


	/**
	 * 生成缩略图
	 * @param srcPicPath 原图的本地地址
	 * @param targetPicPath 新图的本地地址
	 * @param newWidth 新的宽度
	 * @return 是否生成成功
	 */
	public static final boolean makeSmallPic(String srcPicPath,String targetPicPath,int newWidth,int maxHeight){
		try {
            File fi = new File(srcPicPath); //大图文件
            //File fo = new File(targetPicPath); //将要转换出的小图文件

            //AffineTransform transform = new AffineTransform();
            BufferedImage bis = ImageIO.read(fi);

            int w = bis.getWidth();
            int h = bis.getHeight();
           // double scale = (double)w/h;

            int nw = newWidth;
            int nh = (nw * h) / w;
            if(nh>maxHeight) {
                nh = maxHeight;
                nw = (nh * w) / h;
            }

            //double sx = (double)nw / w;
            //double sy = (double)nh / h;

            //transform.setToScale(sx,sy);

            //AffineTransformOp ato = new AffineTransformOp(transform, null);
            BufferedImage bid = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
            bid.getGraphics().drawImage(bis,0,0,nw,nh,null);
            FileOutputStream out = new FileOutputStream(targetPicPath);
            //FIXME 图片处理在jdk7下以下四行报错,jdk6下需要解除以下四行
//			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//			JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(bid);
//			jep.setQuality(0.9f, true);
//			encoder.encode(bid,jep);
            out.close();
            
            //ato.filter(bis,bid);
            //ImageIO.write(bid, "jpeg", fo);
        } catch(Exception e) {
        	log.error("makeSmallPic failed.",e);
           return false;
        }
		return true;
	}

	public static void main(String[] args) {
		System.out.println(Uploader.makeSmallPic("g:/17.png", "g:/17_s.png", 250, 300));
		//String s = "g:/460.jpg";
		//System.out.println(addFileTail("_s", s));
		
	}

}
