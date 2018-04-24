package com.bee.scheduler.runnable;

import com.bee.scheduler.core.BeeSchedulerFactoryBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.regex.Pattern;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class BootStrap {

    private Log logger = LogFactory.getLog(getClass());

    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(BootStrap.class);

        //检查启动参数
        StringBuilder stringBuilder = new StringBuilder(" ");
        for (String arg : args) {
            stringBuilder.append(arg).append(" ");
        }
        if (!Pattern.matches(".* --dburl=\\S+ .*", stringBuilder)) {
            throw new RuntimeException("please specify --dburl in args(e.g. --dburl=127.0.0.1:3306/bee-scheduler?user=root&password=root&useSSL=false)");
        }

        app.run(args);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer PropertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }


    //调度器工厂
    @Bean
    public BeeSchedulerFactoryBean beeSchedulerFactoryBean() {
        BeeSchedulerFactoryBean beeSchedulerFactoryBean = new BeeSchedulerFactoryBean("BeeScheduler", dataSource);
        beeSchedulerFactoryBean.setClusterMode(true);
        if (env.containsProperty("thread-pool-size")) {
            beeSchedulerFactoryBean.setThreadPoolSize(Integer.valueOf(env.getProperty("thread-pool-size")));
        }
        return beeSchedulerFactoryBean;
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