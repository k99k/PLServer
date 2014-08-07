/**
 * 
 */
package com.k99k.khunter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.k99k.tools.JSON;

/**
 * KObject:基础的可扩展对象,包含一个可扩展的属性列表,(带列表关系的参考KSObject).<br />
 * 直接json方式的toString方法
 * @author keel
 *
 */
public class KObject {
	
	public KObject() {
		//默认初始化propMap大小为50
		this(50);
	}

	/**
	 * 由Map直接创建,不检验参数,比较危险,建议仅供Mongodb使用
	 * @param map 
	 */
	public KObject(Map<String, Object> map) {
		this.propMap = new HashMap<String,Object>(map);
	}
	
	/**
	 * @param maxProp 初始化propMap大小
	 */
	public KObject(int maxProp) {
		this.propMap = new HashMap<String,Object>(maxProp);
		this.propMap.put("_id", 0L);
		this.propMap.put("state", 0);
		this.propMap.put("level", 0);
		this.propMap.put("info", "");
		this.propMap.put("createTime", System.currentTimeMillis());
		this.propMap.put("version", 1);
		this.propMap.put("name", "");
		this.propMap.put("creatorName", "");
		this.propMap.put("creatorId", 0L);
		this.propMap.put("url", "");
		this.propMap.put("type", 0);
		
	}
	
	/**
	 * 采用此构造方法性能最佳
	 * @param id
	 * @param state
	 * @param level
	 * @param info
	 * @param createTime
	 * @param version
	 * @param name
	 * @param creatorName
	 * @param creatorId
	 * @param url
	 * @param type
	 * @param maxProp 初始化propMap大小
	 */
	public KObject(long id, int state, int level, String info,
			long createTime, int version, String name, String creatorName,
			long creatorId, String url,int type,int maxProp) {
		this.propMap = new HashMap<String,Object>(maxProp);
		this.propMap.put("_id", id);
		this.propMap.put("state", state);
		this.propMap.put("level", level);
		this.propMap.put("info", info);
		this.propMap.put("createTime", createTime);
		this.propMap.put("version", version);
		this.propMap.put("name", name);
		this.propMap.put("creatorName", creatorName);
		this.propMap.put("creatorId", creatorId);
		this.propMap.put("url", url);
		this.propMap.put("type", type);
		
	}
	
	/**
	 * @param id
	 * @param state
	 * @param level
	 * @param info
	 * @param createTime
	 * @param version
	 * @param name
	 * @param creatorName
	 * @param creatorId
	 * @param url
	 */
	public KObject(long id, int state, int level, String info,
			long createTime, int version, String name, String creatorName,
			long creatorId, String url,int type) {
		this(id,state,level,info,createTime,version,name,creatorName,creatorId,url,type,50);
	}
	
//	/**
//	 * 获取除id之外的所有默认属性(10个)
//	 * @return
//	 */
//	public static final String[] getDefaultPropsWithOutId(){
//		String[] prop = new String[]{
//			"state",
//			"level",
//			"info",
//			"createTime",
//			"version",
//			"name",
//			"creatorName",
//			"creatorId",
//			"url",
//			"type"
//		};
//		return prop;
//	}
//	
//	/**
//	 * 获取除id之外的所有默认属性的类型
//	 * @return
//	 */
//	public static final String[] getDefaultPropTypesWithOutId(){
//		String[] prop = new String[]{
//			"Integer",
//			"Integer",
//			"String",
//			"Long",
//			"Integer",
//			"String",
//			"String",
//			"Long",
//			"String",
//			"String"
//		};
//		return prop;
//	}
	
	/**
	 * 唯一的属性Map
	 */
	private final HashMap<String,Object> propMap;
	
	public final boolean containsProp(String key){
		return this.propMap.containsKey(key);
	}
	
	public final Object removeProp(String key){
		return this.propMap.remove(key);
	}
	
	public final Object getProp(String key){
		return propMap.get(key);
	}

	public final Object setProp(String key,Object value){
		return this.propMap.put(key, value);
	}

	public final int getPropSize(){
		return this.propMap.size();
	}
	
