package com.bee.scheduler.consolenode;

import com.bee.scheduler.consolenode.core.BuildInTaskModuleLoader;
import com.bee.scheduler.consolenode.core.ClassPathJarArchiveTaskModuleLoader;
import com.bee.scheduler.context.BeeSchedulerFactoryBean;
import com.bee.scheduler.context.CustomizedQuartzSchedulerFactoryBean;
import com.bee.scheduler.context.executor.TaskModuleRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class ApplicationBootStrap {
    private static Log logger = LogFactory.getLog(ApplicationBootStrap.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ApplicationBootStrap.class);
        app.addListeners(
                (ApplicationListener<ApplicationEnvironmentPreparedEvent>) event -> {
                    ConfigurableEnvironment env = event.getEnvironment();
                    if (!env.containsProperty("dburl")) {
                        throw new RuntimeException("please specify --dburl in args(e.g. --dburl=127.0.0.1:3306/bee-scheduler?user=root&password=root&useSSL=false&characterEncoding=UTF-8)");
                    }
                },
                (ApplicationListener<ApplicationReadyEvent>) event -> {
                    ConfigurableApplicationContext applicationContext = event.getApplicationContext();
                    logger.info("loading task modules...");
                    try {
                        new BuildInTaskModuleLoader().load().forEach(TaskModuleRegistry::register);
                        new ClassPathJarArchiveTaskModuleLoader().load().forEach(TaskModuleRegistry::register);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    logger.info("starting scheduler...");
                    try {
                        applicationContext.getBean(Scheduler.class).start();
                    } catch (SchedulerException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        app.run(args);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer PropertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    //调度器工厂
    @Bean
    public CustomizedQuartzSchedulerFactoryBean schedulerFactoryBean(Environment env, DataSource dataSource) {
        CustomizedQuartzSchedulerFactoryBean beeSchedulerFactoryBean = new CustomizedQuartzSchedulerFactoryBean("BeeScheduler", dataSource);
        beeSchedulerFactoryBean.setClusterMode(env.containsProperty("cluster"));
        if (env.containsProperty("thread-pool-size")) {
            beeSchedulerFactoryBean.setThreadPoolSize(env.getRequiredProperty("thread-pool-size", Integer.TYPE));
        }
        if (env.containsProperty("instance-id")) {
            beeSchedulerFactoryBean.setInstanceId(env.getRequiredProperty("instance-id"));
        }
        return beeSchedulerFactoryBean;
    }

    @Bean
    public BeeSchedulerFactoryBean beeSchedulerFactoryBean(CustomizedQuartzSchedulerFactoryBean customizedQuartzSchedulerFactoryBean) {
        return new BeeSchedulerFactoryBean(customizedQuartzSchedulerFactoryBean);
    }
}