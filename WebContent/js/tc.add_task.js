var pJSON = {};
function addCompany(){
	return {c:$("#task_company").val()};
}
function aSubmit(){
	$("#add_form").submit();
};
function saveP(){$('#productForm').submit();}
function editP(){
	var e = ($.t<=1)?$('#productFS1'):$('#productFS2');
	e.appendTo($("#task_new"));$('#productFS3').appendTo($("#hide"));
}
function pSelect(pid){
	var pnu = (pid)?"pid="+pid:"p="+encodeURI($("#task_p_search").val());
	$.getJSON($.prefix+"/product/one?"+pnu,function(data){
		if(!data || data==""){alert("产品不存在!请确认产品名称正确.");return;}
		else{
			pJSON = data;
			$("#task_name_v").text(data.name);
			$("#task_p_id_v").text(data._id);
			$("#task_p_sys_v").text($("#task_p_sys > option[value="+data.sys+"]").text());
			$("#task_p_type_v").text($("#task_p_type > option[value="+data.type+"]").text());
			$("#task_p_net_v").text($("label[for='task_p_net"+data.netPort+"']").text());
			$("#task_p_acc_v").text($("label[for='task_p_acc"+data.netPort+"']").text());
			$("#task_p_fee_v").text(data.feeInfo);
			$("#productFS2").appendTo($("#hide"));$("#productFS3").appendTo($("#task_new"));$("#chooseType").hide();
		}
		next(1);
	}).error(function(){alert("查找产品出错!请刷新页面或稍后再试.");});
}
function next(i){
	switch (i) {
	case 1:
		$(".prev1,.next1,#p_e").show();
		gs(false);
		break;
	case 2:
		$(".next1,#p_e").hide();$("#swfBT,.u_ok").show();
		if($("#task_p_sys_v").text()=="WAP"){$("#fileupload").hide();$("#urlSet").show();}else{$("#urlSet").hide();$("#fileupload").show();};
		$("#task_new").append($("#uploadFS"));
		break;

	default:
		break;
	}
}
function gs(show){
	if( $("#chooseCompany")[0].need){$("#c_ok").remove();if(show){$("#chooseCompany").show();}else{$("#chooseCompany").hide().after($("<p id='c_ok'>公司:</p>").append(pJSON.company));}};
}
function pre(i){
	switch (i) {
	case 1:
		$(".next1,#p_e,#chooseType").show();gs(true);$(".prev1").hide();
		$("#productFS1,#productFS3").appendTo("#hide");
		break;
	case 2:
		editP();next(1);
		$("#uploadFS").appendTo($("#hide"));
		break;
	case 3:
		if(pJSON.sys!="2"){
			$("#swfBT,.u_ok,#fileupload .aButton").show();$("#taskFS").appendTo("#hide");
		}else{
			$("#urlInput").show();
			$("#urlSet .blueBold").text("").hide();
			$("#taskFS").appendTo("#hide");
		}
		break;
	default:
		break;
	}
}
function urlSet(){
	var v=$("#task_p_url").val();
	if(!v || $.trim(v).length<=0){alert("请正确填写WAP的入口URL地址");return;}
	else{pJSON.url=v;$("#task_p_json_h").html($.toJSON(pJSON));
	if($("#task_type_h").val()==""){$("#task_type_h").val($('input:radio[name=task_type]:checked').val());};
	$("#urlInput").hide();$("#urlSet .blueBold").text($("#task_p_url").val()).show();
	$("#taskFS").appendTo("#task_new");
	}
}

