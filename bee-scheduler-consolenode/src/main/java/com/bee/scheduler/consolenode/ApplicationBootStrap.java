package com.bee.scheduler.consolenode;

import com.bee.scheduler.consolenode.core.BuildInTaskModuleLoader;
import com.bee.scheduler.consolenode.core.ClassPathJarArchiveTaskModuleLoader;
import com.bee.scheduler.consolenode.web.PassportInterceptor;
import com.bee.scheduler.context.BeeSchedulerFactoryBean;
import com.bee.scheduler.context.CustomizedQuartzSchedulerFactoryBean;
import com.bee.scheduler.context.task.TaskModuleRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.Map;

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
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new PassportInterceptor()).excludePathPatterns("/public/**", "/configs", "/passport/**", "/error", "/", "");
            }
        };
    }

    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
                errorAttributes.remove("exception");
                errorAttributes.remove("status");
                errorAttributes.remove("errors");
                errorAttributes.remove("trace");
                errorAttributes.remove("path");
                return errorAttributes;
            }
        };
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