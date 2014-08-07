function swferr(info){
		$("#uploadInfo").html(info).css("color","red");
}
function swfinfo(info){
		$("#uploadInfo").html(info).css("color","black");
}
function swfok(info){
		$("#uploadInfo").html(info).css("color","green");
}
function fileQueueError(file, errorCode, message) {
	console.log("fileQueueError");
	try {
		switch (errorCode) {
		case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
			swferr("图片文件大小为空.");
			break;
		case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
			swferr("图片文件超过大小限制.");
			break;
		case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
			swferr("无效的文件类型.");
			break;
		default:
			swferr("选择文件错误.");
			break;
		}
	} catch (ex) {
		swferr("选择文件错误.");
	}
}

function fileQueued(file){
	//this.addPostParam("f",file.name);
}

function fileDialogComplete(numFilesSelected, numFilesQueued) {
	try {
		if (numFilesQueued > 0) {
			this.startUpload();return;
		}
	} catch (ex) {}
}

function uploadProgress(file, bytesLoaded) {

	try {
		if(swfu.startProg){
			var percent = Math.ceil((bytesLoaded / file.size) * 100);
			$("#swfuploadProgress").text(percent);			
		}else{
			swfu.startProg = true;	
			swfinfo("文件上传中......已上传 [<span id='swfuploadProgress'>0</span> ] % , <a href='javascript:swfu.cancelUpload(\""+file.id+"\");'>取消</a>");
		}
	} catch (ex) {}
}

function uploadSuccess(file, serverData) {
	try {
		var re = eval((serverData));
		if(re.length==3){
		swfok("上传成功.");
		$("#uploadPreview").html("<div style='text-align:center;'><img src='../images/upload/"+re[2]+"' alt='"+re[0]+"' /></div>");			
		}else{swferr("上传后出现错误.");}
	} catch (ex) {}
}


function uploadError(file, errorCode, message) {
	try {
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
	} catch (ex3) {
	}
}

