package com.bee.lemon.model;

import com.bee.lemon.core.job.JobComponent;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;

/**
 * @author weiwei
 * 
 * 任务类，包含JobDetail、触发器、执行状态
 */
public class Task {
	private Trigger trigger;
	private JobDetail jobDetail;
	private TriggerState triggerState;
	private JobComponent jobComponent;

	public Task() {
	}

	public Task(Trigger trigger, JobDetail jobDetail, TriggerState triggerState, JobComponent jobComponent) {
		super();
		this.trigger = trigger;
		this.jobDetail = jobDetail;
		this.triggerState = triggerState;
		this.jobComponent = jobComponent;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public JobDetail getJobDetail() {
		return jobDetail;
	}

	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}

	public TriggerState getTriggerState() {
		return triggerState;
	}

	public void setTriggerState(TriggerState triggerState) {
		this.triggerState = triggerState;
	}

	public JobComponent getJobComponent() {
		return jobComponent;
	}

	public void setJobComponent(JobComponent jobComponent) {
		this.jobComponent = jobComponent;
	}

}
