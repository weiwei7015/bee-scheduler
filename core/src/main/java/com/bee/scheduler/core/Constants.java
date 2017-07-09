package com.bee.scheduler.core;


public class Constants {

    public static final String SYSNAME = "BeeSheduler";

    public static final String JOB_DATA_KEY_TASK_PARAM = "task_params";

    public static final String JOB_DATA_KEY_TASK_LINKAGE_RULE = "task_linkage_rule";

    public static final String JOB_EXEC_CONTEXT_RESULT_MAP_KEY_TASK_LOG = "Task_Exec_Log";

    public static final String TASK_GROUP_Tmp = "Tmp";

    public static final String TASK_GROUP_Manual = "Manual";

    public static final String TASK_GROUP_Linkage = "Linkage";

    public enum TaskExecState {
        SUCCESS, FAIL, VETOED
    }

    public enum TaskFiredWay {
        SCHEDULE, MANUAL, TMP, LINKAGE
    }
}
