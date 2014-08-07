<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="com.k99k.khunter.*" %>
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
<div>准备保存的新ini:</div>
<pre class="json" id="kobjmgr_ini">

<%=data.getData("newIni") %>

</pre>
<div><input type="button" id="saveIni" value=" Save KObjManager INI " /></div>
<div>返回:</div>
<p id="re"></p>
<script type="text/javascript">
$(function(){
	//json样式
	$("#kobjmgr_ini").snippet("javascript",{style:"ide-eclipse",transparent:false,showNum:false,collapse:true,menu:false});
	//保存动作
	$("#saveIni").click(function(){
		var $saveBT = $(this);
		var url = "<%=prefix%>/console/kobj/ini_save";
		var req = {update:true};
		$saveBT.attr("disabled","disabled");
		$.post(url, req, function(data) {
				$("#re").addClass("re").text(data);
				$saveBT.removeAttr("disabled");
			});
		});
	});
</script>
