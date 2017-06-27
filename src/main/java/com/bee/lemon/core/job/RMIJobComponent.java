package com.bee.lemon.core.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author weiwei
 * 
 * 该组件提供远程调用方法的功能
 */
public class RMIJobComponent extends JobComponent {
	@Override
	public String getName() {
		return "RMIJob(暂不可用)";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getAuthor() {
		return "vivi";
	}

	@Override
	public String getDescription() {
		return "该组件提供远程调用方法的功能";
	}

	@Override
	public String getParamTemplate() {
		StringBuilder t = new StringBuilder();
		t.append("{\r");
		t.append("	\"address\":\"\",\r");
		t.append("	\"method\":\"\"\r");
		t.append("}");
		return t.toString();
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("RMIJob.execute()");
		log.info("params:" + getTaskParam(context));
	}

}
