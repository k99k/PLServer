var swfu;
function initUpload(uName,sucFn,f_types,f_types_say,f_size,f_url,queueCheck){
 swfu= new SWFUpload({
	upload_url : (f_url)?f_url:$.prefix+"/upload",
	flash_url : $.sPrefix+"/js/swfupload.swf",
	//post_params: {"uName":uName},
	use_query_string:true, 
	button_placeholder_id : "spanSWFUploadButton",
	button_width: 100,
	button_height: 29,
	button_text: "<span class='swfTxt1'>选择上传文件</span>",
	button_text_style: ".swfTxt1 {color:#ffffff;}",
	button_text_left_padding: 12,
	button_text_top_padding: 6,
	button_image_url : $.sPrefix+"/images/swfBT_100x29.png",
	
	prevent_swf_caching:false,
	upload_start_handler : fileQueue,
	file_queue_error_handler : fileQueueError,
	file_dialog_complete_handler : (queueCheck) ? queueCheck :fileDialogComplete,
	upload_progress_handler : uploadProgress,
	upload_error_handler : uploadError,
	upload_success_handler : (sucFn)?sucFn:uploadSuccess,
	upload_complete_handler:uploadComplete,
	
	 file_upload_limit : 100,
     file_queue_limit : 100,

	file_types : (f_types)?f_types:"*.rar;*.zip;*.apk;*.jar;*.doc;*.docx;*.xls;*.xlsx;*.ppt;*.pptx;*.txt;*.jpg;*.png;*.gif;*.pdf",  
     file_types_description :(f_types_say)?f_types_say: "上传文件", 
	file_size_limit :(f_size)?f_size: "223000"
	//,debug:true
	
	});
}
function swferr(info){
	$("#uploadInfo").html(info).css("color","red");
}
function swfinfo(info){
	$("#uploadInfo").html(info).css("color","black");
}
function swfok(info){
	$("#upFiles").append(info);
	$("#uploadInfo").html("");
}
function swfreset(){
	swfinfo("文件最大不超过"+(swfu.file_size_limit/1000)+"MB,格式为"+swfu.file_types);
}
function fileQueue(file){
	var f_enc = encodeURIComponent(file.name);
	swfu.addPostParam("f",f_enc);
	if(swfu.newfile){swfu.addPostParam("n",swfu.newfile(file));}
}

function fileQueueError(file, errorCode, message) {
	try {
		switch (errorCode) {
		case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
			swferr("文件大小为空.");
			break;
		case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
			swferr("文件超过大小限制.");
			break;
		case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
			swferr("无效的文件类型.");
			break;
		default:
			swferr("选择文件数量超过限制.");
			break;
		}
	} catch (ex) {
		swferr("选择文件错误.");
	}
}
function uploadComplete(file) {
	if(swfu.end){
		swfu.end();return;
	}
	if (this.getStats().files_queued === 0) {
		//document.getElementById(this.customSettings.cancelButtonId).disabled = true;
	} else {	
		this.startUpload();
	}
}
function fileDialogComplete(numFilesSelected, numFilesQueued) {
	if (numFilesQueued > 0) {
		this.startUpload();return;
	}
}
function uploadProgress(file, bytesLoaded) {
	if(swfu.startProg){
		var percent = Math.ceil((bytesLoaded / file.size) * 100);
		$("#swfuploadProgress").text(percent);			
	}else{
		swfu.startProg = true;	
		swfinfo("文件上传中......已上传 [<span id='swfuploadProgress'>0</span> ] % , <a href='javascript:swfu.cancelUpload(\""+file.id+"\");'>取消</a>");
	}
}
function uploadSuccess(file, serverData) {
	var re = serverData;
	swfu.startProg = false;
	var i  =($.hasFileIndex) ? ($.hasFileIndex+file.index) :file.index;
	//console.log(re);
	//console.log(file);
	if(re.length>=file.name.length){
	swfok("<div class='file_upload' id='fu_"+i+"'>"+file.name+" <span class='greenBold'>上传成功!</span> [ <a href='javascript:delFile(\""+i+"\");'>删除 </a> ]<span class=\"files_name\">"+file.name+"</span></div>");
	}else{swfok("<div class='file_upload file_upload_ERR'>"+file.name+" 上传失败!</div>");}
}
function delFile(fid){
	var t = $("#fu_"+fid);
	if(t.find("#choosePhone").length>=0){$("#choosePhone").appendTo("#hide");}
	t.remove();
}
function uploadError(file, errorCode, message) {
	switch (errorCode) {
	case SWFUpload.UPLOAD_ERROR.FILE_CANCELLED:
		swfinfo("上传已取消.");
		break;
	case SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED:
		swfinfo("上传已停止.");
	case SWFUpload.UPLOAD_ERROR.UPLOAD_LIMIT_EXCEEDED:
		swferr("上传文件超过服务器限制.");
		break;
	default:
		swferr("上传失败:"+message);
		break;
	}
};