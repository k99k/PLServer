/**
 * 
 */
package com.k99k.khunter.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.k99k.khunter.DataSourceInterface;
import com.k99k.khunter.HTUser;
import com.k99k.khunter.HTUserDaoInterface;
import com.k99k.khunter.MongoDao;
import com.mongodb.BasicDBObject;

/**
 * Mongodb下的UserDao
 * @author keel
 *
 */
public class MongoUserDao extends MongoDao implements HTUserDaoInterface{

	/**
	 * @param daoName 数据表名
	 * @param dataSource DataSourceInterface
	 */
	public MongoUserDao(String daoName, DataSourceInterface dataSource) {
		super(daoName,dataSource);
	}
	
	//FIXME MongoUserDao待测试,添加属性等特殊操作未实现
	
	
	/**
	 * 注册,即添加用户
	 * TODO 后期将采用预注册机制
	 * @param user HTUser
	 * @return 是否添加成功
	 */
	public boolean addUser(HTUser user){
		return this.add(user);
	}
	
	/**
	 * 根据id查找用户
	 * @param id
	 * @return
	 */
	public HTUser findUser(long id){
		Map<String, Object> m = this.findOneMap(id);
		if (m != null) {
			return new HTUser(m);
		}
		return null;
	}
	
	
	/**
	 * 按IMEI号获取用户
	 * @param imei
	 * @return
	 */
	public HTUser findUserByImei(String imei){
		Map<String, Object> m = this.findOneMap(new BasicDBObject("imei",imei));
		if (m != null) {
			return new HTUser(m);
		}
		return null;
	}
	
	
	
	/**
	 * 按条件查找批量用户，Map中包含必要的条件参数,以json方式可定义如下:
	 <pre>
	 {
	 	query:{gold:100,hp:{$gt:10},camp:"mycamp"}, 或 query:"where gold=100 and hp>10 and camp = 'mycamp'"
	 	feilds:{name:1,hp:1,camp:1}, 或 feilds:" name,hp,camp from htUser"
	 	sortby:{hp:-1},或sortby:" order by hp desc"
	 	skip:5, 或page:4
	 	len:10, 或top 10
	 } 
	 </pre>
	 * TODO 可增加按页查询方式
	 * @param paras
	 * @return List<Map<String,Object>>
	 */
	@SuppressWarnings("unchecked")
	public List<HashMap<String,Object>> findUserList(Map<String, Object> paras){
		Object o = paras.get("query");
		BasicDBObject query = (o == null)?null:new BasicDBObject((Map)o);
		Object o1 = paras.get("feilds");
		BasicDBObject feilds = (o1 == null)?null:new BasicDBObject((Map)o1);
		Object o2 = paras.get("hint");
		BasicDBObject hint = (o2 == null)?null:new BasicDBObject((Map)o2);
		Object o3 = paras.get("sortby");
		BasicDBObject sortby = (o3 == null)?null:new BasicDBObject((Map)o3);
		return this.query(query, feilds, sortby, (paras.get("skip") == null)?0:Integer.parseInt(paras.get("skip").toString()), (paras.get("len") == null)?0:Integer.parseInt(paras.get("len").toString()),hint);
	}
	
	
	/**
	 * 更新单个用户属性
	 * @param user
	 * @return
	 */
	public boolean updateOneUser(HTUser user){
		return this.updateOne(user.getId(), user);
	}
	
	/**
	 * 更新单个用户属性
	 * @param paras
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean updateOneUser(Map<String, Object> paras){
		Object o = paras.get("query");
		BasicDBObject query = (o == null)?null:new BasicDBObject((Map)o);
		Object o1 = paras.get("set");
		BasicDBObject set = (o1 == null)?null:new BasicDBObject((Map)o1);
		return this.updateOne(query, set);
	}
	
	/**
	 * 批量更新用户属性
	 * @param paras
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean updateUser(Map<String, Object> paras){
		Object o = paras.get("query");
		BasicDBObject query = (o == null)?null:new BasicDBObject((Map)o);
		Object o1 = paras.get("set");
		BasicDBObject set = (o1 == null)?null:new BasicDBObject((Map)o1);
		return this.update(query, set,false,true);
	}
	
	public boolean deleteUser(Map<String, Object> query,boolean multi){
		if (query == null) {
			return false;
		}
		return this.delete(new BasicDBObject(query), multi);
		
	}
	
	/**
	 * 统计用户数
	 * @param query Map
	 * @return
	 */
	public int countUser(Map<String, Object> query){
		if (query == null) {
			return this.count(null);
		}
		return this.count(new BasicDBObject(query));
	}

}
