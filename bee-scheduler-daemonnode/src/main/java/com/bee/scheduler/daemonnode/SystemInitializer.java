package com.bee.scheduler.daemonnode;

import com.bee.scheduler.context.executor.TaskModuleRegistry;
import com.bee.scheduler.core.AbstractTaskModule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.ExplodedArchive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.jar.JarFile;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
//        logger.info("init embedded task(EmbeddedClearHistoryJob) ...");
//        Scheduler scheduler = applicationContext.getBean(Scheduler.class);
//
//        String name = "ClearTaskHistoryJob";
//        String group = Constants.TASK_GROUP_SYSTEM;
//        String cron = "0 0 0 * * ?";
//        String description = "内置任务，用于清除历史任务记录";
//        if (!scheduler.checkExists(new JobKey(name, group))) {
//            JobDetail jobDetail = JobBuilder.newJob(BuildInJobComponent.class).withIdentity(name, group).build();
//
//            JobDataMap dataMap = new JobDataMap();
//            JSONObject taskParam = new JSONObject();
//            taskParam.put("task", "clear_task_history");
//            taskParam.put("keep_days", 5);
//            dataMap.put(Constants.JOB_DATA_KEY_TASK_PARAM, taskParam.toJSONString());
//
//            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group).usingJobData(dataMap).withDescription(description).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
//            scheduler.scheduleJob(jobDetail, trigger);
//        }
    }

    // 初始化任务组件
    public void initJobComponents() throws Exception {
        logger.info("init job component ...");
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        SimpleMetadataReaderFactory simpleMetadataReaderFactory = new SimpleMetadataReaderFactory(resourcePatternResolver);
        JarFile.registerUrlProtocolHandler();

        Archive sourceCodeArchive = createArchive();
        List<Archive> taskModulesArchive = sourceCodeArchive.getNestedArchives(entry -> !entry.isDirectory() && Pattern.matches("task_modules/.*\\.jar$", entry.getName()));
        for (Archive archive : taskModulesArchive) {
            ArrayList<URL> urls = new ArrayList<>();
            urls.add(archive.getUrl());
            List<Archive> nestedArchives = archive.getNestedArchives(entry -> !entry.isDirectory() && entry.getName().startsWith("lib/"));
            for (Archive item : nestedArchives) {
                urls.add(item.getUrl());
            }
            String moduleClass = archive.getManifest().getMainAttributes().getValue("TaskModuleClass");
            ClassLoader classLoader = new LaunchedURLClassLoader(urls.toArray(new URL[0]), ClassUtils.getDefaultClassLoader());

            AbstractTaskModule module = (AbstractTaskModule) classLoader.loadClass(moduleClass).newInstance();

            logger.info("注册组件:" + module);
            TaskModuleRegistry.register(module);
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

    private Archive createArchive() throws Exception {
        ProtectionDomain protectionDomain;
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        if (classLoader instanceof LaunchedURLClassLoader) {
            protectionDomain = ((LaunchedURLClassLoader) classLoader).getClass().getProtectionDomain();
        } else {
            protectionDomain = getClass().getProtectionDomain();
        }
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = (codeSource != null) ? codeSource.getLocation().toURI() : null;
        String path = (location != null) ? location.getSchemeSpecificPart() : null;
        System.out.println(path);
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        }
        File root = new File(path);
        if (!root.exists()) {
            throw new IllegalStateException("Unable to determine code source archive from " + root);
        }
        return (root.isDirectory() ? new ExplodedArchive(root) : new JarFileArchive(root));
    }
}