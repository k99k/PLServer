/**
 * 
 */
package com.k99k.khunter.acts;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;

/**
 * 平台状态与公告
 * @author keel
 *
 */
public class ConsoleStateAction extends Action {

	/**
	 * @param name
	 */
	public ConsoleStateAction(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#act(com.k99k.khunter.ActionMsg)
	 */
	@Override
	public ActionMsg act(ActionMsg msg) {
		msg.addData("state", "ok");
		msg.addData("notice", "新版本暂未上线.");
		return super.act(msg);
	}

	@Override
	public void exit() {
		
	}

	@Override
	public String getIniPath() {
		return null;
	}

	@Override
	public void init() {
		
	}

}
