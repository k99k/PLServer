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
KObjConfig kc = (KObjConfig)data.getData("schema_find");
if(kc == null){
	out.print("KObjConfig is null.");
	return;
}
%>
<div id="rightTop">
<span class="weight">KObj schema: <%=kc.getKobjName() %></span> 
[ <a href="<%=prefix %>/console/editIni/?ini=kobj">edit json</a> | 
<a href="<%=prefix %>/console/kobj">LIST</a> |  
<a href="<%=prefix %>/console/kobj/kobj_act/?schema_key=<%=kc.getKobjName() %>">query KObject</a> |
<a href="<%=prefix %>/console/kobj/kobj_act/?direct_act=add&amp;schema_key=<%=kc.getKobjName() %>">add KObject</a> | 
<a href="<%=prefix %>/console/kobj/ini_save">save INI</a> ] 
</div>
<div id="re"></div>
<div class="weight">Intro:</div>
<div id="schema_intro"><%=kc.getIntro() %></div>
<div class="weight">Dao: </div>
<div id="schema_daojson"><%=JSON.write(kc.getDaoConfig().toMap())%></div>
<div class="weight">Columns: - <span id="schema_col_add"></span></div>
<table id="schema_columns">
<tr><th>column</th><th>required</th><th>default</th><th>type</th><th>intro</th><th>len</th><th>validator</th><th style="width:140px;">EDIT</th></tr>
<%
KObjSchema ks = kc.getKobjSchema();
HashMap<String,KObjColumn> cols = ks.getKObjColumns();
ArrayList<KObjColumn> colList = ks.getColList();
StringBuilder sb = new StringBuilder();
if(colList.size() <= 0){
	sb.append("<tr><td>col</td><td>false</td><td>default</td><td>type</td><td>intro</td><td>0</td><td></td><td></td></tr>\r\n");
}else{
	for (Iterator<KObjColumn> iterator = colList.iterator(); iterator.hasNext();) {
		KObjColumn col = iterator.next();
		sb.append("<tr><td>");
		sb.append(col.getCol());
		sb.append("</td><td>");
		sb.append(col.isRequired());
		sb.append("</td><td>");
		sb.append(col.getDef());
		sb.append("</td><td>");
		sb.append(col.getType());
		sb.append("</td><td>");
		sb.append(col.getIntro());
		sb.append("</td><td>");
		sb.append(col.getLen());
		sb.append("</td><td>");
		sb.append(col.getValidatorString());
		sb.append("</td><td></td></tr>\r\n");
	}
}
out.println(sb);
%>
</table>
<div class="weight">Indexes: - <span id="schema_index_add"></span></div>
<table id="schema_indexes">
<tr><th>column</th><th>asc</th><th>intro</th><th>type</th><th>unique</th><th>EDIT</th></tr>
<%
HashMap<String,KObjIndex> indexes = ks.getIndexes();
sb = new StringBuilder();
if(indexes.size() <= 0){
	sb.append("<tr><td>col</td><td>false</td><td>intro</td><td>type</td><td>false</td><td></td></tr>\r\n");
}else{
	for (Iterator<String> iterator = indexes.keySet().iterator(); iterator.hasNext();) {
		String colKey = iterator.next();
		KObjIndex index = indexes.get(colKey);
		sb.append("<tr><td>");
		sb.append(index.getCol());
		sb.append("</td><td>");
		sb.append(index.isAsc());
		sb.append("</td><td>");
		sb.append(index.getIntro());
		sb.append("</td><td>");
		sb.append(index.getType());
		sb.append("</td><td>");
		sb.append(index.isUnique());
		sb.append("</td><td></td></tr>\r\n");
	}
}
out.println(sb);
%>
</table>
<script type="text/javascript">
$(function(){
	//intro
	var p_intro = {
		preParas : {schema_key:"<%= kc.getKobjName()%>",schema_part:"intro"},
		url:"<%=prefix%>/console/kobj/schema_update",
		key:["schema_intro"],
		msg:"#re"
	};
	$.hotEditor.act(p_intro,"#schema_intro");
	//dao
	var p_dao = {
		key : ["schema_daojson"],
		preParas:{schema_key:"<%= kc.getKobjName()%>",schema_part:"dao"},
		url:"<%=prefix%>/console/kobj/schema_update",
		editor:[$.hotEditor.textAreaEditor],
		msg:"#re"
	};
	$.hotEditor.act(p_dao,"#schema_daojson");
	//col
	var colType = "<select name=\"s\"><option value=\"0\">String</option><option value=\"1\">Integer</option><option value=\"2\">HashMap</option><option value=\"3\">ArrayList</option><option value=\"4\">Long</option><option value=\"5\">Boolean</option><option value=\"6\">Date</option><option value=\"7\">Double</option></select>";
	var p_cols = {
		preParas:{schema_key:"<%= kc.getKobjName()%>",schema_part:"col_edit"},
		subs:["td:eq(0)","td:eq(1)","td:eq(2)","td:eq(3)","td:eq(4)","td:eq(5)","td:eq(6)"],
		key : ["col","required","def","type","intro","len","validator"],
		url:"<%=prefix%>/console/kobj/schema_update",
		editor : [$.hotEditor.inputTextEditor,$.hotEditor.selectEditor,$.hotEditor.inputTextEditor,colType,$.hotEditor.inputTextEditor,$.hotEditor.inputTextEditor,$.hotEditor.inputTextEditor],
		bts : "td:eq(7)",
		jsonTyps:["s","b","a","i","s","i","s"],
		jsonToStr:"schema_coljson",
		msg:"#re"
		,addTarget:">"
		,delBT:">"
		,delPreParas:{schema_key:"<%= kc.getKobjName()%>",schema_part:"col_del"}
	};
	$("#schema_columns tr:gt(0)").each(function (i) {
		$.hotEditor.act(p_cols,this);
	});
	//index
	var p_indexes = {
		preParas:{schema_key:"<%= kc.getKobjName()%>",schema_part:"index_edit"},
		subs:["td:eq(0)","td:eq(1)","td:eq(2)","td:eq(3)","td:eq(4)"],
		url:"<%=prefix%>/console/kobj/schema_update",
		key : ["col","asc","intro","type","unique"],
		editor : [$.hotEditor.inputTextEditor,$.hotEditor.selectEditor,$.hotEditor.inputTextEditor,$.hotEditor.inputTextEditor,$.hotEditor.selectEditor],
		bts : "td:eq(5)",
		jsonTyps:["s","b","s","s","b"],
		jsonToStr:"schema_indexjson",
		msg:"#re"
		,addTarget:">"
		,delBT:">"
		,delPreParas:{schema_key:"<%= kc.getKobjName()%>",schema_part:"index_del"}
	};
	$("#schema_indexes tr:gt(0)").each(function (i) {
		$.hotEditor.act(p_indexes,this);
	});

});
</script>