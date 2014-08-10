/**
 * 
 */
package com.k99k.plserver;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.k99k.khunter.DaoInterface;
import com.k99k.khunter.DaoManager;
import com.k99k.khunter.DataSourceInterface;
import com.k99k.khunter.MongoDao;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author Keel
 *
 */
public class StaticDao extends MongoDao {

	/**
	 * @param daoName
	 * @param dataSource
	 */
	public StaticDao(String daoName, DataSourceInterface dataSource) {
		super(daoName, dataSource);
	}
	
	static final Logger log = Logger.getLogger(StaticDao.class);
	
	static DaoInterface dsUserDao;
	static DaoInterface dsLogDao;
	static DaoInterface dsTaskDao;
	static DaoInterface dsVersionDao;
	
	public static final void initS(){
		dsUserDao = DaoManager.findDao("dsUserDao");
		dsLogDao = DaoManager.findDao("dsLogDao");
		dsTaskDao = DaoManager.findDao("dsTaskDao");
		dsVersionDao = DaoManager.findDao("dsVersionDao");
	}
	
	/**
	 * {_id:-1}
	 */
	public static final BasicDBObject prop_id_desc = new BasicDBObject("_id",-1);

	/**
	 * {state:{$gte:0}}
	 */
	public static final BasicDBObject prop_state_gte0 = new BasicDBObject("state",new BasicDBObject("$gte",0));

	/**
	 * 处理task表生成cache
	 * @param uidMap
	 * @param gidMap
	 * @param tagMap
	 * @param levelMap
	 */
	public static final void cacheTasks(HashMap<String,ArrayList<Long>>[] maps){
		for (int i = 0; i < maps.length; i++) {
			maps[i].clear();
		}
		long cTime = System.currentTimeMillis();
		BasicDBObject q = new BasicDBObject("valTime",new BasicDBObject("$gt",cTime)).append("state",new BasicDBObject("$gte",0));
		DBCursor cur = dsTaskDao.getColl().find(q);
		while (cur.hasNext()) {
			DBObject t = cur.next();
			int type = (int) t.get("type");
			long tid = (long) t.get("_id");
			TaskAction.setCache(maps[type],t.get("value"),tid,type);
		}
	}
	
	
	
	
}
