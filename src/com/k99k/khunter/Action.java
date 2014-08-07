/**
 * 
 */
package com.k99k.khunter;

import java.util.Date;

/**
 * 同步操作,与Task异步操作相区别,多个Action之间可相互调用
 * <p>
 * Action的第一个用法:串连
<pre>
msg.setNextAction(ActionManager.findAction("log"));
</pre>
 * Action的第二个用法:直接调用其act方法
<pre>
Action a = ActionManager.findAction("log");
a.act(msg);
</pre>
 * </p>
 * @author keel
 *
 */
public abstract class Action {
	
	/**
	 * @param name 动作标记
	 */
	public Action(String name) {
		this.name = name;
	}
	
	private String name;
	
	private int id;
	
	/**
	 * 获取方式,single表示单例,normal表示每次创建新的,默认为normal
	 */
	private String type = "normal";
	
	/**
	 * 执行动作,如果有下一个动作则在本动作完成后直接调用下一个Action继续执行
	 * @param msg ActionMsg
	 * @return 执行后的ActionMsg
	 */
	public ActionMsg act(ActionMsg msg){
		//此处加入进行的操作
		
		
		//加入本Action处理的完成时间
		msg.addData("action_"+this.name, new Date().toString());
		//如果有下一个Action,则立即执行
		if (msg.getNextAction() != null) {
			msg.getNextAction().act(msg);
		}
		return msg;
	}

	/**
	 * @return the id
	 */
	public final int getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public final void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public final String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public final void setType(String type) {
		this.type = type;
	}

	/**
	 * 处理退出时的操作,一般在ActionManager关闭时处理
	 */
	public void exit(){};
	
	/**
	 * 处理初化化操作,注意初始化不要太复杂,时间过长
	 */
	public void init(){};
	
	/**
	 * 重新载入
	 */
	public void reLoad(){
		this.exit();
		this.init();
	}
	
	/**
	 * 返回此Action所用到的配置文件路径
	 * @return
	 */
	public String getIniPath(){
		return null;
	}
}
