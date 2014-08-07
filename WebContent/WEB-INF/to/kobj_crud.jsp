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
String kobj_key = data.getData("key").toString();
KObjConfig kc = (KObjConfig)data.getData("kc");
KObjSchema ks = kc.getKobjSchema();
//直接命令,为query或add
String direct_act = data.getData("direct_act").toString();
String[] colArr = ks.getAllColNamesWithRequiredTag();
%>
<div id="rightTop">
<span class="weight">KObj [<%=kobj_key %>] actions: </span>
<input type="button" id="act_query" value="Query" /> <input type="button" id="act_add" value="Add/Update" /> 
[<a href="<%=prefix %>/console/kobj">Schema List</a> | <a href="<%=prefix %>/console/kobj/schema_find/?schema_key=<%=kobj_key %>">Schema</a>]
</div>
<div id="query_form">
<span class="weight">Query:</span> <input type="checkbox" id="q_custom_check" /><label for="q_custom_check">Custom query</label>
<div id="q_normal">
<select id="q_select">
<%
StringBuilder sb = new StringBuilder();
for(int i = 0;i<colArr.length;i++){
	String s = colArr[i];
	if(s.startsWith("*")){
		s = colArr[i].substring(1);
		sb.append("<option value='").append(s).append("'>").append(s).append("</option><span class='red weight'>*</span>");
		continue;
	};
	sb.append("<option value='").append(s).append("'>").append(s).append("</option>");
}
String colSelect = sb.toString();
out.print(colSelect);
%></select>
<select id="q_opt">
<option value=":">==</option>
<option value="$gt">&gt;</option>
<option value="$lt">&lt;</option>
<option value="$gte">&gt;=</option>
<option value="$lte">&lt;=</option>
</select> <input type="text" name="q_select_val" id="q_select_val" /> (string need "")</div>
<div id="q_custom_div">
<textarea id="q_custom" name="q_custom" class="smallTA">{}</textarea>
</div>
<span class="weight">Fields:</span><br />
<div id="q_f" style="width:60%;">
<%
sb = new StringBuilder();
for(int i = 0;i<colArr.length;i++){
	String s = colArr[i];
	if(s.startsWith("*")){
		s = s.substring(1);
		sb.append("<input type=\"checkbox\" id=\"q_field").append(i).append("\" name=\"q_field\" checked=\"checked\" value=\"").append(s).append("\" /><label for=\"q_field").append(i).append("\" class=\"red weight\">").append(s).append("</label> ");
		continue;
	}
	sb.append("<input type=\"checkbox\" id=\"q_field").append(i).append("\" name=\"q_field\" value=\"").append(s).append("\" /><label for=\"q_field").append(i).append("\">").append(s).append("</label> ");
}
String colCheckBox = sb.toString();
out.print(colCheckBox);
%>
</div>
<br /><span class="weight">Skip:</span> <input type="text" id="q_skip" value="0" /> <span class="weight">Len: </span><input type="text" id="q_len" value="20" /><br />
<br /><span class="weight">Order by:</span><input type="checkbox" name="q_sortby" id="q_sortby" /><select name="q_order" id="q_order"><%=colSelect %></select> <select name="q_asc" id="q_asc"><option value="1">ASC</option><option value="-1">DESC</option></select><br />
<span class="weight">Hint:</span><input type="checkbox" name="q_isHint" id="q_isHint" />
<div style="width:50%;" id="q_hit_div">
<%
HashMap<String,KObjIndex> indexes = ks.getIndexes();
sb = new StringBuilder();
for(Iterator<String> it = indexes.keySet().iterator(); it.hasNext();){
	String indexKey = it.next();
	sb.append("<input type=\"checkbox\" name=\"q_hint\" value=\"").append(indexKey).append("\" />").append(indexKey).append(" ");
}
String indexesCheckBox = sb.toString();
out.print(indexesCheckBox);
%>
</div>
<p>
<input type="button" id="q_submit" value="  GO  " />
</p>
</div>
<div id="add_form">
<div><span id="au_tag" class="weight">Add a new kobj:</span><input type="checkbox" name="u_c" id="u_c" /><input type="text" name="u_kobj_id" id="u_kobj_id" /></div>
<div id="a_ins">
<%
sb = new StringBuilder();
for(int i = 0;i<colArr.length;i++){
	String s = colArr[i];
	if(s.startsWith("*")){
		s = s.substring(1);
		sb.append("<label class=\"red weight\" for=\"a_field").append(i).append("\">").append(s).append(": </label> <input type=\"text\" id=\"a_field").append(i).append("\" name=\"").append(s).append("\" value=\"\" /><br />");
		continue;
	}
	sb.append("<label for=\"a_field").append(i).append("\">").append(s).append(": </label> <input type=\"text\" id=\"a_field").append(i).append("\" name=\"").append(s).append("\" value=\"\" /><br />");
}
String coladd = sb.toString();
out.print(coladd);
%>
</div>
<div id="a_area_div">
<textarea id="a_area" class="smallTA"></textarea>
</div>
<div>
<input type="button" id="a_json" value="  TO JSON  " /> <input type="button" id="a_submit" value="  ADD KObj  " /> 
</div>
</div>
<div id="reDiv">
<div id="re"></div>
<table id="reTable">

