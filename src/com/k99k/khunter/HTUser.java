/**
 * 
 */
package com.k99k.khunter;

import java.util.Map;

/**
 * HTUser 玩家
 * @author keel
 *
 */
public class HTUser extends KObject{

	public HTUser() {
		this(50);
	}
	
	/**
	 * 由Map直接创建,不检验参数,比较危险,建议仅供Mongodb使用
	 * @param map 
	 */
	public HTUser(Map<String, Object> map) {
		super(map);
	}

	public HTUser(int maxProp) {
		super(maxProp);
		this.setProp("pwd", "");
		this.setProp("imei", "");
		this.setProp("ip", "");
		this.setProp("nick", "");
		this.setProp("sex", 0);
		this.setProp("synId", "");
		this.setProp("email", "");
		this.setProp("icon", "");
		this.setProp("hp", 0);
		this.setProp("camp", "");
		this.setProp("lastLogin", "");
		this.setProp("gold", 0);
		this.setProp("medal", 0);
		this.setProp("honor", 0);
		this.setProp("x", 0);
		this.setProp("y", 0);
		this.setProp("z", 0);
		this.setProp("rank", 0);
		this.setProp("maxWareHouse", 0);
		this.setProp("curWareHouse", 0);
		this.setProp("equips", null);
		this.setProp("pets", null);
		this.setProp("dress", null);
		this.setProp("friends", null);
		
	}

	/**
	 * @param pwd 密码
	 * @param imei
	 * @param ip
	 * @param nick
	 * @param sex
	 * @param synId 同步id
	 * @param email
	 * @param icon 头像ID
	 * @param hp 体力hp值
	 * @param camp 阵营
	 * @param lastLogin
	 * @param gold 钱
	 * @param medal 勋章
	 * @param honor 荣誉值,cash 购买
	 * @param x 位置x坐标
	 * @param y
	 * @param z
	 * @param rank 军阶
	 * @param maxWareHouse 最大仓库容量
	 * @param curWareHouse
	 * @param equips
	 * @param pets
	 * @param dress
	 * @param friends
	 */
	public HTUser(String pwd, String imei, String ip, String nick, int sex,
			String synId, String email, String icon, int hp, String camp,
			String lastLogin, int gold, int medal, int honor, int x, int y,
			int z, int rank, int maxWareHouse, int curWareHouse, Object equips,
			Object pets, Object dress, Object friends) {
		super();
		this.setProp("pwd", pwd);
		this.setProp("imei", imei);
		this.setProp("ip", ip);
		this.setProp("nick", nick);
		this.setProp("sex", sex);
		this.setProp("synId", synId);
		this.setProp("email", email);
		this.setProp("icon", icon);
		this.setProp("hp", hp);
		this.setProp("camp", camp);
		this.setProp("lastLogin", lastLogin);
		this.setProp("gold", gold);
		this.setProp("medal", medal);
		this.setProp("honor", honor);
		this.setProp("x", x);
		this.setProp("y", y);
		this.setProp("z", z);
		this.setProp("rank", rank);
		this.setProp("maxWareHouse",maxWareHouse);
		this.setProp("curWareHouse", curWareHouse);
		this.setProp("equips", equips);
		this.setProp("pets", pets);
		this.setProp("dress", dress);
		this.setProp("friends", friends);
	}



//	/**
//	 *  密码
//	 */
//	private String pwd;
//	
//
//	/**
//	 * imei设备号
//	 */
//	private String imei;
//	
//	private String ip;
//	
//	private String nick;
//	
//	private int sex;
//	
//	
//	
//	/**
//	 * 同步id
//	 */
//	private String synId;
//	
//	private String email;
//	
//	
//	
//	/**
//	 * 头像ID
//	 */
//	private String icon;
//	
//	
//	
//	/**
//	 * 体力hp值
//	 */
//	private int hp;
//	
//	
//	
//	/**
//	 *  阵营
//	 */
//	private String camp;
//	
//	private String lastLogin;
//	
//	
//	
//	
//	/**
//	 *  * 钱
//	 */
//	private int gold;
//	
//	
//	
//	
//	/**
//	 *  * 勋章
//	 */
//	private int medal;
//	
//	
//	 
//	
//	/**
//	 * * 荣誉值,cash 购买
//	 */
//	private int honor;
//	
//	
//	
//	
//	/**
//	 *  * 位置x坐标
//	 */
//	private int x;
//	
//	
//	
//	private int y;
//	
//
//	
//	private int z;
//	
//	
//	/**
//	 * 军阶
//	 */
//	private int rank;
//	
//	private int maxWareHouse;
//	
//	private int curWareHouse;
//	
//	private Object equips;
//	
//	private Object pets;
//	
//	private Object dress;
//	
//	private Object friends;

