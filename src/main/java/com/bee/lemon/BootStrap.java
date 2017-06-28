package com.bee.lemon;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.bee.lemon.core.SystemInitializer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class BootStrap {

    private Log logger = LogFactory.getLog(getClass());

//    @Autowired
//    private Environment env;

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(BootStrap.class);
//        app.setBanner((environment, sourceClass, out) -> out.append("=============$$$$$$===========\n"));

        Map<String, Object> defaultProperties = new HashMap<>();
        defaultProperties.put("spring.datasource.initialize", false);


        // 是否需要初始化数据库
        if (ArrayUtils.contains(args, "--initdb")) {
            defaultProperties.put("spring.datasource.initialize", true);
        }

        app.setDefaultProperties(defaultProperties);

        app.run(args);
    }

    //    @Bean
//    public static PropertySourcesPlaceholderConfigurer PropertySourcesPlaceholderConfigurer() {
//        return new PropertySourcesPlaceholderConfigurer();
//    }

    // mvc相关配置
    @Bean
    public WebMvcConfigurerAdapter webMvcConfigurerAdapter() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
                converters.add(new FastJsonHttpMessageConverter());
            }
        };
    }

    // Quartz调度器工厂
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setSchedulerName("DefaultQuartzScheduler");
        Properties quartzProperties = new Properties();
        quartzProperties.setProperty("org.quartz.jobStore.useProperties", "true");
        schedulerFactoryBean.setQuartzProperties(quartzProperties);
        return schedulerFactoryBean;
    }

    // 系统启动监听器，用于系统启动完成后的初始化操作
    @Bean
    public ApplicationListener<ContextRefreshedEvent> applicationListener() {
        return new ApplicationListener<ContextRefreshedEvent>() {
            @Override
            public void onApplicationEvent(ContextRefreshedEvent event) {
                ApplicationContext applicationContext = event.getApplicationContext();
                try {
                    logger.info("SpringContext Refreshed!");
                    SystemInitializer systemInitializer = new SystemInitializer(applicationContext);
                    systemInitializer.init();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}