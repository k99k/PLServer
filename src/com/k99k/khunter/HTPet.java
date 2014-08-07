/**
 * 
 */
package com.k99k.khunter;

import java.util.Map;

/**
 * 宠物
 * @author keel
 *
 */
public class HTPet extends KObject{

	public HTPet() {
		super();
		setProp("age", 0);
		setProp("ack", 0);
		setProp("def", 0);
		setProp("special", "");
		setProp("hp", 0);
		setProp("space", 0);
		setProp("price", 0);
	
	}
	
	/**
	 * 由Map直接创建,不检验参数,比较危险,建议仅供Mongodb使用
	 * @param map 
	 */
	public HTPet(Map<String, Object> map) {
		super(map);
	}

	/**
	 * @param age
	 * @param ack
	 * @param def
	 * @param special
	 * @param hp
	 * @param price
	 * @param space
	 */
	public HTPet(int age, int ack, int def, String special, int hp,
			int price, int space) {
		super();
		setProp("age", age);
		setProp("ack", ack);
		setProp("def", def);
		setProp("special", special);
		setProp("hp", hp);
		setProp("space", space);
		setProp("price", price);
	}



//	/**
//	 * 物品类型,可以是多个类型(质数)合成的复值,也可以是基本类型
//	 */
//	private int type;
//	
//	/**
//	 * 功能
//	 */
//	private int age;
//	
//	/**
//	 * 攻击力
//	 */
//	private int ack;
//	
//	/**
//	 * 防御力
//	 */
//	private int def;
//	
//	/**
//	 * 特殊能力,暂以String方式体现,可以是集合
//	 */
//	private String special;
//	
//	/**
//	 * 宠物的HP值
//	 */
//	private int hp;
//	
//	/**
//	 * 基本价格
//	 */
//	private int price;
//	
//	/**
//	 * 占用空间
//	 */
//	private int space;
	

	public final int getHp() {
		return getIntByName("hp");
	}

	public final void setHp(int hp) {
		this.setProp("hp", hp);
	}

	public final int getPrice() {
		return getIntByName("price");
	}

	public final void setPrice(int price) {
		this.setProp("price", price);
	}

	public final int getDef() {
		return getIntByName("def");
	}

	public final void setDef(int def) {
		this.setProp("def", def);
	}

	public final int getAck() {
		return getIntByName("ack");
	}

	public final void setAck(int ack) {
		this.setProp("ack", ack);
	}

	public final int getAge() {
		return getIntByName("age");
	}

	public final void setAge(int age) {
		this.setProp("age", age);
	}

	public final String getSpecial() {
		return getStringByName("special");
	}

	public final void setSpecial(String special) {
		this.setProp("special", special);
	}

	public final int getSpace() {
		return getIntByName("space");
	}

	public final void setSpace(int space) {
		this.setProp("space", space);
	}

	
}
