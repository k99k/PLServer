function fo(tid){
	var err = "<div class='reErr'>关注失败！建议重新登录. <a href='javascript:$.fancybox.close();' class=\"aboxBT\">关闭</a></div>";
	$.getJSON("<%=prefix %>/follow/follow?tid="+tid+"&r="+new Date(),function(data){
		if(data){
			abox("关注成功","<div class='reOk'>已成功关注! <a href='javascript:$.fancybox.close();' class=\"aboxBT\">关闭</a></div>");
			$("#m_"+tid).find(".funBox").html(both(tid));
			setTimeout("$.fancybox.close();",1000);return;
		}else{abox("关注失败",err);}
	}).error(function(){abox("关注失败",err);});
}
function unFo(tid){
	var err = "<div class='reErr'>取消关注失败！建议重新登录. <a href='javascript:$.fancybox.close();' class=\"aboxBT\">关闭</a></div>";
	$.getJSON("<%=prefix %>/follow/unfollow?tid="+tid+"&r="+new Date(),function(data){
		if(data){
			abox("取消关注","<div class='reOk'>已成功取消关注! <a href='javascript:$.fancybox.close();' class=\"aboxBT\">关闭</a></div>");
			$("#m_"+tid).find(".funBox").html(one(tid));
			setTimeout("$.fancybox.close();",1000);return;
		}else{abox("取消失败",err);}
	}).error(function(){abox("取消失败",err);});
};
function abox(title,contentHtml){
	var s = "<div id=\"aboxDiv\" class=\"abox\"><div class=\"aboxTitle\">";
	s+=title;
	s+="</div><div class=\"aboxContent\">";
	s+=contentHtml;
	s+="</div></div>";
	$.fancybox({
		'autoDimensions'	: false,
		'width'         		: 'auto',
		'height'        		: 'auto',
		'transitionIn'		: 'none',
		'transitionOut'	: 'none',
		'content':s
	});
};
function uLI(d,m){
	var s = "<li id='m_";
	s+=d._id;
	s+="'><div class='userPic'><a href='";
	s+=$.prefix;
	s+="/";
	s+=d.name;
	s+="' class='icon'> <img src='";
	s+=$.prefix;
	s+="/images/upload/";
	s+=d.name;
	s+="_3.jpg' alt='";
	s+=d.name;
	s+="' /></a></div><div class='msgBox'><div class='userName'><a target='_blank' href='"
	s+=$.prefix;
	s+="/";
	s+=d.name;
	s+="' title='";
	s+=d.screen_name;
	s+="(@";
	s+=d.name;
	s+=")'>";
	s+=d.screen_name;
	s+="(@";
	s+=d.name;
	s+=")</a></div><div class='msgCnt'>关注 <a href='";
	s+=$.prefix;
	s+="/";
	s+=d.name;
	s+="/follows' class='bigTxt'> ";
	s+=d.followers_count;
	s+=" </a> 人    粉丝<a href='";
	s+=$.prefix;
	s+="/";
	s+=d.name;
	s+="/fans' class='bigTxt'> ";
	s+=d.friends_count;
	s+="</a>人    微博 <a href='";
	s+=$.prefix;
	s+="/";
	s+=d.name;
	s+="' class='bigTxt'> ";
	s+=d.statuses_count;
	s+="</a>条</div><div class='funBox'>";
	if(m["u"+d._id] == 1){
		s+=both(d._id);
	}else {
		s+=one(d._id);
	}
	s+="</div></div></li>";
	return s;
};
function both(id){
	var s = "已相互关注 | <a href='javascript:unFo(";
	s+=id;
	s+=");' class='relay'>取消</a>";
	return s;
};
function one(id){
	var s ="<a href='javascript:fo(";
	s+=id;
	s+=");' class='relay'>关注</a> ";
	return s;
};