	public final String getIcon() {
		return getStringByName("icon");
	}

	public final void setIcon(String icon) {
		this.setProp("icon", icon);
	}

	public final int getSex() {
		return getIntByName("sex");
	}

	public final void setSex(int sex) {
		this.setProp("sex", sex);
	}

	public final int getHp() {
		return getIntByName("hp");
	}

	public final void setHp(int hp) {
		this.setProp("hp", hp);
	}

	public final int getHonor() {
		return getIntByName("honor");
	}

	public final void setHonor(int honor) {
		this.setProp("honor", honor);
	}

	public final int getCurWareHouse() {
		return getIntByName("curWareHouse");
	}

	public final void setCurWareHouse(int curWareHouse) {
		this.setProp("curWareHouse", curWareHouse);
	}

	public final String getImei() {
		return getStringByName("imei");
	}

	public final void setImei(String imei) {
		this.setProp("imei", imei);
	}

	public final int getMaxWareHouse() {
		return getIntByName("maxWareHouse");
	}

	public final void setMaxWareHouse(int maxWareHouse) {
		this.setProp("maxWareHouse", maxWareHouse);
	}

	public final Object getEquips() {
		return getObjectByName("equips");
	}

	public final void setEquips(Object equips) {
		this.setProp("equips", equips);
	}

	public final String getSynId() {
		return getStringByName("synId");
	}

	public final void setSynId(String synId) {
		this.setProp("synId", synId);
	}

	public final String getIp() {
		return getStringByName("ip");
	}

	public final void setIp(String ip) {
		this.setProp("ip", ip);
	}

	public final int getRank() {
		return getIntByName("rank");
	}

	public final void setRank(int rank) {
		this.setProp("rank", rank);
	}

	public final String getPwd() {
		return getStringByName("pwd");
	}

	public final void setPwd(String pwd) {
		this.setProp("pwd", pwd);
	}

	public final String getCamp() {
		return getStringByName("camp");
	}

	public final void setCamp(String camp) {
		this.setProp("camp", camp);
	}

	public final String getLastLogin() {
		return getStringByName("lastLogin");
	}

	public final void setLastLogin(String lastLogin) {
		this.setProp("lastLogin", lastLogin);
	}

	public final Object getFriends() {
		return getObjectByName("friends");
	}

	public final void setFriends(Object friends) {
		this.setProp("friends", friends);
	}

	public final String getNick() {
		return getStringByName("nick");
	}

	public final void setNick(String nick) {
		this.setProp("nick", nick);
	}

	public final String getEmail() {
		return getStringByName("email");
	}

	public final void setEmail(String email) {
		this.setProp("email", email);
	}

	public final Object getDress() {
		return getObjectByName("dress");
	}

	public final void setDress(Object dress) {
		this.setProp("dress", dress);
	}

	public final Object getPets() {
		return getObjectByName("pets");
	}

	public final void setPets(Object pets) {
		this.setProp("pets", pets);
	}

	public final int getGold() {
		return getIntByName("gold");
	}

	public final void setGold(int gold) {
		this.setProp("gold", gold);
	}

	public final long getZ() {
		return getLongByName("z");
	}

	public final void setZ(long z) {
		this.setProp("z", z);
	}

	public final int getMedal() {
		return getIntByName("medal");
	}

	public final void setMedal(int medal) {
		this.setProp("medal", medal);
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




	public static void main(String[] args) {
		HTUser u = new HTUser();
		u.setId(2323);
		u.setPwd("123456");
		u.setProp("newprop", "just test prop value");
		System.out.println(u);
	}
	
	
}
