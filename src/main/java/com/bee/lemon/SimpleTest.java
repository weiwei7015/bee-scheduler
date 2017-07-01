package com.bee.lemon;


import com.alibaba.fastjson.serializer.DateCodec;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.regex.Pattern;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class SimpleTest {

    public static void main(String[] args) throws Exception {

//        // Grab the Scheduler instance from the Factory
//        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//
//        // and start it off
//        scheduler.start();
//
//
//        // define the job and tie it to our HelloJob class
//        JobDetail hellojob = JobBuilder.newJob(HelloJob.class).withIdentity("hellojob").build();
//
//        // Trigger the job to run now, and then repeat every 40 seconds
//        Trigger trigger = TriggerBuilder.newTrigger()
//                .withIdentity("trigger1", "group1")
//                .startNow()
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
////                .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule())
////                .withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().startingDailyAt())
////                .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule())
//                .build();
//
//        // Tell quartz to schedule the job using our trigger
//        scheduler.scheduleJob(hellojob, trigger);
//
//
//        Thread.sleep(9000);
//
//        scheduler.shutdown();

        Date date = DateUtils.parseDate("2017-07-01T02:02:30.311Z", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");


        DateCodec dateCodec = new DateCodec();


        System.out.println("date = " + DateFormatUtils.format(date,"yyyy-MM-dd HH:mm:ss.SSS"));


    }
}
