/**
 * 
 */
package com.k99k.plserver;

import com.k99k.khunter.Action;

/**
 * 最后一个Action,负责Action初始化后的收尾工作
 * @author keel
 *
 */
public class EndAction extends Action {

	/**
	 * @param name
	 */
	public EndAction(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#init()
	 */
	@Override
	public void init() {
		super.init();
		//初始化StaticDao
		StaticDao.initS();
	}
	
	

}
