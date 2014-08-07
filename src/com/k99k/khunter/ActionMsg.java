/**
 * 
 */
package com.k99k.khunter;

import java.util.HashMap;
import java.util.Map;

import com.k99k.tools.JSON;

/**
 * Action请求传递的消息,包括了请求和返回的数据集
 * 
 * @author keel
 *
 */
public class ActionMsg {


	/**
	 * @param actionName
	 */
	public ActionMsg(String actionName) {
		this.actitonName = actionName;
		this.data = new HashMap<String, Object>();
	}
	
	/**
	 * @param actionName
	 * @param dataInitSize data的初始化size
	 */
	public ActionMsg(String actionName,int dataInitSize) {
		this.actitonName = actionName;
		this.data = new HashMap<String, Object>(dataInitSize);
	}
	
	
	private String actitonName;
	
	/**
	 * 下一个Action,根据请求的不同每次的nextAction也可能不同
	 */
	private Action nextAction;
	
	
	/**
	 * 数据集,通常为下一个Action所需要的数据
	 */
	private final Map<String, Object> data;

	
	/**
	 * 在toJson中插入更多的字段,用于继承类覆盖
	 * @param sb 原toJson中的StringBuilder
	 * @return
	 */
	StringBuilder addToJson(StringBuilder sb){
		return sb;
	}
	
	/**
	 * json化输出消息数据
	 * @return
	 */
	public String toJson(){
		StringBuilder sb = new StringBuilder();
		sb.append("{\"act\":\"").append(this.actitonName).append("\",\"next\":");
		if (this.nextAction == null) {
			sb.append("null,");
		}else{
			sb.append("\"").append(this.nextAction.getName()).append("\",");
		}
		sb.append("\"data\":").append(JSON.write(data));
		sb = this.addToJson(sb);
		sb.append("}");
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return this.toJson();
	}
	
	/**
	 * 获取data中的数据
	 * @param key
	 * @return
	 */
	public Object getData(String key){
		return this.data.get(key);
	}

	/**
	 * 是否存在
	 * @param key
	 * @return
	 */
	public boolean containsData(String key){
		return this.data.containsKey(key);
	}
	
	/**
	 * 增加数据
	 * @param key
	 * @param value
	 */
	public void addData(String key,Object value){
		this.data.put(key, value);
	}
	
	/**
	 * 删除数据
	 * @param key
	 */
	public void removeData(String key){
		this.data.remove(key);
	}
	
	/**
	 * @return the actitonName
	 */
	public final String getActitonName() {
		return actitonName;
	}

	/**
	 * @param actitonName the actitonName to set
	 */
	public final void setActitonName(String actitonName) {
		this.actitonName = actitonName;
	}

	/**
	 * @return the nextAction
	 */
	public final Action getNextAction() {
		return nextAction;
	}

	/**
	 * @param nextAction the nextAction to set
	 */
	public final void setNextAction(Action nextAction) {
		this.nextAction = nextAction;
	}


}
