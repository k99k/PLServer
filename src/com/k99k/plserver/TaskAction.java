/**
 * 
 */
package com.k99k.plserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.DaoInterface;
import com.k99k.khunter.DaoManager;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.khunter.KFilter;
import com.k99k.khunter.KObject;
import com.k99k.khunter.TaskManager;
import com.k99k.tools.StringUtil;

/**
 * @author Keel
 *
 */
public class TaskAction extends Action {

	/**
	 * type为0-3对应的ErrCode,注意与TaskAction.TYPE_UID等值要对应
	 */
	private static final int[] ERR_CODE = new int[]{Err.ERR_TASK_UID,Err.ERR_TASK_GID,Err.ERR_TASK_TAG,Err.ERR_TASK_LEVEL};

	/**
	 * @param name
	 */
	public TaskAction(String name) {
		super(name);
	}
	
	public static final int TYPE_UID = 0;
	public static final int TYPE_GID = 1;
	public static final int TYPE_TAG = 2;
	public static final int TYPE_LEVEL = 3;
	

	static final Logger log = Logger.getLogger(TaskAction.class);

	static DaoInterface dao;
	
	//以下为各类任务缓存,每个cache的map由type对应的value做为key,以ArrayList<Long>为值,实现一对多关系
	private static HashMap<String,ArrayList<Long>> uidMap = new HashMap<String,ArrayList<Long>>();
	private static HashMap<String,ArrayList<Long>> gidMap = new HashMap<String,ArrayList<Long>>();
	private static HashMap<String,ArrayList<Long>> tagMap = new HashMap<String,ArrayList<Long>>();
	private static HashMap<String,ArrayList<Long>> levelMap = new HashMap<String,ArrayList<Long>>();
	private static HashMap<String,ArrayList<Long>>[] maps = new HashMap[4];
	static{
		maps[0] = uidMap;
		maps[1] = gidMap;
		maps[2] = tagMap;
		maps[3] = levelMap;
	}
	
	private String downloadLocalPath = "d:/dats/";

	@Override
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
		HttpServletRequest request =  httpmsg.getHttpReq();
		HttpServletResponse response = httpmsg.getHttpResp();
		String subact = KFilter.actPath(msg, 2, "");
		if (subact.equals("update")) {
			//FIXME task管理：增删改
			msg.addData(ActionMsg.MSG_PRINT, "update");
			return super.act(msg);
		}
		else if(subact.equals("reinit-keel")){
			int count = StaticDao.cacheTasks(maps);
			msg.addData(ActionMsg.MSG_PRINT, count);
			return super.act(msg);
		}else if(subact.equals("noti")){
			this.taskNotify(httpmsg, request);
			return super.act(msg);
		}
		
