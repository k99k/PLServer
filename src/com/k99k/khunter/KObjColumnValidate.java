/**
 * 
 */
package com.k99k.khunter;

/**
 * 字段验证接口
 * @author keel
 *
 */
public interface KObjColumnValidate {
	
	/**
	 * 验证字段
	 * @param value 需要验证的值
	 * @return 是否验证成功
	 */
	public boolean validate(Object value);
	
	/**
	 * 设置验证类型和参数并初始化,同时要处理toString输出
	 * @param type
	 * @param paras
	 */
	public void initType(int type,String[] paras);
	
}
