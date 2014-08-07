package com.k99k.khunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 基于mongoDB的基础DAO
 * @author keel
 *
 */
public class MongoDao implements DaoInterface{
	
	
	
	static final Logger log = Logger.getLogger(MongoDao.class);
	
	/**
	 * id生成器,默认初始值为1,增量为1
	 */
	private IDManager idm;// = new IDManager(1,1);
	
	/**
	 * 数据库的表名
	 */
	private String tableName;
	
	/**
	 * Dao的标识name
	 */
	private String name;
	
	/**
	 * 用于标识多个实例
	 */
	private int id;
	
	/**
	 * 数据源
	 */
	private MongoConn dataSource;
	
	/**
	 * 数据库类型
	 */
	private String dbType;
	
	/**
	 * 载入类型
	 */
	private String type;
	
	/**
	 * dao对应的json配置
	 */
	private HashMap<String,Object> jsonConfig = new HashMap<String,Object>();
	
	/**
	 * 创建建时初始化id生成器
	 * @param daoName Dao名称
	 */
	public MongoDao(String daoName,DataSourceInterface dataSource) {
		super();
		this.name = daoName;
		this.dataSource = (MongoConn) dataSource;
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone(){
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			log.error("clone failed.",e);
			return null;
		}
	}



	/* (non-Javadoc)
	 * @see com.k99k.khunter.DaoInterface#init()
	 */
	public boolean init(){
		try {
			idm = new IDManager(1,1);
			idm.setId(initIDM(tableName));
			//log.info("========tableName:"+tableName + " idm id:"+ idm.getCurrentId());
		} catch (Exception e) {
			log.error("init error!", e);
			return false;
		}
		jsonConfig.put("_class", this.getClass().getName());
		jsonConfig.put("_dataSource", this.dataSource.getName());
		jsonConfig.put("tableName", this.tableName);
		jsonConfig.put("dbType", this.dbType);
		jsonConfig.put("type", this.type);
		jsonConfig.put("id", this.id);
		return true;
	}
	
	/**
	 * 获取一个DBCollection
	 * @return DBCollection
	 */
	public DBCollection getColl(){
		return this.dataSource.getColl(tableName);
	}
	

	/**
	 * 按ID查找单个对象
	 * @param id long
	 * @return 未找到返回null
	 */
	public KObject findOne(long id){
		Map<String, Object> m = this.findOneMap(id);
		if (m != null) {
			return  new  KObject(m);
		}
		return null;
		
	}
	
	/**
	 * 按名称查找KObject对象
	 * @param name KObject的name
	 * @return 未找到则返回null
	 */
	@Override
	public KObject findOne(String name){
		Map<String, Object> m = this.findOneMap(new BasicDBObject("name", name),null);
		if (m != null) {
			return  new  KObject(m);
		}
		return null;
	}
	
	/**
	 * 按条件查询单个对象
	 * @param query
	 * @return
	 */
	public KObject findOne(HashMap<String,Object> query){
		Map<String, Object> m = this.findOneMap(query);
		if (m != null) {
			return  new  KObject(m);
		}
		return null;
	}
	
	/**
	 * 查找单个对象
	 * @param query HashMap<String,Object>
	 * @return 未找到返回null
	 */
	public HashMap<String,Object> findOneMap(HashMap<String,Object> query){
		BasicDBObject q = (query==null)?prop_empty:(query instanceof BasicDBObject)?(BasicDBObject)query:new BasicDBObject(query);
		
		return this.findOneMap(q, null);
	}
	
	/**
	 * 查找单个对象
	 * @param query
	 * @param fields
	 * @return 未找则返回null
	 */
	@Override
	@SuppressWarnings("unchecked")
	public HashMap<String,Object> findOneMap(HashMap<String,Object> query,HashMap<String, Object> fields){
		try {
			DBObject q = (query==null)?prop_empty:((query instanceof DBObject)?(DBObject)query:new BasicDBObject(query));
			DBObject f = (fields==null)?null:((query instanceof DBObject)?(DBObject)fields:new BasicDBObject(fields));
			//coll = checkColl(coll);
			DBCollection coll = this.dataSource.getColl(tableName);
			Object o = coll.findOne(q,f);
			if (o==null) {
				return null;
			}
	        return (HashMap<String,Object>)o;
		} catch (Exception e) {
			log.error("findOneMap error!", e);
			return null;
		}
	}
	
