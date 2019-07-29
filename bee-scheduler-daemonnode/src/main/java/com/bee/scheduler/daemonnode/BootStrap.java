package com.bee.scheduler.daemonnode;

import com.bee.scheduler.context.BeeSchedulerFactoryBean;
import com.bee.scheduler.context.CustomizedQuartzSchedulerFactoryBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class BootStrap {
    private Log logger = LogFactory.getLog(getClass());
    @Autowired
    private Environment env;
    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BootStrap.class);
        app.addListeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>) event -> {
            //检查启动参数
            ConfigurableEnvironment env = event.getEnvironment();
            if (!env.containsProperty("dburl")) {
                throw new RuntimeException("please specify --dburl in args(e.g. --dburl=127.0.0.1:3306/bee-scheduler?user=root&password=root&useSSL=false&characterEncoding=UTF-8)");
            }
        });
        app.run(args);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer PropertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    //调度器工厂
    @Bean
    public CustomizedQuartzSchedulerFactoryBean customizedQuartzSchedulerFactoryBean() {
        CustomizedQuartzSchedulerFactoryBean beeSchedulerFactoryBean = new CustomizedQuartzSchedulerFactoryBean("BeeScheduler", dataSource);
        beeSchedulerFactoryBean.setClusterMode(true);
        if (env.containsProperty("thread-pool-size")) {
            beeSchedulerFactoryBean.setThreadPoolSize(env.getRequiredProperty("thread-pool-size", Integer.TYPE));
        }
        return beeSchedulerFactoryBean;
    }

    @Bean
    public BeeSchedulerFactoryBean beeSchedulerFactoryBean(CustomizedQuartzSchedulerFactoryBean customizedQuartzSchedulerFactoryBean) {
        return new BeeSchedulerFactoryBean(customizedQuartzSchedulerFactoryBean);
    }


    // 系统启动监听器，用于系统启动完成后的初始化操作
    @Bean
    public ApplicationListener<ContextRefreshedEvent> applicationListener() {
        return event -> {
            ApplicationContext applicationContext = event.getApplicationContext();
            try {
                logger.info("SpringContext Refreshed!");
                SystemInitializer systemInitializer = new SystemInitializer(applicationContext);
                systemInitializer.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}