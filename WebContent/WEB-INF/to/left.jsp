<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="com.k99k.khunter.*" %>
<%String prefix = KFilter.getPrefix(); %>
<div>Control</div>
<ul>
<li><a href="<%=prefix %>/console/state" >home</a></li>
<li><a href="<%=prefix %>/console/exit" >logout</a></li>
<li><a href="<%=prefix %>/console/reload/show" >reload</a></li>
<li><a href="<%=prefix %>/console/reload/confirm/?sub=allactions" >reload Actions</a></li>
<li><a href="<%=prefix %>/console/reload/confirm/?sub=alldaos" >reload Daos</a></li>
<li><a href="<%=prefix %>/console/reload/confirm/?sub=allkobjs" >reload KObjs</a></li>
<li><a href="<%=prefix %>/console/editIni" >editIni</a></li>
<li><a href="<%=prefix %>/console/test" >test</a></li>
<li><a href="<%=prefix %>/console/reload/buildNewDB" >buildAllDBData</a></li>
<li><a href="<%=prefix %>/console/reload/buildDBTable" >reBuild Table</a></li>
</ul>
<div>Manage</div>
<ul>
<li><a href="<%=prefix %>/console/kobj" >KObject</a></li>
</ul>
<div>Config</div>
<ul>
<li><a href="<%=prefix %>/console/config" >config paras</a></li>
<li><a href="#" >classPath</a></li>
<li><a href="#" >iniPath</a></li>
<li><a href="#" >updates</a></li>
<li><a href="#" >version</a></li>
<li><a href="#" >info</a></li>
</ul>
<div>User</div>
<ul>
<li><a href="<%=prefix %>/console/groups" >groups</a></li>
<li><a href="<%=prefix %>/console/users" >users</a></li>
</ul>
<div>Statistics(统计)</div>
<ul>
<li><a href="<%=prefix %>/console/changelogs" >changeLogs</a></li>
</ul>