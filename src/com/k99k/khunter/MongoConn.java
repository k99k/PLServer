/**
 * 
 */
package com.k99k.khunter;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.k99k.tools.CnToSpell;
import com.k99k.tools.JSON;
import com.k99k.tools.Net;
import com.k99k.tools.StringUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;

/**
 * MongoDB连接处理器，单例
 * @author keel
 *
 */
public final class MongoConn implements DataSourceInterface{

	static final Logger log = Logger.getLogger(MongoConn.class);
	
	private String ip = "127.0.0.1";
	private int port = 27017;
	private String dbName = "ds"; //测试数据库
	private String user = "keel";
	private String pwd = "jsGame_1810";
	
	private int connectionsPerHost = 50;
	private int threadsAllowedToBlockForConnectionMultiplier = 50;
	private int maxWaitTime = 5000;
	
	//static Mongo mongo;
	static MongoClient mongo;
	private DB db;
	
	private String name = "mongodb_local";
	
	public MongoConn() {
		//init();
	}
	
	/*
	 * 引入新CP(老)
	 * @param ip
	 * @param cpid
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final void importNewCompany(String ip,String[] cpid){
		MongoConn mongo = new MongoConn();
		mongo.setIp(ip);
		mongo.setPort(27017);
		mongo.setDbName("tc");
		mongo.setUser("keel");
		mongo.setPwd("jsGame_1810");
		if (mongo.init()) {
			DBCollection coc = mongo.getColl("TCCompany");
			DBCollection cuc = mongo.getColl("TCUser");
			//确定数据库表中的最大id
			DBCursor cur = cuc.find(new BasicDBObject(),new BasicDBObject("_id",1)).sort(new BasicDBObject("_id",-1)).limit(1);
			long lastId = 0;
			if (cur.hasNext()) {
				DBObject cc = (DBObject) cur.next();
				lastId = Long.parseLong(cc.get("_id").toString())+1;
			}
			cur = coc.find(new BasicDBObject(),new BasicDBObject("_id",1)).sort(new BasicDBObject("_id",-1)).limit(1);
			long lastcId = 0;
			if (cur.hasNext()) {
				DBObject cc = (DBObject) cur.next();
				lastcId = Long.parseLong(cc.get("_id").toString())+1;
			}
			//插入对象
			for (int i = 0; i < cpid.length; i++) {
				//获取公司接口信息
				String url = "http://202.102.39.9/MIS/v/entitytest/cps?startIndex=0&pageSize=10&cpId="+cpid[i];
				String comInfo = Net.getUrlContent(url, 3000, false, "utf-8");
				if (!StringUtil.isStringWithLen(comInfo, 10)) {
					System.out.println("获取公司接口数据失败:"+cpid[i]);
					continue;
				}
				
				HashMap<String,Object> comMap = (HashMap<String, Object>) JSON.read(comInfo);
				comMap = (HashMap<String, Object>) (((ArrayList)comMap.get("rows")).get(0));
				if (comMap ==  null || comMap.isEmpty()) {
					System.out.println("此公司不存在或无法从接口获取:"+cpid[i]);
					continue;
				}
				
				DBObject co = new BasicDBObject();
				co.put("name", cpid[i]);
				co.put("pwd", "egame");
				co.put("type", 1);
				co.put("level", 0);
				co.put("info", comMap.get("cpName").toString());
				co.put("phoneNumber", comMap.get("loginMobilePhone").toString());
				co.put("email", comMap.get("accessMail").toString());
				co.put("company", comMap.get("cpShortName").toString());
				co.put("newNews", 0);
				co.put("newTasks", 0);
				co.put("qq", "");
				co.put("state", 0);
				co.put("groupID", 0);
				co.put("groupLeader", 0);
				
				//更新TCUser

				co.put("_id", lastId);
				cuc.save(co);
				//更新TCCompany
				
				DBObject co2 = new BasicDBObject();
				String mainUser = co.get("name").toString();
				String name = co.get("company").toString();
				co2.put("_id", lastcId);
				co2.put("shortName", CnToSpell.getLetter(name));
				co2.put("mainUser", mainUser);
				co2.put("name", name);
				co2.put("state", 0);
				co2.put("level", 0);
				co2.put("type", 0);
				co2.put("version", 1);
				coc.save(co2);
				System.out.println("add ok:"+cpid[i]+" _id-u:"+lastId+" _id-c:"+lastcId);
				lastId++;
				lastcId++;
			}
			
		}
		
		mongo.close();
	}
	 */
	
