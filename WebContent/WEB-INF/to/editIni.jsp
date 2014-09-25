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
<div id="rightTop" class="weight">Edit config:</div>
<% 
String subact = StringUtil.objToStrNotNull(data.getData("subact"));
String save = StringUtil.objToStrNotNull(data.getData("save"));
if(subact.equals("save")){ 
	out.print(save);
} else{ %>
<form id="eIniForm" action="<%=prefix %>/console/editIni/save" method="post">
<input type="hidden" id="ini" name="ini" value="kconfig" />
<textarea name="json" id="json"><%=data.getData("json") %></textarea>
<p><input type="submit" value=" Save " /> [ <a href="<%=prefix %>/console/<%=data.getData("ini")%>">Back</a> ]</p>
</form>
<% } %>