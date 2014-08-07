$.hotEditor = {
inputTextEditor : "<input type=\"text\" name=\"t\" class=\"hotEditInput\">",
textAreaEditor : "<textarea name=\"a\" class=\"hotEditTA\"></textarea>",
HENull : "HE#NULL",
checkBoxEditor : "<input type=\"checkbox\" name=\"c\" class=\"hotEditCheckbox\">",
btSet:"SET",
btEdit:"EDIT",
btCancel:"CANCEL",
btAdd:"ADD",
btDel:"DEL",
selectEditor : "<select name=\"s\"><option value=\"false\">false</option><option value=\"true\">true</option></select>"
};
/*
验证参数,并进行初始化,初始化结果体现在ep.initOK上
(所有操作附加在输入的ep参数对象上实现)
ep:被操作的参数对象
return:ep
*/
$.hotEditor.init = function (editParas) {
	//创建副本
	var ep = $.extend({},editParas);
	ep.editParas = $.extend({},editParas);
	//检验ep参数是否正确,最少的参数需要target和msg
 	var epOK = false;
	if(ep.target && ep.msg){
		ep.tbak = $(ep.target).clone();
		ep.t = $(ep.target);
		ep.msg = $(ep.msg);
		//ep.key为请求的键集合，若无则默认为ep.target.text()
		if (!ep.key){ep.key = [ep.t.text()];ep.len=1};
		//ep.editor为编辑输入的dom string,若无则为text类型的input
		if (!ep.editor) {ep.editor = [$.hotEditor.inputTextEditor]};
		//ep.bts为编辑按钮添加(append)的位置，若无则直接为target
		if (!ep.bts) {ep.bts = ep.t}else{ep.bts = ep.t.find(ep.bts);};
		//ep.url为ajax提交的地址,若无则表示不进行ajax提交
		if (!ep.len) {ep.len = ep.key.length;};
		//无子对象的情况
		if (ep.len === 1 && ep.editor.length===1) {
			epOK = true;
		}
		//有子对象的情况
		else if (ep.len > 1 && ep.subs && ep.len === ep.editor.length && ep.len === ep.subs.length) {
			epOK = true;
		};
		//ep.val为编辑状态时的默认填充值,如果有则需要与len相同
		if (ep.val) {
			epOK = (ep.val.length === len);
			//hasVal表示有默认填充值
			ep.hasVal = true;
		};
	};
	ep.initOK = epOK;
	//失败则直接返回
	if (!epOK) {
		alert("ep error!");
		return ep;
	}
	//初始化
	ep.span =  $("<span class='hotEditSpan'></span>");
	//preParas为预设的ajax键值对参数
	if(!ep.preParas){ep.preParas = {};};
	//设置操作按钮
	ep.btText1 = $.hotEditor.btSet;
	ep.btText2 = $.hotEditor.btEdit;
	ep.btText3 = $.hotEditor.btCancel;
	ep.btText4 = $.hotEditor.btAdd;
	ep.btText5 = $.hotEditor.btDel;
	ep.bt1 = $("<input type=\"button\" class=\"hotEditBT\" value=\""+ep.btText2+"\" />");
	ep.bt2 = $("<input type=\"button\" class=\"hotEditBT\" value=\""+ep.btText3+"\" />");
	ep.bt3 = $("<input type=\"button\" class=\"hotEditBT3\" value=\""+ep.btText4+"\" />");
	ep.bt4 = $("<input type=\"button\" class=\"hotEditBT\" value=\""+ep.btText5+"\" />");
	return ep;
};
 /*
 处理可编辑状态	 
 */
 $.hotEditor.editable = function(ep) {
 	if (!ep.initOK) {return ep;};
 	//初始化编辑区,tars为子编辑区集合,若无子对象则就是target
 	var tars = [];
	for (var i=0; i < ep.len; i++) {
		if (!ep.subs) {
			tars[i] = ep.t;
		}else{
			tars[i] = ep.t.find(ep.subs[i]);
		};
		if(ep.hasVal && (ep.val[i] != $.hotEditor.HENull )){
			tars[i].oldVal = ep.val[i];
			tars[i].spanTxt = tars[i].text();
		}else{
			tars[i].oldVal = tars[i].text();
			tars[i].spanTxt = tars[i].oldVal;
		};
		tars[i].span = ep.span.clone().text(tars[i].spanTxt);
		//清空原subs.text,以span代替
		tars[i].text("");
		//增加editor
		if (ep.editor[i] ===$.hotEditor.HENull) {
			tars[i].ed = tars[i].span.clone();
		}else{
			tars[i].ed = $(ep.editor[i]).attr("name",ep.key[i]).val(tars[i].oldVal);
		};
		tars[i].append(tars[i].span).append(tars[i].ed.hide());
	};
	ep.tars = tars;
	//放置按钮
	ep.bts.append(ep.bt1).append(ep.bt2.hide());
	ep.isSet = false;
	//按钮事件
	ep.bt1.click(function() {
		if (!ep.isSet) {
			//原状态,转为可编辑
			$.hotEditor.stateA(ep);
		}else{
			//编辑状态,提交更新动作
			ep.bt1.val("loading").attr("disabled","disabled");
			ep.paras = $.hotEditor.prepare($.hotEditor.gather(ep.t),ep.key,ep.jsonToStr,ep.jsonTyps,ep.preParas);
			/*
			for(k in paras){
				alert ("paras["+k +"] = "+paras[k]);
			}
			*/
			//console.log(ep.paras);
			if (ep.url && ep.paras) {$.hotEditor.ajax(ep,ep.url,ep.paras,false);}else{
				//不提交,直接更新span
				for (var i=0; i < ep.len; i++) {
					if (ep.editor[i]===$.hotEditor.HENull) {continue;};
					ep.tars[i].span.text(ep.tars[i].ed.val());
				}
			};
			$.hotEditor.stateB(ep);
		};
	});
	ep.bt2.click(function() {
		$.hotEditor.stateB(ep);
	});
	//数据添加的处理
	if (ep.addTarget) {
		$.hotEditor.add(ep);
	};
	//数据删除
	if (ep.delBT) {
		$.hotEditor.del(ep);
	};
 	return ep;
};
$.hotEditor.del = function(ep) {
	//确定bt4的位置
	if (ep.delBT != ">") {
		$(ep.delBT).append(ep.bt4);
	}else{
		//如果未指定bt4的位置,则添加在ep的bt3后面,bt4一直存在
		if(ep.bts.length === 0){
			ep.t.append(ep.bt4);
		}else{
			ep.bts.append(ep.bt4);
		};
	};
	ep.delUrl = (ep.delUrl) ? ep.delUrl : ep.url;
	ep.delPreParas = (ep.delPreParas)? ep.delPreParas :ep.preParas;
	ep.delJsonToStr = (ep.delJsonToStr)?ep.delJsonToStr: ep.jsonToStr;
	ep.delJsonTyps = (ep.delJsonTyps) ?ep.delJsonTyps: ep.jsonTyps;
	ep.delConfirm = (ep.delConfirm)?ep.delConfirm :"This will be deleted, confirm?";
	ep.delRemove = function() {
		ep.t.remove();
	};
	//删除
	ep.bt4.click(function() {
		//ep.bt4.val("loading").attr("disabled","disabled");
		ep.delParas = $.hotEditor.prepare($.hotEditor.gather(ep.t),ep.key,ep.delJsonToStr,ep.delJsonTyps,ep.delPreParas);
		//console.log(ep.delParas);
		if (confirm(ep.delConfirm) ) {
			if (ep.delUrl && ep.delParas) {
				if ($.hotEditor.ajax(ep,ep.delUrl,ep.delParas,function(){ep.delRemove();})) {
					
				}
			}else{
				//不提交,直接去除
				ep.delRemove();
			}
		};
	});
};
/*
add实现
*/
$.hotEditor.add = function (ep) {
	//确定addBT即bt3的位置
	if (ep.addBT) {
		$(ep.addBT).append(ep.bt3);
	}else{
		//如果未指定bt3的位置,则添加在root的bt2后面,bt3一直存在
		if(ep.bts.length === 0){
			ep.t.append(ep.bt3);
		}else{
			ep.bts.append(ep.bt3);
		};
	};
	
	if (ep.addTarget === ">") {
		ep.ta = $(ep.t).clone(true).removeAttr("id").hide();
		ep.ta.find(".hotEditSpan").remove();
		ep.ta.find(".hotEditBT").remove();
		ep.ta.find(".hotEditBT3").remove();
	}else{
		//直接使用addTarget对象作为ta
		ep.ta = $(ep.addTarget);
	}
	if (!ep.editParas.bts) {ep.ta.bts = ep.ta}else{ep.ta.bts = ep.ta.find(ep.editParas.bts);};
	ep.ta.bt1 = ep.bt1.clone().val(ep.btText1).show();
	ep.ta.bt2 = ep.bt2.clone().show();
	ep.ta.bts.append(ep.ta.bt1).append(ep.ta.bt2);
	ep.ta.addClass("hotEditADD");
	//addPosition
	ep.t.after(ep.ta);
	ep.ta.eds = [];
	for (var i=0; i < ep.len; i++) {
		if (ep.editor[i] ===$.hotEditor.HENull) {
			ep.ta.eds[i] = ep.ta.find(ep.subs[i]).text(ep.tars[i].span.text());
			continue;
		}
		var w = ep.tars[i].span.width()+20;
		if (w<50) {w=80;};
		ep.ta.eds[i] = ep.ta.find("[name='"+ep.key[i]+"']").width(w).show();
	};
	ep.ta.preParas = (ep.addPreParas)? ep.addPreParas :ep.preParas;
	ep.ta.jsonToStr = (ep.addJsonToStr)?ep.addJsonToStr: ep.jsonToStr;
	ep.ta.jsonTyps = (ep.addJsonTyps) ?ep.addJsonTyps: ep.jsonTyps;
	ep.ta.url = (ep.addUrl) ?ep.addUrl:ep.url;
	//ep.ta.addTo:完成数据添加后将新数据添加到的位置,注意是after的方式
	ep.ta.addTo = (ep.addTo) ? $(ep.addTo) : ep.ta;
	ep.ta.stateA =function(){ep.ta.bt1.removeAttr("disabled").val(ep.btText1);ep.ta.show();}
	ep.ta.stateB =function(){ep.ta.bt1.removeAttr("disabled").val(ep.btText1);ep.ta.hide();}
	//在addTo不为HENull的情况下将新数据添加到的位置
	ep.ta.appendNew = function(reVals) {
		if (ep.ta.addTo != $.hotEditor.HENull && reVals) {
			var newT = ep.tbak.clone(true).removeAttr("id");
			if (ep.len === 1) {
				newT.text(reVals[ep.key[i]]);
			}else{
				for (var i=0; i < ep.len; i++) {
					if (ep.editor[i]===$.hotEditor.HENull) {continue;};
					var d = reVals[ep.key[i]];
					var newD = (d == null)?"":((typeof(d) === 'object')?$.toJSON(d):d);
					newT.find(ep.subs[i]).text(newD);
				};
			}
			ep.ta.addTo.after(newT);
			var epNew = $.extend({},ep.editParas);
			if (ep.addBT) {epNew.addTarget=undefined;};
			$.hotEditor.act(epNew,newT);
		};
	};
	ep.ta.bt1.click(function() {
		ep.ta.bt1.val("loading").attr("disabled","disabled");
		ep.ta.paras = $.hotEditor.prepare($.hotEditor.gather(ep.ta),ep.key,ep.ta.jsonToStr,ep.ta.jsonTyps,ep.ta.preParas);
		/*
		for(k in paras){
			alert ("paras["+k +"] = "+paras[k]);
		}
		*/
		//console.log(ep.ta.paras);
		if (ep.ta.url && ep.ta.paras) {
			if ($.hotEditor.ajax(ep,ep.ta.url,ep.ta.paras,ep.ta.appendNew)) {
				
			}
		}else{
			//不提交,直接添加
			var reval = {};
			for (var i=0; i < ep.len; i++) {
				reval[ep.key[i]] = ep.ta.eds[i].val();
			}
			ep.ta.appendNew(reval);
		};
		//最后隐藏输入
		ep.ta.stateB();
	});
	ep.ta.bt2.click(function(){
		ep.ta.stateB();
	});
	ep.bt3.click(function() {
		ep.ta.stateA();
	});
	ep.ta.stateB();
};
/*
输入状态	
*/
$.hotEditor.stateA = function(ep) {
	for (var i=0; i < ep.len; i++) {
		var w = ep.tars[i].span.width()+20;
		if (w<50) {w=80;};
		ep.tars[i].span.hide();
		ep.tars[i].ed.show().width(w);
	}
	ep.bt1.val(ep.btText1);
	ep.isSet = true;
	ep.bt2.show();
 	return ep;
};
/*
原状态	
*/
$.hotEditor.stateB = function(ep) {
	for (var i=0; i < ep.len; i++) {
		ep.tars[i].span.show();
		ep.tars[i].ed.hide();
	}
	ep.bt1.val(ep.btText2).removeAttr("disabled");
	ep.bt2.hide();
	ep.isSet = false;
 	return ep;
};
/*
将target内的数据进行整理,准备提交,可重写ep.prepare操作来替代	
*/
$.hotEditor.prepare = function(newParas,key,jsonToStr,jsonTyps,preParas) {
	//如果有重新定义,则直接执行
	//if (ep.prepare) {return ep.prepare(newParas,key,jsonToStr,jsonTyps,preParas);};
	//收集值
	//var newParas = $.hotEditor.gather(ep.t);
	
	//editParas.jsonToStr和 jsonTyps设置后将使所有的输入形成一个json字符串,作为jsonToStr:json的键值对进入paras
	//注意此处用到jquery.json插件
	if (jsonToStr && jsonTyps) {
		newParas = $.hotEditor.parseJson(jsonTyps,key,newParas);
		if (!newParas) {alert("jsonTypes parse error");return false;};
		var jsonStr = $.toJSON(newParas);
		var paras = $.extend({},preParas);
		paras[jsonToStr] = jsonStr;
		return paras;
	}else{
		return $.extend(newParas,preParas);
	};
};
/*
收集数据方法
*/
$.hotEditor.gather = function($tar) {
	var newParas = {};
	$tar.find("textarea,input:text,input:checkbox:checked,input:radio:checked,option:selected").each(function (i) {
		if(this.tagName.toLowerCase() === "option"){
			if($(this).parent().get(0).tagName.toLowerCase() === "select"){
				newParas[$(this).parent().get(0).name] = this.value;
			}
		}else{
			newParas[this.name] = this.value;
		}
	});
	return newParas;
};
/*
ajax	
*/
$.hotEditor.ajax = function(ep,url,paras,fillFunc) {
	//返回方法如果不设，则为JSON处理DATA后在msg显示数据
	if (!ep.callback) {ep.callback = function (data,state) {
		if (state != "ok") {
			ep.msg.text("err:post failed.");
		}else{
			try{
				var re = $.parseJSON(data);
				if (!re) {
					ep.msg.text("err:parseJSON error");
				}else if (re.re && re.re === "ok" && re.d ) {
					//填充数据
					if (!fillFunc) {
						for (var i=0; i < ep.len; i++) {
							if (re.d[i] != $.hotEditor.HENull) {
								var d = re.d[ep.key[i]];
								var newD = (d == null)?"":((typeof(d) === 'object')?$.toJSON(d):d);
								ep.tars[i].span.text(newD);
								ep.tars[i].ed.val(d);
							};
						};
					}else{
						fillFunc(re.d);
					};
					ep.msg.text(re.re);
					return true;
				}else{
					//显示错误
					ep.msg.text("err:"+re.re+":"+re.d);
				};
			}catch(e){
				ep.msg.text("err:callback err:"+data);
			}
		}
		return false;
	};};
	$.post(url, paras ,function(data) {
		//alert("success:"+data);
		return ep.callback(data,"ok");
	}).error(function() {return ep.callback(data,"err");});
 	return false;
};
/*
操作入口	
*/
$.hotEditor.act = function (ep,target) {
	if (target) {ep.target = target;};
	ep = $.hotEditor.init(ep);
	if (ep.initOK) {
		$.hotEditor.editable(ep);
	};
};

