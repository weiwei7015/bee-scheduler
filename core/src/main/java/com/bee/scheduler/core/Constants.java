package com.bee.scheduler.core;


public class Constants {

    public static final String SYSNAME = "BeeSheduler";

    public static final String TASK_PARAM_JOB_DATA_KEY = "task_params";

    public static final String TASK_GROUP_Tmp = "Tmp";

    public static final String TASK_GROUP_Manual = "Manual";

    public enum TaskExecState {
        SUCCESS, FAIL, VETOED
    }

    public enum TaskFiredWay {
        SCHEDULE, MANUAL, TMP
    }
}
