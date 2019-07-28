package com.bee.scheduler.context;

import com.bee.scheduler.context.exception.TaskSchedulerException;
import com.bee.scheduler.context.executor.TaskExecutor;
import com.bee.scheduler.context.model.TaskConfig;
import com.bee.scheduler.context.model.QuickTaskConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.*;
import org.quartz.spi.OperableTrigger;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author weiwei
 */
public class TaskScheduler {
    private Scheduler scheduler;

    public TaskScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public List<String> getTaskGroups() throws TaskSchedulerException {
        try {
            return scheduler.getTriggerGroupNames();
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }

    public String getSchedulerName() throws TaskSchedulerException {
        try {
            return scheduler.getSchedulerName();
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }

    public void schedule(TaskConfig taskConfig) throws TaskSchedulerException {
        try {
            JobDataMap jobDataMap = TaskExecutionContextUtil.buildJobDataMapForTask(taskConfig.getJobModule(), taskConfig.getParams(), taskConfig.getLinkageRule());

            JobDetail jobDetail = JobBuilder.newJob(TaskExecutor.class).withIdentity(taskConfig.getName(), taskConfig.getGroup()).build();
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(taskConfig.getName(), taskConfig.getGroup()).usingJobData(jobDataMap).withDescription(taskConfig.getDescription());

            if (taskConfig.getStartAtType() == TaskConfig.START_AT_TYPE_NOW) {
                triggerBuilder.startNow();
            } else {
                triggerBuilder.startAt(taskConfig.getStartAt());
            }
            if (taskConfig.getEndAtType() != TaskConfig.END_AT_TYPE_NEVER) {
                triggerBuilder.endAt(taskConfig.getEndAt());
            }

            if (taskConfig.getScheduleType() == TaskConfig.SCHEDULE_TYPE_SIMPLE_TRIGGER) {
                TaskConfig.ScheduleTypeSimpleOptions scheduleOptions = taskConfig.getScheduleTypeSimpleOptions();

                SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
                scheduleBuilder.withIntervalInMilliseconds(scheduleOptions.getInterval()).withRepeatCount(scheduleOptions.getRepeatType() == TaskConfig.REPEAT_TYPE_INFINITE ? -1 : scheduleOptions.getRepeatCount());
                if (scheduleOptions.getMisfireHandlingType() == SimpleTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
                    scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
                } else if (scheduleOptions.getMisfireHandlingType() == SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW) {
                    scheduleBuilder.withMisfireHandlingInstructionFireNow();
                } else if (scheduleOptions.getMisfireHandlingType() == SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT) {
                    scheduleBuilder.withMisfireHandlingInstructionNowWithExistingCount();
                } else if (scheduleOptions.getMisfireHandlingType() == SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT) {
                    scheduleBuilder.withMisfireHandlingInstructionNowWithRemainingCount();
                } else if (scheduleOptions.getMisfireHandlingType() == SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT) {
                    scheduleBuilder.withMisfireHandlingInstructionNextWithRemainingCount();
                } else if (scheduleOptions.getMisfireHandlingType() == SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT) {
                    scheduleBuilder.withMisfireHandlingInstructionNextWithExistingCount();
                }

                SimpleTrigger trigger = triggerBuilder.withSchedule(scheduleBuilder).build();
                scheduler.scheduleJob(jobDetail, trigger);
            } else if (taskConfig.getScheduleType() == TaskConfig.SCHEDULE_TYPE_CALENDAR_INTERVAL_TRIGGER) {
                TaskConfig.ScheduleTypeCalendarIntervalOptions scheduleOptions = taskConfig.getScheduleTypeCalendarIntervalOptions();

                CalendarIntervalScheduleBuilder scheduleBuilder = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
                scheduleBuilder.withInterval(scheduleOptions.getInterval(), scheduleOptions.getIntervalUnit());

                if (scheduleOptions.getMisfireHandlingType() == CalendarIntervalTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
                    scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
                } else if (scheduleOptions.getMisfireHandlingType() == CalendarIntervalTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) {
                    scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
                } else if (scheduleOptions.getMisfireHandlingType() == CalendarIntervalTrigger.MISFIRE_INSTRUCTION_DO_NOTHING) {
                    scheduleBuilder.withMisfireHandlingInstructionDoNothing();
                }

                CalendarIntervalTrigger trigger = triggerBuilder.withSchedule(scheduleBuilder).build();

                scheduler.scheduleJob(jobDetail, trigger);
            } else if (taskConfig.getScheduleType() == TaskConfig.SCHEDULE_TYPE_DAILY_TIME_INTERVAL_TRIGGER) {
                TaskConfig.ScheduleTypeDailyTimeIntervalOptions scheduleOptions = taskConfig.getScheduleTypeDailyTimeIntervalOptions();

                DailyTimeIntervalScheduleBuilder scheduleBuilder = DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule();
                scheduleBuilder.withInterval(scheduleOptions.getInterval(), scheduleOptions.getIntervalUnit());

                if (scheduleOptions.getStartTimeOfDay() != null) {
                    scheduleBuilder.startingDailyAt(scheduleOptions.getStartTimeOfDay());
                }
                if (scheduleOptions.getEndTimeOfDay() != null) {
                    scheduleBuilder.endingDailyAt(scheduleOptions.getEndTimeOfDay());
                }
                if (ArrayUtils.isNotEmpty(scheduleOptions.getDaysOfWeek())) {
                    scheduleBuilder.onDaysOfTheWeek(scheduleOptions.getDaysOfWeek());
                }

                if (scheduleOptions.getMisfireHandlingType() == DailyTimeIntervalTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
                    scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
                } else if (scheduleOptions.getMisfireHandlingType() == DailyTimeIntervalTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) {
                    scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
                } else if (scheduleOptions.getMisfireHandlingType() == DailyTimeIntervalTrigger.MISFIRE_INSTRUCTION_DO_NOTHING) {
                    scheduleBuilder.withMisfireHandlingInstructionDoNothing();
                }

                DailyTimeIntervalTrigger trigger = triggerBuilder.withSchedule(scheduleBuilder).build();
                scheduler.scheduleJob(jobDetail, trigger);
            } else if (taskConfig.getScheduleType() == TaskConfig.SCHEDULE_TYPE_CRON_TRIGGER) {
                TaskConfig.ScheduleTypeCronOptions scheduleOptions = taskConfig.getScheduleTypeCronOptions();

                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleOptions.getCron());

                if (scheduleOptions.getMisfireHandlingType() == CronTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
                    scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
                } else if (scheduleOptions.getMisfireHandlingType() == CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) {
                    scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
                } else if (scheduleOptions.getMisfireHandlingType() == CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING) {
                    scheduleBuilder.withMisfireHandlingInstructionDoNothing();
                }

                CronTrigger trigger = triggerBuilder.withSchedule(scheduleBuilder).build();

                scheduler.scheduleJob(jobDetail, trigger);
            }
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }

    public void reschedule(TaskConfig taskConfig) throws TaskSchedulerException {
        try {
            JobDataMap dataMap = TaskExecutionContextUtil.buildJobDataMapForTask(taskConfig.getJobModule(), taskConfig.getParams(), taskConfig.getLinkageRule());

            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(taskConfig.getName(), taskConfig.getGroup()).usingJobData(dataMap).withDescription(taskConfig.getDescription());

            if (taskConfig.getStartAtType() == TaskConfig.START_AT_TYPE_NOW) {
                triggerBuilder.startNow();
            } else {
                triggerBuilder.startAt(taskConfig.getStartAt());
            }
            if (taskConfig.getEndAtType() != TaskConfig.END_AT_TYPE_NEVER) {
                triggerBuilder.endAt(taskConfig.getEndAt());
            }


            if (taskConfig.getScheduleType() == TaskConfig.SCHEDULE_TYPE_SIMPLE_TRIGGER) {
                TaskConfig.ScheduleTypeSimpleOptions scheduleOptions = taskConfig.getScheduleTypeSimpleOptions();

                SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
                scheduleBuilder.withIntervalInMilliseconds(scheduleOptions.getInterval())
                        .withRepeatCount(scheduleOptions.getRepeatType() == TaskConfig.REPEAT_TYPE_INFINITE ? -1 : scheduleOptions.getRepeatCount());

                if (scheduleOptions.getMisfireHandlingType() == SimpleTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
                    scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
                } else if (scheduleOptions.getMisfireHandlingType() == SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW) {
                    scheduleBuilder.withMisfireHandlingInstructionFireNow();
                } else if (scheduleOptions.getMisfireHandlingType() == SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT) {
                    scheduleBuilder.withMisfireHandlingInstructionNowWithExistingCount();
                } else if (scheduleOptions.getMisfireHandlingType() == SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT) {
                    scheduleBuilder.withMisfireHandlingInstructionNowWithRemainingCount();
                } else if (scheduleOptions.getMisfireHandlingType() == SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT) {
                    scheduleBuilder.withMisfireHandlingInstructionNextWithRemainingCount();
                } else if (scheduleOptions.getMisfireHandlingType() == SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT) {
                    scheduleBuilder.withMisfireHandlingInstructionNextWithExistingCount();
                }

                SimpleTrigger trigger = triggerBuilder.withSchedule(scheduleBuilder).build();

                scheduler.rescheduleJob(trigger.getKey(), trigger);
            } else if (taskConfig.getScheduleType() == TaskConfig.SCHEDULE_TYPE_CALENDAR_INTERVAL_TRIGGER) {
                TaskConfig.ScheduleTypeCalendarIntervalOptions scheduleOptions = taskConfig.getScheduleTypeCalendarIntervalOptions();

                CalendarIntervalScheduleBuilder scheduleBuilder = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
                scheduleBuilder.withInterval(scheduleOptions.getInterval(), scheduleOptions.getIntervalUnit());

                if (scheduleOptions.getMisfireHandlingType() == CalendarIntervalTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
                    scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
                } else if (scheduleOptions.getMisfireHandlingType() == CalendarIntervalTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) {
                    scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
                } else if (scheduleOptions.getMisfireHandlingType() == CalendarIntervalTrigger.MISFIRE_INSTRUCTION_DO_NOTHING) {
                    scheduleBuilder.withMisfireHandlingInstructionDoNothing();
                }


                CalendarIntervalTrigger trigger = triggerBuilder.withSchedule(scheduleBuilder).build();

                scheduler.rescheduleJob(trigger.getKey(), trigger);
            } else if (taskConfig.getScheduleType() == TaskConfig.SCHEDULE_TYPE_DAILY_TIME_INTERVAL_TRIGGER) {
                TaskConfig.ScheduleTypeDailyTimeIntervalOptions scheduleOptions = taskConfig.getScheduleTypeDailyTimeIntervalOptions();

                DailyTimeIntervalScheduleBuilder scheduleBuilder = DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule();
                scheduleBuilder.withInterval(scheduleOptions.getInterval(), scheduleOptions.getIntervalUnit());

                if (scheduleOptions.getStartTimeOfDay() != null) {
                    scheduleBuilder.startingDailyAt(scheduleOptions.getStartTimeOfDay());
                }
                if (scheduleOptions.getEndTimeOfDay() != null) {
                    scheduleBuilder.endingDailyAt(scheduleOptions.getEndTimeOfDay());
                }
                if (ArrayUtils.isNotEmpty(scheduleOptions.getDaysOfWeek())) {
                    scheduleBuilder.onDaysOfTheWeek(scheduleOptions.getDaysOfWeek());
                }

                if (scheduleOptions.getMisfireHandlingType() == DailyTimeIntervalTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
                    scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
                } else if (scheduleOptions.getMisfireHandlingType() == DailyTimeIntervalTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) {
                    scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
                } else if (scheduleOptions.getMisfireHandlingType() == DailyTimeIntervalTrigger.MISFIRE_INSTRUCTION_DO_NOTHING) {
                    scheduleBuilder.withMisfireHandlingInstructionDoNothing();
                }

                DailyTimeIntervalTrigger trigger = triggerBuilder.withSchedule(scheduleBuilder).build();

                scheduler.rescheduleJob(trigger.getKey(), trigger);
            } else if (taskConfig.getScheduleType() == TaskConfig.SCHEDULE_TYPE_CRON_TRIGGER) {
                TaskConfig.ScheduleTypeCronOptions scheduleOptions = taskConfig.getScheduleTypeCronOptions();

                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleOptions.getCron());

                if (scheduleOptions.getMisfireHandlingType() == CronTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) {
                    scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
                } else if (scheduleOptions.getMisfireHandlingType() == CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) {
                    scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
                } else if (scheduleOptions.getMisfireHandlingType() == CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING) {
                    scheduleBuilder.withMisfireHandlingInstructionDoNothing();
                }

                CronTrigger trigger = triggerBuilder.withSchedule(scheduleBuilder).build();

                scheduler.rescheduleJob(trigger.getKey(), trigger);
            }
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }

    public TaskConfig getTaskDef(String group, String name) throws TaskSchedulerException {
        try {
            Trigger abstractTrigger = scheduler.getTrigger(new TriggerKey(name, group));
            if (abstractTrigger == null) {
                return null;
            }

            JobDetail jobDetail = scheduler.getJobDetail(abstractTrigger.getJobKey());

            TaskConfig taskConfig = new TaskConfig();
            taskConfig.setName(abstractTrigger.getKey().getName());
            taskConfig.setGroup(abstractTrigger.getKey().getGroup());
            taskConfig.setStartAtType(abstractTrigger.getStartTime() == null ? TaskConfig.START_AT_TYPE_NOW : TaskConfig.START_AT_TYPE_GIVEN_TIME);
            taskConfig.setStartAt(abstractTrigger.getStartTime());
            taskConfig.setEndAtType(abstractTrigger.getEndTime() == null ? TaskConfig.END_AT_TYPE_NEVER : TaskConfig.END_AT_TYPE_GIVEN_TIME);
            taskConfig.setEndAt(abstractTrigger.getEndTime());
            taskConfig.setJobModule(jobDetail.getJobClass().getSimpleName());
            taskConfig.setParams(abstractTrigger.getJobDataMap().getString(Constants.JOB_DATA_KEY_TASK_PARAM));
            taskConfig.setDescription(abstractTrigger.getDescription());
            taskConfig.setLinkageRule(abstractTrigger.getJobDataMap().getString(Constants.JOB_DATA_KEY_TASK_LINKAGE_RULE));


            if (abstractTrigger instanceof SimpleTrigger) {
                SimpleTrigger trigger = (SimpleTrigger) abstractTrigger;

                taskConfig.setScheduleType(TaskConfig.SCHEDULE_TYPE_SIMPLE_TRIGGER);

                TaskConfig.ScheduleTypeSimpleOptions scheduleOptions = taskConfig.getScheduleTypeSimpleOptions();
                scheduleOptions.setInterval(trigger.getRepeatInterval());
                scheduleOptions.setRepeatType(trigger.getRepeatCount() == -1 ? TaskConfig.REPEAT_TYPE_INFINITE : TaskConfig.REPEAT_TYPE_GIVEN_COUNT);
                scheduleOptions.setRepeatCount(trigger.getRepeatCount());
                scheduleOptions.setMisfireHandlingType(trigger.getMisfireInstruction());
            } else if (abstractTrigger instanceof CalendarIntervalTrigger) {
                CalendarIntervalTrigger trigger = (CalendarIntervalTrigger) abstractTrigger;

                taskConfig.setScheduleType(TaskConfig.SCHEDULE_TYPE_CALENDAR_INTERVAL_TRIGGER);

                TaskConfig.ScheduleTypeCalendarIntervalOptions scheduleOptions = taskConfig.getScheduleTypeCalendarIntervalOptions();
                scheduleOptions.setInterval(trigger.getRepeatInterval());
                scheduleOptions.setIntervalUnit(trigger.getRepeatIntervalUnit());
                scheduleOptions.setMisfireHandlingType(trigger.getMisfireInstruction());
            } else if (abstractTrigger instanceof DailyTimeIntervalTrigger) {
                DailyTimeIntervalTrigger trigger = (DailyTimeIntervalTrigger) abstractTrigger;

                taskConfig.setScheduleType(TaskConfig.SCHEDULE_TYPE_DAILY_TIME_INTERVAL_TRIGGER);

                TaskConfig.ScheduleTypeDailyTimeIntervalOptions scheduleOptions = taskConfig.getScheduleTypeDailyTimeIntervalOptions();
                scheduleOptions.setStartTimeOfDay(trigger.getStartTimeOfDay());
                scheduleOptions.setEndTimeOfDay(trigger.getEndTimeOfDay());
                scheduleOptions.setDaysOfWeek(trigger.getDaysOfWeek().toArray(new Integer[0]));
                scheduleOptions.setInterval(trigger.getRepeatInterval());
                scheduleOptions.setIntervalUnit(trigger.getRepeatIntervalUnit());
                scheduleOptions.setMisfireHandlingType(trigger.getMisfireInstruction());
            } else if (abstractTrigger instanceof CronTrigger) {
                CronTrigger trigger = (CronTrigger) abstractTrigger;

                taskConfig.setScheduleType(TaskConfig.SCHEDULE_TYPE_CRON_TRIGGER);

                TaskConfig.ScheduleTypeCronOptions scheduleOptions = taskConfig.getScheduleTypeCronOptions();
                scheduleOptions.setCron(trigger.getCronExpression());
                scheduleOptions.setMisfireHandlingType(trigger.getMisfireInstruction());
            }
            return taskConfig;
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }


    public void unschedule(String group, String name) throws TaskSchedulerException {
        try {
            scheduler.unscheduleJob(new TriggerKey(name, group));
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }

    public TaskConfig.TriggerState getTriggerState(String group, String name) throws TaskSchedulerException {
        try {
            Trigger.TriggerState triggerState = scheduler.getTriggerState(new TriggerKey(name, group));
            switch (triggerState) {
                case NONE:
                    return TaskConfig.TriggerState.NONE;
                case NORMAL:
                    return TaskConfig.TriggerState.NORMAL;
                case PAUSED:
                    return TaskConfig.TriggerState.PAUSED;
                case COMPLETE:
                    return TaskConfig.TriggerState.COMPLETE;
                case ERROR:
                    return TaskConfig.TriggerState.ERROR;
                case BLOCKED:
                    return TaskConfig.TriggerState.BLOCKED;
                default:
                    return null;
            }
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }


    public void pause(String group, String name) throws TaskSchedulerException {
        try {
            TriggerKey triggerKey = new TriggerKey(name, group);
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            if (!Trigger.TriggerState.PAUSED.equals(triggerState)) {
                scheduler.pauseTrigger(triggerKey);
            }
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }

    public void resume(String group, String name) throws TaskSchedulerException {
        try {
            TriggerKey triggerKey = new TriggerKey(name, group);
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            if (Trigger.TriggerState.PAUSED.equals(triggerState)) {
                scheduler.resumeTrigger(triggerKey);
            }
            scheduler.resumeTrigger(triggerKey);
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }

    public void execute(String group, String name) throws TaskSchedulerException {
        try {
            JobKey jobKey = new JobKey(name, group);
            Trigger trigger = scheduler.getTrigger(new TriggerKey(name, group));
            JobDataMap jobDataMap = trigger.getJobDataMap();

            String randomTriggerName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
            OperableTrigger operableTrigger = (OperableTrigger) newTrigger().withIdentity(randomTriggerName, Constants.TASK_GROUP_MANUAL).forJob(jobKey).withDescription("手动执行【" + group + "." + name + "】").build();
            if (jobDataMap != null) {
                operableTrigger.setJobDataMap(jobDataMap);
            }
            scheduler.scheduleJob(operableTrigger);
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }

    public void quickTask(QuickTaskConfig quickTaskConfig) throws TaskSchedulerException {
        try {
            String name = quickTaskConfig.getName();
            String group = Constants.TASK_GROUP_TMP;

            JobDataMap jobDataMap = TaskExecutionContextUtil.buildJobDataMapForTask(quickTaskConfig.getJobModule(), quickTaskConfig.getParams(), quickTaskConfig.getLinkageRule());
            JobDetail jobDetail = JobBuilder.newJob(TaskExecutor.class).withIdentity(name, group).build();
            OperableTrigger operableTrigger = (OperableTrigger) newTrigger().withIdentity(name, group).usingJobData(jobDataMap).withDescription("临时任务").build();

            if (quickTaskConfig.getEnableStartDelay() && quickTaskConfig.getStartDelay() != null) {
                Calendar startTime = Calendar.getInstance();
                startTime.add(Calendar.MILLISECOND, quickTaskConfig.getStartDelay());
                operableTrigger.setStartTime(startTime.getTime());
            }
            scheduler.scheduleJob(jobDetail, operableTrigger);
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }
}
