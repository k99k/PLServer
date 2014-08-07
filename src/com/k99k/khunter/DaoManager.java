/**
 * 
 */
package com.k99k.khunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.k99k.tools.JSON;
import com.k99k.tools.StringUtil;

/**
 * DAO管理器
 * @author keel
 *
 */
public final class DaoManager {

	private DaoManager() {
	}
	
	static final Logger log = Logger.getLogger(ActionManager.class);
	
	//private static final DaoManager me = new DaoManager();
	
	/**
	 * 用于在json中定位
	 * @return 返回"daos"
	 */
	public static final String getName(){
		return "daos";
	}
	
	/**
	 * 存储Action的Map,初始化大小为50
	 */
	private static final Map<String, DaoInterface> daoMap = new HashMap<String, DaoInterface>(50);
	
	private static boolean isInitOK = false;
	
	private static String iniFilePath;
	private static String classFilePath;
	public static final boolean isInitOK(){
		return isInitOK;
	}
	
	/**
	 * 用于保存所有的class字符串
	 */
	private static final HashMap<String,String> daoClasses = new HashMap<String,String>(50);
	
	/**
	 * 获取所有的class字符串，用于创建新的DAO
	 * @return String[]
	 */
	public static final String[] getDaoClasses(){
		int len = daoClasses.size();
		String[] arr = new String[len];
		int i = 0;
		for (Iterator<String> it = daoClasses.keySet().iterator(); it.hasNext();) {
			String clazz = it.next();
			arr[i] = clazz;
			i++;
		}
		return arr;
	}
	
	/**
	 * 返回一个指定key的Dao clone
	 * @param daoKey
	 * @return 找不到时或clone失败时返回null
	 */
	public static final DaoInterface cloneDao(String daoKey){
		DaoInterface dao = daoMap.get(daoKey);
		if (dao == null) {
			return null;
		}
		return (DaoInterface)dao.clone();
	}
	
	/**
	 * 初始化DaoManager
	 * @param iniFile 配置文件路径
	 * @param classPath class文件所在的路径
	 * @return 是否初始化成功
	 */
	@SuppressWarnings("unchecked")
	public final static boolean init(String iniFile,String classPath){
		if (!isInitOK) {
			//读取配置文件刷新注入的Dao数据
			try {
				
				String ini = KIoc.readTxtInUTF8(iniFile);
				Map<String,?> root = (Map<String,?>) JSON.read(ini);
				//先定位到json的actions属性
				Map<String, ?> dm = (Map<String, ?>) root.get(DaoManager.getName());

				//循环加入Dao
				int i = 0;
				for (Iterator<String> iter = dm.keySet().iterator(); iter.hasNext();) {
					String daoName = iter.next();
					Map<String, ?> m = (Map<String, ?>) dm.get(daoName);
					
					//读取必要的属性，如果少则报错并继续下一个
					if (m.containsKey("_class") &&  m.containsKey("_dataSource")) {
						
						String _class = (String) m.get("_class");
						
						//定位_dataSource
						String _dataSource = (String) m.get("_dataSource");
						DataSourceInterface ds = DataSourceManager.findDataSource(_dataSource);
						if (ds == null) {
							log.error("DataSourceManager.findDataSource error! _class:"+_class+" daoName:"+daoName+" _dataSource:"+_dataSource);
							continue;
						}
						//Dao初始化需要dataSource
						Object o = KIoc.loadClassInstance("file:/"+classPath, _class, new Object[]{daoName,ds});
						if (o == null) {
							log.error("loadClassInstance error! _class:"+_class+" actionName:"+daoName+" _dataSource:"+_dataSource);
							continue;
						}
						DaoInterface dao = (DaoInterface)o;
						
						HTManager.fetchProps(dao, m);
						String tableName = StringUtil.objToStrNotNull( m.get("tableName")).trim();
						//加入Dao
						if (tableName.equals("") || tableName.equals("*")) {
							dao.setTableName("*");
							daoMap.put(dao.getName(), dao);
							daoClasses.put(_class, daoName);
							log.info("Common Dao added: "+dao.getName());
						}else{
							//初始化并加入
							if (dao.init()) {
								daoMap.put(dao.getName(), dao);
								daoClasses.put(_class, daoName);
								log.info("Dao added: "+dao.getName());
							}else{
								log.error("Dao init failed:"+dao.getName()+" id:"+dao.getId());
							}
						}
						
						
						
						
					}else{
						log.error("Dao init Error! miss one or more key props. Position:"+i);
						continue;
					}
					i++;
				}
				
				
				
			} catch (Exception e) {
				log.error("DaoManager init Error!", e);
				isInitOK = false;
				return false;
			}
			isInitOK = true;
			//更新配置文件位置
			iniFilePath = iniFile;
			classFilePath = classPath;
			log.info("DaoManager init OK!");
		}
		return true;
	}
	
