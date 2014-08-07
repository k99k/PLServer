/**
 * 
 */
package com.k99k.khunter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import com.k99k.tools.JSON;
import com.k99k.tools.StringUtil;

/**
 * KObject管理器
 * @author keel
 *
 */
public final class KObjManager {

	private KObjManager() {
	}
	
	static final Logger log = Logger.getLogger(KObjManager.class);
	
	
	/**
	 * 用于在json中定位
	 * @return 返回"daos"
	 */
	public static final String getName(){
		return "kobjs";
	}
	

	/**
	 * 存储Action的Map,初始化大小为50
	 */
	private static final HashMap<String, KObjConfig> kobjMap = new HashMap<String, KObjConfig>(50);

	
	private static boolean isInitOK = false;
	
	private static String iniFilePath;
	
	public static final boolean isInitOK(){
		return isInitOK;
	}
	
	/**
	 * 查找所有包含key字符串的的KObj的名称List,不区分大小写
	 * @param key 查找key
	 * @return HashMap<String, KObjConfig> 
	 */
	@SuppressWarnings("unchecked")
	public static final HashMap<String, KObjConfig> searchKObjList(String key){
		key = StringUtil.objToStrNotNull(key).trim();
		if (key.equals("")) {
			return (HashMap<String, KObjConfig>) kobjMap.clone();
		}
		key = key.toLowerCase();
		HashMap<String, KObjConfig> reMap = new HashMap<String, KObjConfig>(50);
		for (Iterator<String> it = kobjMap.keySet().iterator(); it.hasNext();) {
			String kobj =  it.next();
			KObjConfig kc = (KObjConfig) kobjMap.get(kobj);
			if (kobj.toLowerCase().indexOf(key) > -1 || (kc.getIntro().toLowerCase().indexOf(key) > -1)) {
				reMap.put(kobj,kc);
			}
		}
		return reMap;
	}
	
	/**
	 * 保存配置,同时将原文件按时间扩展名备份
	 * @return
	 */
	public static final boolean saveIni(){
		//保存
		int re = KIoc.saveJsonToFile(iniFilePath, getCurrentIni());
		if (re != 0) {
			ErrorCode.logError(log, 9,re, " - in KObjManager.save()");
			return false;
		}
		return true;
	}
	
	/**
	 * 获取当前配置
	 * @return HashMap<String,Object>
	 */
	public static final HashMap<String,Object> getCurrentIni(){
		HashMap<String,Object> root = new HashMap<String,Object>();
		HashMap<String,Object> map = new HashMap<String,Object>();
		for (Iterator<String> iterator = kobjMap.keySet().iterator(); iterator.hasNext();) {
			String kobjName = iterator.next();
			map.put(kobjName, ((KObjConfig)kobjMap.get(kobjName)).toMap());
		}
		root.put("kobjs", map);
		return root;
	}
	
	/**
	 * 返回kobjMap,这里返回的是clone后的对象
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public static final HashMap<String, KObjConfig> getKObjMap(){
		return (HashMap<String, KObjConfig>) kobjMap.clone();
	}
	
	public static final KObjSchema findSchema(String kobjName){
		KObjConfig kc = kobjMap.get(kobjName);
		return kc.getKobjSchema();
	}
	
	public static final KObjConfig findKObjConfig(String kobjName){
		return kobjMap.get(kobjName);
	}

	public static final KObject createEmptyKObj(String kobjName){
		KObjConfig kc = (KObjConfig)kobjMap.get(kobjName);
		return kc.getKobjSchema().createEmptyKObj();
	}
	
	/**
	 * 创建新的KObjConfig，并创建一个默认数据到数据库中，创建相关的索引，再进行删除,注意此方法不会更新配置文件
	 * @param key kobjName
	 * @param map json配置
	 * @return 成功返回0,其他为错误码
	 */
	public static final int createKObjConfigToDB(String key,HashMap<String,Object> map){
		if (key == null || map == null) {
			return 15;
		}
		if (kobjMap.containsKey(key)) {
			return 16;
		}
		KObjConfig kc = KObjConfig.newInstance(key, map);
		if (kc == null) {
			return 15;
		}
		DaoInterface dao = kc.getDaoConfig().findDao();
		if (dao == null) {
			return 18;
		}
		kobjMap.put(key, kc);
		KObjSchema ks = kc.getKobjSchema();
		//生成新id
		KObject oneKObj = ks.createEmptyKObj();
		long id = oneKObj.getId();
		//使用save方法保持id
		if (dao.save(oneKObj)) {
			if (ks.applyIndexes() == 0) {
				if (dao.deleteForever(id)) {
					return 0;
				}
				return 21;
			}
			return 20;
		}
		return 19;
	}
	
