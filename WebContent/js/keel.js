/*
 * 获取url中的参数
 * */
function QueryString(fieldName){ 
	var urlString = document.location.search;
	if(urlString != null){
		var typeQu = fieldName+"=";
		var urlEnd = urlString.indexOf(typeQu);
		if(urlEnd != -1){
			var paramsUrl = urlString.substring(urlEnd+typeQu.length);
			var isEnd = paramsUrl.indexOf('&');
			if(isEnd != -1){
				return paramsUrl.substring(0, isEnd);
			}else{
				return paramsUrl;
			}
		}else{
			return null;
		}
	}else{
		return null;
	}
}

/*
 * 将原对象中的内容以html形式替换掉,注意原对象最好是pre或textarea，不然在IE下会无法替换换行
 * 目前实现了换行,空格,链接的转换
 * */
function showHtml(target) {
	var s = $(target).html(),pa = $(target).parent();;
	$(target).remove();
	s=s.replace(/\x20/g,"&nbsp;");
	s=s.replace(/\n/g,"<br />") ;
	s=s.replace( /(http[s]?:\/\/[\w-\/]*(\.[\w-\/]*)+)/ig ,"<a href='$1' target='_blank'>$1</a>") ;
	pa.append($("<div>"+s+"</div>"));
}

/*
 * 显示毫秒时间的方式
 * */
function sentTime(ms){
	var t = new Date(ms);
	var now = new Date();
	var dd = now.getTime()-new Date(now.getFullYear(),now.getMonth(),now.getDate(),0,0,0).getTime();
	var showDate = dd+86400000;
	var lastHour = 3600000;
	var pas = now-t;
	if (pas>=showDate) {
		return (t.format("yyyy-MM-dd hh:mm:ss"));
	}else if(pas>=dd && pas<showDate){
		return ("昨天:"+t.format("hh:mm:ss"));
	}else if(pas>=lastHour && pas<dd){
		return ("今天:"+t.format("hh:mm:ss"));
	}else if(pas<600000){
		return ("刚刚");
	}else{
		return (Math.floor(pas/60/1000)+"分钟前");
	}
};
