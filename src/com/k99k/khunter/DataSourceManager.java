/**
 * 
 */
package com.k99k.khunter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.k99k.tools.JSON;

/**
 * DataSource管理器,注意它仅支持newDataSource方法中的数据源,不从配置文件中生成新对象
 * @author keel
 *
 */
public final class DataSourceManager {

	private DataSourceManager() {
	}
	
	static final Logger log = Logger.getLogger(DataSourceManager.class);
	/*
	private static final DataSourceManager me = new DataSourceManager();
	
	public static final DataSourceManager getInstance(){
		
		return me;
	}
	*/
	/**
	 * 用于在json中定位
	 * @return 返回"dataSources"
	 */
	public static final String getName(){
		return "dataSources";
	}
	
	private static boolean isInitOK = false;
	
	/**
	 * 存储DataSourceInterface的Map,初始化大小为20
	 */
	private static final Map<String, DataSourceInterface> dataSourceMap = new HashMap<String, DataSourceInterface>(20);
	
	private static String iniFilePath;
	
//	private static String classFilePath;
	
	/**
	 * 根据dbType创建DataSource
	 * @param dbType 数据源类型
	 * @return 新的DataSourceInterface实例,如果dbType未匹配则返回null
	 */
	private static final DataSourceInterface newDataSource(String dbType){
		if (dbType.equals("mongodb")) {
			return new MongoConn();
		}
		return null;
	}
	
//	/**
//	 * 预先将支持的各种类型的DataSource存储在Map中,不由反射生成对象
//	 */
//	private final static Map<String,DataSourceInterface> preparedDataSources = createPreparedDataSources();
//	
//	
//	/**
//	 * 生成各类DataSource
//	 * @return preparedDataSources
//	 */
//	private final static Map<String,DataSourceInterface> createPreparedDataSources(){
//		Map<String,DataSourceInterface> m = new HashMap<String, DataSourceInterface>();
//		//mongodb
//		
//		
//		return m;
//	}
	
	/**
	 * 初始化DataSourceManager
	 * @param iniFile 配置文件路径
	 * @param classPath class文件所在的路径
	 * @return 是否初始化成功
	 */
	@SuppressWarnings("unchecked")
	public static boolean init(String iniFile,String classPath){
		if (!isInitOK) {
			//读取配置文件刷新注入的DataSourceInterface数据
			try {
				String ini = KIoc.readTxtInUTF8(iniFile);
				Map<String,?> root = (Map<String,?>) JSON.read(ini);
				//先定位到json的dataSources属性
				Map<String, ?> dsMap = (Map<String, ?>) root.get(DataSourceManager.getName());
				//循环加入DataSourceInterface
				int i = 0;
				for (Iterator<String> iter = dsMap.keySet().iterator(); iter.hasNext();) {
					String dsName = iter.next();
					Map<String, Object> m = (Map<String, Object>) dsMap.get(dsName);
					m.put("name", dsName);
					//读取必要的属性，如果少则报错并继续下一个
					if (m.containsKey("_dbType")) {
						String _dbType = (String) m.get("_dbType");
						DataSourceInterface ds = newDataSource(_dbType);
						if (ds == null) {
							log.error("Get newDataSource error: _dbType:"+_dbType+" _name:"+dsName);
							continue;
						}
						
						HTManager.fetchProps(ds, m);
						//初始化ds并加入DataSourceInterface
						if (ds.init()) {
							dataSourceMap.put(ds.getName(), ds);
							log.info("DataSourceInterface added: "+ds.getName());
						}else{
							log.error("DataSource init failed:"+ds.getName());
						}
						
						
					}else{
						log.error("DataSourceInterface init Error! miss one or more key props. Position:"+i);
						continue;
					}
					i++;
				}
				
				
				
			} catch (Exception e) {
				log.error("DataSourceManager init Error!", e);
				isInitOK = false;
				return false;
			}
			isInitOK = true;
			iniFilePath = iniFile;
//			classFilePath = classPath;
			log.info("DataSourceManager init OK!");
		}
		return true;
	}
	
