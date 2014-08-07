/**
 * 
 */
package com.k99k.khunter.acts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import com.k99k.khunter.Action;
import com.k99k.khunter.ActionManager;
import com.k99k.khunter.ActionMsg;
import com.k99k.khunter.KFilter;
import com.k99k.khunter.DaoInterface;
import com.k99k.khunter.DaoManager;
import com.k99k.khunter.ErrorCode;
import com.k99k.khunter.HttpActionMsg;
import com.k99k.khunter.KObjConfig;
import com.k99k.khunter.KObjDaoConfig;
import com.k99k.khunter.KObjManager;
import com.k99k.khunter.KObjSchema;
import com.k99k.khunter.KObject;
import com.k99k.tools.JSON;
import com.k99k.tools.StringUtil;
import com.mongodb.BasicDBObject;

/**
 * KObj的管理和查询
 * @author keel
 *
 */
public class KObjAction extends Action{

	public KObjAction(String name) {
		super(name);
	}
	
	static final Logger log = Logger.getLogger(ActionManager.class);
	
	public static final int ERR_CODE1 = 15;
	
	private static final String JS_HOTEDITER = "<script type=\"text/javascript\" src=\""+KFilter.getStaticPrefix()+"/js/jquery.json-2.2.min.js\"></script><script type=\"text/javascript\" src=\""+KFilter.getPrefix()+"/js/hotEdit.js\"></script>";
	private static final String JS_SNIPPET = "<link rel=\"stylesheet\" type=\"text/css\" href=\""+KFilter.getStaticPrefix()+"/css/jquery.snippet.min.css\" /><script type=\"text/javascript\" src=\""+KFilter.getPrefix()+"/js/jquery.snippet.min.js\"></script>";
	
