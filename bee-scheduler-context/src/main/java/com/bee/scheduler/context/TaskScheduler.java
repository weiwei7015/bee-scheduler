package com.bee.scheduler.context;

import com.bee.scheduler.context.common.Constants;
import com.bee.scheduler.context.common.TaskFiredWay;
import com.bee.scheduler.context.common.TaskSpecialGroup;
import com.bee.scheduler.context.exception.TaskSchedulerException;
import com.bee.scheduler.context.executor.TaskExecutor;
import com.bee.scheduler.context.model.QuickTaskConfig;
import com.bee.scheduler.context.model.TaskConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.quartz.*;
import org.quartz.spi.OperableTrigger;

import java.util.Calendar;
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
            return scheduler.getJobGroupNames();
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
            JobDetail jobDetail = JobBuilder.newJob(TaskExecutor.class).withIdentity(getJobKey(taskConfig.getGroup(), taskConfig.getName())).build();

            JobDataMap jobDataMap = TaskExecutionContextUtil.buildJobDataMapForTask(taskConfig.getTaskModule(), taskConfig.getParams(), taskConfig.getLinkageRule());
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(getTriggerKeyOfScheduleWay(taskConfig.getGroup(), taskConfig.getName())).withDescription(taskConfig.getDescription()).usingJobData(jobDataMap);

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
                scheduleBuilder.withIntervalInSeconds(scheduleOptions.getInterval().intValue()).withRepeatCount(scheduleOptions.getRepeatType() == TaskConfig.REPEAT_TYPE_INFINITE ? -1 : scheduleOptions.getRepeatCount());
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
            TriggerKey taskTriggerKey = getTriggerKeyOfScheduleWay(taskConfig.getGroup(), taskConfig.getName());
            JobDataMap jobDataMap = TaskExecutionContextUtil.buildJobDataMapForTask(taskConfig.getTaskModule(), taskConfig.getParams(), taskConfig.getLinkageRule());
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(taskTriggerKey).withDescription(taskConfig.getDescription()).usingJobData(jobDataMap);

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
                scheduleBuilder.withIntervalInSeconds(scheduleOptions.getInterval().intValue()).withRepeatCount(scheduleOptions.getRepeatType() == TaskConfig.REPEAT_TYPE_INFINITE ? -1 : scheduleOptions.getRepeatCount());

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

    public TaskConfig getTaskConfig(String group, String name) throws TaskSchedulerException {
        try {
            Trigger taskTrigger = scheduler.getTrigger(getTriggerKeyOfScheduleWay(group, name));
            if (taskTrigger == null) {
                return null;
            }

            JobDetail jobDetail = scheduler.getJobDetail(getJobKey(group, name));

            TaskConfig taskConfig = new TaskConfig();
            taskConfig.setName(jobDetail.getKey().getName());
            taskConfig.setGroup(jobDetail.getKey().getGroup());
            taskConfig.setStartAtType(taskTrigger.getStartTime() == null ? TaskConfig.START_AT_TYPE_NOW : TaskConfig.START_AT_TYPE_GIVEN_TIME);
            taskConfig.setStartAt(taskTrigger.getStartTime());
            taskConfig.setEndAtType(taskTrigger.getEndTime() == null ? TaskConfig.END_AT_TYPE_NEVER : TaskConfig.END_AT_TYPE_GIVEN_TIME);
            taskConfig.setEndAt(taskTrigger.getEndTime());
            taskConfig.setTaskModule(taskTrigger.getJobDataMap().getString(Constants.TRIGGER_DATA_KEY_TASK_MODULE_ID));
            taskConfig.setParams(taskTrigger.getJobDataMap().getString(Constants.TRIGGER_DATA_KEY_TASK_PARAM));
            taskConfig.setDescription(taskTrigger.getDescription());
            taskConfig.setLinkageRule(taskTrigger.getJobDataMap().getString(Constants.TRIGGER_DATA_KEY_TASK_LINKAGE_RULE));


            if (taskTrigger instanceof SimpleTrigger) {
                SimpleTrigger trigger = (SimpleTrigger) taskTrigger;

                taskConfig.setScheduleType(TaskConfig.SCHEDULE_TYPE_SIMPLE_TRIGGER);

                TaskConfig.ScheduleTypeSimpleOptions scheduleOptions = taskConfig.getScheduleTypeSimpleOptions();
                scheduleOptions.setInterval(trigger.getRepeatInterval() / 1000);
                scheduleOptions.setRepeatType(trigger.getRepeatCount() == -1 ? TaskConfig.REPEAT_TYPE_INFINITE : TaskConfig.REPEAT_TYPE_GIVEN_COUNT);
                scheduleOptions.setRepeatCount(trigger.getRepeatCount());
                scheduleOptions.setMisfireHandlingType(trigger.getMisfireInstruction());
            } else if (taskTrigger instanceof CalendarIntervalTrigger) {
                CalendarIntervalTrigger trigger = (CalendarIntervalTrigger) taskTrigger;

                taskConfig.setScheduleType(TaskConfig.SCHEDULE_TYPE_CALENDAR_INTERVAL_TRIGGER);

                TaskConfig.ScheduleTypeCalendarIntervalOptions scheduleOptions = taskConfig.getScheduleTypeCalendarIntervalOptions();
                scheduleOptions.setInterval(trigger.getRepeatInterval());
                scheduleOptions.setIntervalUnit(trigger.getRepeatIntervalUnit());
                scheduleOptions.setMisfireHandlingType(trigger.getMisfireInstruction());
            } else if (taskTrigger instanceof DailyTimeIntervalTrigger) {
                DailyTimeIntervalTrigger trigger = (DailyTimeIntervalTrigger) taskTrigger;

                taskConfig.setScheduleType(TaskConfig.SCHEDULE_TYPE_DAILY_TIME_INTERVAL_TRIGGER);

                TaskConfig.ScheduleTypeDailyTimeIntervalOptions scheduleOptions = taskConfig.getScheduleTypeDailyTimeIntervalOptions();
                scheduleOptions.setStartTimeOfDay(trigger.getStartTimeOfDay());
                scheduleOptions.setEndTimeOfDay(trigger.getEndTimeOfDay());
                scheduleOptions.setDaysOfWeek(trigger.getDaysOfWeek().toArray(new Integer[0]));
                scheduleOptions.setInterval(trigger.getRepeatInterval());
                scheduleOptions.setIntervalUnit(trigger.getRepeatIntervalUnit());
                scheduleOptions.setMisfireHandlingType(trigger.getMisfireInstruction());
            } else if (taskTrigger instanceof CronTrigger) {
                CronTrigger trigger = (CronTrigger) taskTrigger;

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
            scheduler.unscheduleJob(getTriggerKeyOfScheduleWay(group, name));
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
            TriggerKey triggerKey = getTriggerKeyOfScheduleWay(group, name);
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
            TriggerKey triggerKey = getTriggerKeyOfScheduleWay(group, name);
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            if (Trigger.TriggerState.PAUSED.equals(triggerState)) {
                scheduler.resumeTrigger(triggerKey);
            }
            scheduler.resumeTrigger(triggerKey);
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }

    public void trigger(String group, String name) throws TaskSchedulerException {
        try {
            JobKey taskJobKey = getJobKey(group, name);
            Trigger taskTrigger = scheduler.getTrigger(getTriggerKeyOfScheduleWay(group, name));
            JobDataMap taskTriggerDataMap = taskTrigger.getJobDataMap();

            TriggerBuilder triggerBuilder = newTrigger().withIdentity(group + "." + name, TaskFiredWay.MANUAL.name()).usingJobData(taskTriggerDataMap).forJob(taskJobKey);

            scheduler.scheduleJob(triggerBuilder.build());
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }

    public void quickTask(QuickTaskConfig quickTaskConfig) throws TaskSchedulerException {
        try {
            String name = quickTaskConfig.getName();
            String group = TaskSpecialGroup.TMP.name();

            JobDetail jobDetail = JobBuilder.newJob(TaskExecutor.class).withIdentity(name, group).build();

            JobDataMap jobDataMap = TaskExecutionContextUtil.buildJobDataMapForTask(quickTaskConfig.getTaskModule(), quickTaskConfig.getParams(), quickTaskConfig.getLinkageRule());
            OperableTrigger operableTrigger = (OperableTrigger) newTrigger().withIdentity(group + "." + name, TaskFiredWay.TMP.name()).usingJobData(jobDataMap).build();

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

    private JobKey getJobKey(String taskGroup, String taskName) {
        return new JobKey(taskName, taskGroup);
    }

    private TriggerKey getTriggerKeyOfScheduleWay(String taskGroup, String taskName) {
        return new TriggerKey(taskGroup + "." + taskName, TaskFiredWay.SCHEDULE.name());
    }
}