	/**
	 * 创建或更新一个KObjConfig
	 * @param kobjName
	 * @param map HashMap<String,Object> KObjConfig的json配置
	 * @return
	 */
	public static final boolean setKObjConfig(String kobjName,HashMap<String,Object> map){
		KObjConfig kc = KObjConfig.newInstance(kobjName, map);
		if (kc == null) {
			return false;
		}
		kobjMap.put(kobjName, kc);
		return true;
	}
	
	/**
	 * 查找一个kobj对象的Dao
	 * @param kobjKey
	 * @return
	 */
	public static final DaoInterface findDao(String kobjKey){
		KObjConfig kc = findKObjConfig(kobjKey);
		return kc.getDaoConfig().findDao();
	}
	
	/**
	 * 新增一个具体的KObject对象,更多的操作应使用Schema结合Dao完成
	 * @param kobjKey
	 * @param kobj
	 * @return
	 */
	public static final int addKObj(String kobjKey,KObject kobj){
		KObjConfig kc = findKObjConfig(kobjKey);
		KObjSchema ks = kc.getKobjSchema();
		DaoInterface dao = kc.getDaoConfig().findDao();
		if (!ks.validate(kobj.getPropMap())) {
			return 13;
		}
		if(dao == null || (!dao.add(kobj))){
			return 14;
		}
		return 0;
	}
	
	/**
	 * 新增一个具体的KObject对象
	 * @param kobjKey
	 * @param map
	 * @return
	 */
	public static final int addKObj(String kobjKey,HashMap<String,Object> map){
		KObjConfig kc = findKObjConfig(kobjKey);
		KObjSchema ks = kc.getKobjSchema();
		DaoInterface dao = kc.getDaoConfig().findDao();
		if (!ks.validate(map)) {
			return 13;
		}
		if(dao == null || (!dao.add(new KObject(map)))){
			return 14;
		}
		return 0;
	}
	
	/**
	 * 是否存在此kobjName的KObjConfig
	 * @param kobjName
	 * @return
	 */
	public static final boolean containsKObj(String kobjName){
		return kobjMap.containsKey(kobjName);
	}
	
	/**
	 * 设置某KObject对象的属性,注意不支持包含ArrayList的节点
	 * @param kobj
	 * @param kobjPath
	 * @param prop
	 * @return
	 */
	public static final boolean setProp(KObject kobj,String kobjPath,Object prop){
		KObjConfig kc = (KObjConfig)kobjMap.get(kobj.getName());
		return kc.getKobjSchema().setProp(kobj, kobjPath, prop);
	}
	
	public static final boolean validateKObjMap(String kobjName,HashMap<String,Object> kMap){
		KObjConfig kc = (KObjConfig)kobjMap.get(kobjName);
		return kc.getKobjSchema().validate(kMap);
	}
	
	public static final boolean validateKObjPath(String kobjName,String kobjPath,Object value){
		KObjConfig kc = (KObjConfig)kobjMap.get(kobjName);
		return kc.getKobjSchema().validateColumns(kobjPath, value);
	}
	
	/**
	 * 创建新的数据库结构,注意此方法仅操作数据库,不写入配置文件,调用前需要手动修改kconfig.json,新增dataSources相关节点数据
	 * @param newDataSourceName 必须事先创建好
	 * @param iniFile
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static final boolean buildAllNewData(String newDataSourceName){
		try {
			DataSourceInterface ds = DataSourceManager.findDataSource(newDataSourceName.trim());
			if (ds == null) {
				log.error("KObjManager.buildNewData error: dataSourceName not exist.");
				return false;
			}
//			String ini = KIoc.readTxtInUTF8(iniFilePath);
//			Map<String,?> root = (Map<String,?>) JSON.read(ini);
//			//先定位到json的对应属性
//			Map<String, ?> mgr = (Map<String, ?>) root.get(getName());
			Iterator it = kobjMap.entrySet().iterator(); 
			int i = 0;
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next(); 
//				String keyName = (String)entry.getKey();
//				HashMap<String, Object> m = (HashMap<String, Object>)entry.getValue();
				KObjConfig kc = (KObjConfig)entry.getValue();
				if (kc == null) {
					ErrorCode.logError(log, 8,26, "i:"+i);
					continue;
				}
				//创建数据结构
				boolean re = ds.buildNewTable(kc);
				log.info("[CREATE KOBJ DATA] "+kc.getKobjName()+" "+ re);
				i++;
			}
		} catch (Exception e) {
			log.error("KObjManager.buildNewData error",e);
			return false;
		}
		return true;
	}
	
	/**
	 * 重建表数据(先drop掉,然后重建)
	 * @param kcName KObjConfig name
	 * @return
	 */
	public static final boolean reBuildNewData(String kcName){
		try {
			KObjConfig kc = (KObjConfig)kobjMap.get(kcName.trim());
			if (kc == null) {
				ErrorCode.logError(log, 8,27, "kcName not exsit.");
			}
			//创建数据结构
			boolean re = kc.getDaoConfig().findDao().getDataSource().buildNewTable(kc);
			log.info("[CREATE KOBJ DATA] "+kcName+" "+ re);
			return re;
		} catch (Exception e) {
			log.error("KObjManager.buildNewData error",e);
			return false;
		}
	}
	
