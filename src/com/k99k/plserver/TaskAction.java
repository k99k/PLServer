/**
 * 
 */
package com.k99k.plserver;

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
	 * @param name
	 */
	public TaskAction(String name) {
		super(name);
	}

	static final Logger log = Logger.getLogger(TaskAction.class);

	static DaoInterface dao;

	@Override
	public ActionMsg act(ActionMsg msg) {
		//FIXME task管理：增删改
		
		return super.act(msg);
	}
	
	/**
	 * 生成需要同步的任务id集
	 * @param msg
	 * @return
	 */
	public static final String synTasks(KObject user,HttpActionMsg msg){
		//TODO 查找uid的任务
		
		//TODO 查找gid的任务
		
		//TODO 查找tag的任务
		
		//TODO 查找level的任务
		
		
		
		StringBuilder sb = new StringBuilder();
		
		
		
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
		super.init();
	}
	
	

}