</table>
</div>
<script type="text/javascript">
$(function(){
	var key = "<%=kobj_key%>";
	var isAdd = "<%=direct_act%>";
	var showAdd = false;
	if(isAdd === "add"){
		$("#query_form").hide();
		$("#add_form").show();
		showAdd = true;
	}else{
		$("#query_form").show();
		$("#add_form").hide();
		showAdd = false;
	}
	function clearRE(){
		$("#re").text("");
		//$("#reTable").html("");
		//$("#reDiv").hide();
	}
	$("#act_query").click(function(){
		if(showAdd){
			$("#add_form").slideUp("fast",function(){$("#query_form").slideDown("fast");});
			showAdd = false;
			clearRE();
		}
	});
	$("#act_add").click(function(){
		if(!showAdd){
			$("#query_form").slideUp("fast",function(){$("#add_form").slideDown("fast");});
			showAdd = true;
			clearRE();
		}
	});
	$("#reDiv").hide();
	//update prepare-----------------
	var isUpdate = false;
	$("#u_kobj_id").hide();
	$("#u_c").click(function(){
		if(this.checked){
			$("#u_kobj_id").show();
			isUpdate = true;
			$("#a_submit").val(" UPDATE KObj ");
			$("#au_tag").text("Update kobj id:");
		}else{
			$("#u_kobj_id").hide();
			isUpdate = false;
			$("#a_submit").val(" ADD KObj ");
			$("#au_tag").text("Add a new kobj:");
		}
	});
	//add or update------------------------
	$("#a_area_div").hide();$("#a_submit").hide();
	$("#a_json").click(function(){
		var json = "{\r\n";
		$("#a_ins input").each(function(){
			if($(this).val() != ""){
				var v = $(this).val();
				v = (isNaN(v) && (!(/(^\[[^\]]*\]$)|(^\{[^\}]*\}$)/.test(v.toString()))))?"\""+v+"\"":v;
				json = json + "\"" + $(this).attr("name")+"\":" + v+",\r\n";
			}
		});
		json = (json === "{\r\n") ? "" : json.substr(0,json.length-3)+"\r\n}" ;
		$("#a_area").val(json);
		$("#a_area_div").show();
		$("#a_submit").show();
	});
	$("#a_submit").click(function(){
		var url = "<%=prefix %>/console/kobj/kobj_act/?kobj_act=update&schema_key="+key;
		if(isUpdate && !isNaN($("#u_kobj_id").val())){
			url = url+"&kobj_id="+$("#u_kobj_id").val();
		}else{
			url = url+"&act_add=true";
		}
		var a = {kobj_json:$("#a_area").val()};
		//console.log(a);
		$.post(url,a,function(data){
			$("#re").text(data);
			$("#reTable").html("");
			$("#reDiv").show();
		});
	});
	
	//query----------------------
	$("#q_custom_div").hide();
	var q_custom = false;
	$("#q_custom_check").click(function(){
		if(!this.checked){
			$("#q_custom_div").slideUp("fast",function(){$("#q_normal").slideDown();});
			q_custom = false;
		}else{
			$("#q_normal").slideUp("fast",function(){$("#q_custom_div").slideDown();});
			q_custom = true;
		}
	});
	var q_sort = false;
	$("#q_order").hide();$("#q_asc").hide();
	$("#q_sortby").click(function(){
		if(this.checked){
			$("#q_order").show();$("#q_asc").show();
			q_sort = true;
		}else{
			$("#q_order").hide();$("#q_asc").hide();
			q_sort = false;
		}
	});
	//q_isHint
	var q_hint = false;
	$("#q_hit_div").hide();
	$("#q_isHint").click(function(){
		if(this.checked){
			$("#q_hit_div").show();
			q_hint = true;
		}else{
			$("#q_hit_div").hide();
			q_hint = false;
		}
	});
	//query
	var q_prepare = function(){
		var q = {};
		if(q_custom){
			q.kobj_queryjson = $.quoteString($("#q_custom").val());
		}else{
			var opt = $("#q_opt").val();
			var s = "\""+$("#q_select").val()+"\"";
			if($("#q_select_val").val() != ""){
				if(opt === ":"){
					s = "{"+s+":"+$("#q_select_val").val()+"}";
				}else{
					s = "{"+s+":{\""+opt+"\":"+$("#q_select_val").val()+"}}";
				}
			}else{s = "{}";}
			q.kobj_queryjson = s;
		}
		//
		var f = "{";
		$("#q_f input:checked").each(function(i){
			f=f+"\""+$(this).val()+"\":1,";
		});
		f = (f === "{") ? "{\"id\":1}" : f.substr(0,f.length-1)+"}" ; 
		q.kobj_fieldsjson = f;
		//
		if(q_sort){
			var sort = "{\""+$('#q_order').val()+"\":"+$('#q_asc').val()+"}";
			q.kobj_sortjson = sort;
		}
		//skip,len
		q.kobj_skip = $("#q_skip").val();
		q.kobj_len = $("#q_len").val();
		//hint
		if(q_hint){
			var h = "{";
			$("#q_hit_div input:checked").each(function(i){
				h=h+"\""+$(this).val()+"\":1,";
			});
			h = (h === "{") ? null : h.substr(0,h.length-1)+"}" ; 
			q.kobj_hint = h;
		};
		return q;
	};
	$("#q_submit").click(function(){
		var q = q_prepare();
		var url = "<%=prefix %>/console/kobj/kobj_act/?kobj_act=search&schema_key="+key;
		//console.log(q);
		$.post(url,q,function(data){
			//$("#reDiv").text(data).show();
			showRE(data);
		});
	});
	//显示数据
	function showRE(data){
		$("#reTable").html("");
		var backTH = $("<tr></tr>");
		var re = $.parseJSON(data);
		if(re && re.re &&  re.re === "ok" && re.d && re.d.list){
			if(re.d.list.length == 0){
				$("#reDiv").show();$("#re").text("Nothing was found.");return;
			}else{
				//处理th,取字段属性最多的为表头
				var max = 0,maxii = 0;
				for ( var i = 0; i < re.d.list.length; i++) {
					var jj = 0;
					for(k in re.d.list[i]){
						jj++;
					}
					if(max < jj){max = jj;maxii=i;}
				}
				var hdata = re.d.list[maxii];
				var cols = [];
				var j = 0;
				for(k in hdata){
					cols[j] = k;
					backTH.append($("<th>"+k+"</th>"));
					$("#reTable").append(backTH);
					j++;
				}
				//处理数据
				for ( var i = 0; i < re.d.list.length; i++) {
					var tr = $("<tr></tr>");
					for ( var ii = 0; ii < cols.length; ii++) {
						var d = re.d.list[i][cols[ii]];
						d = (d == null) ? "" : ((typeof(d) === 'object')?$.toJSON(d):d);
						tr.append($("<td>"+d+"</td>"));
					}
					$("#reTable").append(tr);
				}
			}
		}else{
			$("#reDiv").show();$("#re").text(data);return;
		}
		$("#reDiv").show();
	}
});
</script>

