/**
 * 
 */
package com.k99k.khunter;

import java.util.Date;

/**
 * Task 异步操作,与Action同步操作相区别
 * @author keel
 *
 */
public class Task implements Runnable{

	
	/**
	 * @param actionMsg ActionMsg
	 */
	public Task(ActionMsg actionMsg) {
		this.actionMsg = actionMsg;
	}
	

	/**
	 * @param name
	 * @param actionMsg ActionMsg
	 */
	public Task(String name,ActionMsg actionMsg) {
		super();
		this.name = name;
		this.actionMsg = actionMsg;
	}
	
	

	/**
	 * @param name
	 * @param actionMsg
	 * @param canCanceled
	 */
	public Task(String name,ActionMsg actionMsg, boolean canCanceled ) {
		super();
		this.name = name;
		this.actionMsg = actionMsg;
		this.canCanceled = canCanceled;
	}



	private ActionMsg actionMsg;
	
	private boolean canCanceled = true;
	
//	/**
//	 * Task处理Action
//	 * @return 执行后的ActionMsg
//	 */
//	public ActionMsg exe(){
//		//执行Action
//		this.actionMsg = ActionManager.findAction(this.actionMsg.getActitonName()).act(actionMsg);
//		//加入本Task处理的完成时间
//		actionMsg.addData("task_"+this.name, new Date());
//		return actionMsg;
//	}

	@Override
	public void run() {
		//执行Action
		this.actionMsg = ActionManager.findAction(this.actionMsg.getActitonName()).act(actionMsg);
		//加入本Task处理的完成时间
		actionMsg.addData("task_"+this.name, new Date().toString());
		//清除taskMap的引用 
		TaskManager.removeFromTaskMap(this.name);
		
		//System.out.println("task done:"+this.name);
	}

	
	private String name;

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}
	
	
	



	/**
	 * @return the canCanceled
	 */
	public final boolean isCanCanceled() {
		return canCanceled;
	}


	/**
	 * @param canCanceled the canCanceled to set
	 */
	public final void setCanCanceled(boolean canCanceled) {
		this.canCanceled = canCanceled;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"task\":\"").append(this.name) //.append(",\"id\":").append(this.id)
		.append("\",\"msg\":").append(this.actionMsg.toJson()).append("}");
		return sb.toString();
	}



}
