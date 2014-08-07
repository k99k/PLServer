package com.k99k.khunter;


/**
 * ID管理器,增量默认为1
 * @author keel
 *
 */
public class IDManager {

	public IDManager() {
		this.increment = 1;
	}
	
	public IDManager(long initId) {
		id = initId;
		this.increment = 1;
	}
	
	public IDManager(long initId,int increment) {
		id = initId;
		this.increment = increment;
	}

	/**
	 * 增量
	 */
	private final int increment;

	
	/**
	 * 当前id
	 */
	private long id = 1; 
	
	/**
	 * 将id增长一次后返回增长后的ID
	 * @return
	 */
	public long nextId(){
		id = id + increment;
		return id;
	}
	
	/**
	 * 返回当前id
	 * @return
	 */
	public long getCurrentId() {
		return id;
	}
	
	 public void setId(long id){
		this.id = id;
	}
	
}
