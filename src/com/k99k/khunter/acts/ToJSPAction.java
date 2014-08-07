/**
 * 
 */
package com.k99k.khunter.acts;

import com.k99k.khunter.Action;
import com.k99k.khunter.ActionMsg;

/**
 * 用于指向某个jsp
 * @author keel
 *
 */
public class ToJSPAction extends Action {

	/**
	 * @param name
	 */
	public ToJSPAction(String name) {
		super(name);
	}
	
	private String jspPath;
	
	

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#act(com.k99k.khunter.ActionMsg)
	 */
	@Override
	public ActionMsg act(ActionMsg msg) {
		msg.addData("[jsp]", this.jspPath);
		return super.act(msg);
	}

	/**
	 * @return the jspPath
	 */
	public final String getJspPath() {
		return jspPath;
	}

	/**
	 * @param jspPath the jspPath to set
	 */
	public final void setJspPath(String jspPath) {
		this.jspPath = jspPath;
	}
	
	

}
