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
<form id="testForm" action="" method="post">
<div>测试URL:
<input type="text" id="testUrl" name="testUrl" value="<%=prefix%>/console/test" />
<span class="paraOneValue" id="testUrlShow"></span> - <input type="button" id="testUrlSet" value="set" />
</div>
<div>测试请求:
<input type="text" id="paraName" name="paraName" value="paraName" />
<input type="text" id="paraValue" name="paraValue" value="paraValue" />
<input type="button" id="addPara" value="addPara" /><br />
<div id="paras">
</div>
<div id="paraOne" class="hide">
	<div><span class="paraOneName"></span> :  <span class="paraOneValue"></span> - <input type="button" value="DEL" /><input type="hidden" name="p1" value="p1" /></div>
</div>
<div>jsonName:
<input type="text" id="jsonName" name="jsonName" value="jsonName" />
<span class="weight" id="jsonNameShow"></span> - <input type="button" id="jsonNameSet" value="set" />
</div>
<textarea name="json" id="json" class="smallTA">{

}</textarea>
</div>
<div><input type="submit" id="submitTest" value=" Send Request " /></div>
</form>
<div>返回:</div>
<p id="re"></p>
<script type="text/javascript">
$(function(){
//设置testUrl
$("#testUrlSet").click(function(){
	if(!this.isSet){
		var val = $.trim($("#testUrl").val());
		$("#testUrlShow").text(val);
		$("#testUrlSet").val("edit");
		$("#testUrl").hide();
		this.isSet = true;
		$("#testForm").attr('action',val);
	}else{
		$("#testForm").attr('action',"");
		$("#testUrlSet").val("set");
		this.isSet = false;
		$("#testUrlShow").text("");
		$("#testUrl").show();
	}
});
//add Paras
$("#addPara").click(function(){
	var paraOne = $("#paraOne").find('div').clone();
	var pName = $.trim($("#paraName").val());
	var pVal = $.trim($("#paraValue").val());
	paraOne.find('.paraOneName').text(pName);
	paraOne.find('.paraOneValue').text(pVal);
	paraOne.find('input[type="hidden"]').attr('name',pName).val(pVal);
	paraOne.find('input[type="button"]').click(function(){
		$(this).parent().remove();
	});
	$("#paras").append(paraOne);
});
//set json name
$("#jsonNameSet").click(function(){
	if(!this.isSet){
		var val = $.trim($("#jsonName").val());
		$("#jsonNameShow").text(val);
		$("#jsonNameSet").val("edit");
		$("#jsonName").hide();
		this.isSet = true;
		$("#json").attr('name',val);
	}else{
		$("#json").attr('name',"json");
		$("#jsonNameSet").val("set");
		this.isSet = false;
		$("#jsonNameShow").text("");
		$("#jsonName").show();
	}
});
//ajax submit
$("#testForm").submit(function(event){
	// stop form from submitting normally
	event.preventDefault();
	$("#re").text("");
	//check
	if($("#testUrl").val() == 'console/test' || $("#json").attr('name')=='json'){
		alert("url or json is not ready.");
		return false;
	}
	// get some values from elements on the page:
	var submitBt = $("#submitTest");
	submitBt.attr("disabled","disabled");
	var $form = $(this), url = $form.attr( 'action' );
	var req = {};
	$("#testForm").find("textarea,input[type='hidden']").each(function (i) {
		req[this.name] = this.value;
		//alert(this.name+":"+this.value);
	});
	// Send the data using post and put the results in a div
	$.post( url, req ,
	  function( data ) {
	      $("#re").addClass("re").text(data);
	      submitBt.removeAttr("disabled");
	  }
	).error(function(data){
		submitBt.removeAttr("disabled");
		$("#re").addClass("re").text("err:"+data);
	});
	
});
	
});
</script>
