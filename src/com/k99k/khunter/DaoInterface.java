package com.k99k.khunter;

import java.util.ArrayList;
import java.util.HashMap;

import com.mongodb.DBCollection;


/**
 * DAO接口
 * @author keel
 *
 */
public interface DaoInterface extends Cloneable{
	
	/**
	 * 获取ID管理器
	 * @return
	 */
	public IDManager getIdm();
	
	/**
	 * 创建一个DBCollection,注意coll操作将无法支持KObject默认字段,务必谨慎使用,建议仅在读操作中使用
	 * @return DBCollection
	 */
	public DBCollection getColl();
	
	/**
	 * 增加或更新一个索引
	 * @param ki
	 * @return
	 */
	public boolean updateIndex(KObjIndex ki);
	
	/**
	 * 删除一个索引
	 * @param ki
	 * @return
	 */
	public boolean removeIndex(KObjIndex ki);
	
	/**
	 * 按ID查找单个对象,顺序号为1
	 * @param id long
	 * @return 未找到返回null
	 */
	public KObject findOne(long id);
	
	/**
	 * 按名称查找KObject对象,顺序号为2
	 * @param name KObject的name
	 * @return
	 */
	public KObject findOne(String name);
	
	
	/**
	 * 按条件查询单个对象
	 * @param query
	 * @return
	 */
	public KObject findOne(HashMap<String,Object> query);
	
	/**
	 * 判断名称是否已存在,存在则返回true
	 * @param name KObject的name
	 * @return 存在则返回true
	 */
	public boolean checkName(String name);
	
	/**
	 * 判断id是否已存在,存在则返回true
	 * @param name KObject的name
	 * @return 存在则返回true
	 */
	public boolean checkId(long id);
	
	
	/**
	 * 判断某个字段值为某值的记录是否存在
	 * @param paraName
	 * @param query
	 * @return
	 */
	public boolean checkExist(String paraName,Object query);
	
	
	/**
	 * 判断是否存在有此条件的记录,返回第一条记录的id
	 * @param query
	 * @return 存在则第一条记录的id,否则返回-1
	 */
	public long checkExist(HashMap<String,Object> query);
	
	/**
	 * 查找单个对象的Map形式,顺序号为3
	 * @param query
	 * @param fields
	 * @return Map<String,Object>
	 */
	public HashMap<String,Object> findOneMap(HashMap<String,Object> query,HashMap<String,Object> fields);
	
	/**
	 * 查找单个对象的Map形式
	 * @param query
	 * @return
	 */
	public HashMap<String,Object> findOneMap(HashMap<String,Object> query);
	
	/**
	 * 查找Map形式对象,顺序号为4
	 * @param id long
	 * @return Map形式,未找到返回null
	 */
	public HashMap<String, Object> findOneMap(long id);
	
	/**
	 * 通用的查找过程,顺序号为5
	 * @param query 必须有
	 * @param fields 全部则为null
	 * @param sortBy 无则为null
	 * @param skip 无则为0
	 * @param len 无则为0
	 * @param hint 无则为null
	 * @return ArrayList&ltMap&ltString,Object&gt&gt
	 */
	public ArrayList<HashMap<String,Object>> query(HashMap<String,Object> query,HashMap<String,Object> fields,HashMap<String,Object> sortBy,int skip,int len,HashMap<String,Object> hint);
	
	/**
	 * 通用的查找过程
	 * @param query 必须有,为null则为默认查询
	 * @param fields 全部则为null
	 * @param sortBy 无则为null
	 * @param skip 无则为0
	 * @param len 无则为0
	 * @param hint 无则为null
	 * @return ArrayList&ltKObject&gt
	 */
	public ArrayList<KObject> queryKObj(HashMap<String,Object> query,HashMap<String,Object> fields,HashMap<String,Object> sortBy,int skip,int len,HashMap<String,Object> hint);