	/**
	 * 引入新CP
	 * @param ip
	 * @param cpid
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final void importNewCompany2(String ip,String[] cpid){
		MongoConn mongo = new MongoConn();
		mongo.setIp(ip);
		mongo.setPort(27017);
		mongo.setDbName("tc");
		mongo.setUser("keel");
		mongo.setPwd("jsGame_1810");
		if (mongo.init()) {
			DBCollection coc = mongo.getColl("TCCompany");
			DBCollection cuc = mongo.getColl("TCUser");
			
			//确定数据库表中的最大id
			DBCursor cur = cuc.find(new BasicDBObject(),new BasicDBObject("_id",1)).sort(new BasicDBObject("_id",-1)).limit(1);
			long lastId = 0;
			if (cur.hasNext()) {
				DBObject cc = (DBObject) cur.next();
				lastId = Long.parseLong(cc.get("_id").toString())+1;
			}
			cur = coc.find(new BasicDBObject(),new BasicDBObject("_id",1)).sort(new BasicDBObject("_id",-1)).limit(1);
			long lastcId = 0;
			if (cur.hasNext()) {
				DBObject cc = (DBObject) cur.next();
				lastcId = Long.parseLong(cc.get("_id").toString())+1;
			}
			//插入对象
			for (int i = 0; i < cpid.length; i++) {
				
				//确定cpid是否已存在，如存在则跳过 --如果去除此段可对已存在用户进行更新
				cur = coc.find(new BasicDBObject("mainUser",cpid[i]));
				if (cur.hasNext()) {
					//skip
					System.out.println("已存在此公司,跳过:"+cpid[i]);
					continue;
				}
				
				//获取公司接口信息
				String url = "http://202.102.39.9:82/Business/entitytest/cps.do?cpId="+cpid[i];
				String comInfo = Net.getUrlContent(url, 3000, false, "utf-8");
				if (!StringUtil.isStringWithLen(comInfo, 10)) {
					System.out.println("获取公司接口数据失败:"+cpid[i]);
					continue;
				}
				
				HashMap<String,Object> comMap = (HashMap<String, Object>) JSON.read(comInfo);
				comMap = (HashMap<String, Object>) (((ArrayList)comMap.get("rows")).get(0));
				if (comMap ==  null || comMap.isEmpty()) {
					System.out.println("此公司不存在或无法从接口获取:"+cpid[i]);
					continue;
				}
				
				DBObject co = new BasicDBObject();
				co.put("name", cpid[i]);
				co.put("pwd", "egame");
				co.put("type", 1);
				co.put("level", 0);
				co.put("info", comMap.get("cnName").toString());
				co.put("phoneNumber", comMap.get("linkPhone").toString());
				co.put("email", comMap.get("linkEmail").toString());
				co.put("company", comMap.get("shortName").toString());
				co.put("newNews", 0);
				co.put("newTasks", 0);
				co.put("qq", "");
				co.put("state", 0);
				co.put("groupID", 0);
				co.put("groupLeader", 0);
				
				//更新TCUser

				co.put("_id", lastId);
				cuc.save(co);
				//更新TCCompany
				
				DBObject co2 = new BasicDBObject();
				String mainUser = co.get("name").toString();
				String name = co.get("company").toString();
				co2.put("_id", lastcId);
				co2.put("shortName", CnToSpell.getLetter(name));
				co2.put("mainUser", mainUser);
				co2.put("name", name);
				co2.put("state", 0);
				co2.put("level", 0);
				co2.put("type", 0);
				co2.put("version", 1);
				coc.save(co2);
				System.out.println("add ok:"+cpid[i]+" _id-u:"+lastId+" _id-c:"+lastcId);
				lastId++;
				lastcId++;
			}
			
		}
		System.out.println("end of import ip:"+ip);
		mongo.close();
	}
	
	/**
	 * 修改测试人员名称及关联测试对象
	 * @param orgName
	 * @param newName
	 */
	public static final void changeTestUserName(MongoConn mongo,String orgName,String newName){
		
		if (mongo.init()) {
			//更新TCUser,TCTask,TCTestUnit.tester
			DBCollection coll = mongo.getColl("TCUser");
			coll.update(new BasicDBObject("name",orgName),  new BasicDBObject("$set",new BasicDBObject("name",newName)), false, true);
			System.out.println("TCUser updated.");
			
			
			
//			coll = mongo.getColl("TCTask");
//			coll.update(new BasicDBObject("log.user",orgName),  new BasicDBObject("$set",new BasicDBObject("log.$.user",newName)), false, true);
//			System.out.println("TCTask updated.");
			
			
			
			coll = mongo.getColl("TCTestUnit");
			coll.update(new BasicDBObject("tester",orgName),  new BasicDBObject("$set",new BasicDBObject("tester",newName)), false, true);
			System.out.println("TCTestUnit updated.");
			
			
		}
	}
	
