package com.bee.scheduler.context.listener;

import com.bee.scheduler.context.TaskExecutionContextUtil;
import com.bee.scheduler.context.common.TaskExecState;
import com.bee.scheduler.core.ExecutionResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.utils.DBConnectionManager;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author weiwei
 */
public class TaskHistoryListener extends TaskListenerSupport {
    private Log logger = LogFactory.getLog(TaskHistoryListener.class);

    @Override
    public String getName() {
        return "TaskHistoryListener";
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        try {
            JobDetail jobDetail = context.getJobDetail();
            Trigger trigger = context.getTrigger();
            Scheduler scheduler = context.getScheduler();
            String taskGroup = jobDetail.getKey().getGroup();
            String taskName = jobDetail.getKey().getName();
            String triggerGroup = trigger.getKey().getGroup();
            String schedulerInstanceId = scheduler.getSchedulerInstanceId();
            String schedulerName = scheduler.getSchedulerName();
            Date now = Calendar.getInstance().getTime();

            ExecutionResult moduleExecutionResult = TaskExecutionContextUtil.getModuleExecutionResult(context);

            String execState;
            if (jobException != null) {
                execState = TaskExecState.FAIL.name();
            } else {
                execState = moduleExecutionResult == null ? "UNKNOWN" : moduleExecutionResult.isSuccess() ? TaskExecState.SUCCESS.name() : TaskExecState.FAIL.name();
            }

            // 记录执行历史
            TaskHistory taskHistory = new TaskHistory();
            taskHistory.setSchedName(schedulerName);
            taskHistory.setInstanceId(schedulerInstanceId);
            taskHistory.setFireId(context.getFireInstanceId());
            taskHistory.setTaskName(taskName);
            taskHistory.setTaskGroup(taskGroup);
            taskHistory.setFiredTime(context.getFireTime().getTime());
            taskHistory.setFiredWay(triggerGroup);
            taskHistory.setCompleteTime(now.getTime());
            taskHistory.setExpendTime(context.getJobRunTime());
            taskHistory.setRefired(context.getRefireCount());
            taskHistory.setExecState(execState);
            taskHistory.setLog(getTaskLog());

            save(taskHistory);
        } catch (Exception e) {
            logger.error("记录任务历史异常", e);
        }
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        try {
            JobDetail jobDetail = context.getJobDetail();
            Trigger trigger = context.getTrigger();
            Scheduler scheduler = context.getScheduler();
            String taskGroup = jobDetail.getKey().getGroup();
            String taskName = jobDetail.getKey().getName();
            String triggerGroup = trigger.getKey().getGroup();
            String schedulerInstanceId = scheduler.getSchedulerInstanceId();
            String schedulerName = scheduler.getSchedulerName();
            Date now = Calendar.getInstance().getTime();

            logger.warn("任务[" + taskGroup + "." + taskName + "]已被取消执行,FireId:" + context.getFireInstanceId());

            // 记录执行历史
            TaskHistory taskHistory = new TaskHistory();
            taskHistory.setSchedName(schedulerName);
            taskHistory.setInstanceId(schedulerInstanceId);
            taskHistory.setFireId(context.getFireInstanceId());
            taskHistory.setTaskName(taskName);
            taskHistory.setTaskGroup(taskGroup);
            taskHistory.setFiredTime(context.getFireTime().getTime());
            taskHistory.setFiredWay(triggerGroup);
            taskHistory.setCompleteTime(now.getTime());
            taskHistory.setExpendTime(context.getJobRunTime());
            taskHistory.setRefired(context.getRefireCount());
            taskHistory.setExecState(TaskExecState.VETOED.name());
            taskHistory.setLog(getTaskLog());

            save(taskHistory);
        } catch (Exception e) {
            logger.error("记录任务历史异常", e);
        }
    }

    private void save(TaskHistory taskHistory) throws SQLException {
        String sql = "INSERT INTO BS_TASK_HISTORY(SCHED_NAME,INSTANCE_ID,FIRE_ID, TASK_NAME, TASK_GROUP, FIRED_TIME, FIRED_WAY, COMPLETE_TIME, EXPEND_TIME, REFIRED, EXEC_STATE, LOG) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (
                Connection connection = DBConnectionManager.getInstance().getConnection(LocalDataSourceJobStore.TX_DATA_SOURCE_PREFIX + taskHistory.getSchedName());
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, taskHistory.getSchedName());
            preparedStatement.setString(2, taskHistory.getInstanceId());
            preparedStatement.setString(3, taskHistory.getFireId());
            preparedStatement.setString(4, taskHistory.getTaskName());
            preparedStatement.setString(5, taskHistory.getTaskGroup());
            preparedStatement.setLong(6, taskHistory.getFiredTime());
            preparedStatement.setString(7, taskHistory.getFiredWay());
            preparedStatement.setLong(8, taskHistory.getCompleteTime());
            preparedStatement.setLong(9, taskHistory.getExpendTime());
            preparedStatement.setInt(10, taskHistory.getRefired());
            preparedStatement.setString(11, taskHistory.getExecState());
            preparedStatement.setString(12, taskHistory.getLog());
            preparedStatement.execute();
        }
    }

    private static class TaskHistory {
        private String schedName;
        private String instanceId;
        private String fireId;
        private String taskName;
        private String taskGroup;
        private Long firedTime;
        private String firedWay;
        private Long completeTime;
        private Long expendTime;
        private Integer refired;
        private String execState;
        private String log;

        String getSchedName() {
            return schedName;
        }

        void setSchedName(String schedName) {
            this.schedName = schedName;
        }

        String getInstanceId() {
            return instanceId;
        }

        void setInstanceId(String instanceId) {
            this.instanceId = instanceId;
        }

        String getFireId() {
            return fireId;
        }

        void setFireId(String fireId) {
            this.fireId = fireId;
        }

        String getTaskName() {
            return taskName;
        }

        void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        String getTaskGroup() {
            return taskGroup;
        }

        void setTaskGroup(String taskGroup) {
            this.taskGroup = taskGroup;
        }

        Long getFiredTime() {
            return firedTime;
        }

        void setFiredTime(Long firedTime) {
            this.firedTime = firedTime;
        }

        String getFiredWay() {
            return firedWay;
        }

        void setFiredWay(String firedWay) {
            this.firedWay = firedWay;
        }

        Long getCompleteTime() {
            return completeTime;
        }

        void setCompleteTime(Long completeTime) {
            this.completeTime = completeTime;
        }

        Long getExpendTime() {
            return expendTime;
        }

        void setExpendTime(Long expendTime) {
            this.expendTime = expendTime;
        }

        Integer getRefired() {
            return refired;
        }

        void setRefired(Integer refired) {
            this.refired = refired;
        }

        String getExecState() {
            return execState;
        }

        void setExecState(String execState) {
            this.execState = execState;
        }

        String getLog() {
            return log;
        }

        void setLog(String log) {
            this.log = log;
        }
    }

}