	public final HashMap<String,Object> getPropMap(){
		return this.propMap;
	}
	/**
	 * 以json方式输出toString
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return JSON.write(this.propMap);
		
		/*
		StringBuilder sb  = new StringBuilder("{");
		//处理属性
		Iterator it = this.propMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            
        }
		
		for (Iterator<String> iterator = this.propMap.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			Object o = this.propMap.get(key);
			sb.append("\"").append(key).append("\":");
			if (o instanceof String || o instanceof Character) {
				jsonString(sb,o);
				//sb.append("\"").append(o).append("\"");
			} 
			
			else{
				sb.append(o);
			}
			sb.append(",");
		}
		//sb.append("\"_class\":\"").append(this.getClass().getName()).append("\"");
		sb.append("\"_class\":\":\"KObject\"}");
		return sb.toString();*/
	}
	
	final int getIntByName(String pName){
		Object o = this.propMap.get(pName);
		return (o == null) ? null : Integer.parseInt(o.toString());
	}
	
	final Object getObjectByName(String pName){
		return this.propMap.get(pName);
	}
	
	final long getLongByName(String pName){
		Object o = this.propMap.get(pName);
		return (o == null) ? null : Long.parseLong(o.toString());
	}
	
	final String getStringByName(String pName){
		Object o = this.propMap.get(pName);
		return (o == null) ? null : o.toString();
	}
	
	public final void setId(long id){
		this.propMap.put("_id", id);
	}
	
	public final long getId(){
		return getLongByName("_id");
	}
	
	/**
	 * @return the state
	 */
	public final int getState() {
		return getIntByName("state");
	}

	/**
	 * @param state the state to set
	 */
	public final void setState(int state) {
		this.propMap.put("state", state);
	}

	/**
	 * @return the level
	 */
	public final int getLevel() {
		return getIntByName("level");
	}

	/**
	 * @param level the level to set
	 */
	public final void setLevel(int level) {
		this.propMap.put("level", level);
	}

	/**
	 * @return the info
	 */
	public final String getInfo() {
		return getStringByName("info");
	}

	/**
	 * @param type the type to set
	 */
	public final void setType(int type) {
		this.propMap.put("type", type);
	}
	
	/**
	 * @return the type
	 */
	public final int getType() {
		return getIntByName("type");
	}

	/**
	 * @param info the info to set
	 */
	public final void setInfo(String info) {
		this.propMap.put("info", info);
	}

	/**
	 * @return the createTime
	 */
	public final long getCreateTime() {
		return getLongByName("createTime");
	}

	/**
	 * @param createTime the createTime to set
	 */
	public final void setCreateTime(long createTime) {
		this.propMap.put("createTime", createTime);
	}

	/**
	 * @return the version
	 */
	public final int getVersion() {
		return getIntByName("version");
	}

	/**
	 * @param version the version to set
	 */
	public final void setVersion(int version) {
		this.propMap.put("version", version);
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return getStringByName("name");
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.propMap.put("name", name);
	}

	/**
	 * @return the creatorName
	 */
	public final String getCreatorName() {
		return getStringByName("creatorName");
	}

	/**
	 * @param creatorName the creatorName to set
	 */
	public final void setCreatorName(String creatorName) {
		this.propMap.put("creatorName", creatorName);
	}

	/**
	 * @return the creatorId
	 */
	public final long getCreatorId() {
		return getIntByName("creatorId");
	}

	/**
	 * @param creatorId the creatorId to set
	 */
	public final void setCreatorId(long creatorId) {
		this.propMap.put("creatorId", creatorId);
	}


	/**
	 * @return the url
	 */
	public final String getUrl() {
		return getStringByName("url");
	}

	/**
	 * @param url the url to set
	 */
	public final void setUrl(String url) {
		this.propMap.put("url", url);
	}
	
	/**
	 * @param sb
	 * @param type 类型，注意首字母要大写，如:String、Object
	 * @param key map的key
	 * @param isPrim 是否是基本类型，如long、int ,这时涉及一个大小写问题
	 * @return
	 */
	private static final StringBuilder createPropGetterAndSetterHelp(StringBuilder sb,String type,String key,boolean isPrim){
		sb.append("public final ")
		.append((isPrim)?type.toLowerCase():type).append(" ");
		sb.append(KIoc.getGetterMethodName(key));
		sb.append("() {\n return get")
		.append(type).append("ByName(\"")
		.append(key).append("\");\n}\n\n");
		
		sb.append("public final void ");
		sb.append(KIoc.getSetterMethodName(key));
		sb.append("(")
		.append((isPrim)?type.toLowerCase():type).append(" ")
		.append(key).append(") {\n");
		sb.append("this.propMap.put(\"")
		.append(key).append("\", ")
		.append(key).append(");\n}\n\n");
		return sb;
	}
	
	/**
	 * 构建KObject子类所需要的getter和setter工具方法,以HTPlace举例:
	 <pre>
	 	Map propMap = new HashMap();
		propMap.put("type", 0);
		propMap.put("x", 0);
		propMap.put("y", 0);
		propMap.put("z", 0);
		propMap.put("special", "");
		propMap.put("building", "");
		propMap.put("camp", "");
		System.out.println(createPropGetterAndSetterString(propMap));
	 </pre>
	 * @param propMap
	 * @return
	 */
	public static final String createPropGetterAndSetterString(HashMap<String, Object> propMap){
		StringBuilder sb = new StringBuilder();
		for (Iterator<String> it = propMap.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			Object value = propMap.get(key);
			if (value instanceof String) {
				createPropGetterAndSetterHelp(sb,"String",key,false);
				
			}else if(value instanceof Integer) {
				createPropGetterAndSetterHelp(sb,"Int",key,true);
				
			}else if(value instanceof Long) {
				createPropGetterAndSetterHelp(sb,"Long",key,true);
				
			}else if(value == null){
				createPropGetterAndSetterHelp(sb,"Object",key,false);
				
			}else if(value instanceof Boolean) {
				createPropGetterAndSetterHelp(sb,"Boolean",key,true);
			}else{
				System.out.println("-----未识别出此value类型:"+value+" prop:"+key);
			}
		}
		return sb.toString();
	}
	
//	public static void main(String[] args) {
//		KObject kobj = new KObject();
//		kobj.setId(5);
//		kobj.setName("ssse..dfasd");
//		Map m = new HashMap();
//		m.put("sss", "sdfasdfas");
//		m.put("sfff", "ffee\" 要职");
//		ArrayList al = new ArrayList<String>();
//		al.add("sssfeef");
//		al.add("nnnwew");
//		long[] lo = new long[]{43234L,333L,33L};
//		
//		kobj.setProp("map", m);
//		kobj.setProp("lo", lo);
//		kobj.setProp("al", al);
//		System.out.println(kobj.toString());
//	}
	
}
