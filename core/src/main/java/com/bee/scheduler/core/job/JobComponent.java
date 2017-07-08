package com.bee.scheduler.core.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.JobExecutionContextUtil;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLog;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author weiwei 任务组件接口
 */
public abstract class JobComponent implements Job {
    /**
     * 名称
     */
    public abstract String getName();

    /**
     * 描述
     */
    public abstract String getDescription();

    /**
     * 参数模板：json格式
     */
    public abstract String getParamTemplate();

    /**
     * 版本号
     */
    public abstract String getVersion();

    /**
     * 作者
     */
    public abstract String getAuthor();

    public abstract boolean run(TaskExecutionContext context) throws Exception;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        boolean runFailed = false;
        JobDetail jobDetail = context.getJobDetail();

        JSONObject taskParam = JobExecutionContextUtil.getTaskParam(context);
        TaskExecutionLog taskLogger = new TaskExecutionLog(context);
        try {
            taskLogger.info("开始执行任务 -> " + jobDetail.getKey());
            taskLogger.info("任务参数 -> " + taskParam.toString());

            TaskExecutionContext taskExecutionContext = new TaskExecutionContext();
            taskExecutionContext.setScheduler(context.getScheduler());
            taskExecutionContext.setTaskParam(taskParam);
            taskExecutionContext.setLogger(taskLogger);

            runFailed = !run(taskExecutionContext);
        } catch (Exception e) {
            runFailed = true;
            taskLogger.error("执行任务异常 -> " + e.getCause(), e);
            throw new JobExecutionException(e);
        } finally {
            taskLogger.info("执行任务结束 -> " + (runFailed ? "失败" : "成功"));
        }


        JSONArray taskLinkageRule = JobExecutionContextUtil.getTaskLinkageRule(context);
        if (!runFailed && taskLinkageRule != null) {


            taskLogger.warning(taskLinkageRule.toJSONString());


//            JobKey jobKey = new JobKey(name, group);
//            Trigger trigger = scheduler.getTrigger(new TriggerKey(name, group));
//            JobDataMap jobDataMap = trigger.getJobDataMap();
//
//            String randomTriggerName = "MT_" + Long.toString(RandomUtils.nextLong(), 30 + (int) (System.currentTimeMillis() % 7));
//            OperableTrigger operableTrigger = (OperableTrigger) newTrigger().withIdentity(randomTriggerName, Constants.TASK_GROUP_Manual).forJob(jobKey).build();
//            if (jobDataMap != null) {
//                operableTrigger.setJobDataMap(jobDataMap);
//            }
//
//            scheduler.scheduleJob(operableTrigger);


        }
    }
}