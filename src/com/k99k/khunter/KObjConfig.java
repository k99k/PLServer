/**
 * 
 */
package com.k99k.khunter;

import java.util.ArrayList;
import java.util.HashMap;

import com.k99k.tools.JSON;

/**
 * KObjConfig
 * @author keel
 *
 */
public class KObjConfig {

	private KObjConfig() {
	}
	
	private String kobjName;
	
	private String intro;
	
	private KObjDaoConfig daoConfig;
	
	private KObjSchema kobjSchema;
	
	
	/**
	 * 创建新的KObjConfig
	 * @param key kobjName
	 * @param map json配置
	 * @return 创建失败返回null
	 */
	@SuppressWarnings("unchecked")
	public static final KObjConfig newInstance(String key,HashMap<String,Object> map){
		KObjConfig kc = new KObjConfig();
		kc.setKobjName(key);
		try {
			if (!JSON.checkMapTypes(map,new String[]{"intro","dao","columns","indexes"},new Class[]{String.class,HashMap.class,ArrayList.class,ArrayList.class})) {
				ErrorCode.logError(KObjManager.log, 8, 22, map.toString());
				return null;
			}
			String intro = (String) map.get("intro");
			HashMap<String,Object> daoMap = (HashMap<String, Object>) map.get("dao");
			ArrayList<HashMap<String,Object>> colList = (ArrayList<HashMap<String, Object>>) map.get("columns");
			ArrayList<HashMap<String,Object>> indexList = (ArrayList<HashMap<String, Object>>) map.get("indexes");
			KObjSchema ks = new KObjSchema();
			if (!ks.initSchema(key, colList, indexList)) {
				ErrorCode.logError(KObjManager.log, 8, 23, key);
				return null;
			}
			kc.setKobjSchema(ks);
			if (!kc.setDaoConfig(daoMap)) {
				ErrorCode.logError(KObjManager.log, 8, 24, daoMap.toString());
				return null;
			}
			kc.setIntro(intro);
			
		} catch (Exception e) {
			ErrorCode.logError(KObjManager.log, 8, 12, e, " -in KObjConfig.newInstance");
			return null;
		}
		
		return kc;
	}
	
	
	
	/**
	 * KObjConfig的Json化HashMap，注意此Map不含kobjName
	 * @return
	 */
	public final HashMap<String,Object> toMap() {
		 HashMap<String,Object> map = this.kobjSchema.toMap();
		 map.put("intro", this.intro);
		 map.put("dao", daoConfig.toMap());
		 return map;
	}
	
//	/**
//	 * 验证Dao的配置
//	 * @param map
//	 * @return
//	 */
//	public final boolean checkDaoMap(HashMap<String, Object> map){
//		if(!JSON.checkMapTypes(map,new String[]{"daoName","newDaoName"},new Class[]{String.class,String.class})){
//			return false;
//		}
//		//如果create为new,则必须有tableName字段
//		if (map.get("newDaoName").toString().trim().equals("")) {
//			if ((!map.containsKey("tableName")) || map.get("tableName").toString().length()<=0) {
//				return false;
//			}
//		}
//		return true;
//	}

	/**
	 * @return the kobjName
	 */
	public final String getKobjName() {
		return kobjName;
	}

	/**
	 * @param kobjName the kobjName to set
	 */
	public final void setKobjName(String kobjName) {
		this.kobjName = kobjName;
	}

	/**
	 * @return the daoConfig
	 */
	public final KObjDaoConfig getDaoConfig() {
		return daoConfig;
	}

	/**
	 * 更新或新增daoConfig
	 * @param daoConfig the daoConfig to set
	 */
	public final boolean setDaoConfig(HashMap<String, Object> daoConfig) {
		KObjDaoConfig kdc = KObjDaoConfig.newInstance(daoConfig);
		if (kdc == null) {
			return false;
		}
		this.daoConfig = kdc;
		return true;
	}
	
	/**
	 * @param kdc the daoConfig to set
	 */
	public final void setDaoConfig(KObjDaoConfig kdc) {
		this.daoConfig = kdc;
	}
	

	/**
	 * @return the kobjSchema
	 */
	public final KObjSchema getKobjSchema() {
		return kobjSchema;
	}

	/**
	 * @param kobjSchema the kobjSchema to set
	 */
	public final void setKobjSchema(KObjSchema kobjSchema) {
		this.kobjSchema = kobjSchema;
	}

	/**
	 * @return the intro
	 */
	public final String getIntro() {
		return intro;
	}

	/**
	 * @param intro the intro to set
	 */
	public final void setIntro(String intro) {
		this.intro = intro;
	}

}
