<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="com.k99k.khunter.*,com.k99k.tools.*,java.util.*" %>
<%String prefix = KFilter.getPrefix(); %>
<div id="rightTop">
<span class="weight">KObj schema add</span> 
[ <a href="<%=prefix %>/console/editIni/?ini=kobj">edit json</a> | 
<a href="<%=prefix %>/console/kobj/schema_add">add schema</a>  | 
<a href="<%=prefix %>/console/kobj">list</a> |  
<a href="<%=prefix %>/console/kobj/ini_save">save INI</a> ] 
</div>
<div id="re"></div>
<div class="weight">KObj name:</div>
<div id="schema_key">schema_key</div>
<div class="weight">Intro:</div>
<div id="schema_intro">intro</div>
<div class="weight" id="dao">Dao: </div>
<label for="daoName">daoName:</label><input id="daoName" type="text" name="daoName" value="mongoDao"  /><br />
newdao : <input type="checkbox" id="isnewdao" />
<div id="newdaoconfig">
<label for="tableName">tableName:</label><input id="tableName" type="text" name="tableName" value="" /><br />
<label for="newDaoName">newDaoName:</label><input id="newDaoName" type="text" name="newDaoName" value="" /><br />
<label for="daoid">id:</label><input id="daoid" type="text" name="id" value="" /><br />
<input id="daoType" type="hidden" name="type" value="single" />
</div>
<div id="schema_daojson">{"tableName":"dsUser","newDaoName":"dsUserDao","daoName":"mongoDao","props":{"id":11,"type":"single"}}</div>
<div class="weight">Columns: - <span id="schema_col_add">(default值:除了string外,均需加标记:long和int表示为i@0,ArrayList为l@[],HashMap为m@{},Boolean为b@true)</span></div>
<table id="schema_columns">
<tr><th>column</th><th>required</th><th>default</th><th>type</th><th>intro</th><th>len</th><th>validator</th><th>EDIT</th></tr>
<tr><td>col</td><td>false</td><td>default</td><td>type</td><td>intro</td><td>0</td><td></td><td></td></tr>
</table>
<div class="weight">Indexes: - <span id="schema_index_add"></span></div>
<table id="schema_indexes">
<tr><th>column</th><th>asc</th><th>intro</th><th>type</th><th>unique</th><th>EDIT</th></tr>
<tr><td>col</td><td>false</td><td>intro</td><td>type</td><td>false</td><td></td></tr>
</table>
<hr />
<div id="json">
<div id="jsonPre">

