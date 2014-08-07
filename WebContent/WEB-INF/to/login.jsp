<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="com.k99k.khunter.*,com.k99k.tools.*" session="false" %>
<%
String sPrefix = KFilter.getStaticPrefix();
String prefix = KFilter.getPrefix();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>KHT console</title>
<link rel="stylesheet" type="text/css" href="<%=sPrefix %>/css/style.css" />
</head>
<body>

<div style=" position: relative;width: 380px;margin: 0 auto;padding:150px 0 0 0;">
	<div id="loginTitle" style="width:360px;font-size:120%;padding:10px 0;text-align: center;">
		Console Login
	</div>
<form action="<%=prefix %>/console/login" method="post" id="consoleLogin">
	<p><label for="form_name">用户名:</label><br />
	<input type="text" name="form_name" id="form_name" style="width:300px;" /></p>
	<p><label for="form_pwd">密 &nbsp;&nbsp;码:</label><br />
	<input type="password" name="form_pwd" id="form_pwd" style="width:300px;" /></p>
	<p style="float:left;padding-top:10px;"><input type="checkbox" name="saveLogin" value="" id="saveLogin"/><label for="saveLogin"> 保存登录状态</label></p>
	<p style="float:right;padding:10px 20px 0 0;"><input type="submit" value="登录" style="width:80px;" /></p>
	<div class="clear"></div>
</form>
	<div class="tx_center">
		 &copy; Keel 2011-8 All rights reserved.
	</div>
</div>

</body>
</html>