	/**
	 * 新增机型组,拆分
	 */
	public void newGroupSplit(String newGroup,String newShortName,long newEgameId,String oldGroup,HashMap<String,Object> newParas){
		long lastId = 0;
		DBCollection coll = this.getColl("TCPhone");
		DBCursor cur = coll.find().limit(1).sort(new BasicDBObject("_id",-1));
		if (cur.hasNext()) {
			lastId = (Long) cur.next().get("_id");
		}else {
			System.out.println("empty table TCPhone");
			return;
		}
		cur = coll.find(new BasicDBObject("name",oldGroup));
		if (cur.hasNext()) {
			DBObject c = cur.next();
			lastId++;
			c.put("_id", lastId);
			c.put("name", newGroup);
			c.put("shortName", newShortName);
			if (!newParas.isEmpty()) {
				Iterator it = newParas.entrySet().iterator();
				while(it.hasNext()){
					Entry en = (Entry) it.next();
					c.put(en.getKey().toString(), en.getValue());
				}
			}
			coll.insert(c);
		}
		//目前通过和未测游戏新增拆分出来的机型组
		 coll = this.getColl("TCTestUnit");
		 cur = coll.find().limit(1).sort(new BasicDBObject("_id",-1));
			if (cur.hasNext()) {
				lastId = (Long) cur.next().get("_id");
			}else {
				System.out.println("empty table TCTestUnit");
				return;
			}
		 HashMap<String,Object> q = new HashMap<String, Object>();
		HashMap<String,Object> in = new HashMap<String, Object>();
		in.put("$in", new int[]{0,2,4});
		q.put("state", in);
		q.put("phone", oldGroup);
		 cur = coll.find(new BasicDBObject(q));
		while (cur.hasNext()) {
			DBObject c = cur.next();
			lastId++;
			c.put("_id", lastId);
			c.put("phone", newGroup);
			c.put("egameId", newEgameId);
			coll.insert(c);
		}
	}
	/**
	 * 机型组合并
	 */
	public  void newGroupMerge(String newGroup,String oldGroup,HashMap<String,Object> newParas){
		DBCollection coll = this.getColl("TCPhone");
		//coll.update(new BasicDBObject("name",oldGroup),new BasicDBObject("state",-1),false,false);
		coll.remove(new BasicDBObject("name",oldGroup));
		//将被合的机型组改为新机型组
		 coll = this.getColl("TCTestUnit");
		HashMap<String,Object> q = new HashMap<String, Object>();
		HashMap<String,Object> in = new HashMap<String, Object>();
		in.put("$in", new int[]{0,2,4});
		q.put("state", in);
		q.put("phone", oldGroup);
		DBCursor cur = coll.find(new BasicDBObject(q));
		while (cur.hasNext()) {
			DBObject c = cur.next();
			String state = c.get("state").toString();
			if (state.trim().equals("0")) {
				coll.remove(c);
				continue;
			}
			c.put("phone", newGroup);
			coll.save(c);
		}
	}
	
	
	public static final void fixUser(String ip,String userName){
		MongoConn mongo = new MongoConn();
		mongo.setIp(ip);
		mongo.setPort(27017);
		mongo.setDbName("tc");
		mongo.setUser("keel");
		mongo.setPwd("jsGame_1810");
		if (mongo.init()) {
			DBCollection coll = mongo.getColl("TCUser");
			BasicDBObject q = new BasicDBObject("_id",6);
			DBCursor cur = coll.find(q);
			
			if (cur.hasNext()) {
				DBObject u = cur.next();
				BasicDBList ls = (BasicDBList) u.get("unReadTasks");
				StringBuilder sb = new StringBuilder();
				int count =  ls.size();
				for (int i = 0; i < count; i++) {
					sb.append(ls.get(i)).append(",");
				}
				System.out.println(sb.toString());
				
				DBCollection cot = mongo.getColl("TCTask");
				DBCursor cu2 = cot.find(new BasicDBObject("_id",new BasicDBObject("$in",ls)));
				while (cu2.hasNext()) {
					DBObject tu = cu2.next();
					int state = (Integer) tu.get("state");
					if (state<0) {
						ls.remove(tu.get("_id"));
						continue;
					}
					if (!tu.get("operator").equals(userName)) {
						ls.remove(tu.get("_id"));
					}
				}
				sb = new StringBuilder();
				count =  ls.size();
				for (int i = 0; i < count; i++) {
					sb.append(ls.get(i)).append(",");
				}
				System.out.println(sb.toString());
				System.out.println(count);
				BasicDBObject set = new BasicDBObject("$set",new BasicDBObject("unReadTasks",ls).append("newTasks", count));
				WriteResult re = coll.update(q, set);
				System.out.println(re.getError());
			}
			
		}
	}
	/**
	 * 比较机型组通过结果
	 * @param mongo
	 */
	public static void cc(MongoConn mongo){
		
		if (mongo.init()) {
			String p2 = "#三星I579";
			String p1= "#酷派5855";
			DBCollection coll = mongo.getColl("TCTestUnit");
			DBCursor cur = coll.find(new BasicDBObject("phone",p1).append("state", 2));
			long re = 0;
			long cc = 0;
			while(cur.hasNext()){
				DBObject d = cur.next();
//				if (d.get("PID") == null) {
//					continue;
//				}
				int pid = Integer.parseInt(d.get("PID").toString());
				DBCursor cur2 = coll.find(new BasicDBObject("PID",pid).append("phone",p2).append("state", 9));
				if (cur2.hasNext()) {
					DBObject d1 = cur2.next();
					re++;
				}
				cc++;
			}
			System.out.println(p1+":"+cc);
			System.out.println(p2+":"+re);
//			long cc = coll.count(new BasicDBObject("state",3).append("phone", "华为C8650+"));
//			System.out.println("华为C8650+:"+cc);
//			cc = coll.count(new BasicDBObject("state",3).append("phone", "中兴N760"));
//			System.out.println("中兴N760:"+cc);
		}
		mongo.close();
	}
	
	
	/**
	 * 平台升级割接,增加机型组支持
	 * @param ip
	 */
	public static void newEgame(String ip){
		String[] pGroup = {"华为C8500", "中兴N600", "中兴N606", "三星I559", "华为C8600", "酷派D539", "华为C8650+", "中兴N760", "三星I579", "三星I909MR", "酷派5855", "摩托XT800", "摩托XT800+", "摩托XT882", "摩托XT928"};
		long[] pGroupEgameId = {1140, 1141, 1142, 1143, 1144, 1145, 1146, 1147, 1148, 1149, 1150, 1151, 1152, 1153, 1154};
		//获取原phone表中的记录,改为机型组的信息，再加入表中
		HashMap<String,Long> newPG = new HashMap<String, Long>();
		for (int i = 0; i < pGroup.length; i++) {
			newPG.put(pGroup[i], pGroupEgameId[i]);
		}
		try {
			MongoConn mongo = new MongoConn();
			mongo.setIp(ip);
			mongo.setPort(27017);
			mongo.setDbName("tc");
			mongo.setUser("keel");
			mongo.setPwd("jsGame_1810");
			if (mongo.init()) {
				DBCollection coll = mongo.getColl("TCPhone");
				DBCursor cur = null;
				BasicDBObject q = new BasicDBObject();
				cur = coll.find(q).sort(new BasicDBObject("_id",-1)).limit(1);
				long maxId = 0;
				if (cur.hasNext()) {
					maxId = (Long) cur.next().get("_id");
				}
				System.out.println("TCPhone maxId:"+maxId);
				
				cur = coll.find(q);
				while (cur.hasNext()) {
					DBObject c = cur.next();
					String cn = (String) c.get("name");
					//对"华为C8650+"单独处理
					if (cn.equals("华为C8650")) {
						maxId++;
						c.put("_id", maxId);
						c.put("name", "华为C8650+");
						c.put("shortName", "C8650+");
						c.put("egameId", 1866);
						coll.insert(c);
						System.out.println(maxId+" add:"+c.get("name"));
						maxId++;
						c.removeField("_id");
						c.put("name", "#华为C8650+");
						c.put("group", 10);
						c.put("egameId", newPG.get("华为C8650+"));
						c.put("_id", maxId);
						coll.insert(c);
						System.out.println(maxId+" add:"+c.get("name"));
					}else if (newPG.containsKey(cn)) {
						c.removeField("_id");
						c.put("name", "#" + cn);
						c.put("group", 10);
						c.put("egameId", newPG.get(cn));
						maxId++;
						c.put("_id", maxId);
						coll.insert(c);
						System.out.println(maxId+" add:"+c.get("name"));
					}

				}
				// 更新TCPhoneGroup表的android主机型
				coll = mongo.getColl("TCPhoneGroup");
				String[] pGroupS = new String[pGroup.length];
				for (int i = 0; i < pGroup.length; i++) {
					pGroupS[i] ="#"+pGroup[i];
				}
				q = new BasicDBObject("_id", 2L);
				BasicDBObject set = new BasicDBObject();
				BasicDBObject update = new BasicDBObject();
				set.put("phone", pGroupS);
				update.put("$set", set);
				coll.update(q, update);
				System.out.println("phoneGroup updated.");
				//fee信息变更
				coll = mongo.getColl("TCProduct");
				q = new BasicDBObject();
				cur = coll.find(q);
				while (cur.hasNext()) {
					DBObject c = cur.next();
					String feeInfo = null;
					if (c.get("feeInfo") instanceof BasicDBList) {
						feeInfo = JSON.write(c);
					}else{
						feeInfo = (String) c.get("feeInfo");
					}
					feeInfo = feeInfo.replace("\"id\":", "\"consumeId\":");
					feeInfo = feeInfo.replace("\"consumecode\":", "\"consumeCode\":");
					feeInfo = feeInfo.replace("\"consumecodedsc\":", "\"description\":");
					feeInfo = feeInfo.replace("\"consumecodename\":", "\"consumeName\":");
					feeInfo = feeInfo.replace("\"fee\":", "\"price\":");
					feeInfo = feeInfo.replace("\"memo\":", "\"feeType\":");
					feeInfo = feeInfo.replace("\"notecode\":", "\"smcode\":");
					feeInfo = feeInfo.replace("\"paychanel\":", "\"buyGuide\":");
					feeInfo = feeInfo.replace("\"serviceid\":", "\"gameId\":");
					feeInfo = feeInfo.replace("\"triger\":", "\"trigerCondition\":");
					feeInfo = feeInfo.replace("\"typeflag\":", "\"consumeTypeName\":");
					c.put("feeInfo", feeInfo);
					coll.save(c);
				}
				System.out.println("feeInfo updated.");
				
				//testCase增加
				BasicDBObject[] newCases = {
						new BasicDBObject("name","游戏安装").append("caseId", 1).append("level", 10).append("info", "1.1	能正常安装游戏到测试终端。<br /> 1.2	Android单机游戏安装时验证权限，一般要求不能有“网络通讯”、“短信接收”、“访问通讯录”等无关权限。如有特殊情况(如嵌入爱游戏SDK等)需要在提交测试时说明。<br /> 1.3	如果是需要加载数据包的单机游戏，允许“网络通讯”权限，允许游戏第一次运行时联网下载数据包。<br /> 1.4	安装完成后手机中只能出现一个游戏图标。").append("state", 0),
						new BasicDBObject("name","游戏启动").append("caseId", 2).append("level", 10).append("info", "2.1	运行游戏程序，在启动中无长时间停顿和异常挂起。<br /> 2.2	游戏启动进入主画面耗时3秒以上，需提供进度提示。<br /> 2.3	开机界面中应出现“爱游戏”LOGO，不能出现其它运营商LOGO。").append("state", 0),
						new BasicDBObject("name","游戏名称").append("caseId", 3).append("level", 10).append("info", "3.1	游戏安装后在手机内显示的程序名称、游戏运行时显示名称应与在爱游戏平台申报的名称以及游戏内容一致。").append("state", 0),
						new BasicDBObject("name","游戏菜单").append("caseId", 4).append("level", 2).append("info", "4.1	游戏“更多游戏”链接的指向游戏平台。<br /> 4.2	游戏帮助：游戏玩法的详细说明，帮助中必须包含：应用中文说明、按键说明。<br /> 4.3	关于:包含应用中文名称、应用类型、公司名称、客服电话、免责声明、版本号。").append("state", 0),
						new BasicDBObject("name","屏幕适配").append("caseId", 5).append("level", 10).append("info", "5.1	屏幕画面与测试终端适配，不影响游戏运行和操作，游戏功能项必须全部完整显示，并保证完整的视觉效果。").append("state", 0),
						new BasicDBObject("name","游戏使用").append("caseId", 6).append("level", 10).append("info", "6.1	各功能键使用（触摸）正常；能正常打开菜单进行操作；在游戏操作中无报错、死机、反应过慢、自动退出等异常情况。<br /> 6.2	特殊功能：如终端支持重力感应，且在游戏中有应用，按操作说明能正常游戏。比如支持横竖屏切换，适配正常，游戏进度正常。如终端支持GPS，且游戏有调用GPS模块，按操作说明能正常游戏，如产生流量资费需要先提示说明。<br /> 6.3	游戏声音：游戏暂停时不能有游戏声音，如果有声音开关必须能正常使用。<br /> 6.4	游戏内输入文字等信息时，能正常调用输入法正常输入。<br /> 6.5	JAVA游戏在触屏手机上使用必须有虚拟键盘。").append("state", 0),
						new BasicDBObject("name","游戏文字及内容信息安全").append("caseId", 7).append("level", 10).append("info", "7.1	游戏中所有提示、对话、说明均为中文。<br /> 7.2	无乱码，无明显文字错误。<br /> 7.3	无粗口及涉嫌淫秽、赌博、暴力、政治等文字内容；<br /> 7.4	游戏中不能存在与本游戏无关的宣传广告和链接。").append("state", 0),
						new BasicDBObject("name","游戏中断").append("caseId", 8).append("level", 10).append("info", "8.1	游戏运行中手机待机、关闭屏幕、手机来电或有其他优先操作，游戏必须暂停。<br /> 8.2	游戏中手机来电时，来电提示正常。接听、挂断电话等操作后，返回游戏，游戏正常运行。<br /> 8.3	游戏中手机来短信时，短信提示正常。回复短信后，返回游戏，游戏正常运行。").append("state", 0),
						new BasicDBObject("name","计费").append("caseId", 9).append("level", 10).append("info", "9.1	游戏计费必须与平台申报的计费点完全一致，无重复计费及扣费差异（多扣或少扣）。<br /> 9.2	Android游戏必须使用短代SDK。").append("state", 0),
						new BasicDBObject("name","游戏退出").append("caseId", 10).append("level", 10).append("info", "10.1	可使用菜单退出或使用手机自带退出键退出，如果有需要保存进度的游戏必须提供保存功能，退出后可继续游戏。").append("state", 0),
						new BasicDBObject("name","游戏卸载").append("caseId", 11).append("level", 10).append("info", "11.1	能正常卸载已经安装的游戏。").append("state", 0),
						new BasicDBObject("name","WAP游戏").append("caseId", 12).append("level", 10).append("info", "12.1	游戏登录时必须使用爱游戏登录(同步)接口。<br /> 12.2	必须有帮助说明。<br /> 12.3	必须有“返回爱游戏”链接。<br /> 12.4	游戏内不能有指向游戏以外的链接（指向爱游戏平台除外）。").append("state", 0)
						
				};
//				HashMap<Long,BasicDBObject> newCaseMap = new HashMap<Long, BasicDBObject>();
//				newCaseMap.put(3L, newCases[0]);
//				newCaseMap.put(4L, newCases[1]);
//				newCaseMap.put(6L, newCases[2]);
//				newCaseMap.put(10L, newCases[3]);
//				newCaseMap.put(7L, newCases[4]);
//				newCaseMap.put(8L, newCases[5]);
//				newCaseMap.put(13L, newCases[6]);
//				newCaseMap.put(15L, newCases[7]);
//				newCaseMap.put(16L, newCases[8]);
//				newCaseMap.put(17L, newCases[9]);
//				newCaseMap.put(18L, newCases[10]);
//				newCaseMap.put(999L, newCases[11]);
//				
//				newCaseMap.put(20L, newCases[0]);
//				newCaseMap.put(21L, newCases[1]);
//				newCaseMap.put(23L, newCases[2]);
//				newCaseMap.put(26L, newCases[3]);
//				newCaseMap.put(24L, newCases[4]);
//				newCaseMap.put(25L, newCases[5]);
//				newCaseMap.put(29L, newCases[6]);
//				newCaseMap.put(31L, newCases[7]);
//				newCaseMap.put(32L, newCases[8]);
//				newCaseMap.put(33L, newCases[9]);
//				newCaseMap.put(34L, newCases[10]);
				
				coll = mongo.getColl("TCTestCase");
				//将老所有的state改为-1
				q = new BasicDBObject();
				set = new BasicDBObject();
				update = new BasicDBObject(); 
				set.put("state", -1);
				update.put("$set", set);
				coll.update(q, update,false,true);
				System.out.println("old TCTestCase'state updated.");
				//添加新的TestCase
				int mId = 36;
				for (int i = 0; i < newCases.length; i++) {
					newCases[i].append("type", 0);
					newCases[i].append("_id", mId);
					coll.insert(newCases[i]);
					mId++;
				}
				for (int i = 0; i < newCases.length; i++) {
					newCases[i].append("type", 1);
					newCases[i].append("_id", mId);
					coll.insert(newCases[i]);
					mId++;
				}
				System.out.println("new TCTestCase added.");
				//更新老的结果记录
				HashMap<String,String> repla = new HashMap<String, String>();
				repla.put("1", "1");
				repla.put("2", "1");
				repla.put("3", "1");
				repla.put("4", "2");
				repla.put("6", "3");
				repla.put("7", "5");
				repla.put("8", "6");
				repla.put("9", "6");
				repla.put("10", "4");
				repla.put("11", "4");
				repla.put("12", "4");
				repla.put("13", "7");
				repla.put("14", "8");
				repla.put("15", "8");
				repla.put("16", "9");
				repla.put("17", "10");
				repla.put("18", "11");
				repla.put("19", "1");
				repla.put("20", "1");
				repla.put("21", "2");
				repla.put("22", "2");
				repla.put("23", "3");
				repla.put("24", "5");
				repla.put("25", "6");
				repla.put("26", "4");
				repla.put("27", "4");
				repla.put("28", "4");
				repla.put("29", "7");
				repla.put("30", "8");
				repla.put("31", "8");
				repla.put("32", "9");
				repla.put("33", "10");
				repla.put("34", "11");
				
				coll = mongo.getColl("TCTestUnit");
				q = new BasicDBObject();
				cur = coll.find(q);
				while (cur.hasNext()) {
					DBObject c = cur.next();
					if (c.get("re")!=null ) {
						BasicDBList ls = (BasicDBList) c.get("re");
						if (ls.size()>0) {
							for (int i = 0; i < ls.size(); i++) {
								DBObject r = (DBObject) ls.get(i);
								String rr = String.valueOf(r.get("caseId"));
								if (repla.containsKey(rr)) {
									r.put("caseId", repla.get(rr));
								}
							}
							//c.put("re", ls);
							coll.save(c);
						}
					}
				}
				System.out.println("TCTestUnit updated.");
//				coll = mongo.getColl("TCTask");
//				q = new BasicDBObject();
//				cur = coll.find(q);
//				while (cur.hasNext()) {
//					DBObject c =  cur.next();
//					String re = (String)c.get("result");
//					if (StringUtil.isStringWithLen(re, 2)) {
//						HashMap<String,Object> result = (HashMap<String, Object>) JSON.read(re);
//						if (result == null) {
//							System.out.println(re);
//							continue;
//						}
//						HashMap<String,Object> nr = new HashMap<String, Object>();
//						Iterator<Entry<String, Object>> it = result.entrySet().iterator();
//						while (it.hasNext()) {
//							Entry<String, Object> entry = it.next();
//							String k = entry.getKey().toString();
//							if (repla.containsKey(k)) {
//								String n = repla.get(k);
//								ArrayList<HashMap<String,Object>> l = (ArrayList<HashMap<String, Object>>) entry.getValue();
//								for (int i = 0; i < l.size(); i++) {
//									HashMap<String,Object> m = l.get(i);
//									m.put("caseId", n);
//								}
//								nr.put(n, l);
//							}
//						}
//						c.put("result", JSON.write(nr));
//						coll.save(c);
//					}
//				}
//				System.out.println("TCTask updated.");
				
			}
			System.out.println("finished-----");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public static void updatePGroup(String ip){
		String[] pGroup = {"华为C8500", "中兴N600", "中兴N606", "三星I559", "华为C8600", "酷派D539", "华为C8650+", "中兴N760", "三星I579", "三星I909MR", "酷派5855", "摩托XT800", "摩托XT800+", "摩托XT882", "摩托XT928"};
		HashMap<String,String> map = new HashMap<String, String>();
		for (int i = 0; i < pGroup.length; i++) {
			map.put(pGroup[i], pGroup[i]);
		}
		MongoConn mongo = new MongoConn();
		mongo.setIp(ip);
		mongo.setPort(27017);
		mongo.setDbName("tc");
		mongo.setUser("keel");
		mongo.setPwd("jsGame_1810");
		if (mongo.init()) {
			DBCollection coll = mongo.getColl("TCTestUnit");
			DBCursor cur = null;
			BasicDBObject q = new BasicDBObject();
			BasicDBObject state = new BasicDBObject("$gt",2000L);
			q.put("TID", state);
			cur = coll.find(q);
			while (cur.hasNext()) {
				DBObject c = cur.next();
				String p = c.get("phone").toString();
				if (map.containsKey(p)) {
					c.put("phone", "#"+p);
					coll.save(c);
				}
			}
		}
		
		
	}
	
	public static void main(String[] args) {
		String ip = "127.0.0.1";
	}
	
	/**
	 * @param ip
	 * @param port
	 * @param dbName
	 * @param user
	 * @param pwd
	 */
	public MongoConn(String ip, int port, String dbName, String user, String pwd) {
		super();
		this.ip = ip;
		this.port = port;
		this.dbName = dbName;
		this.user = user;
		this.pwd = pwd;
		//init();
	}

	/**
	 * 获取一个数据库连接,失败时返回null,本方法自身不处理失败情况
	 * @param colName
	 * @return DBCollection
	 */
	public DBCollection getColl(String colName){
		try {
			return db.getCollection(colName);
		} catch (Exception e) {
			log.error("getColl error!!", e);
			return null;
		}
	}
	
	
	/**
	 * 创建新表结构,先drop,后创建,同时生成索引 ,最后清除数据
	 * @param kc KObjConfig
	 * @return
	 */
	public boolean buildNewTable(KObjConfig kc){
		try {
			DaoInterface dao = kc.getDaoConfig().findDao();
			DBCollection coll = this.getColl(dao.getTableName());
			coll.drop();
			coll = this.getColl(dao.getTableName());
			KObject kobj = kc.getKobjSchema().createDefaultKObj();
			coll.save(new MongoWrapper(kobj));
			//index
			HashMap<String,KObjIndex> ins = kc.getKobjSchema().getIndexes();
			Iterator<Map.Entry<String, KObjIndex>> it = ins.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, KObjIndex> entry = it.next();
				KObjIndex ki = entry.getValue();
				coll.ensureIndex(new BasicDBObject(ki.getCol(), (ki.isAsc())?1:-1),ki.getCol(), ki.isUnique());
			}
			// 再清除
			coll.remove(new MongoWrapper(kobj));
		} catch (Exception e) {
			log.error("buildNewTable error!!", e);
			return false;
		}
		return true;
	}
	
	
	/**
	 * 创建DB对象
	 */
	public final boolean init(){
		try {
			if (mongo == null) {
//				ServerAddress sadd = new ServerAddress(this.ip, this.port);
//				MongoClientOptions opt = new MongoClientOptions();
//				MongoClientOptions o = new MongoClientOptions(null);
//				opt.autoConnectRetry = false;
//				opt.connectionsPerHost = this.connectionsPerHost;
//				opt.threadsAllowedToBlockForConnectionMultiplier = this.threadsAllowedToBlockForConnectionMultiplier;
//				opt.maxWaitTime = this.maxWaitTime;
				mongo = new MongoClient(this.ip, this.port);//new Mongo(sadd,opt);
			}
			db = mongo.getDB(this.dbName);
			boolean auth = false;
			if (!db.isAuthenticated()) {
				auth = db.authenticate(this.user, this.pwd.toCharArray());
			}else{
				auth = true;
			}
			if (!auth) {
				log.error("auth error! user:"+this.user);
			}else{
				log.info("=========== mongoConn init OK!["+this.dbName+"]["+this.ip+"] ============");
				return true;
			}
		} catch (UnknownHostException e) {
			log.error("init error!!", e);
			return false;
		} catch (MongoException e) {
			log.error("init error!!", e);
			return false;
		} catch (Exception e) {
			log.error("init error!!", e);
			return false;
		}
		return false;
	}
	
	

	
	/**
	 * 获取DB
	 * @return DB
	 */
	public final DB getDB(){
		return db;
	}
	
	/**
	 * 重新初始化Mongo及DB,可在参数变动后调用
	 * @return 是否初始化成功
	 */
	public final boolean reset(){
		if (mongo != null) {
			mongo.close();
		}
		return init();
	}
	
	/**
	 * 关闭Mongo
	 */
	public final void close(){
		mongo.close();
	}

	/**
	 * @return the ip
	 */
	public final String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public final void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public final int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public final void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the dbName
	 */
	public final String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName the dbName to set
	 */
	public final void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @return the user
	 */
	public final String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public final void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the pwd
	 */
	public final String getPwd() {
		return pwd;
	}

	/**
	 * @param pwd the pwd to set
	 */
	public final void setPwd(String pwd) {
		this.pwd = pwd;
	}

	/**
	 * @return the connectionsPerHost
	 */
	public final int getConnectionsPerHost() {
		return connectionsPerHost;
	}

	/**
	 * @param connectionsPerHost the connectionsPerHost to set
	 */
	public final void setConnectionsPerHost(int connectionsPerHost) {
		this.connectionsPerHost = connectionsPerHost;
	}

	/**
	 * @return the threadsAllowedToBlockForConnectionMultiplier
	 */
	public final int getThreadsAllowedToBlockForConnectionMultiplier() {
		return threadsAllowedToBlockForConnectionMultiplier;
	}

	/**
	 * @param threadsAllowedToBlockForConnectionMultiplier the threadsAllowedToBlockForConnectionMultiplier to set
	 */
	public final void setThreadsAllowedToBlockForConnectionMultiplier(
			int threadsAllowedToBlockForConnectionMultiplier) {
		this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
	}

	/**
	 * @return the maxWaitTime
	 */
	public final int getMaxWaitTime() {
		return maxWaitTime;
	}

	/**
	 * @param maxWaitTime the maxWaitTime to set
	 */
	public final void setMaxWaitTime(int maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	@Override
	public void exit() {
		mongo.close();
	}
	
	
	
}