	/**
	 * 查找Map形式对象
	 * @param id long
	 * @return Map形式,未找到返回null
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> findOneMap(long id){
		try {
			//coll = checkColl(coll);
			DBCollection coll = this.dataSource.getColl(tableName);
			DBObject o = coll.findOne(id);
			if (o != null) {
				return (HashMap<String, Object>)o;
			}
			return null;
		} catch (Exception e) {
			log.error("findOneMap error!", e);
			return null;
		}
	}

	/**
	 * {_id:-1}
	 */
	public static final BasicDBObject prop_id_desc = new BasicDBObject("_id",-1);
	/**
	 * {_id:1}
	 */
	public static final BasicDBObject prop_id = new BasicDBObject("_id",1);
	/**
	 * {state:0}
	 */
	public static final BasicDBObject prop_state_0 = new BasicDBObject("state",0);
	/**
	 * {state:{$gte:0}}
	 */
	public static final BasicDBObject prop_state_normal = new BasicDBObject("state",new BasicDBObject("$gte",0));
	
	/**
	 * {state:{$gte:0},cate:0}
	 */
	public static final BasicDBObject prop_topic_pub = new BasicDBObject("state",new BasicDBObject("$gte",0)).append("cate", 0);
	/**
	 * {state:{$gte:0},cate:1}
	 */
	public static final BasicDBObject prop_topic_company = new BasicDBObject("state",new BasicDBObject("$gte",0)).append("cate", 1);
	/**
	 * {state:{$gte:0},cate:2}
	 */
	public static final BasicDBObject prop_topic_doc = new BasicDBObject("state",new BasicDBObject("$gte",0)).append("cate", 2);

	/**
	 * {}
	 */
	public static final BasicDBObject prop_empty = new BasicDBObject();
	/**
	 * {$set:{state:-1}}
	 */
	public static final BasicDBObject prop_state_del_set = new BasicDBObject("$set",new BasicDBObject("state",-1));


	@Override
	public boolean checkName(String name) {
		try {
			DBCollection coll = this.dataSource.getColl(tableName);
			DBCursor cur = coll.find(new BasicDBObject("name", name),prop_id).limit(1);
			if (cur.hasNext()) {
				return true;
			}
		} catch (Exception e) {
			log.error("checkName error!", e);
			return false;
		}
		return false;
	}

	@Override
	public boolean checkId(long id) {
		try {
			DBCollection coll = this.dataSource.getColl(tableName);
			DBCursor cur = coll.find(new BasicDBObject("_id", id),prop_id).limit(1);
			if (cur.hasNext()) {
				return true;
			}
		} catch (Exception e) {
			log.error("checkId error!", e);
			return false;
		}
		return false;
	}
	
	@Override
	public boolean checkExist(String paraName,Object obj) {
		try {
			DBCollection coll = this.dataSource.getColl(tableName);
			DBCursor cur = coll.find(new BasicDBObject(paraName, obj),prop_id).limit(1);
			if (cur.hasNext()) {
				return true;
			}
		} catch (Exception e) {
			log.error("checkExist error!", e);
			return false;
		}
		return false;
	}
	
	
	@Override
	public long checkExist(HashMap<String,Object> query) {
		try {
			DBCollection coll = this.dataSource.getColl(tableName);
			DBCursor cur = coll.find(new BasicDBObject(query),prop_id).limit(1);
			if (cur.hasNext()) {
				return (Long)cur.next().get("_id");
			}
		} catch (Exception e) {
			log.error("checkId error!", e);
			return -1;
		}
		return -1;
	}