	/**
	 * 重新初始化
	 * @param iniFile 配置文件路径
	 * @param classPath class文件所在的路径
	 * @return 是否初始化成功
	 */
	public final static boolean reInit(String iniFile,String classPath){
		exit();
		String ini = (iniFile == null)? iniFilePath : iniFile;
		String cPath = (classPath == null)? classFilePath : classPath;
		return init(ini,cPath);
	}

	/**
	 * 获取一个Dao,如果该Dao的tableName为*,则返回一个clone对象
	 * @param name Dao的name
	 * @return DaoInterface,未找到返回null
	 */
	public static final DaoInterface findDao(String name){
		DaoInterface dao = daoMap.get(name);
		if (dao == null) {
			return null;
		}
		if (dao.getTableName() == null || dao.getTableName().equals("*")) {
			Object o = dao.clone();
			if (o == null) {
				return null;
			}
			dao = (DaoInterface)o;
		}
		return dao;
	}
	
	/**
	 * 判断是否存在
	 * @param name
	 * @return
	 */
	public static final boolean containsDao(String name){
		return daoMap.containsKey(name);
	}
	
	/**
	 * 添加一个Dao 
	 * @param dao Dao对象
	 * @return 如果已存在同名dao或dao初始化失败则返回false
	 */
	public static final boolean addDao(DaoInterface dao){
		if (dao == null) {
			return false;
		}
		if (daoMap.containsKey(dao.getName())) {
			return false;
		}
		if (!dao.init()) {
			return false;
		}
		daoMap.put(dao.getName(), dao);
		log.info("Dao added: "+dao.getName());
		return true;
	}
	
	/**
	 * 添加一个Dao
	 * @param name String
	 * @param _class
	 * @param _dataSource
	 * @param dbType
	 * @param type
	 * @param tableName
	 * @param id
	 * @return 是否添加成功
	 */
	public static final boolean addDao(String name,String _class,String _dataSource,String dbType,String type,String tableName,int id){
		DataSourceInterface ds = DataSourceManager.findDataSource(_dataSource);
		if (ds == null) {
			log.error("DataSourceManager.findDataSource error! _class:"+_class+" Name:"+name+" _dataSource:"+_dataSource);
			return false;
		}
		Object o = KIoc.loadClassInstance("file:/"+classFilePath, _class, new Object[]{name,ds});
		if (o == null) {
			log.error("loadClassInstance error! _class:"+_class+" _name:"+name);
			return false;
		}
		DaoInterface dao = (DaoInterface)o;
		dao.setDbType(dbType);
		dao.setTableName(tableName);
		dao.setType(type);
		dao.setId(id);
		return addDao(dao);
	}
	
	/**
	 * 去除Dao，同时更新配置文件
	 * @param name daoName
	 * @return
	 */
	public static final boolean removeDao(String name){
		daoMap.remove(name);
		boolean re =KIoc.updateIniFileNode(iniFilePath, new String[]{"daos"}, 2, -1, name, null);
		return re;
	}
	
	
	/**
	 * 更改某一个key对应的Dao实例
	 * @param name Dao的name
	 * @param dao 新的Dao
	 * @return dao为null或初始化失败则返回false
	 */
	public static final boolean changeDao(String name,DaoInterface dao){
		if (dao == null) {
			return false;
		}
		if (!dao.init()) {
			return false;
		}
		daoMap.put(name, dao);
		log.info("Dao changed: "+dao.getName());
		return true;
	}
	