/*
更换类型,主要用于json中的值
type为类型标识，如果不能识别类型标识，直接返回原对象
l为Array类型，m为map类型
a为任意类型，根据指令符判断,如 s@abc 表示将abc转换成string,i@abc表示将abc转换成int	
*/
$.hotEditor.parseType = function(type,obj){
	if (type === "s") {
		var re = "";
		if (obj) {re = obj.toString()};
		return re;
	}else if (type === "i") {
		var re = parseInt(obj);
		if (!re) {return 0;};
		return re;
	}else if (type === "b") {
		if (obj == "false") {return false};
		return Boolean(obj);
	}else if (type === "f") {
		var re = parseFloat(obj);
		if (!re) {return 0.0;};
		return re;
	}else if (type === "l") {
		try{
			var re = $.parseJSON(obj);
			if(re && re.constructor === Array){return re;};
		}catch(e){
			return [];
		}
		return [];
	}else if (type === "m") {
		try{
			var re = $.parseJSON(obj);
			if(re && typeof(re) == 'object' && re.constructor != Array){return re;};
		}catch(e){
			return {};
		}
		return {};
	}else if(type === "a"){
		//指定类型判断
		if (!obj) {return ""};
		var re = obj.toString();
		var tag = re.charAt(0);
		if (re.indexOf('@') == 1 && /[sibfml]/.test(tag)) {return $.hotEditor.parseType(tag,re.substr(2))};
		return obj;
	}else {
		return obj;
	}
};
/*处理json的数据类型,types与keys要严格对应*/
$.hotEditor.parseJson = function(types,keys,json){
	if (types && keys  && json && keys.length === types.length) {
		for (var i=0; i < types.length; i++) {
			json[keys[i]] = $.hotEditor.parseType(types[i],json[keys[i]]);
		};
		return json;
	}else{
		return false;	
	}
};
