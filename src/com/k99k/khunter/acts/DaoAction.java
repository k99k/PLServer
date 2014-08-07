/**
 * 
 */
package com.k99k.khunter.acts;

import java.util.HashMap;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.DaoInterface;
import com.k99k.khunter.DaoManager;
import com.k99k.khunter.KObjManager;
import com.k99k.khunter.KObject;
import com.k99k.tools.JSON;

/**
 * DaoAction
 * @author keel
 *
 */
public class DaoAction extends Action {

	/**
	 * @param name
	 */
	public DaoAction(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#act(com.k99k.khunter.ActionMsg)
	 */
	@Override
	public ActionMsg act(ActionMsg msg) {
		
		
		return super.act(msg);
	}
	
	
	
	/**
	 * 添加DAO,并更新DAO配置
	 * @param daoName
	 * @param _class
	 * @param _dataSource
	 * @param dbType
	 * @param type
	 * @param tableName
	 * @param id
	 * @return
	 */
	public int addDao(String daoName,String _class,String _dataSource,String dbType,String type,String tableName,int id){
		if(!DaoManager.addDao(daoName,_class,_dataSource,dbType,type,tableName,id)){
			return 27;
		}
		if(!DaoManager.storeDao(daoName)){
			return 28;
		}
		return 0;
	}
	
	/**
	 * 删除dao,同时更新配置
	 * @param daoName
	 * @return
	 */
	public int removeDao(String daoName){
		if (!DaoManager.removeDao(daoName)) {
			return 29;
		}
		return 0;
	}
	
	/**
	 * 更新Dao属性,不影响Kobj,注意_class和_dataSource无法在这里更新
	 * @param daoName
	 * @param propName
	 * @param propValue
	 * @return
	 */
	public int updateDaoProps(String daoName,String propName,String propValue){
		DaoInterface dao = DaoManager.findDao(daoName);
		if (dao == null) {
			return 30;
		}
		if (propName.equals("dbType")) {
			dao.setDbType(propValue);
		}else if(propName.equals("type")){
			dao.setType(propValue);
		}else if(propName.equals("tableName")){
			dao.setTableName(propValue);
		}else if(propName.equals("id") && propValue.matches("\\d+")){
			dao.setId(Integer.parseInt(propValue));
		}
		//更新配置
		boolean re = DaoManager.storeDao(daoName);
		if (!re) {
			return 31;
		}
		return 0;
	}
	
	/**
	 * 重新载入DAO,使用配置文件的当前配置
	 * @param daoName
	 * @return
	 */
	public boolean reloadDao(String daoName){
		return DaoManager.reLoadDao(daoName);
	}
	
	
	/**
	 * 执行DAO请求
	 * @param kobjName String
	 * @param daoFunc 序号为DaoInterface中方法的顺序,如1为findOne(long id)
	 * @param jsonReq String 结构如下:
<pre>
{
	"req":"23" //id为23
	"req":{ //其他请求使用json参数方式
		...
	}
}
</pre>
	 * @return Object 执行后的结果,如果为int类型则为错误码(count的结果以String返回)
	 */
	@SuppressWarnings("unchecked")
	public final Object execDaoFunction(String kobjName,int daoFunc,String jsonReq){
		DaoInterface dao = KObjManager.findDao(kobjName);
		if (dao == null || jsonReq == null) {
			return 20;
		}
		HashMap<String,Object> reqMap = (HashMap<String,Object>)JSON.read(jsonReq);
		if (reqMap == null || (!reqMap.containsKey("req"))) {
			return 21;
		}
		Object o = reqMap.get("req");
		if (o == null) {
			return 21;
		}
		//1-15个function的处理
		
		switch (daoFunc) {
		
		case 1:
			//---------------findOne(long id)
			if (o instanceof Long) {
				long id = (Long)o;
				return dao.findOne(id);
			}else{
				return 22;
			}
			
		case 2:
			//---------------findOne(String name)
			if (o instanceof String) {
				return dao.findOne(o.toString());
			}else{
				return 22;
			}
		case 3:
			//---------------findOneMap(BasicDBObject query,BasicDBObject fields)
			if (o instanceof HashMap<?,?>) {
				HashMap<String,Object> req  = (HashMap<String,Object>)o;
				Object o1 = req.get("query");
				Object o2 = req.get("fields");
				if (o1==null || o2==null) {
					return 22;
				}
				if (o1 instanceof HashMap<?,?> && o2 instanceof HashMap<?,?>) {
					return dao.findOneMap((HashMap<String,Object>)o1,(HashMap<String,Object>)o2);
				}else{
					return 22;
				}
			}else{
				return 22;
			}
		case 4:
			//---------------findOneMap(long id)
			if (o instanceof Long) {
				long id = (Long)o;
				return dao.findOneMap(id);
			}else{
				return 22;
			}
		case 5:
			//---------------query(HashMap<String,Object> query,HashMap<String,Object> fields,
			//HashMap<String,Object> sortBy,int skip,int len,HashMap<String,Object> hint)
			if (o instanceof HashMap<?,?>) {
				HashMap<String,Object> req  = (HashMap<String,Object>)o;
				Object o1 = req.get("query");
				Object o2 = req.get("fields");
				Object o3 = req.get("sortBy");
				Object o4 = req.get("skip");
				Object o5 = req.get("len");
				Object o6 = req.get("hint");
				if (o1==null || o2==null || o3==null  || o4==null  || o5==null  || o6==null ) {
					return 22;
				}
				if (o1 instanceof HashMap<?,?> && o2 instanceof HashMap<?,?> && o3 instanceof HashMap<?,?> && o4 instanceof Long && o5 instanceof Long && o6 instanceof HashMap<?,?>) {
					return dao.query((HashMap<String,Object>)o1,(HashMap<String,Object>)o2,(HashMap<String,Object>)o3,Integer.parseInt(o4.toString()),Integer.parseInt(o5.toString()),(HashMap<String,Object>)o6);
				}else{
					return 22;
				}
			}else{
				return 22;
			}
		case 6:
			//---------------count(HashMap<String,Object> query,HashMap<String,Object> hint)
			if (o instanceof HashMap<?,?>) {
				HashMap<String,Object> req  = (HashMap<String,Object>)o;
				Object o1 = req.get("query");
				Object o2 = req.get("hint");
				if (o1==null || o2==null ) {
					return 22;
				}
				if (o1 instanceof HashMap<?,?> && o2 instanceof HashMap<?,?> ) {
					//注意返回String的形式，与错误码区分
					return dao.count((HashMap<String,Object>)o1,(HashMap<String,Object>)o2)+"";
				}else{
					return 22;
				}
			}else{
				return 22;
			}
		case 7:
			//---------------count(HashMap<String,Object> query)
			if (o instanceof HashMap<?,?>) {
				HashMap<String,Object> req  = (HashMap<String,Object>)o;
				Object o1 = req.get("query");
				if (o1==null ) {
					return 22;
				}
				if (o1 instanceof HashMap<?,?> ) {
					//注意返回String的形式，与错误码区分
					return dao.count((HashMap<String,Object>)o1)+"";
				}else{
					return 22;
				}
			}else{
				return 22;
			}
		case 8:
			//---------------add(KObject kObj)
			if (o instanceof HashMap<?,?>) {
				HashMap<String,Object> req  = (HashMap<String,Object>)o;
				KObject kobj = new KObject(req);
				return dao.add(kobj);
			}else{
				return 22;
			}
		case 9:
			//---------------save(KObject kObj)
			if (o instanceof HashMap<?,?>) {
				HashMap<String,Object> req  = (HashMap<String,Object>)o;
				KObject kobj = new KObject(req);
				return dao.save(kobj);
			}else{
				return 22;
			}
		case 10:
			//---------------updateOne(long id,KObject newObj)
			if (o instanceof HashMap<?,?>) {
				HashMap<String,Object> req  = (HashMap<String,Object>)o;
				Object o1 = req.get("id");
				Object o2 = req.get("newObj");
				if (o1==null || o2==null ) {
					return 22;
				}
				if (o1 instanceof Long && o2 instanceof HashMap<?,?> ) {
					//注意返回String的形式，与错误码区分
					KObject kobj = new KObject((HashMap<String,Object>)o2);
					return dao.updateOne((Long)o1, kobj);
				}else{
					return 22;
				}
			}else{
				return 22;
			}
		case 11:
			//---------------updateOne(HashMap<String,Object> query,HashMap<String,Object> set)
			if (o instanceof HashMap<?,?>) {
				HashMap<String,Object> req  = (HashMap<String,Object>)o;
				Object o1 = req.get("query");
				Object o2 = req.get("set");
				if (o1==null || o2==null ) {
					return 22;
				}
				if (o1 instanceof HashMap<?,?> && o2 instanceof HashMap<?,?> ) {
					//注意返回String的形式，与错误码区分
					return dao.updateOne((HashMap<String,Object>)o1,(HashMap<String,Object>)o2);
				}else{
					return 22;
				}
			}else{
				return 22;
			}
		case 12:
			//---------------update(HashMap<String,Object> query,HashMap<String,Object> set,boolean upset,boolean multi)
			if (o instanceof HashMap<?,?>) {
				HashMap<String,Object> req  = (HashMap<String,Object>)o;
				Object o1 = req.get("query");
				Object o2 = req.get("set");
				Object o3 = req.get("upset");
				Object o4 = req.get("multi");
				if (o1==null || o2==null || o3==null || o4==null ) {
					return 22;
				}
				if (o1 instanceof HashMap<?,?> && o2 instanceof HashMap<?,?> && o3 instanceof Boolean && o4 instanceof Boolean ) {
					//注意返回String的形式，与错误码区分
					return dao.update((HashMap<String,Object>)o1,(HashMap<String,Object>)o2,(Boolean)o3,(Boolean)o4);
				}else{
					return 22;
				}
			}else{
				return 22;
			}
		case 13:
			//---------------deleteOne(long id)
			if (o instanceof Long) {
				long id = (Long)o;
				return dao.deleteOne(id);
			}else{
				return 22;
			}
		case 14:
			//---------------delete(HashMap<String,Object> query,boolean multi)
			if (o instanceof HashMap<?,?>) {
				HashMap<String,Object> req  = (HashMap<String,Object>)o;
				Object o1 = req.get("query");
				Object o2 = req.get("multi");
				if (o1==null || o2==null ) {
					return 22;
				}
				if (o1 instanceof HashMap<?,?> && o2 instanceof Boolean ) {
					//注意返回String的形式，与错误码区分
					return dao.delete((HashMap<String,Object>)o1,(Boolean)o2);
				}else{
					return 22;
				}
			}else{
				return 22;
			}
		case 15:
			//---------------deleteForever(HashMap<String,Object> query)
			if (o instanceof HashMap<?,?>) {
				HashMap<String,Object> req  = (HashMap<String,Object>)o;
				Object o1 = req.get("query");
				if (o1==null ) {
					return 22;
				}
				if (o1 instanceof HashMap<?,?> ) {
					//注意返回String的形式，与错误码区分
					return dao.deleteForever((HashMap<String,Object>)o1);
				}else{
					return 22;
				}
			}else{
				return 22;
			}
		default:
			return 23;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#exit()
	 */
	@Override
	public void exit() {
	}

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#getIniPath()
	 */
	@Override
	public String getIniPath() {
		return "kconfig.json";
	}

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#init()
	 */
	@Override
	public void init() {
	}
	
	

}
