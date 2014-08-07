<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="com.k99k.khunter.*,com.k99k.tools.*,java.util.*" %>
<%
Object o = request.getAttribute("[jspAttr]");
HttpActionMsg data = null;
if(o != null ){
	data = (HttpActionMsg)o;
}else{
	out.print("attr is null.");
	return;
}
String prefix = KFilter.getPrefix();
%>
<div id="rightTop">
<span class="weight">KObj config: </span> [ <a href="<%=prefix %>/console/editIni/?ini=kobj">edit json</a> | 
<a href="<%=prefix %>/console/kobj/schema_add">add schema</a>  | 
<a href="<%=prefix %>/console/kobj/ini_save">save INI</a>]
<form id="f_search" action="<%=prefix%>/console/kobj/search" method="post">
<input type="text" id="search_key" name="search_key" /><input type="submit" value="search" />
</form>
</div>
<% 
//String subact = StringUtil.objToStrNotNull(data.getData("subact"));
//String save = StringUtil.objToStrNotNull(data.getData("save"));
Object od = data.getData("list");
if(od==null){
	out.print("list not exist.");
	return;
}
try{
	HashMap<String, KObjConfig> kcMap = (HashMap<String, KObjConfig>)od;
	if(kcMap.size()<=0){
		out.print("Empty.");
		return;
	}else{
		StringBuilder sb = new StringBuilder();
		for (Iterator<String> it = kcMap.keySet().iterator(); it.hasNext();) {
			String kobjName =  it.next();
			KObjConfig kc = kcMap.get(kobjName);
			//HashMap<String,Object> table = (HashMap<String,Object>)kobjMap.get(kobjName);
			sb.append("<p class='tb_list' ><span class='orangeBold'>");
			sb.append(kobjName).append("</span> ");
			//sb.append(kc.getIntro());
			sb.append(" [ <a href='")
			.append(prefix)
			.append("/console/kobj/schema_find/?schema_key=")
			.append(kobjName)
			.append("'>schema</a> | <a href='")
			.append(prefix)
			.append("/console/kobj/kobj_act/?schema_key=");
			sb.append(kobjName);
			sb.append("'>query KObject</a>  | <a href='")
			.append(prefix)
			.append("/console/kobj/kobj_act/?direct_act=add&amp;schema_key=");
			sb.append(kobjName);
			sb.append("'>add KObject</a> ] - ");
			sb.append(kc.getIntro());
			sb.append("</p>\r\n");
			
		}
		out.print(sb.toString());
	}
}catch(Exception e){
	out.print("Error:"+e.getMessage());
	return;
}
 %>