	/**
	 * 通用的查找过程
	 * @param query 必须有,为null则为默认查询
	 * @param fields 全部则为null
	 * @param sortBy 无则为null
	 * @param skip 无则为0
	 * @param len 无则为0
	 * @param hint 无则为null
	 * @return ArrayList<Map<String,Object>>
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String,Object>> query(HashMap<String,Object> query,HashMap<String,Object> fields,HashMap<String,Object> sortBy,int skip,int len,HashMap<String,Object> hint){
		try {
			BasicDBObject q = (query==null)?prop_empty:(query instanceof BasicDBObject)?(BasicDBObject)query:new BasicDBObject(query);
			BasicDBObject field = (fields==null)?null:new BasicDBObject(fields);
			BasicDBObject sort = (sortBy==null)?null:new BasicDBObject(sortBy);
			BasicDBObject hin = (hint==null)?null:new BasicDBObject(hint);
			
			int initSize = 24;
			if (len > 0) {
				initSize = len;
			}
			ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>(initSize);
			
				//coll = checkColl(coll);
			DBCollection coll = this.dataSource.getColl(tableName);
			DBCursor cur = null;
			if (sortBy != null) {
				cur = coll.find(q, field).sort(sort).skip(skip).limit(len).hint(hin);
			} else {
				cur = coll.find(q, field).skip(skip).limit(len).hint(hin);
			}
	        while(cur.hasNext()) {
	        	HashMap<String, Object> m = (HashMap<String, Object>) cur.next();
	        	list.add(m);
	        }
	        return list;
		} catch (Exception e) {
			log.error("query error!", e);
			return null;
		}
		
	}
	/**
	 * 通用的查找过程
	 * @param query 必须有,为null则为默认查询
	 * @param fields 全部则为null
	 * @param sortBy 无则为null
	 * @param skip 无则为0
	 * @param len 无则为0
	 * @param hint 无则为null
	 * @return ArrayList<KObject>
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<KObject> queryKObj(HashMap<String,Object> query,HashMap<String,Object> fields,HashMap<String,Object> sortBy,int skip,int len,HashMap<String,Object> hint){
		try {
			BasicDBObject q = (query==null)?prop_empty:(query instanceof BasicDBObject)?(BasicDBObject)query:new BasicDBObject(query);
			BasicDBObject field = (fields==null)?null:new BasicDBObject(fields);
			BasicDBObject sort = (sortBy==null)?null:new BasicDBObject(sortBy);
			BasicDBObject hin = (hint==null)?null:new BasicDBObject(hint);
			
			int initSize = 24;
			if (len > 0) {
				initSize = len;
			}
			ArrayList<KObject> list = new ArrayList<KObject>(initSize);
			
				//coll = checkColl(coll);
			DBCollection coll = this.dataSource.getColl(tableName);
			DBCursor cur = null;
			if (sortBy != null) {
				cur = coll.find(q, field).sort(sort).skip(skip).limit(len).hint(hin);
			} else {
				cur = coll.find(q, field).skip(skip).limit(len).hint(hin);
			}
	        while(cur.hasNext()) {
	        	HashMap<String, Object> m = (HashMap<String, Object>) cur.next();
	        	list.add(new KObject(m));
	        }
	        return list;
		} catch (Exception e) {
			log.error("query error!", e);
			return null;
		}
		
	}
	
	/**
	 * 按页查找,注意结果第一项为总计数,直接用getId()可取到
	 * @param page 页码
	 * @param pageSize 每页项目数
	 * @param query
	 * @param fields
	 * @param sortBy
	 * @param hint
	 * @return ArrayList<KObject> 第一项为总计数,直接用getId()可取到
	 */
	public final ArrayList<KObject> queryByPage(int page,int pageSize,HashMap<String,Object> query,HashMap<String,Object> fields,HashMap<String,Object> sortBy,HashMap<String,Object> hint){
		int sum = this.count(query);
		int skip = pagedSkip(page,sum,pageSize);
		if (skip < 0) {
			return null;
		}
		ArrayList<KObject> list = this.queryKObj(query, fields, sortBy, skip, pageSize, hint);
		if (list == null) {
			return null;
		}
		KObject cc = new KObject();
		cc.setId(sum);
		list.add(0, cc);
		return list;
	}
	
	/**
	 * 按条件查询数量
	 * @param query 必须有
	 * @param hint 无则为null
	 * @return 数量
	 */
	public int count(HashMap<String,Object> query,HashMap<String,Object> hint){
		
		try {
			BasicDBObject q = (query==null)?prop_empty:(query instanceof BasicDBObject)?(BasicDBObject)query:new BasicDBObject(query);
			BasicDBObject hin = (hint==null)?null:new BasicDBObject(hint);
			//coll = checkColl(coll);
			DBCollection coll = this.dataSource.getColl(tableName);
			return coll.find(q).hint(hin).count();
		} catch (Exception e) {
			log.error("count error!", e);
			return 0;
		}
		
	}
	
	/**
	 * 按条件查询数量
	 * @param query 必须有
	 * @return 数量
	 */
	public int count(HashMap<String,Object> query){
		
		try {
			BasicDBObject q = (query==null)?prop_empty:(query instanceof BasicDBObject)?(BasicDBObject)query:new BasicDBObject(query);
			
			DBCollection coll = this.dataSource.getColl(tableName);
			return coll.find(q).count();
		} catch (Exception e) {
			log.error("count error!", e);
			return 0;
		}
		
	}
	
