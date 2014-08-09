/**
 * 
 */
package com.k99k.plserver;

import org.apache.log4j.Logger;

import com.k99k.khunter.DaoInterface;
import com.k99k.khunter.DaoManager;
import com.k99k.khunter.DataSourceInterface;
import com.k99k.khunter.MongoDao;
import com.mongodb.BasicDBObject;

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


	
}