	/**
	 * 按页查找
	 * @param page 页码
	 * @param pageSize 每页项目数
	 * @param query
	 * @param fields
	 * @param sortBy
	 * @param hint
	 * @return ArrayList&ltKObject&gt
	 */
	public ArrayList<KObject> queryByPage(int page,int pageSize,HashMap<String,Object> query,HashMap<String,Object> fields,HashMap<String,Object> sortBy,HashMap<String,Object> hint);
	/**
	 * 按条件查询数量,顺序号为6
	 * @param query 必须有
	 * @param hint 无则为null
	 * @return 数量
	 */
	public int count(HashMap<String,Object> query,HashMap<String,Object> hint);
	
	/**
	 * 按条件查询数量,顺序号为7
	 * @param query 必须有
	 * @return 数量
	 */
	public int count(HashMap<String,Object> query);
	
	/**
	 * 创建新对象,自动生成新ID,顺序号为8
	 * @param kObj KObject
	 * @return 
	 */
	public boolean add(KObject kObj);
	
	
	/**
	 * 创建或更新对象,注意此方法不自动生成ID,顺序号为9
	 * @param kObj KObject
	 * @return
	 */
	public boolean save(KObject kObj);
	
	
	/**
	 * 更新对象,顺序号为10
	 * @param id long
	 * @param newObj KObject
	 * @return
	 */
	public boolean updateOne(long id,KObject newObj);
	
	/**
	 * 更新单个对象,顺序号为11
	 * @param query HashMap<String,Object>
	 * @param set HashMap<String,Object>
	 * @return
	 */
	public boolean updateOne(HashMap<String,Object> query,HashMap<String,Object> set);
	
	/**
	 * 更新对象,顺序号为12
	 * @param query HashMap<String,Object>
	 * @param set HashMap<String,Object>
	 * @param upset 如果不存在是否新建
	 * @param multi 是否更新多个
	 * @return 是否完成更新
	 */
	public boolean update(HashMap<String,Object> query,HashMap<String,Object> set,boolean upset,boolean multi);
	
	/**
	 * 标记删除,即将state置为-1,顺序号为13,用于确认被删除对象是否存在
	 * @param id
	 * @return Object 原对象,不存在则返回null
	 */
	public Object deleteOne(long id);
	
	/**
	 * 标记删除,即将state置为-1,顺序号为13,用于确认被删除对象是否存在
	 * @param query
	 * @return Object 原对象,不存在则返回null
	 */
	public Object deleteOne(HashMap<String,Object> query);
	
	/**
	 * 标记删除,即将state置为-1,顺序号为14
	 * @param query
	 * @param multi 是否批量
	 * @return
	 */
	public boolean delete(HashMap<String,Object> query,boolean multi);
	
	
	/**
	 * 从数据库中按条件批量彻底删除,顺序号为15
	 * @param id
	 * @return
	 */
	public boolean deleteForever(HashMap<String,Object> query);
	
	/**
	 * 从数据库中按条件批量彻底删除,顺序号为16
	 * @param id
	 * @return
	 */
	public boolean deleteForever(long id);
	
	public DataSourceInterface getDataSource();
	
	public void setDataSource(DataSourceInterface dataSource);
	
	public String getName();
	
	public void setName(String name);
	
	public boolean init();
	
	public int getId();
	
	public void setId(int id);
	
	public String getTableName();
	
	public void setTableName(String tableName);
	
	/**
	 * 生成此dao的配置,用于更新配置文件,如：<pre>
"_class":"com.k99k.khunter.dao.MongoUserDao",
"_dataSource":"mongodb_local",
"dbType":"mongodb",
"type":"single",
"tableName":"testKHT",
"id":2
	 * </pre>
	 * @return HashMap<String,Object>
	 */
	public HashMap<String,Object> toJsonConfig();
	
	/**
	 * 数据库类型,如mongodb
	 * @return
	 */
	public String getDbType();

	/**
	 * 设置数据库类型,如mongodb
	 * @param dbType
	 */
	public void setDbType(String dbType);
	
	/**
	 * DAO的获取方式,如单例为single
	 */
	public String getType();

	/**
	 * 设置DAO的获取方式,如单例为single
	 */
	public void setType(String type);
	
	/**
	 * 支持clone
	 * @see java.lang.Object#clone()
	 * @return 
	 */
	public Object clone();

}