	/**
	 * 创建新对象,自动生成新ID
	 * @param kObj KObject
	 * @return 
	 */
	public boolean add(KObject kObj){
		try {
			//coll = checkColl(coll);
			DBCollection coll = this.dataSource.getColl(tableName);
			kObj.setId(idm.nextId());
			MongoWrapper w = new MongoWrapper(kObj);
			coll.insert(w);
		} catch (Exception e) {
			log.error("add error!", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 创建或更新对象,注意此方法不自动生成ID
	 * @param kObj KObject
	 * @return
	 */
	public boolean save(KObject kObj){
		try {
			//coll = checkColl(coll);
			DBCollection coll = this.dataSource.getColl(tableName);
			coll.save(new MongoWrapper(kObj));
		} catch (Exception e) {
			log.error("save error!", e);
			return false;
		}
		return true;
	}
	
	
	/**
	 * 更新对象
	 * @param id long
	 * @param newObj KObject
	 * @return
	 */
	public boolean updateOne(long id,KObject newObj) {
		try {
			//coll = checkColl(coll);
			DBCollection coll = this.dataSource.getColl(tableName);
			coll.update(new BasicDBObject("_id",id),new MongoWrapper(newObj));
		} catch (Exception e) {
			log.error("update error!", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 更新单个对象
	 * @param query BasicDBObject
	 * @param newObj KObject
	 * @param upset 如果不存在是否新建
	 * @param multi 是否更新多个
	 * @return 是否完成更新
	 */
	public boolean updateOne(HashMap<String,Object> query,KObject newObj,boolean upset) {
		try {
			DBObject q = (query instanceof DBObject)?(DBObject)query:new BasicDBObject(query);
			//coll = checkColl(coll);
			DBCollection coll = this.dataSource.getColl(tableName);
			coll.update(q,new MongoWrapper(newObj),upset,false);
		} catch (Exception e) {
			log.error("update error!", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 更新单个对象,注意这里未处理KObject的默认字段,upsert为false
	 * @param query BasicDBObject
	 * @param set BasicDBObject
	 * @return
	 */
	public boolean updateOne(HashMap<String,Object> query,HashMap<String,Object> set) {
		try {
			DBObject q = (query instanceof DBObject)?(DBObject)query:new BasicDBObject(query);
			BasicDBObject s = new BasicDBObject(set);
			//coll = checkColl(coll);
			DBCollection coll = this.dataSource.getColl(tableName);
			coll.update(q,s,false,false);
		} catch (Exception e) {
			log.error("update error!", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 更新对象,注意这里未处理KObject的默认字段
	 * @param query BasicDBObject
	 * @param set BasicDBObject
	 * @param upset 如果不存在是否新建
	 * @param multi 是否更新多个
	 * @return 更新是否完成
	 */
	public boolean update(HashMap<String,Object> query,HashMap<String,Object> set,boolean upset,boolean multi) {
		try {
			DBObject q = (query instanceof DBObject)?(DBObject)query:new BasicDBObject(query);
			BasicDBObject s = new BasicDBObject(set);
			//coll = checkColl(coll);
			DBCollection coll = this.dataSource.getColl(tableName);
			coll.update(q,s,upset,multi);
		} catch (Exception e) {
			log.error("update error!", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 标记删除,即将state置为-1,
	 * @param id
	 * @return Object 被删除对象不存在则返回null
	 */
	public Object deleteOne(long id){
		try {
			//coll = checkColl(coll);
			DBCollection coll = this.dataSource.getColl(tableName);
			DBObject re = coll.findAndModify(new BasicDBObject("_id",id), prop_state_del_set);
			return re;
			//coll.update(new BasicDBObject("_id",id),prop_state_del_set,false,false);
		} catch (Exception e) {
			log.error("delete error!", e);
			return null;
		}
	}
	
	/**
	 * 标记删除,即将state置为-1,
	 * @param query
	 * @return Object 被删除对象不存在则返回null
	 */
	public Object deleteOne(HashMap<String,Object> query){
		try {
			//coll = checkColl(coll);
			DBObject q = (query instanceof DBObject)?(DBObject)query:new BasicDBObject(query);
			DBCollection coll = this.dataSource.getColl(tableName);
			DBObject re = coll.findAndModify(q, prop_state_del_set);
			return re;
			//coll.update(new BasicDBObject("_id",id),prop_state_del_set,false,false);
		} catch (Exception e) {
			log.error("delete error!", e);
			return null;
		}
	}
	
	
	/**
	 * 标记删除,即将state置为-1,支持多项删除,所以结果与deleteOne方法不同
	 * @param query
	 * @param multi 是否批量
	 * @return 返回false不能确认删除对象是否存在,与deleteOne方法不同
	 */
	public boolean delete(HashMap<String,Object> query,boolean multi){
		try {
			DBObject q = (query instanceof DBObject)?(DBObject)query:new BasicDBObject(query);
			
			DBCollection coll = this.dataSource.getColl(tableName);
			coll.update(q,prop_state_del_set,false,multi);
		} catch (Exception e) {
			log.error("delete error!", e);
			return false;
		}
		return true;
	}
	
	
	/**
	 * 从数据库中按条件批量彻底删除
	 * @param id
	 * @return
	 */
	public boolean deleteForever(HashMap<String,Object> query){
		try {
			DBObject q = (query instanceof DBObject)?(DBObject)query:new BasicDBObject(query);
			//coll = checkColl(coll);
			DBCollection coll = this.dataSource.getColl(tableName);
			coll.remove(q);
		} catch (Exception e) {
			log.error("deleteForever error!", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 按id从数据库中彻底删除
	 * @param id
	 * @return
	 */
	public boolean deleteForever(long id){
		try {
			//coll = checkColl(coll);
			DBCollection coll = this.dataSource.getColl(tableName);
			coll.remove(new BasicDBObject("_id",id));
		} catch (Exception e) {
			log.error("deleteForever error!", e);
			return false;
		}
		return true;
	}
	/**
	 * 初始化id生成器,注意这里使用的_id
	 * @param tableName
	 * @return 当前最大id值  long
	 */
	long initIDM(String tableName){
		DBCollection coll = this.dataSource.getColl(tableName);
		DBCursor cur = coll.find(prop_empty,prop_id).sort(prop_id_desc).limit(1);
		if (cur.hasNext()) {
			DBObject o = cur.next();
			return Long.parseLong(o.get("_id").toString());
		}else{
			return 1;
		}
	}
	
	/*
	private final DBCollection checkColl(DBCollection coll){
		if (coll == null || (!coll.getName().equals(tableName))) {
			return this.dataSource.getColl(tableName);
		}else{
			return coll;
		}
	}
	*/

	 @Override
	public boolean updateIndex(KObjIndex ki) {
		try {
			DBCollection coll = this.dataSource.getColl(tableName);
			coll.ensureIndex(new BasicDBObject(ki.getCol(), (ki.isAsc())?1:-1),ki.getCol(), ki.isUnique());
		} catch (Exception e) {
			log.error("updateIndex error!", e);
			return false;
		}
		return true;
	}



	@Override
	public boolean removeIndex(KObjIndex ki) {
		try {
			DBCollection coll = this.dataSource.getColl(tableName);
			coll.dropIndex(new BasicDBObject(ki.getCol(), (ki.isAsc())?1:-1));
		} catch (Exception e) {
			log.error("removeIndex error!", e);
			return false;
		}
		return true;
	}

	/**
	 * json输出ArrayList<KObject>
	 * @param list ArrayList<KObject>
	 * @return String
	 */
	public static final String writeKObjList(ArrayList<KObject> list){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (list == null || list.isEmpty()) {
			sb.append("]");
			return sb.toString();
		}
		for (Iterator<KObject> it = list.iterator(); it.hasNext();) {
			KObject kobj = it.next();
			sb.append(kobj.toString());
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * 由页码,总数,每页项目数计算skip数
	 * @param page
	 * @param sum
	 * @param pageSize
	 * @return
	 */
	public static int pagedSkip(int page,int sum,int pageSize){
		if (sum<=0 || pageSize<=0) {
			return -1;
		}
		int pageCount = sum/pageSize;
		int mod = sum%pageSize;
		if (mod != 0) {
			pageCount++;
		}
		if (page<=0) {
			page = 1;
		}
		if (page > pageCount) {
			page = pageCount;
		}
		int skip = pageSize*(page-1);
		
		return skip;
	}
	
	public static int[] pagedSkipForSlice(int page,int sum,int pageSize){
		if (sum<=0 || pageSize<=0) {
			return null;
		}
		int[] ii = new int[2];
		int pageCount = sum/pageSize;
		int mod = sum%pageSize;
		if (mod != 0) {
			pageCount++;
		}
		
		if (page<=0) {
			page = 1;
		}
		if (page >= pageCount) {
			//最后一页
			ii[0] = 0-sum;
			ii[1] = (mod==0)?pageSize:mod;
			return ii;
		}
		
		ii[0] = 0-pageSize*page;
		ii[1] = pageSize;
		return ii;
	}

	/**
	 * @return the dataSource
	 */
	public final DataSourceInterface getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public final void setDataSource(DataSourceInterface dataSource) {
		this.dataSource = (MongoConn) dataSource;
	}

	/*public static void main(String[] args) throws Exception {

	        // connect to the local database server
	        Mongo m = new Mongo();

	        // get handle to "mydb"
	        DB db = m.getDB( "mydb" );
	    
	        // Authenticate - optional
	        // boolean auth = db.authenticate("foo", "bar");


	        // get a list of the collections in this database and print them out
	        Set<String> colls = db.getCollectionNames();
	        for (String s : colls) {
	            System.out.println(s);
	        }  

	        // get a collection object to work with
	        DBCollection coll = db.getCollection("testCollection");
	        
	        // drop all the data in it
	        coll.drop();


	        // make a document and insert it
	        BasicDBObject doc = new BasicDBObject();

	        doc.put("name", "MongoDB");
	        doc.put("type", "database");
	        doc.put("count", 1);

	        BasicDBObject info = new BasicDBObject();

	        info.put("x", 203);
	        info.put("y", 102);

	        doc.put("info", info);

	        coll.insert(doc);

	        // get it (since it's the only one in there since we dropped the rest earlier on)
	        DBObject myDoc = coll.findOne();
	        System.out.println(myDoc);

	        // now, lets add lots of little documents to the collection so we can explore queries and cursors
	        for (int i=0; i < 100; i++) {
	            coll.insert(new BasicDBObject().append("i", i));
	        }
	        System.out.println("total # of documents after inserting 100 small ones (should be 101) " + coll.getCount());

	        //  lets get all the documents in the collection and print them out
	        DBCursor cur = coll.find();
	        while(cur.hasNext()) {
	            System.out.println(cur.next());
	        }

	        //  now use a query to get 1 document out
	        BasicDBObject query = new BasicDBObject();
	        query.put("i", 71);
	        cur = coll.find(query);

	        while(cur.hasNext()) {
	            System.out.println(cur.next());
	        }

	        //  now use a range query to get a larger subset
	        query = new BasicDBObject();
	        query.put("i", new BasicDBObject("$gt", 50));  // i.e. find all where i > 50
	        cur = coll.find(query);

	        while(cur.hasNext()) {
	            System.out.println(cur.next());
	        }

	        // range query with multiple contstraings
	        query = new BasicDBObject();
	        query.put("i", new BasicDBObject("$gt", 20).append("$lte", 30));  // i.e.   20 < i <= 30
	        cur = coll.find(query);

	        while(cur.hasNext()) {
	            System.out.println(cur.next());
	        }

	        // create an index on the "i" field
	        coll.createIndex(new BasicDBObject("i", 1));  // create index on "i", ascending


	        //  list the indexes on the collection
	        List<DBObject> list = coll.getIndexInfo();
	        for (DBObject o : list) {
	            System.out.println(o);
	        }

	        // See if the last operation had an error
	        System.out.println("Last error : " + db.getLastError());

	        // see if any previous operation had an error
	        System.out.println("Previous error : " + db.getPreviousError());

	        // force an error
	        db.forceError();

	        // See if the last operation had an error
	        System.out.println("Last error : " + db.getLastError());

	        db.resetError();
	        m.close();
	    }*/

	@Override
	public String getName() {
		return this.name;
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

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	@Override
	public int getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the dbType
	 */
	public final String getDbType() {
		return dbType;
	}

	/**
	 * @param dbType the dbType to set
	 */
	public final void setDbType(String dbType) {
		this.dbType = dbType;
	}

	/**
	 * @return the type
	 */
	public final String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public final void setType(String type) {
		this.type = type;
	}

	

	@Override
	public HashMap<String, Object> toJsonConfig() {
		return this.jsonConfig;
	}



	/**
	 * @return the idm
	 */
	public final IDManager getIdm() {
		return idm;
	}



	
}
