package com.bee.lemon;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.regex.Pattern;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class SimpleTest {

    public static void main(String[] args) throws Exception {

        // Grab the Scheduler instance from the Factory
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // and start it off
        scheduler.start();


        // define the job and tie it to our HelloJob class
        JobDetail hellojob = JobBuilder.newJob(HelloJob.class).withIdentity("hellojob").build();

        // Trigger the job to run now, and then repeat every 40 seconds
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
//                .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule())
//                .withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().startingDailyAt())
//                .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule())
                .build();

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(hellojob, trigger);


        Thread.sleep(9000);

        scheduler.shutdown();
    }
}
