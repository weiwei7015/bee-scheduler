package com.bee.scheduler.admin.core;

import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.Constants;
import com.bee.scheduler.core.job.BuildInJobComponent;
import com.bee.scheduler.core.job.JobComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

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
        initJobComponents();
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

    // 初始化任务组件
    public void initJobComponents() throws Exception {
        logger.info("init job component ...");
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        SimpleMetadataReaderFactory simpleMetadataReaderFactory = new SimpleMetadataReaderFactory(resourcePatternResolver);
        Resource[] resources = resourcePatternResolver.getResources("classpath*:com/bee/scheduler/core/job/*.class");
        for (Resource resource : resources) {
            @SuppressWarnings("unchecked")
            Class<? extends JobComponent> clazz = (Class<? extends JobComponent>) Class.forName(simpleMetadataReaderFactory.getMetadataReader(resource).getClassMetadata().getClassName());
            if (clazz != JobComponent.class && JobComponent.class.isAssignableFrom(clazz)) {
                JobComponent jobComponent = clazz.newInstance();
                RamLocal.JobComponentMap.put(clazz.getSimpleName(), jobComponent);
            }
        }
    }

    // 初始化Scheduler
    public void initScheduler() throws SchedulerException {
        logger.info("init Scheduler ...");
        // 获取Scheduler
        Scheduler scheduler = applicationContext.getBean(Scheduler.class);
//        // 检查scheduler状态
//        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.<JobKey>anyGroup())) {
//            try {
//                Class<?> jobClass = Class.forName(scheduler.getJobDetail(jobKey).getJobClass().getName());
//                logger.info(jobKey + "-正常");
//            } catch (ClassNotFoundException | JobPersistenceException e) {
//                logger.warn("" + jobKey + "相关的任务组件已卸载，将被删除");
//                scheduler.deleteJob(jobKey);
//            }
//        }
        // 启动Scheduler
        scheduler.start();
    }
}