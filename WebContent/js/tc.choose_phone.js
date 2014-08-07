//-----------机型选择,要求页面中有#hide和#fu_[i]的实体包列表-------------------
var phoneType = {0:"其他",1:"240x320",2:"320x480",3:"480x800",4:"480x854",5:"960x540",10:"主机型"};
var allPData = [];
var aaData = [],gMap={};
var cGroup = 1;
var chooseDiv = "<div id=\"choosePhone\" class=\"inBox\" style=\"width:95%;\"> <div style=\"padding:10px;\"> <div id=\"selectedPhones\"> <div class=\"inBoxTitle\">已选中机型：<span class=\"gray normal\">(点击删除)</span>	</div> <div class=\"inBoxContent\" style=\"border-bottom: 1px dotted #aaa;background-color:#FFF;\"> <table width=\"100%\"> <tr><td id=\"td_in\"></td> <td style=\"width:90px;\"><a class=\"aButton\" href=\"javascript:selectOK();\" style=\"width:70px;text-align:center;\">确定所选</a></td></tr> </table> </div> </div> <div id=\"phones\"> <div id=\"phoneCates\" class=\"inBoxTitle\">备选机型组：<span class=\"gray normal\">(点击组名选择分组,点击机型名或全选进行选择,搜索框可在<span class=\"black bold\">该类系统所有机型</span>中筛选)</span></div><span class=\"aButton phoneCate\"><label for=\"phone_fast\">搜索:</label><input style=\"padding:3px 5px;margin:0;width:100px;\" type=\"text\" name=\"phone_fast\" id=\"phone_fast\" /></span> <div class=\"inBoxContent\" style=\"border-bottom:1px dotted #aaa;border-top:1px dotted #aaa;background-color:#FFF;\"> <table width=\"100%\"> <tr><td id=\"td_out\"><div id=\"g999\"></div></td> <td style=\"width:60px;\"><a class=\"aButton\" href=\"javascript:chooseAll();\">全选</a></td></tr> </table> </div> </div> </div> </div>";

function selectOK(){
	var ok = $("<div class='sok'></div>");
	$("#td_in").find(".phone1").each(function(i){
		$("<span class='txtBox' id='s"+this.id+"'>"+$(this).text()+"</span>").appendTo(ok);
	});
	ok.appendTo($("#cfu_"+$("#choosePhone").data("fu")));
	clearIn();$("#choosePhone").appendTo("#hide");
}
function selectPhone(i){
	
	$("#cfu_"+i).css("background-color","#FFF");
	if(allPData.length == 0){
		//abox("Loading...","请稍侯...");
		$.getJSON($("#choosePhone").data("url"),function(sData){
			if(sData==""){alert("产品操作系统不正确.请返回上一步重设.");return;}
			var data = sData.gg;
			for(var i=1,j=data.length;i<j;i++){
				var gg = $("<a class=\"aButton phoneCate\" href=\"javascript:showGroup("+data[i].g+");\" id='ga"+data[i].g+"'>"+phoneType[data[i].g]+"<\/a>");
				$("#phoneCates").after(gg);
			}
			allPData = data;
			aaData = sData.aa;
			$("#phone_fast").keyup(function(e){scPh(e);});
			addP2Group(data);
			//addGG(aaData);
			//aboxClose();
		});
	}else{clearIn();
		$("#cfu_"+i).find(".txtBox").each(function(){
			var a = this.id.split("_");$("#p_"+a[1]+"_"+a[2])[0].io();
		});
	$("#cfu_"+i).find(".sok").remove();}
	$("#choosePhone").data("fu",i);
	$("#choosePhone").appendTo($("#cfu_"+i));
}
function addGG(aa){
	var i = 0;
	for (p in aa) {
		var gg = $("<a class=\"aButton phoneCate\" href=\"javascript:aaGroup('"+p+"');\">"+p+"<\/a>");
		$("#phoneCates").after(gg);
	}
}
function aaGroup(p){
	$("#g"+cGroup).hide();cGroup = 999;$("#g999").show();
	for(var i=0,n=aaData[p];i<n.length;i++){
		for(var c in gMap){
			if(c == n[i]){
				$(gMap[c])[0].out();
			}
		}
	}
}
function clearIn(){
	$("#td_in").find(".phone1").each(function(i){
		this.io();
	});
}
function scPh(e){
	var k = e.keyCode;
	if(k==38||k==40||k==9||k==13||k==46||(k>8&&k<32)){return;}
	var q = $.trim($("#phone_fast").val());
	if(q==""){showGroup(0);return;}
	$("#g"+cGroup).hide();
	cGroup = 999;$("#g999").show();
	q = q.toLowerCase();
	for(var c in gMap){
		var e = $(gMap[c])[0];
		if(c.toLowerCase().indexOf(q)>=0){
			e.out();
		}else if(e.state==2){e.reset();}
	}
}
function clear999(){
	$("#g999 .phone").each(function(i){
		this.reset();
	});
}
function io(i,j){
	$("#p_"+i+"_"+j)[0].io();
}
function createPh(i,j,c){
	var p = $("<a class=\"phone\" href=\"javascript:io("+i+","+j+");\" id='p_"+i+"_"+j+"'>"+c+"<\/a>");
	p[0].state=1;p[0].i=i;p[0].j=j;p[0].c=c;
	p[0].io = function(){
		if(this.state!=0){$(this).addClass("phone1").appendTo("#td_in");this.state=0;}
		else{$(this).removeClass("phone1").appendTo("#g"+(this.i-1));this.state=1;}
	};
	//如果不在in中则移动到当前group,用于search
	p[0].out = function(){
		if(this.state!=0){$(this).appendTo("#g"+cGroup);this.state=2;}
	};
	p[0].reset = function(){
		if(this.state==0){this.io();}else if(this.state==2){$(this).appendTo("#g"+(this.i-1));}
	};
	gMap[c] = "#p_"+i+"_"+j;
	return p;
}
function createPhg(d){
	var p = $("<a class=\"phone\" href=\"javascript:io("+i+","+j+");\" id='p_"+i+"_"+j+"'>"+c+"<\/a>");
	for(var j = 0,k=data[i].d.length;j<k;j++){
		createPh(i,j,data[i].d[j]).appendTo(gg);
	}
	p.click(function(){
		
	});
	return p;	
}
function addP2Group(data){
	var max = 0;
	for ( var i = 1; i < data.length; i++) {
		var gg = $("<div id='g"+data[i].g+"'></div>");
		for(var j = 0,k=data[i].d.length;j<k;j++){
			max = data[i].g;
			createPh(max+1,j,data[i].d[j]).appendTo(gg);
		}
		gg.hide().appendTo($("#td_out"));
	}
	showGroup(max);
}
function chooseAll(){
	$("#g"+cGroup).find(".phone").each(function(){var a=this.id.split("_");io(a[1],a[2]);});
}
function showGroup(i){
	clear999();
	if($("#td_out").find("#g"+i).length>0){
		$("#g"+cGroup).hide();
		$("#g"+i).show();
		cGroup = i;
	}
}