package com.bee.scheduler.consolenode.web.controller;

import com.bee.scheduler.context.exception.TaskSchedulerException;
import com.bee.scheduler.context.task.TaskScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author weiwei
 */

@RestController
public class DemoController {

    @Autowired
    private TaskScheduler taskScheduler;

    @GetMapping("/create/task")
    public void creatTask() throws TaskSchedulerException {
//        for (int i = 0; i < 20000; i++) {
//            TaskConfig taskConfig = new TaskConfig();
//            taskConfig.setScheduleType(TaskConfig.SCHEDULE_TYPE_SIMPLE_TRIGGER);
//            taskConfig.setTaskModule("JustTestModule");
//            taskConfig.setGroup("Default");
//            String taskName = "Task" + (i + 1);
//            taskConfig.setName(taskName);
//            taskConfig.setParams("{content:\"hello,i'm " + taskName + "\"}");
//            taskConfig.setStartAt(new Date());
//            TaskConfig.ScheduleTypeSimpleOptions scheduleTypeSimpleOptions = taskConfig.getScheduleTypeSimpleOptions();
//            scheduleTypeSimpleOptions.setInterval(10L);
//            scheduleTypeSimpleOptions.setRepeatType(TaskConfig.REPEAT_TYPE_INFINITE);
//            taskScheduler.schedule(taskConfig);
//            System.out.println(taskName + " scheduled!");
//        }

        for (int i = 0; i < 20000; i++) {
            try {
                taskScheduler.unschedule("Default", "Task" + (i + 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