    	String tid = request.getParameter("id");
    	String v = request.getHeader("v");
    	String test = request.getHeader("test");
    	if (StringUtil.isDigits(tid) && (test != null || StringUtil.isStringWithLen(v, 2))) {
			//FIXME 解密v验证
    		   
    		//下载任务文件
    		String localPath = this.downloadLocalPath+tid+".dat";
    		DownAction.download(request, response, localPath, tid+".dat", httpmsg);
		}  
//    	else{
//			//TODO 手动初始化任务，生成cache
//			String initTasks = request.getParameter("init");
//			if (initTasks != null && initTasks.equals("keel")) {
//				this.init();
//				msg.addData(ActionMsg.MSG_PRINT, "ok");
//			}
//		}
		return super.act(msg);
	}
	
	private void taskNotify(ActionMsg msg,HttpServletRequest request){
		String tidstr = request.getParameter("t");
		String uidstr = request.getParameter("u");
		String msgstr = request.getParameter("m");
		String typestr = request.getParameter("y");
		if (!StringUtil.isDigits(typestr)) {
			typestr = request.getParameter("f");
		}
		if (!StringUtil.isDigits(tidstr) || !StringUtil.isDigits(uidstr)) {
			return;
		}
		//生成Task
		ActionMsg atask1 = new ActionMsg("actLogTask");
		//任务采用单队列的处理
		int type = (StringUtil.isDigits(typestr)) ? Integer.parseInt(typestr) : 0;
		atask1.addData(TaskManager.TASK_TYPE, TaskManager.TASK_TYPE_EXE_SINGLE);
		atask1.addData("tid", tidstr);
		atask1.addData("uid", uidstr);
		atask1.addData("type", type);
		String msge = "tk@@"+type+"@@"+tidstr+"@@"+msgstr;
		atask1.addData("msg", msge);
		TaskManager.makeNewTask("logTask:"+tidstr+":"+System.currentTimeMillis(), atask1);
		msg.addData(ActionMsg.MSG_PRINT, tidstr);
	}
	
	/**
	 * 初始化任务缓存
	 */
	private void initTaskCache(){
		int count = StaticDao.cacheTasks(maps);
		log.info("task init:"+count);
	}
	
	/**
	 * 生成需要同步的任务id集
	 * @param msg
	 * @return
	 */
	public static final String synTasks(KObject user,HttpActionMsg msg){
		long[] done =(long[])user.getProp("doneTasks");
		long[] running =(long[])user.getProp("tasks");
		HashSet<Long> tidSet = new HashSet<>();
		//查找各类任务
		//uid任务
		String uid = String.valueOf(user.getId());
		ArrayList<Long> tidLs = maps[0].get(uid);
		if (tidLs != null) {
			tidSet.addAll(tidLs);
		}
		//gid任务
		Object gms = user.getProp("games");
		if (gms != null) {
			long[] games = (long[])gms;
			for (int i = 0; i < games.length; i++) {
				tidLs = maps[1].get(String.valueOf(games[i]));
				if (tidLs != null) {
					tidSet.addAll(tidLs);
				}
			}
		}
		//tag任务
		Object tgs = user.getProp("tags");
		if (!maps[2].isEmpty() && tgs != null) {
			String[] tags = (String[])tgs;
			for (int i = 0; i < tags.length; i++) {
				tidLs = maps[2].get(tags[i]);
				if (tidLs != null) {
					tidSet.addAll(tidLs);
				}
			}
		}
		//level任务
		int lv = user.getLevel();
		if (!maps[3].isEmpty() && lv >= 0) {
			tidLs = maps[3].get(String.valueOf(lv));
			if (tidLs != null) {
				tidSet.addAll(tidLs);
			}
		}
		
		//去掉已完成的任务
		for (int i = 0; i < done.length; i++) {
			tidSet.remove(done[i]);
		}
		for (int i = 0; i < running.length; i++) {
			tidSet.remove(running[i]);
		}
		//生成String
		StringBuilder sb = new StringBuilder("");
		for (Iterator<Long> it = tidSet.iterator(); it.hasNext();) {
			Long tid = it.next();
			sb.append("_").append(tid);
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}
	
	
	

	/**
	 * 将"_"号分隔的Task的String转成long[]
	 * @param tasks
	 * @return
	 */
	public static final long[] checkTaskIds(String tasks){
		if (!StringUtil.isStringWithLen(tasks, 1)) {
			return new long[]{};
		}
		String[] arr = tasks.split("_");
		long[] ids = new long[arr.length];
		for (int i = 0; i < arr.length; i++) {
			if (StringUtil.isDigits(arr[i])) {
				ids[i] = Long.parseLong(arr[i]);
			}
		}
		return ids;
	}

	@Override
	public void init() {
		dao = DaoManager.findDao("dsTaskDao");
		this.initTaskCache();
		super.init();
	}

	/**
	 * 生成单个cache,每个cache的map由type对应的value做为key,以ArrayList<Long>为值,实现一对多关系
	 * @param map
	 * @param value
	 * @param tid
	 * @param type
	 */
	public static final void setCache(HashMap<String,ArrayList<Long>> map,Object value,long tid,int type){
		boolean checkValue = true;
		if (type == TYPE_TAG) {
			checkValue = StringUtil.isStringWithLen(value, 1);
		}else{
			checkValue = StringUtil.isDigits(value);
		}
		if (!checkValue) {
			StaticDao.log.error(ERR_CODE[type]+" tid:"+tid+" val:"+value);
			return;
		}
		String key = String.valueOf(value);
		ArrayList<Long> ls;
		if (map.containsKey(key)) {
			ls = map.get(key);
		}else{
			ls = new ArrayList<Long>();
			map.put(key, ls);
		}
		ls.add(tid);
	}

	public final String getDownloadLocalPath() {
		return downloadLocalPath;
	}

	public final void setDownloadLocalPath(String downloadLocalPath) {
		this.downloadLocalPath = downloadLocalPath;
	}
	
	

}
