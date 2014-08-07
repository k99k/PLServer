/**
 * 
 */
package com.k99k.khunter;

import java.util.HashMap;

/**
 * 因为KObject重写，所以此类可能也需要重写，只不过目前还未用到
 * KObject:基础的可扩展对象,包含一个可扩展的属性列表，一个子对象(member)列表，一个父对象(belongTo)列表
 * @author keel
 *
 */
public class KSObject extends KObject{
	

	
	/**
	 * 成员KObject的Map，初始大小为30
	 */
	private final HashMap<String, KObject> memberMap = new HashMap<String, KObject>(30);
	
	/**
	 * 添加/设置KObject成员
	 * @param key member的id(TODO 或member的url?)
	 * @param member 可设为id为0的空KObject,待需要获取时再由id进行获取
	 */
	public final void setMember(String key,KObject member){
		this.memberMap.put(key, member);
	}
	
	/**
	 * 取得KObject成员
	 * @param key
	 * @return KObject
	 */
	public final KObject getMember(String key){
		return this.memberMap.get(key);
	}
	
	/**
	 * 获取所有member的名称
	 * @return String[]
	 */
	public final String[] getMemberNames(){
		String[] arr = this.memberMap.keySet().toArray(new String[0]);
		return arr;
	}
	
	/**
	 * 归属于(BelongTo)KObject的Map，初始大小为10
	 */
	private final HashMap<String, KObject> belongToMap = new HashMap<String, KObject>(10);
	
	/**
	 * 添加/设置BelongTo
	 * @param belongTo对象的id或url
	 */
	public final void setBelongTo(String key,KObject member){
		this.belongToMap.put(key, member);
	}
	
	/**
	 * 取得BelongTo对象
	 * @param key belongTo对象的id或url
	 * @return KObject
	 */
	public final KObject getBelongTo(String key){
		return this.belongToMap.get(key);
	}
	
	/**
	 * 获取所有BelongTo的名称
	 * @return String[]
	 */
	public final String[] getBelongTo(){
		return this.belongToMap.keySet().toArray(new String[0]);
	}
	
}
