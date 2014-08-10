/**
 * 
 */
package com.k99k.plserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.DaoInterface;
import com.k99k.khunter.DaoManager;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.khunter.KObject;
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

	@Override
	public ActionMsg act(ActionMsg msg) {
		//FIXME task管理：增删改
		
		
		
		return super.act(msg);
	}
	
	/**
	 * 初始化任务缓存
	 */
	private void initTaskCache(){
		StaticDao.cacheTasks(maps);
	}
	
	/**
	 * 生成需要同步的任务id集
	 * @param msg
	 * @return
	 */
	public static final String synTasks(KObject user,HttpActionMsg msg){
		long[] done = (long[]) user.getProp("doneTasks");
		HashSet<Long> tidSet = new HashSet<>();
		//查找各类任务
		String uid = String.valueOf(user.getId());
		for (int i = 0; i < maps.length; i++) {
			HashMap<String,ArrayList<Long>> map = maps[i];
			if (map.containsKey(uid)) {
				ArrayList<Long> tidLs = map.get(uid);
				tidSet.addAll(tidLs);
			}
		}
		//去掉已完成的任务
		for (int i = 0; i < done.length; i++) {
			tidSet.remove(done[i]);
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
	
	

}