function filesSet(fileScope){
	//检测是否每个文件都指定了机型组
	var b = true,tmp = [],i=0;
	var scope = (fileScope) ? fileScope : "#upFiles";
	$(scope).find(".file_upload").each(function(){
		var v = $(this).find(".txtBox"),n = $(this).find(".filename").text(),j={"name":n,"fileName":$(this).find(".newname").text(),"size":$(this).find(".size").text(),"groups":[]};
		if(v.length<=0){b=false;return false;}
		else{
			v.each(function(){
				j.groups.push($(this).attr("title"));
			});
			tmp.push(j);
		}
		i++;
	});
	if(!b){alert("请为所有文件都指定适配参数!");return;}
	if(i==0){alert("请上传文件并指定适配参数!");return;}
	//生成文件json
	if(tmp.length>0){pJSON.files=tmp;$("#task_p_json_h").html($.toJSON(pJSON));}
	if($("#task_type_h").val()==""){$("#task_type_h").val($('input:radio[name=task_type]:checked').val());};
	$("#swfBT,.u_ok,#fileupload .aButton").hide();
	$("#taskFS").appendTo("#task_new");
}
function task_company(){
	$("#task_company_h").val($("#task_company").val());
}
var phTypes2 = [["1_240x320","1_320x480","1_480x800","1_480x854","1_960x540"],
               ["2_Androd2.1","2_Androd2.2","2_Androd2.3","2_Androd4.0","2_Androd4.2"],
               ["3_128M","3_256M","3_512M","3_1G"],
               ["华为C5900","天语E329","三星W239","三星F839","三星F339","天语E379","华为C7500","华为C7600","中兴R516","其他"]];
var apkPara=["分辨率","系统版本","最小内存"];
function choosePhType2(fu){
	var pt = pJSON.sys;
	if(!pt && pt != 0){pt=$.sys;pJSON.sys=$.sys;}
	$("#fu_"+fu).css("background-color","#FFF");
	if($("#phTypes").length>0){
		$("#phTypes").remove();
	}
		var tt = $("<div id='phTypes'></div>");
		if(pt ==0){
			makeChoosePh(tt,phTypes2[3],0,"机型组:");
			tt.append("<br />");
		}else if(pt ==1){
			makeChoosePh(tt,phTypes2[0],1,apkPara[0]+":");
			makeChoosePh(tt,phTypes2[1],2,apkPara[1]+":");
			makeChoosePh(tt,phTypes2[2],3,apkPara[2]+":");
		}
		tt.append("<a href=\"javascript:phtSet("+pt+");\" class=\"aButton\">确定<\/a>");
		tt[0].fu = fu;
		$("#fu_"+fu).find(".sok").remove();
		tt.appendTo($("#fu_"+fu));
	//}else{
	//	var p = $("#phTypes");p.find(".pht:checked").removeAttr("checked");
	//	p.appendTo($("#fu_"+fu));p[0].fu = fu;$("#fu_"+fu).find(".sok").remove();
	//}
}
function makeChoosePh(tt,dataArr,cate,title){
	if(title){
		$("<span>"+title+"</span>").appendTo(tt);
	}
	var iType=(cate==3) ? "radio" : "checkbox";
	for ( var i = 0; i < dataArr.length; i++) {
		var as = dataArr[i].split("_");
		if(cate==1 && as[0].charAt(0)=='-'){continue;}else if(cate==0){as[1]=as[0];};
		$("<input type='"+iType+"' class='pht' name='pht"+cate+"' id='pht"+cate+"_"+i+"' value='"+dataArr[i]+"' /><label for='pht"+cate+"_"+i+"'>"+as[1]+"</label> ").appendTo(tt);
	}
	tt.append("<br />");
	return tt;
}

function phtSet(sys){
	var ok = $("<div class='sok'></div>");var type = [0,0,0];
	$("#phTypes").find(".pht:checked").each(function(i){
		var v = $(this).val(),n=v;
		if(sys == 1){
			var a=v.split("_");  n = a[1],t=a[0]-1;
			if(a[0] == 1){
				var p=(this.id.split("_"))[1];
				phTypes2[0][p]="-"+phTypes2[0][p];
			};
			if(type[t] == 0){
				$("<br />").appendTo(ok);type[t]=1;
			}
		}
		$("<span class='txtBox' title="+v+">"+n+"</span>").appendTo(ok);
	});
	if(sys==1 && type.join(',') !='1,1,1'){
		var as = "";
		for(var i=0;i<3;i++){if(type[i]==0){as+=","+apkPara[i];}};
		as=as.substring(1);
		alert("请补充 "+as+" 参数!");return;
	}
	ok.appendTo($("#fu_"+$("#phTypes")[0].fu));
	$("#phTypes").appendTo($("#hide"));
}