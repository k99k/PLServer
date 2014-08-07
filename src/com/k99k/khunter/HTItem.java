/**
 * 
 */
package com.k99k.khunter;

import java.util.Map;

/**
 * 物品
 * @author keel
 *
 */
public class HTItem extends KObject{

	public HTItem() {
		super();
		this.setProp("function", "");
		this.setProp("ack", 0);
		this.setProp("def", 0);
		this.setProp("special", "");
		this.setProp("costHP", 0);
		this.setProp("space", 0);
		this.setProp("price", 0);
	}
	
	/**
	 * 由Map直接创建,不检验参数,比较危险,建议仅供Mongodb使用
	 * @param map 
	 */
	public HTItem(Map<String, Object> map) {
		super(map);
	}

//	/**
//	 * 物品类型,可以是多个类型(质数)合成的复值,也可以是基本类型
//	 */
//	private int type;
//	
//	/**
//	 * 功能
//	 */
//	private String function;
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
//	 * 对HP的消耗量
//	 */
//	private int costHP;
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

	public final int getCostHP() {
		return getIntByName("costHP");
	}

	public final void setCostHP(int costHP) {
		this.setProp("costHP", costHP);
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


	public final String getFunction() {
		return getStringByName("function");
	}

	public final void setFunction(String function) {
		this.setProp("function", function);
	}
	
	
	public static void main(String[] args) {
		HTItem i = new HTItem();
		i.setId(33323);
		i.setProp("haha", 3);
		i.setSpace(5);
		System.out.println(i);
		
	}
	
}
