package com.bee.scheduler.daemonnode;

import com.bee.scheduler.context.BeeSchedulerFactoryBean;
import com.bee.scheduler.context.CustomizedQuartzSchedulerFactoryBean;
import com.bee.scheduler.context.task.TaskModuleRegistry;
import com.bee.scheduler.daemonnode.core.BuildInTaskModuleLoader;
import com.bee.scheduler.daemonnode.core.ClassPathJarArchiveTaskModuleLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import javax.sql.DataSource;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class ApplicationBootStrap {
    private static Log logger = LogFactory.getLog(ApplicationBootStrap.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ApplicationBootStrap.class);
        //检查启动参数
        SimpleCommandLinePropertySource commandLinePropertySource = new SimpleCommandLinePropertySource(args);
        String dburl = commandLinePropertySource.getProperty("dburl");
        if (dburl == null) {
            throw new RuntimeException("please specify --dburl in args(e.g. --dburl=jdbc:mysql://127.0.0.1:3306/bee-scheduler?user=root&password=root&useSSL=false&characterEncoding=UTF-8)");
        }
        if (dburl.startsWith("jdbc:mysql://")) {
            app.setAdditionalProfiles("mysql");
        } else if (dburl.startsWith("jdbc:postgresql://")) {
            app.setAdditionalProfiles("postgresql");
        } else {
            throw new RuntimeException("invalid argument:dburl");
        }
        //添加自定义初始化
        app.addInitializers(applicationContext -> {
            try {
                logger.info("loading build-in task modules...");
                new BuildInTaskModuleLoader().load().forEach(TaskModuleRegistry::register);
                logger.info("loading classpath-jar-archive task modules...");
                new ClassPathJarArchiveTaskModuleLoader().load().forEach(TaskModuleRegistry::register);
            } catch (Exception e) {
                throw new ApplicationContextException("scheduler init failed!", e);
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
    public CustomizedQuartzSchedulerFactoryBean customizedQuartzSchedulerFactoryBean(Environment env, DataSource dataSource) {
        CustomizedQuartzSchedulerFactoryBean beeSchedulerFactoryBean = new CustomizedQuartzSchedulerFactoryBean("BeeScheduler", dataSource);
        beeSchedulerFactoryBean.setClusterMode(true);
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