package com.k99k.plserver;

import org.apache.log4j.Logger;

import com.k99k.khunter.DaoInterface;
import com.k99k.khunter.KObject;

public class UpTaskA implements Runnable {

	private KObject u;
	private DaoInterface d;
	static final Logger log = Logger.getLogger(UpTaskA.class);
	
	
	public UpTaskA(KObject user,DaoInterface dao){
		this.u = user;
		this.d = dao;
	}
	
	@Override
	public void run() {
		if (!d.save(u)) {
//			log.error(Err.ERR_ADD_USER+" uid:"+u.getId());
		}
	}
	

}