	/**
	 * 重新初始化
	 * @param iniFile 配置文件路径
	 * @param classPath class文件所在的路径
	 * @return 是否初始化成功
	 */
	public static boolean reInit(String iniFile,String classPath){
		exit();
		return init(iniFile,classPath);
	}
	
	public static final boolean isInitOK(){
		return isInitOK;
	}
	
	public static final void exit(){
		for (Iterator<String> it = dataSourceMap.keySet().iterator(); it.hasNext();) {
			DataSourceInterface ds = dataSourceMap.get(it.next());
			ds.exit();
		}
		isInitOK = false;
		log.info("DataSourceManager exited.");
	}

	/**
	 * 获取一个DataSourceInterface
	 * @param dsName
	 * @return DataSourceInterface,未找到返回null
	 */
	public static final DataSourceInterface findDataSource(String dsName){
		return dataSourceMap.get(dsName);
	}
	
	
	/**
	 * 添加一个DataSourceInterface,同时确定它 获取方式(单例或是每次均新建)
	 * @param ds
	 * @return
	 */
	public static final boolean addDataSource(DataSourceInterface ds){
		if (ds == null) {
			return false;
		}
		if (dataSourceMap.containsKey(ds.getName())) {
			return false;
		}
		dataSourceMap.put(ds.getName(), ds);
		log.info("DataSourceInterface added: "+ds.getName());
		return true;
	}
	
	/**
	 * 更改某一个key对应的DataSourceInterface实例
	 * @param dsName ds的name
	 * @param ds 新的DataSourceInterface
	 */
	public static final void changeDataSource(String dsName,DataSourceInterface ds){
		if (ds == null) {
			return;
		}
		dataSourceMap.put(dsName, ds);
		log.info("DataSource changed: "+ds.getName());
	}
	
	/**
	 * 刷新(重载)一个DataSourceInterface
	 * @param dsName DataSource的name
	 */
	@SuppressWarnings("unchecked")
	public static final boolean reLoadDataSource(String dsName){
		try {
			String ini = KIoc.readTxtInUTF8(iniFilePath);
			Map<String,?> root = (Map<String,?>) JSON.read(ini);
			//先定位到json的dataSources属性
			Map<String, ?> dsMap = (Map<String, ?>) root.get(DataSourceManager.getName());
			Map<String, ?> m = (Map<String, ?>) dsMap.get(dsName);
			if (!m.containsKey("_dbType")) {
				log.error("DataSource init Error! miss key prop:_dbType");
				return false;
			}
				
			String _dbType = (String) m.get("_dbType");
			DataSourceInterface ds = newDataSource(_dbType);
			if (ds == null) {
				log.error("Get newDataSource error: _dbType:"+_dbType+" _name:"+dsName);
				return false;
			}
			HTManager.fetchProps(ds, m);
			dataSourceMap.put(dsName, ds);
			
		} catch (Exception e) {
			log.error("DataSourceManager reLoadDataSource Error:"+dsName, e);
			return false;
		}
		log.info("DataSource reLoaded: "+dsName);
		return true;
	}
	
	public static void main(String[] args) {
		String webRoot = "f:/works/workspace_keel/KHunter/WebContent/WEB-INF/";
		String jsonFilePath = webRoot+"kconfig.json";
		String classPath = webRoot+"classes/";
		DataSourceManager.init(jsonFilePath, classPath);
		MongoConn m = (MongoConn)DataSourceManager.findDataSource("mongodb_local");
		System.out.println(m.getPort());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		DataSourceManager.reLoadDataSource("mongodb_local");
		m = (MongoConn)DataSourceManager.findDataSource("mongodb_local");
		System.out.println(m.getPort());
	}
	
}
