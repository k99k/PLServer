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
String sPrefix = KFilter.getStaticPrefix();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>KHT console</title>
<link rel="stylesheet" type="text/css" href="<%=sPrefix %>/css/style.css" />
<script src="<%=sPrefix %>/js/jquery.js" type="text/javascript"></script>
<%=StringUtil.objToStrNotNull(data.getData("headerAdd")) %>
</head>
<body>

<div id="wrapper">
	<div id="header">
		<h1>Console</h1>
	</div>
	
	<div id="mainBody">
		<div id="sideNav"><jsp:include page="left.jsp" flush="false" /></div>
		<div id="mainContent">
		<%if(data.getData("right")==null ){ %>
			<p>empty right.</p>
		<%}else{
			String r = data.getData("right").toString()+".jsp";
			request.setAttribute("[jspAttr]",data);
			%>
			<jsp:include page="<%=r %>" flush="true" />
		<%} %>
		<div class="clear"></div>
		</div>
	</div>
		<div class="clear"></div>
	<div id="footer">
		<div id="copyright">
			Copyright:&copy;2010-2012 KEEL.SIKE All rights reserved. 
		</div>
	</div>
</div>

</body>
</html>