	/**
	 * 将dao配置保存到配置文件中
	 * @param dao
	 * @return
	 */
	public static final boolean storeDao(DaoInterface dao){
		String key = dao.getName();
		//新增或更新某一个Dao
		boolean re = KIoc.updateIniFileNode(iniFilePath, new String[]{"daos"}, 0,-1, key, dao.toJsonConfig());
		return re;
	}
	
	/**
	 * 将dao配置保存到配置文件中,用于Dao已加入daoMap之后
	 * @param daoName
	 * @return
	 */
	public static final boolean storeDao(String daoName){
		DaoInterface dao = findDao(daoName);
		if (dao == null) {
			return false;
		}
		//新增或更新某一个Dao
		boolean re = KIoc.updateIniFileNode(iniFilePath, new String[]{"daos"}, 0,-1, daoName, dao.toJsonConfig());
		return re;
	}
	
	/**
	 * 刷新(重载)一个Dao
	 * @param name dao名
	 */
	@SuppressWarnings("unchecked")
	public static final boolean reLoadDao(String name){
		try {
			String ini = KIoc.readTxtInUTF8(iniFilePath);
			Map<String,?> root = (Map<String,?>) JSON.read(ini);
			//先定位到json的daos属性
			Map<String, ?> daosMap = (Map<String, ?>) root.get(DaoManager.getName());
			Map<String, ?> m = (Map<String, ?>) daosMap.get(name);
			if (!m.containsKey("_class") ||  !m.containsKey("_dataSource")) {
				log.error("Dao init Error! miss key prop:_class");
				return false;
			}
				
			String _class = (String) m.get("_class");
			String _dataSource = (String) m.get("_dataSource");
			DataSourceInterface ds = DataSourceManager.findDataSource(_dataSource);
			if (ds == null) {
				log.error("DataSourceManager.findDataSource error! _class:"+_class+" Name:"+name+" _dataSource:"+_dataSource);
				return false;
			}
			Object o = KIoc.loadClassInstance("file:/"+classFilePath, _class, new Object[]{name,ds});
			if (o == null) {
				log.error("loadClassInstance error! _class:"+_class+" _name:"+name);
				return false;
			}
			DaoInterface dao = (DaoInterface)o;
			HTManager.fetchProps(dao, m);
			daoMap.put(name, dao);
		} catch (Exception e) {
			log.error("DaoManager init Error!", e);
			return false;
		}
		log.info("Dao reLoaded: "+name);
		return true;
		
		
	}
	

	/**
	 * @return the daomap
	 */
	public static final Map<String, DaoInterface> getDaoMap() {
		return daoMap;
	}
	
	/**
	 * 获取DaoName列表
	 * @return
	 */
	public static final ArrayList<String> getDaoNames() {
		ArrayList<String> list = new ArrayList<String>();
		for (Iterator<String> it = daoMap.keySet().iterator(); it.hasNext();) {
			String dName = it.next();
			list.add(dName);
		}
		return list;
	}
	/**
	 * 退出DaoManager时的操作
	 */
	public static final void exit(){
		daoMap.clear();
		daoClasses.clear();
		isInitOK = false;
		log.info("DaoManager exited.");
	}
	
	public static void main(String[] args) {
		
		String webRoot = "f:/works/workspace_keel/KHunter/WebContent/WEB-INF/";
		String jsonFilePath = webRoot+"kconfig.json";
//		String classPath = webRoot+"classes/";
		//需要先初始化HTManager
		HTManager.init(jsonFilePath);
		//DaoManager.init(jsonFilePath, classPath);
		DaoInterface a = DaoManager.findDao("mongoUserDao");
		System.out.println(a.getName()+ " id:"+a.getId());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		DaoManager.reLoadDao("mongoUserDao");
		a = DaoManager.findDao("mongoUserDao");
		System.out.println(a.getName()+ " id:"+a.getId());
	}
	
	
}