</div>
<hr />
</div>
<input id="schema_add_json" type="button" name="schema_add_json" value="  SCHEMA JSON  " />  
<input id="schema_addBT" type="button" name="schema_addBT" value="  SCHEMA ADD  " />
<script type="text/javascript">
$(function(){
	
	//dao配置
	$("#newdaoconfig").hide();
	$("#isnewdao").click(function(){
		if(this.checked){
			$("#newdaoconfig").show();
		}else{
			$("#newdaoconfig").hide();
		}
	});
	var kobjName = null;
	var p_k = {msg:"#re"};
	$.hotEditor.act(p_k,"#schema_key");
	//intro
	//var p_intro = {msg:"#re"};
	$.hotEditor.act(p_k,"#schema_intro");
	//col
	var colType = "<select name=\"s\"><option value=\"0\">String</option><option value=\"1\">Integer</option><option value=\"2\">HashMap</option><option value=\"3\">ArrayList</option><option value=\"4\">Long</option><option value=\"5\">Boolean</option><option value=\"6\">Date</option><option value=\"7\">Double</option></select>";
	var p_cols = {
		subs:["td:eq(0)","td:eq(1)","td:eq(2)","td:eq(3)","td:eq(4)","td:eq(5)","td:eq(6)"],
		key : ["col","required","def","type","intro","len","validator"],
		editor : [$.hotEditor.inputTextEditor,$.hotEditor.selectEditor,$.hotEditor.inputTextEditor,colType,$.hotEditor.inputTextEditor,$.hotEditor.inputTextEditor,$.hotEditor.inputTextEditor],
		bts : "td:eq(7)",
		jsonTyps:["s","b","a","i","s","i","s"],
		jsonToStr:"schema_coljson",
		msg:"#re"
		,addTarget:">"
		,delBT:">"
	};
	$("#schema_columns tr:gt(0)").each(function (i) {
		$.hotEditor.act(p_cols,this);
	});
	//index
	var p_indexes = {
		subs:["td:eq(0)","td:eq(1)","td:eq(2)","td:eq(3)","td:eq(4)"],
		key : ["col","asc","intro","type","unique"],
		editor : [$.hotEditor.inputTextEditor,$.hotEditor.selectEditor,$.hotEditor.inputTextEditor,$.hotEditor.inputTextEditor,$.hotEditor.selectEditor],
		bts : "td:eq(5)",
		jsonTyps:["s","b","s","s","b"],
		jsonToStr:"schema_indexjson",
		msg:"#re"
		,addTarget:">"
		,delBT:">"
	};
	$("#schema_indexes tr:gt(0)").each(function (i) {
		$.hotEditor.act(p_indexes,this);
	});
	
	//合成json
	function toJson(){
		//var json = {};
		if(!$("#schema_key").find("input[name='schema_key']")){
			return false;
		}
		var schema_key = checkInput($("#schema_key").find("input[name='schema_key']"));
		kobjName = schema_key;
		var kobj = {};
		
		var dao = {};
		var daoName = checkInput("#daoName");
		if(daoName === false){return false;}else{
			dao["daoName"] = daoName;
		};
		if($("#isnewdao").get(0).checked){
			var tableName = checkInput("#tableName");
			var newdaoName = checkInput("#newDaoName");
			var daoid = checkInput("#daoid",1,"i");
			var daoType = checkInput("#daoType");
			if(tableName === false && newdaoName === false || daoid === false|| daoType === false){
				return false;
			}else{
				dao["tableName"] = tableName;
				dao["newDaoName"] = newdaoName;
				var prop = {"id":daoid,"type":daoType};
				dao["props"] = prop;
			};
		}
		kobj["dao"] = dao;
		var intro = checkInput($("#schema_intro").find("input[name='intro']"));
		if(intro === false){return false;};
		kobj["intro"] = intro;
		var cols  = [];
		var types = ["s","b","a","i","s","i","s"];
		var keys = ["col","required","def","type","intro","len","validator"];
		$("#schema_columns tr:gt(0)").each(function (i) {
			if(!$(this).hasClass("hotEditADD")){
				cols.push($.hotEditor.parseJson(types,keys,$.hotEditor.gather($(this))));
			}
		});
		kobj["columns"] = cols;
		var indexes = [];
		types = ["s","b","s","s","b"];
		keys = ["col","asc","intro","type","unique"];
		$("#schema_indexes tr:gt(0)").each(function (i) {
			if(!$(this).hasClass("hotEditADD")){
				indexes.push($.hotEditor.parseJson(types,keys,$.hotEditor.gather($(this))));
			}
		});
		kobj["indexes"] = indexes;
		//json[schema_key] = kobj;
		
		return kobj;
	};

	function checkInput(input,minLen,type){
		if(!minLen){minLen = 2;};
		var inp = ($(input))?$.trim($(input).val()):false;
		if(!inp){return false;};
		if(inp.length < minLen){return false;}
		if(type){inp = $.hotEditor.parseType(type,inp);};
		return inp;
	}
	
	$("#schema_addBT,#json").hide();
	//json显示
	var data = null;
	$("#schema_add_json").click(function(){
		data = toJson();
		if(data){
			$("#jsonPre").html($.toJSON(data));
			$("#json").show();
			$("#schema_addBT").show();
		}else{
			$("#json").hide();
			$("#schema_addBT").hide();
		}
	});	
	//提交
	$("#schema_addBT").click(function(){
		//提交
		if(data && kobjName){
			var paras = {"schema_key":kobjName};
			paras["schema_kcjson"] = $.toJSON(data);
			$.post("<%=prefix%>/console/kobj/kc_add", paras ,function(re) {
				//alert("success:"+data);
				$("#re").text(re);
			}).error(function() {return alert("post error!");});
		}else{
			alert("data error!");
		}
	});

});
</script>