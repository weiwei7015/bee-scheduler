package com.bee.lemon.model;

import org.quartz.DateBuilder;
import org.quartz.TimeOfDay;

import java.util.Date;
import java.util.Map;

/**
 * @author weiwei
 */
public class TaskConfig {
    private String name = "";
    private String group = "";
    private Integer scheduleType = 4;
    private ScheduleTypeSimpleOptions scheduleTypeSimpleOptions = new ScheduleTypeSimpleOptions();
    private ScheduleTypeCalendarIntervalOptions scheduleTypeCalendarIntervalOptions = new ScheduleTypeCalendarIntervalOptions();
    private ScheduleTypeDailyTimeIntervalOptions scheduleTypeDailyTimeIntervalOptions = new ScheduleTypeDailyTimeIntervalOptions();
    private ScheduleTypeCronOptions scheduleTypeCronOptions = new ScheduleTypeCronOptions();
    private Integer startAtType = 1;
    private Date startAt;
    private Integer endAtType = 1;
    private Date endAt;
    private String jobComponent = "";
    private String params = "";
    private String description = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(Integer scheduleType) {
        this.scheduleType = scheduleType;
    }

    public ScheduleTypeSimpleOptions getScheduleTypeSimpleOptions() {
        return scheduleTypeSimpleOptions;
    }

    public void setScheduleTypeSimpleOptions(ScheduleTypeSimpleOptions scheduleTypeSimpleOptions) {
        this.scheduleTypeSimpleOptions = scheduleTypeSimpleOptions;
    }

    public ScheduleTypeCalendarIntervalOptions getScheduleTypeCalendarIntervalOptions() {
        return scheduleTypeCalendarIntervalOptions;
    }

    public void setScheduleTypeCalendarIntervalOptions(ScheduleTypeCalendarIntervalOptions scheduleTypeCalendarIntervalOptions) {
        this.scheduleTypeCalendarIntervalOptions = scheduleTypeCalendarIntervalOptions;
    }

    public ScheduleTypeDailyTimeIntervalOptions getScheduleTypeDailyTimeIntervalOptions() {
        return scheduleTypeDailyTimeIntervalOptions;
    }

    public void setScheduleTypeDailyTimeIntervalOptions(ScheduleTypeDailyTimeIntervalOptions scheduleTypeDailyTimeIntervalOptions) {
        this.scheduleTypeDailyTimeIntervalOptions = scheduleTypeDailyTimeIntervalOptions;
    }

    public ScheduleTypeCronOptions getScheduleTypeCronOptions() {
        return scheduleTypeCronOptions;
    }

    public void setScheduleTypeCronOptions(ScheduleTypeCronOptions scheduleTypeCronOptions) {
        this.scheduleTypeCronOptions = scheduleTypeCronOptions;
    }

    public Integer getStartAtType() {
        return startAtType;
    }

    public void setStartAtType(Integer startAtType) {
        this.startAtType = startAtType;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Integer getEndAtType() {
        return endAtType;
    }

    public void setEndAtType(Integer endAtType) {
        this.endAtType = endAtType;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }

    public String getJobComponent() {
        return jobComponent;
    }

    public void setJobComponent(String jobComponent) {
        this.jobComponent = jobComponent;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class ScheduleTypeSimpleOptions {
        private Long interval = 6000l;
        private Integer repeatType = 1;
        private Integer repeatCount = 10;
        private Integer misfireHandlingType = 0;

        public Long getInterval() {
            return interval;
        }

        public void setInterval(Long interval) {
            this.interval = interval;
        }

        public Integer getRepeatType() {
            return repeatType;
        }

        public void setRepeatType(Integer repeatType) {
            this.repeatType = repeatType;
        }

        public Integer getRepeatCount() {
            return repeatCount;
        }

        public void setRepeatCount(Integer repeatCount) {
            this.repeatCount = repeatCount;
        }

        public Integer getMisfireHandlingType() {
            return misfireHandlingType;
        }

        public void setMisfireHandlingType(Integer misfireHandlingType) {
            this.misfireHandlingType = misfireHandlingType;
        }
    }

    public static class ScheduleTypeCalendarIntervalOptions {
        private Integer interval = 2;
        private DateBuilder.IntervalUnit intervalUnit = DateBuilder.IntervalUnit.HOUR;
        private Integer misfireHandlingType = 0;

        public Integer getInterval() {
            return interval;
        }

        public void setInterval(Integer interval) {
            this.interval = interval;
        }

        public DateBuilder.IntervalUnit getIntervalUnit() {
            return intervalUnit;
        }

        public void setIntervalUnit(DateBuilder.IntervalUnit intervalUnit) {
            this.intervalUnit = intervalUnit;
        }

        public Integer getMisfireHandlingType() {
            return misfireHandlingType;
        }

        public void setMisfireHandlingType(Integer misfireHandlingType) {
            this.misfireHandlingType = misfireHandlingType;
        }
    }

    public static class ScheduleTypeDailyTimeIntervalOptions {
        private TimeOfDay startTimeOfDay;
        private TimeOfDay endTimeOfDay;
        private Integer[] daysOfWeek = new Integer[0];
        private Integer interval = 2;
        private DateBuilder.IntervalUnit intervalUnit = DateBuilder.IntervalUnit.HOUR;
        private Integer misfireHandlingType = 0;

        public TimeOfDay getStartTimeOfDay() {
            return startTimeOfDay;
        }

        public void setStartTimeOfDay(TimeOfDay startTimeOfDay) {
            this.startTimeOfDay = startTimeOfDay;
        }

        public TimeOfDay getEndTimeOfDay() {
            return endTimeOfDay;
        }

        public void setEndTimeOfDay(TimeOfDay endTimeOfDay) {
            this.endTimeOfDay = endTimeOfDay;
        }

        public Integer[] getDaysOfWeek() {
            return daysOfWeek;
        }

        public void setDaysOfWeek(Integer[] daysOfWeek) {
            this.daysOfWeek = daysOfWeek;
        }

        public Integer getInterval() {
            return interval;
        }

        public void setInterval(Integer interval) {
            this.interval = interval;
        }

        public DateBuilder.IntervalUnit getIntervalUnit() {
            return intervalUnit;
        }

        public void setIntervalUnit(DateBuilder.IntervalUnit intervalUnit) {
            this.intervalUnit = intervalUnit;
        }

        public Integer getMisfireHandlingType() {
            return misfireHandlingType;
        }

        public void setMisfireHandlingType(Integer misfireHandlingType) {
            this.misfireHandlingType = misfireHandlingType;
        }
    }

    public static class ScheduleTypeCronOptions {
        private String cron = "";
        private Integer misfireHandlingType = 0;

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public Integer getMisfireHandlingType() {
            return misfireHandlingType;
        }

        public void setMisfireHandlingType(Integer misfireHandlingType) {
            this.misfireHandlingType = misfireHandlingType;
        }
    }
}
