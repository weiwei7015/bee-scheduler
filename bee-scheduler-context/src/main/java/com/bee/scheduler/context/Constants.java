package com.bee.scheduler.context;


public class Constants {

    public static final String JOB_DATA_KEY_TASK_PARAM = "TASK_PARAMS";

    public static final String JOB_DATA_KEY_TASK_LINKAGE_RULE = "TASK_LINKAGE_RULE";

    public static final String JOB_DATA_KEY_TASK_MODULE_ID = "";

    public static final String JOB_EXEC_CONTEXT_RESULT_MAP_KEY_TASK_LOG = "TASK_EXEC_LOGGER";

    public static final String TASK_GROUP_TMP = "Tmp";

    public static final String TASK_GROUP_MANUAL = "Manual";

    public static final String TASK_GROUP_LINKAGE = "Linkage";

    public static final String TASK_GROUP_SYSTEM = "System";

    public enum TaskExecState {
        SUCCESS, FAIL, VETOED
    }

    public enum TaskFiredWay {
        SCHEDULE, MANUAL, TMP, LINKAGE
    }
}
