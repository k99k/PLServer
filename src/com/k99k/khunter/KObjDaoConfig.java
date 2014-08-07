/**
 * 
 */
package com.k99k.khunter;

import java.util.HashMap;

import com.k99k.tools.JSON;
import com.k99k.tools.StringUtil;

/**
 * KObjConfig中的daoConfig
 * @author keel
 *
 */
public class KObjDaoConfig {

	private KObjDaoConfig() {
	}
	
	/**
	 * 创建一个新的KObjDaoConfig
	 * @param map HashMap<String,Object>
	 * @return 如果创建失败返回null
	 */
	@SuppressWarnings("unchecked")
	public static final KObjDaoConfig newInstance(HashMap<String,Object> map){
		if (map == null) {
			ErrorCode.logError(KObjManager.log, 8, 24, "newInstance's para 'map' is null.");
			return null;
		}
		KObjDaoConfig kdc = new KObjDaoConfig();
		if(!JSON.checkMapTypes(map,new String[]{"daoName"},new Class[]{String.class})){
			ErrorCode.logError(KObjManager.log, 8, 24, "newInstance's para 'map' JSON.checkMapTypes error.");
			return null;
		}
		kdc.daoName = (String) map.get("daoName");
		//如果DaoManager中不存在,返回null
		if (!DaoManager.containsDao(kdc.daoName)) {
			ErrorCode.logError(KObjManager.log, 8, 24, "daoName is not in DaoManager");
			return null;
		}
		//如果存在newDaoName字段,则表示为非引用Dao,必须有tableName字段生成新Dao
		if (map.containsKey("newDaoName") ) {
			if ((!map.containsKey("tableName")) || map.get("tableName").toString().length()<=0) {
				ErrorCode.logError(KObjManager.log, 8, 24, "have newDaoName,but tableName is not ok.");
				return null;
			}
			kdc.newDaoName = map.get("newDaoName").toString();
			kdc.tableName = map.get("tableName").toString();
			Object o = map.get("props");
			if (o != null && o instanceof HashMap) {
				HashMap<String,Object> m = (HashMap<String, Object>) o;
				Object ido = m.get("id");
				Object typeo = m.get("type");
				if (StringUtil.isDigits(ido.toString())) {
					kdc.setPropId(Integer.parseInt(ido.toString()));
				}
				if (typeo != null && typeo instanceof String) {
					kdc.setPropType(typeo.toString());
				}
			}
			//--------在DaoManager中创建-----------
			//如果新名称已被占用，返回null
			if (DaoManager.containsDao(kdc.newDaoName)) {
				ErrorCode.logError(KObjManager.log, 8, 24, "newDaoName is already exist.");
				return null;
			}else{
				DaoInterface newDao = DaoManager.cloneDao(kdc.daoName);
				newDao.setName(kdc.newDaoName);
				newDao.setTableName(kdc.getTableName());
				if (kdc.props != null) {
					if (kdc.props.id != 0) {
						newDao.setId(kdc.props.id);
					}
					if (kdc.props.type != null) {
						newDao.setType(kdc.props.type);
					}
				}
				
				if(!DaoManager.addDao(newDao)){
					ErrorCode.logError(KObjManager.log, 8, 24, "newDao.init error or DaoManager.addDao failed.");
					return null;
				}
			}
		}
		return kdc;
	}
	
	/**
	 * 在DaoManager中找到对应的DaoInterface
	 * @return DaoInterface
	 */
	public final DaoInterface findDao(){
		if (this.newDaoName != null) {
			return DaoManager.findDao(this.newDaoName);
		}
		return DaoManager.findDao(this.daoName);
	}
	
	private String daoName;
	
	private String newDaoName;
	
	private String tableName;
	
	private Props props = new Props();
	
	class Props{
		public Props(){};
		int id;
		String type;
		
		HashMap<String,Object> toMap(){
			HashMap<String,Object> m = new HashMap<String, Object>();
			m.put("id", this.id);
			m.put("type", this.type);
			return m;
		}
	}
	
	public final HashMap<String,Object> toMap(){
		HashMap<String,Object> m = new HashMap<String, Object>();
		m.put("daoName", this.daoName);
		if (this.newDaoName != null) {
			m.put("newDaoName", this.newDaoName);
			m.put("tableName", this.tableName);
			if (this.props != null) {
				m.put("props", this.props.toMap());
			}
		}
		return m;
	}
	
	public final void setPropId(int id){
		this.props.id = id;
	}
	
	public final void setPropType(String type){
		this.props.type = type;
	}

	/**
	 * @return the daoName
	 */
	public final String getDaoName() {
		return daoName;
	}

	/**
	 * @param daoName the daoName to set
	 */
	public final void setDaoName(String daoName) {
		this.daoName = daoName;
	}

	/**
	 * @return the newDaoName
	 */
	public final String getNewDaoName() {
		return newDaoName;
	}

	/**
	 * @param newDaoName the newDaoName to set
	 */
	public final void setNewDaoName(String newDaoName) {
		this.newDaoName = newDaoName;
	}

	/**
	 * @return the tableName
	 */
	public final String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public final void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
