package com.bee.scheduler.runnable;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.Constants;
import com.bee.scheduler.core.job.BuildInJobComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.springframework.context.ApplicationContext;

/**
 * @author weiwei 系统初始化程序
 */
public class SystemInitializer {

    private Log logger = LogFactory.getLog(SystemInitializer.class);
    private ApplicationContext applicationContext;

    public SystemInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void init() throws Exception {
        initScheduler();
        initEmbeddedTask();
    }

    private void initEmbeddedTask() throws SchedulerException {
        logger.info("init embedded task(EmbeddedClearHistoryJob) ...");
        Scheduler scheduler = applicationContext.getBean(Scheduler.class);

        String name = "ClearTaskHistoryJob";
        String group = Constants.TASK_GROUP_SYSTEM;
        String cron = "0 0 0 * * ?";
        String description = "内置任务，用于清除历史任务记录";
        if (!scheduler.checkExists(new JobKey(name, group))) {
            JobDetail jobDetail = JobBuilder.newJob(BuildInJobComponent.class).withIdentity(name, group).build();

            JobDataMap dataMap = new JobDataMap();
            JSONObject taskParam = new JSONObject();
            taskParam.put("task", "clear_task_history");
            taskParam.put("keep_days", 5);
            dataMap.put(Constants.JOB_DATA_KEY_TASK_PARAM, taskParam.toJSONString());

            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group).usingJobData(dataMap).withDescription(description).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }

    // 初始化Scheduler
    public void initScheduler() throws SchedulerException {
        logger.info("init Scheduler ...");
        Scheduler scheduler = applicationContext.getBean(Scheduler.class);
        // 启动Scheduler
        scheduler.start();
    }
}