	/**
	 * 初始化KObjManager
	 * @param iniFile 配置文件路径
	 * @param classPath class文件所在的路径
	 * @return 是否初始化成功
	 */
	@SuppressWarnings("unchecked")
	public final static boolean init(String iniFile){
		if (!isInitOK) {
			//读取配置文件
			try {
				
				String ini = KIoc.readTxtInUTF8(iniFile);
				Map<String,?> root = (Map<String,?>) JSON.read(ini);
				//先定位到json的对应属性
				Map<String, ?> mgr = (Map<String, ?>) root.get(getName());

				//循环加入
				int i = 0;
				for (Iterator<String> iter = mgr.keySet().iterator(); iter.hasNext();) {
					String keyName = iter.next();
					HashMap<String, Object> m = (HashMap<String, Object>) mgr.get(keyName);
//					if(!JSON.checkMapTypes(m, new String[]{"intro","dao","columns","indexes"}, new Class[]{String.class,HashMap.class,ArrayList.class,ArrayList.class})){
//						ErrorCode.logError(log, 8, 6," key:"+keyName);
//						continue;
//					}
					KObjConfig kc = KObjConfig.newInstance(keyName, m);
					if (kc == null) {
						ErrorCode.logError(log, 8, 7, "i:"+i);
						continue;
					}
//					if(!kc.setDaoConfig((HashMap<String, Object>) m.get("dao"))){
//						ErrorCode.logError(log, 8, 7, " dao error. i:"+i);
//						continue;
//					}
//					KObjSchema ks = new KObjSchema();
//					boolean initSchema = ks.initSchema(keyName,(ArrayList<HashMap<String, Object>>) m.get("columns"),(ArrayList<HashMap<String, Object>>) m.get("indexes"));
//					if (!initSchema) {
//						ErrorCode.logError(log, 8, 7, " i:"+i);
//						continue;
//					}
//					kc.setKobjSchema(ks);
//					kc.setKobjName(keyName);
					kobjMap.put(keyName, kc);
					log.info("KObjConfig added:"+keyName);
				}
				
			} catch (Exception e) {
				ErrorCode.logError(log, 8, 8, e, "");
				isInitOK = false;
				return false;
			}
			isInitOK = true;
			//更新配置文件位置
			iniFilePath = iniFile;
			//classFilePath = classPath;
			log.info("KObjManager init OK!");
		}
		return true;
	}
	
	/**
	 * 重载入某一个KObjConfig
	 * @param kcName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final static boolean reload(String kcName){
		//读取配置文件
		try {
			String ini = KIoc.readTxtInUTF8(iniFilePath);
			Map<String,?> root = (Map<String,?>) JSON.read(ini);
			//先定位到json的对应属性
			Map<String, ?> mgr = (Map<String, ?>) root.get(getName());

			HashMap<String, Object> m = (HashMap<String, Object>) mgr.get(kcName);
			KObjConfig kc = KObjConfig.newInstance(kcName, m);
			if (kc == null) {
				ErrorCode.logError(log, 8, 7, "kobj:"+kcName);
				return false;
			}else{
				kobjMap.put(kcName, kc);
				log.info("KObjConfig reloaded:"+kcName);
			}
		} catch (Exception e) {
			ErrorCode.logError(log, 8, 8, e, "");
			return false;
		}
		return true;
	}
	
	/**
	 * 重新初始化
	 * @param iniFile 配置文件路径,为null时使用当前的配置
	 * @return 是否初始化成功
	 */
	public final static boolean reInit(String iniFile){
		exit();
		String ini = (iniFile == null)? iniFilePath : iniFile;
		return init(ini);
	}

	/**
	 * 退出KObjManager时的操作
	 */
	public static final void exit(){
		kobjMap.clear();
		isInitOK = false;
		log.info("KObjManager exited.");
	}
	
	
}
