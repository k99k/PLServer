/**
 * 
 */
package com.k99k.khunter;

import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

import com.k99k.tools.JSON;

/**
 * KHunter系统管理者,协调管理中心
 * @author keel
 *
 */
public final class HTManager {

	private HTManager() {
	}
	
	static final Logger log = Logger.getLogger(HTManager.class);
	
	//private static final HTManager me = new HTManager();
	
	
	/**
	 * 调试模式
	 */
	public static boolean debug = true;
	
	/**
	 * 类加载路径,注意这里需要配置成绝对路径
	 */
	private static String classPath;
	
	/**
	 * 配置文件根路径,注意这里需要配置成绝对路径
	 */
	private static String iniPath;
	
	/**
	 * 配置文件路径
	 */
	private static String ini;
	
	/**
	 * 是否初始化
	 */
	private static boolean isInited = false;
	
	/**
	 * 类加载路径(绝对路径)
	 * @return
	 */
	public static final String getClassPath(){
		return classPath;
	}
	
	
	/**
	 * 配置文件放置路径
	 * @return the iniPath
	 */
	public static final String getIniPath() {
		return iniPath;
	}




	/**
	 * 处理各Manager的初始化
	 * @param iniFile 配置文件路径
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean init(String iniFile){
		boolean initOK = false;
		if (!isInited) {
			
			log.info("================ [HTManager starting... ] ================");
			try {
				String iniJson = KIoc.readTxtInUTF8(iniFile);
				Map<String,?> root = (Map<String,?>) JSON.read(iniJson);
				if (root.containsKey("classPath") && root.containsKey("iniPath")) {
					classPath = (String) root.get("classPath");
					//ini = (String) root.get("iniPath");
					iniPath = (String) root.get("iniPath");
					ini = iniPath + "kconfig.json";
					//调试模式
					String s = root.get("debug").toString();
					debug = (Boolean.parseBoolean(s)) ? true :false ;
					log.info("debug : " + debug);
					
					//初始化DataSourceManager
					initOK = DataSourceManager.init(ini,classPath);
					log.info("DataSourceManager inited OK? " + initOK);

					//初始化DaoManager
					initOK = DaoManager.init(ini,classPath);
					log.info("DaoManager inited OK? " + initOK);
					
					//初始化KObjManager
					initOK = KObjManager.init(iniPath+"kobj.json");
					log.info("KObjManager inited OK? " + initOK);
					
					//初始化TaskManager
					initOK = TaskManager.init(ini,classPath);
					log.info("TaskManager inited OK? " + initOK);
					
					//初始化IOManager
					initOK = IOManager.init(ini,classPath);
					log.info("IOManager inited OK? " + initOK);
					
					//最后初始化ActionManager
					initOK = ActionManager.init(ini,classPath);
					log.info("ActionManager inited OK? " + initOK);
					
				}
			} catch (Exception e) {
				log.error("HTManager init error!", e);
				return false;
			}
			if (initOK) {
				isInited = true;
				log.info("================ [HTManager init OK!] ================");
			}
			
		}else{
			log.warn("================ [HTManager already inited] ================");
		}
		return initOK;
	}
	
	public static final boolean reInit(String iniFile){
		isInited = false;
		return init(iniFile);
	}
	
	public static final String getIniFilePath(){
		return ini;
	}
	
	/**
	 * 从指定的Manager中找到指定对象
	 * @param managerName Manager的name
	 * @param name 被查找对象的name
	 * @return 如果未找到则返回null
	 */
	public static final Object findFromManager(String managerName,String name){
		
		if (managerName.equals("actions")) {
			return ActionManager.findAction(name);
		}else if(managerName.equals("daos")){
			return DaoManager.findDao(name);
		}else if(managerName.equals("kobjs")){
			return KObjManager.findKObjConfig(name);
		}else if(managerName.equals("io")){
			return IOManager.findIO(name);
		}else if(managerName.equals("dataSources")){
			return DataSourceManager.findDataSource(name);
		}

		return null;
	}
	
	
	/**
	 * 供各个Manager从配置文件的Map中设置对象属性用,注意属性除了String,ini外也支持HashMap,ArrayList等stringtree读取的对象
	 * @param obj 待设置对象
	 * @param m 由json配置文件对应节点读取出的对象属性Map
	 * @return 设置属性后的对象
	 */
	public static final Object fetchProps(Object obj,Map<String,?> m){
		//加入属性值
		for (Iterator<String> it = m.keySet().iterator(); it.hasNext();) {
			String prop = it.next();
			//不以下划线开头的属性用setter方法注入
			if (!prop.startsWith("_")) {
				if (prop.indexOf("#") == -1) {
					Object value = m.get(prop);
					//处理Long形式的整数属性值,因为stringtree对数字读取为Long, BigInteger, Double or BigDecimal
					//TODO 对浮点数未处理 ,对真正的Long未处理,实际上支持HashMap,ArrayList等stringtree读取的对象
					if (value instanceof Long) {
						int iv = ((Long)value).intValue();
						KIoc.setProp(obj, prop, iv);
					}else{
						KIoc.setProp(obj, prop, value);
					}
					
				}
				//由#号分为propName#manager两部分,后部分为指定的manager名
				else{
					String[] propArr = prop.split("#");
					String targetName = (String) m.get(prop);
					Object value = HTManager.findFromManager(propArr[1],targetName);
					if (value != null) {
						KIoc.setProp(obj, propArr[0], value);
					}else{
						log.error("The prop can't find from HTManager, didn't set this prop:"+prop);
					}
				}
				
			}
		}
		return obj;
	}
	
	
	/**
	 * 退出操作
	 */
	public static void exit(){
		log.info("================ [HTManager exiting] ================");
		ActionManager.exit();
		TaskManager.exit();
		IOManager.exit();
		DaoManager.exit();
		KObjManager.exit();
		DataSourceManager.exit();
		isInited = false;
		log.info("================ [HTManager exited] ================");
	}
	
	
	
//	static final Map<String,Object> managerMap = createManagerMap();
//	
//	private static final Map<String,Object> createManagerMap(){
//		Map<String,Object> map = new HashMap<String, Object>();
//		map.put("actions", ActionManager.getInstance());
//		return map;
//	}
}
