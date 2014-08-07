/**
 * 
 */
package com.k99k.khunter;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * 拒绝任务处理方法,目前是记录任务日志然后抛弃任务
 * @author keel
 *
 */
public class RejectedTaskHandler implements RejectedExecutionHandler {

	public RejectedTaskHandler() {
	}
	
	public RejectedTaskHandler(String name) {
		this.name = name;
	}
	
	private String name = "noName";
	
	static final Logger log = Logger.getLogger(RejectedTaskHandler.class);

	/* (non-Javadoc)
	 * @see java.util.concurrent.RejectedExecutionHandler#rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor)
	 */
	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		//使用原线程来执行
		executor.execute(r);
		// 进行日志记录
		log.error("TASK redo! task:"+r.toString());
		StringBuilder sb = new StringBuilder();
		sb.append("ThreadPoolExecutor :").append(this.name).append("\n");
		sb.append("[ isShutdown:").append(executor.isShutdown());
		sb.append(" isTerminated:").append(executor.isTerminated());
		sb.append(" activeCount:").append(executor.getActiveCount());
		sb.append(" taskCount:").append(executor.getTaskCount());
		sb.append(" corePoolSize:").append(executor.getCorePoolSize());
		sb.append(" completedTaskCount:").append(executor.getCompletedTaskCount());
		sb.append(" keepAliveTime:").append(executor.getKeepAliveTime(TimeUnit.SECONDS)).append(" ]");
		log.error(sb.toString());
		
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	
}
