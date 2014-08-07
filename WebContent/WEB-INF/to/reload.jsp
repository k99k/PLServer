<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="com.k99k.khunter.*,com.k99k.tools.*" %>
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
<div id="rightTop" class="weight">Reload:</div>
<% 
String subact = StringUtil.objToStrNotNull(data.getData("subact"));
String sub = StringUtil.objToStrNotNull(data.getData("sub"));
String re = StringUtil.objToStrNotNull(data.getData("re"));
String re_name = StringUtil.objToStrNotNull(data.getData("re_name"));
//显示结果
if(!re.equals("")){ 
	out.print(re);
} 
//确认
else if(subact.equals("confirm")){
%>
<form id="confirmForm" action="<%=prefix%>/console/reload/<%=sub %>" method="post">
Reload <%=sub %> <%=re_name %>  ? 
<input type="hidden" name="reload_name" value="<%=re_name%>" />
<p><input type="submit" value=" Yes " /> [ <a href="<%=prefix %>/console/reload/show">Cancel</a> ]</p>
</form>
<% }
//确认是否创建新表数据
else if(subact.equals("buildNewDB")){
%>
<form id="newdbForm" action="<%=prefix%>/console/reload/newdb" method="post">
Rebuild DB data <span class="red bold"> All Data will be deleted!!!</span><br />
DataSource: <input type="text" name="ds" id="ds" value="" /> 
<p><input type="submit" value=" Yes " /> </p>
</form>
<% }
//确认是否创建新表数据
else if(subact.equals("buildDBTable")){
%>
<form id="newdbForm" action="<%=prefix%>/console/reload/retable" method="post">
Rebuild Table data <span class="red bold"> All Data in this table will be deleted!!!</span><br />
Table: <input type="text" name="tb" id="tb" value="" /> 
<p><input type="submit" value=" Yes " /> </p>
</form>
<% }
//显示reload菜单
else{ %>
<form id="reloadForm" action="<%=prefix%>/console/reload/confirm" method="post">
<ul>
<li class="hasReName"><input type="radio" name="sub" value="action" />action : <span></span></li>
<li class="hasReName"><input type="radio" name="sub" value="dao" />dao : <span></span></li>
<li class="hasReName"><input type="radio" name="sub" value="kobj" />kobj : <span></span></li>
<li><input type="radio" name="sub" value="allactions" />All Actions</li>
<li><input type="radio" name="sub" value="alldaos" />All Daos</li>
<li><input type="radio" name="sub" value="allkobjs" />All Kobjs</li>
<li><input type="radio" name="sub" value="system" />System</li>
</ul>
<input type="submit" value=" RELOAD " />
</form>
<script type="text/javascript">
$(function(){
var re_name = $("<input type=\"text\" name=\"re_name\" />");
$("#reloadForm input:radio").click(function(){
	$(".hasReName span").html("");
	var li = $(this).parent();
	if(li.attr("class") === "hasReName"){
		li.find("span").html(re_name);
	}
});
});
</script>
<% }%>
<hr />
<p>
&gt;&gt; <a href="<%=prefix %>/console/reload/stop">STOP all requert for maintenance</a><br />
&gt;&gt; <a href="<%=prefix %>/console/reload/start/?keelcontrolsall=true">Come back from maintenance</a>
</p>