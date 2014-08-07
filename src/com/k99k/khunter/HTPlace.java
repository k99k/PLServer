/**
 * 
 */
package com.k99k.khunter;

import java.util.Map;

/**
 * 地点
 * @author keel
 *
 */
public class HTPlace extends KObject{

	public HTPlace() {
		super();
		setProp("x", 0);
		setProp("y", 0);
		setProp("z", 0);
		setProp("special", "");
		setProp("building", "");
		setProp("camp", "");
	}
	
	/**
	 * 由Map直接创建,不检验参数,比较危险,建议仅供Mongodb使用
	 * @param map 
	 */
	public HTPlace(Map<String, Object> map) {
		super(map);
	}

//	/**
//	 * x坐标
//	 */
//	private int x;
//	
//	/**
//	 * y坐标
//	 */
//	private int y;
//	
//	/**
//	 * z坐标
//	 */
//	private int z;
//	
//	/**
//	 * 类型,可以是多个类型(质数)合成的复值,也可以是基本类型
//	 */
//	private int type;
//	
//	/**
//	 * 建筑物ID
//	 */
//	private String building;
//	
//	/**
//	 * 特殊作用,暂以String方式体现,可以是集合
//	 */
//	private String special;
//	
//	/**
//	 * 阵营
//	 */
//	private String camp;
	
	public final String getCamp() {
		return getStringByName("camp");
	}

	public final void setCamp(String camp) {
		this.setProp("camp", camp);
	}

	public final String getBuilding() {
		return getStringByName("building");
	}

	public final void setBuilding(String building) {
		this.setProp("building", building);
	}

	public final String getSpecial() {
		return getStringByName("special");
	}

	public final void setSpecial(String special) {
		this.setProp("special", special);
	}

	public final int getZ() {
		return getIntByName("z");
	}

	public final void setZ(int z) {
		this.setProp("z", z);
	}

	public final int getY() {
		return getIntByName("y");
	}

	public final void setY(int y) {
		this.setProp("y", y);
	}

	public final int getX() {
		return getIntByName("x");
	}

	public final void setX(int x) {
		this.setProp("x", x);
	}
	
}
