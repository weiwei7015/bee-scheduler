package com.bee.scheduler.core;


public class Constants {

    public static final String SYSNAME = "BeeSheduler";

    public static final String TASK_PARAM_JOB_DATA_KEY = "task_params";

    public static final String TASK_GROUP_Tmp = "Tmp";
    public static final String TASK_GROUP_Manual = "Manual";

    //触发类型：调度触发
    public static Integer TASK_TRIGGER_TYPE_SCHEDULER = 1;
    //触发类型：手动触发
    public static Integer TASK_TRIGGER_TYPE_MANUAL = 2;
    //触发类型：临时任务
    public static Integer TASK_TRIGGER_TYPE_TMP = 3;

    public static String TaskExecState_VOTED = "VETOED";
    public static String TaskExecState_SUCCESS = "SUCCESS";
    public static String TaskExecState_FAIL = "FAIL";


}
