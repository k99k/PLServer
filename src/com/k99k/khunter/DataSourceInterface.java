/**
 * 
 */
package com.k99k.khunter;

/**
 * @author keel
 *
 */
public interface DataSourceInterface {

	public String getName();
	
	public boolean init();
	
	public boolean reset();
	
	public void exit();
	
	/**
	 * 获取一个Collection
	 * @param tableName 表名
	 * @return
	 */
	public Object getColl(String tableName);
	
	/**
	 * 创建新表结构
	 * @param kc KObjConfig
	 * @return
	 */
	public boolean buildNewTable(KObjConfig kc);
	
}
