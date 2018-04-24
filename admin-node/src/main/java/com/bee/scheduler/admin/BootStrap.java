package com.bee.scheduler.admin;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.DateCodec;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.bee.scheduler.admin.core.SystemInitializer;
import com.bee.scheduler.core.BeeSchedulerFactoryBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.TimeOfDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class BootStrap {

    private static Log logger = LogFactory.getLog(BootStrap.class);

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

    // mvc相关配置
    @Bean
    public WebMvcConfigurerAdapter webMvcConfigurerAdapter() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
                FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
                FastJsonConfig fastJsonConfig = new FastJsonConfig();
                fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue);

                ParserConfig.getGlobalInstance().putDeserializer(TimeOfDay.class, new ObjectDeserializer() {
                    public final DateCodec dateCodec = new DateCodec();

                    @Override
                    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
                        Date date = dateCodec.deserialze(parser, Date.class, fieldName);
                        return (T) TimeOfDay.hourAndMinuteAndSecondFromDate(date);
                    }

                    @Override
                    public int getFastMatchToken() {
                        return 0;
                    }
                });

                SerializeConfig serializeConfig = new SerializeConfig();
                serializeConfig.put(TimeOfDay.class, (serializer, object, fieldName, fieldType, features) -> {
                    SerializeWriter out = serializer.getWriter();
                    if (object == null) {
                        serializer.getWriter().writeNull();
                        return;
                    }
                    TimeOfDay timeOfDay = (TimeOfDay) object;
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, timeOfDay.getHour());
                    cal.set(Calendar.MINUTE, timeOfDay.getMinute());
                    cal.set(Calendar.SECOND, timeOfDay.getSecond());
                    out.write(String.valueOf(cal.getTimeInMillis()));
                });

                fastJsonConfig.setSerializeConfig(serializeConfig);

                fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);

                converters.add(fastJsonHttpMessageConverter);
            }
        };
    }

    //调度器工厂
    @Bean
    public BeeSchedulerFactoryBean beeSchedulerFactoryBean() {
        BeeSchedulerFactoryBean beeSchedulerFactoryBean = new BeeSchedulerFactoryBean("BeeScheduler", dataSource);
        beeSchedulerFactoryBean.setClusterMode(env.containsProperty("cluster"));
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