	/**
	 * 保存配置,实际上是KObjManager的配置
	 * @return
	 */
	public final boolean save(){
		return KObjManager.saveIni();
	}
	
	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#act(com.k99k.khunter.ActionMsg)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ActionMsg act(ActionMsg msg) {
		HttpActionMsg httpmsg = (HttpActionMsg)msg;
		String subact = KFilter.actPath(msg, 3, "list");//httpmsg.getHttpReq().getParameter("subact");
		msg.addData("subact", subact);
		if (subact.equals("list")) {
			msg.addData("list", KObjManager.getKObjMap());
		}
		//schema_find
		else if(subact.equals("schema_find")){
			msg.addData("headerAdd", JS_HOTEDITER);
			String key  = httpmsg.getHttpReq().getParameter("schema_key");
			KObjConfig kc = KObjManager.findKObjConfig(key);
			msg.addData("right", "schema");
			msg.addData("schema_find", kc);
		}
		//schema_add
		else if(subact.equals("schema_add")){
			msg.addData("headerAdd", JS_HOTEDITER);
			msg.addData("right", "schema_add");
		}
		//schema_update
		else if(subact.equals("schema_update")){
			String key  = httpmsg.getHttpReq().getParameter("schema_key");
			String part  = httpmsg.getHttpReq().getParameter("schema_part");
			if (key == null || part == null) {
				msg.addData("[print]", "{\"re\":\"err\",\"d\":{\"schema_key\":\"schema_key or part error.\"}}");
				return super.act(msg);
			}
			KObjConfig kc = KObjManager.findKObjConfig(key);
			if (kc == null) {
				msg.addData("[print]", "{\"re\":\"err\",\"d\":{\"schema_key\":\"not found.\"}}");
				return super.act(msg);
			}
			//失败原因
			String rePrint = "{\"re\":\"err\",\"d\":{}";
			//----------------更新intro---------------
			if (part.equals("intro")) {
				String intro = httpmsg.getHttpReq().getParameter("schema_intro");
				if (StringUtil.isStringWithLen(intro, 0)) {
					kc.setIntro(intro);
					//print在jsp前面，所以不用remove jsp
					msg.addData("[print]", "{\"re\":\"ok\",\"d\":{\"schema_intro\":\""+intro+"\"}}");
					return super.act(msg);
				}
				rePrint = "{\"re\":\"err\",\"d\":{\"schema_intro\":\"intro input error\"}}";
			}
			//----------------更新dao---------------
			else if(part.equals("dao")){
				String daoJson = httpmsg.getHttpReq().getParameter("schema_daojson");
				if (StringUtil.isStringWithLen(daoJson, 2)) {
					KObjDaoConfig kdc = KObjDaoConfig.newInstance((HashMap<String,Object>)JSON.read(daoJson));
					if (kdc != null) {
						//更新KObjConfig
						kc.setDaoConfig(kdc);
						msg.addData("[print]", "{\"re\":\"ok\",\"d\":{\""+JSON.write(kdc.toMap())+"\"}}");
						return super.act(msg);
					}
					rePrint = "{\"re\":\"err\",\"d\":{\"schema_daojson\":\"KObjDaoConfig.newInstance error\"}}";
				}else{
					rePrint = "{\"re\":\"err\",\"d\":{\"schema_daojson\":\"daoJson error\"}}";
				}
			}
			//----------------更新column---------------
			else if(part.equals("col_edit")){
				String colJson = httpmsg.getHttpReq().getParameter("schema_coljson");
				if (StringUtil.isStringWithLen(colJson, 2)) {
					KObjSchema ks = kc.getKobjSchema();
					if(ks.setColumn((HashMap<String,Object>)JSON.read(colJson)) == 0){
						msg.addData("[print]", "{\"re\":\"ok\",\"d\":"+colJson+"}");
						return super.act(msg);
					}
					rePrint = "{\"re\":\"err\",\"d\":{\"schema_coljson\":\"ks.setColumn error\"}}";
				}else{
					rePrint = "{\"re\":\"err\",\"d\":{\"schema_coljson\":\"schema_coljson error\"}}";
				}
			}
			//----------------删除column---------------
			else if(part.equals("col_del")){
				String colJson = httpmsg.getHttpReq().getParameter("schema_coljson");
				if (StringUtil.isStringWithLen(colJson, 2)) {
					KObjSchema ks = kc.getKobjSchema();
					String k = ((HashMap<String,Object>)JSON.read(colJson)).get("col").toString();
					if(ks.containsColumn(k)){
						ks.removeColumn(k);
						msg.addData("[print]", "{\"re\":\"ok\",\"d\":"+colJson+"}");
						return super.act(msg);
					}
					rePrint = "{\"re\":\"err\",\"d\":{\"schema_coljson\":\"Column key not exsit.\"}}";
				}else{
					rePrint = "{\"re\":\"err\",\"d\":{\"schema_coljson\":\"schema_coljson error\"}}";
				}
			}
			//----------------更新index---------------
			else if(part.equals("index_edit")){
				String indexJson = httpmsg.getHttpReq().getParameter("schema_indexjson");
				if (StringUtil.isStringWithLen(indexJson, 2)) {
					KObjSchema ks = kc.getKobjSchema();
					if (ks.setIndex((HashMap<String,Object>)JSON.read(indexJson),true) == 0) {
						msg.addData("[print]", "{\"re\":\"ok\",\"d\":"+indexJson+"}");
						return super.act(msg);
					}
					rePrint = "{\"re\":\"err\",\"d\":{\"schema_indexjson\":\"setIndex error\"}}";
				}else{
					rePrint = "{\"re\":\"err\",\"d\":{\"schema_indexjson\":\"schema_indexjson error\"}}";
				}
			}
			//----------------删除index---------------
			else if(part.equals("index_del")){
				String indexJson = httpmsg.getHttpReq().getParameter("schema_indexjson");
				if (StringUtil.isStringWithLen(indexJson, 2)) {
					KObjSchema ks = kc.getKobjSchema();
					String k = ((HashMap<String,Object>)JSON.read(indexJson)).get("col").toString();
					if (ks.removeIndex(k)) {
						msg.addData("[print]", "{\"re\":\"ok\",\"d\":"+indexJson+"}");
						return super.act(msg);
					}
					rePrint = "{\"re\":\"err\",\"d\":{\"schema_indexjson\":\"removeIndex error\"}}";
				}else{
					rePrint = "{\"re\":\"err\",\"d\":{\"schema_indexjson\":\"schema_indexjson error\"}}";
				}
			}
			//失败的情况
			msg.addData("[print]", rePrint);
			return super.act(msg);	
		}
		//具体KObject的crud操作
		else if(subact.equals("kobj_act")){
			msg.addData("right", "kobj_crud");
			String kobj_act = StringUtil.toStrNotNull(httpmsg.getHttpReq().getParameter("kobj_act"),"req");
			String key  = httpmsg.getHttpReq().getParameter("schema_key");
			String rePrint = "{\"re\":\"err\",\"d\":{}";
			if (!StringUtil.isStringWithLen(key, 2) || !StringUtil.isStringWithLen(key, 2)) {
				msg.addData("[print]", "{\"re\":\"err\",\"d\":{\"schema_key\":\"schema_key or kobj_act error.\"}}");
				return super.act(msg);
			}
			KObjConfig kc = KObjManager.findKObjConfig(key);
			if (kc == null) {
				msg.addData("[print]", "{\"re\":\"err\",\"d\":{\"schema_key\":\"not found.\"}}");
				return super.act(msg);
			}
			msg.addData("key", key);
			//请求页
			if (kobj_act.equals("req")) {
				msg.addData("headerAdd", JS_HOTEDITER);
				String direct_act = (httpmsg.getHttpReq().getParameter("direct_act")==null)?"query":"add";
				msg.addData("direct_act", direct_act);
				msg.addData("kc", kc);
				return super.act(msg);
			}
			//查询kobj
			else if(kobj_act.equals("search")){
				try {
					String kobj_q = httpmsg.getHttpReq().getParameter("kobj_queryjson");
					String kobj_f = httpmsg.getHttpReq().getParameter("kobj_fieldsjson");
					String kobj_sort = httpmsg.getHttpReq().getParameter("kobj_sortjson");
					String kobj_skip = httpmsg.getHttpReq().getParameter("kobj_skip");
					String kobj_len = httpmsg.getHttpReq().getParameter("kobj_len");
					String kobj_hint = httpmsg.getHttpReq().getParameter("kobj_hint");
					HashMap<String,Object> query = StringUtil.isStringWithLen(kobj_q, 2) ? (HashMap<String,Object>)JSON.read(kobj_q) : null;
					HashMap<String,Object> fields = StringUtil.isStringWithLen(kobj_f, 2) ? (HashMap<String,Object>)JSON.read(kobj_f) : null;
					HashMap<String,Object> sortBy = StringUtil.isStringWithLen(kobj_sort, 2) ? (HashMap<String,Object>)JSON.read(kobj_sort) : null;
					if (query == null) {
						query = new HashMap<String,Object>(2);
					}
					int skip = (StringUtil.isDigits(kobj_skip))?Integer.parseInt(kobj_skip):0;
					//默认长度为20
					int len = (StringUtil.isDigits(kobj_len))?Integer.parseInt(kobj_len):20;
					HashMap<String,Object> hint = StringUtil.isStringWithLen(kobj_hint, 2) ? (HashMap<String,Object>)JSON.read(kobj_hint) : null;
					List list = kc.getDaoConfig().findDao().query(query, fields, sortBy, skip, len, hint);
					String d = JSON.write(list);
					msg.addData("[print]", "{\"re\":\"ok\",\"d\":{\"list\":"+d+"}}");
					return super.act(msg);
				}catch(Exception e){
					rePrint = "{\"re\":\"err\",\"d\":{\"kobj_queryjson\":\"excpetion:"+e.getMessage()+"\"}}";
				}
				
			}
			//删除kobj
			else if(kobj_act.equals("del")){
				String kobj_id = httpmsg.getHttpReq().getParameter("kobj_id");
				if (StringUtil.isDigits(kobj_id)) {
					long kid = Long.parseLong(kobj_id);
					if (httpmsg.getHttpReq().getParameter("delforever") == null) {
						if(kc.getDaoConfig().findDao().deleteOne(kid) != null){
							msg.addData("[print]", "{\"re\":\"ok\",\"d\":{\"id\":"+kid+"}}");
							return super.act(msg);
						}
					}else{
						if (kc.getDaoConfig().findDao().deleteForever(Long.parseLong(kobj_id))) {
							msg.addData("[print]", "{\"re\":\"ok\",\"d\":{\"id\":"+kid+",\"forever\":true}}");
							return super.act(msg);
						}
					}
				}else{
					rePrint = "{\"re\":\"err\",\"d\":{\"kobj_id\":\"kobj_id error\"}}";
				}
			}
			//add or update KObj
			else if (kobj_act.equals("update")) {
				String kobj_json = httpmsg.getHttpReq().getParameter("kobj_json");
				String act_add = httpmsg.getHttpReq().getParameter("act_add");
				boolean isAdd = (act_add != null);
				//如果有kobj_id参数则为更新,无则为添加
				String kobj_id = httpmsg.getHttpReq().getParameter("kobj_id");
				if (StringUtil.isStringWithLen(key, 2) && StringUtil.isStringWithLen(kobj_json, 2)) {
					Object jj = JSON.read(kobj_json);
					if (jj != null) {
						HashMap<String,Object> nk = (HashMap<String,Object>)jj;
						KObjSchema ks = kc.getKobjSchema();
						DaoInterface dao = kc.getDaoConfig().findDao();
						//去掉不存在的属性
						for (Iterator<String> iterator = nk.keySet().iterator(); iterator.hasNext();) {
							String prop = iterator.next();
							if (!ks.containsColumn(prop) && !containsProp(prop)) {
								nk.remove(prop);
							}
						}
						
						
						KObject newKObj = (StringUtil.isDigits(kobj_id))?dao.findOne(Long.parseLong(kobj_id)):ks.createEmptyKObj();
						boolean re = false;
						if (newKObj != null) {
							if (isAdd) {
								re = ks.setPropFromMapForCreate(nk, newKObj) && dao.save(newKObj);
							}else{
								re = dao.update(new BasicDBObject("_id",Long.parseLong(kobj_id)),new BasicDBObject("$set",nk),false,false);
							}
						}else{
							//kobj_id未找到
							msg.addData("[print]", "{\"re\":\"err\",\"kobj_json\":{\"id\":"+kobj_id+" can't find!}}");
							return super.act(msg);
						}
						if(re){
							msg.addData("[print]", "{\"re\":\"ok\",\"d\":{\"id\":"+newKObj.getId()+"}}");
							return super.act(msg);
						}else{
							rePrint = "{\"re\":\"err\",\"d\":{\"kobj_json\":\"setPropFromMap or dao.save error\"}}";
						}
					}else{
					rePrint = "{\"re\":\"err\",\"d\":{\"kobj_json\":\"readJsonString error\"}}";
					}
				}else{
				rePrint = "{\"re\":\"err\",\"d\":{\"kobj_json\":\"kobj_json error\"}}";
				}
			}
			//失败的情况
			msg.addData("[print]", rePrint);
			return super.act(msg);
		}
		//新增KobjConfig
		else if(subact.equals("kc_add")){
			String key  = httpmsg.getHttpReq().getParameter("schema_key");
			String kcjson  = httpmsg.getHttpReq().getParameter("schema_kcjson");
			String rePrint = "para error";
			if (StringUtil.isStringWithLen(key, 2) && StringUtil.isStringWithLen(kcjson, 2)) {
				int re = KObjManager.createKObjConfigToDB(key, (HashMap<String,Object>)JSON.read(kcjson));
				if (re == 0) {
					msg.addData("[print]", "ok");
					return super.act(msg);	
				}
				rePrint = ErrorCode.getErrorInfo(8, re);
			}
			//失败的情况
			msg.addData("[print]", "err:"+rePrint);
			return super.act(msg);	
		}
		//保存KObjManager的配置文件
		else if(subact.equals("ini_save")){
			String update  = httpmsg.getHttpReq().getParameter("update");
			if (update == null) {
				msg.addData("right", "kobjmgr_saveini");
				msg.addData("headerAdd", JS_SNIPPET);
				msg.addData("newIni", JSON.writeFormat(KObjManager.getCurrentIni(), 6));
				return super.act(msg);
			}else{
				String re = "err:savaIni error.";
				if(KObjManager.saveIni()){
					re = "ok:ok";
				}
				msg.addData("[print]", re);
				return super.act(msg);
			}
		}
		//查询dao的Names,返回以,号分开的所有daoNames
		else if(subact.equals("dao_names")){
			ArrayList<String> list = DaoManager.getDaoNames();
			StringBuilder sb = new StringBuilder();
			for (Iterator<String> it = list.iterator(); it.hasNext();) {
				String str = it.next();
				sb.append(str).append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			msg.addData("[print]", sb.toString());
			return super.act(msg);
		}
		//查询
		else if(subact.equals("search")){
			String key  = httpmsg.getHttpReq().getParameter("search_key");
			if (key != null) {
				HashMap<String, KObjConfig> re = KObjManager.searchKObjList(key);
				msg.addData("list", re);
			}else{
				msg.addData("list", KObjManager.searchKObjList(""));
			}
		}
		//其他未知subact
		else{
			msg.addData("subact", "list");
			msg.addData("list", KObjManager.searchKObjList(""));
		}
		return super.act(msg);
	}

	private final static KObject kobj = new KObject();
	private final static boolean containsProp(String prop){
		return kobj.containsProp(prop);
	}

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#init()
	 */
	@Override
	public void init() {
	}
	

	/* (non-Javadoc)
	 * @see com.k99k.khunter.Action#getIniPath()
	 */
	@Override
	public String getIniPath() {
		return "kobj.json";
	}

	@Override
	public void exit() {
		
	}
	
	
}
