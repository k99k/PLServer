/**
 * 
 */
package com.k99k.khunter.acts;

import org.apache.log4j.Logger;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;

/**
 * Log输出ActionMsg的Action
 * @author keel
 *
 */
public class LogAction extends Action {
	
	static final Logger log = Logger.getLogger(LogAction.class);
	/**
	 * @param name
	 */
	public LogAction(String name) {
		super(name);
	}
	
	@Override
	public ActionMsg act(ActionMsg msg) {
		log.info("[LogAction]:"+msg);
		msg.setNextAction(null);
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
