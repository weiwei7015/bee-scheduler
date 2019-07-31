package com.bee.scheduler.context.listener;

import com.bee.scheduler.context.Constants;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLogger;
import com.bee.scheduler.core.TaskExecutionResult;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.utils.DBConnectionManager;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;

/**
 * @author weiwei 任务事件监听， 存储数据库
 */
public class TaskEventRecorder extends AbstractTaskListener {
    private Log logger = LogFactory.getLog(getClass());

    @Override
    public String getName() {
        return "TaskEventRecorder";
    }


    @Override
    public void taskExecutionVetoed(TaskExecutionContext context, Scheduler scheduler) {
        String taskJobGroup = context.getJobGroup();
        String taskJobName = context.getJobName();
        String triggerGroup = context.getTriggerGroup();
        String triggerName = context.getTriggerName();
        String schedulerInstanceId = context.getSchedulerInstanceId();
        String schedulerName = context.getSchedulerName();
        TaskExecutionLogger taskLogger = context.getLogger();

        Date currentTime = Calendar.getInstance().getTime();

        taskLogger.warning("任务[" + taskJobGroup + "." + taskJobName + "]已被取消执行！");

        // 记录执行历史
        Constants.TaskFiredWay firedWay = triggerGroup.equals(Constants.TASK_GROUP_MANUAL) ? Constants.TaskFiredWay.MANUAL : triggerGroup.equals(Constants.TASK_GROUP_TMP) ? Constants.TaskFiredWay.TMP : triggerGroup.equals(Constants.TASK_GROUP_LINKAGE) ? Constants.TaskFiredWay.LINKAGE : Constants.TaskFiredWay.SCHEDULE;

        try {
            String sql = "INSERT INTO BS_TASK_HISTORY(SCHED_NAME,INSTANCE_ID,FIRE_ID, TASK_NAME, TASK_GROUP, FIRED_TIME, FIRED_WAY, COMPLETE_TIME, EXPEND_TIME, REFIRED, EXEC_STATE, LOG) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            try (
                    Connection connection = DBConnectionManager.getInstance().getConnection(LocalDataSourceJobStore.TX_DATA_SOURCE_PREFIX + schedulerName);
                    PreparedStatement preparedStatement = connection.prepareStatement(sql)
            ) {
                preparedStatement.setString(1, schedulerName);
                preparedStatement.setString(2, schedulerInstanceId);
                preparedStatement.setString(3, context.getFireInstanceId());
                preparedStatement.setString(4, taskJobName);
                preparedStatement.setString(5, taskJobGroup);
                preparedStatement.setLong(6, context.getFireTime().getTime());
                preparedStatement.setString(7, firedWay.toString());
                preparedStatement.setLong(8, currentTime.getTime());
                preparedStatement.setLong(9, context.getJobRunTime());
                preparedStatement.setInt(10, context.getRefireCount());
                preparedStatement.setString(11, Constants.TaskExecState.VETOED.toString());
                preparedStatement.setString(12, taskLogger.getLog());
                preparedStatement.execute();
            } catch (Exception e) {
                logger.error(e);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void taskWasExecuted(TaskExecutionContext context, TaskExecutionResult result, Scheduler scheduler, JobExecutionException jobException) {
        String taskJobGroup = context.getJobGroup();
        String taskJobName = context.getJobName();
        String triggerGroup = context.getTriggerGroup();
        String triggerName = context.getTriggerName();
        String schedulerInstanceId = context.getSchedulerInstanceId();
        String schedulerName = context.getSchedulerName();
        TaskExecutionLogger taskLogger = context.getLogger();
        Date currentTime = Calendar.getInstance().getTime();

        // 记录执行历史
        Constants.TaskExecState execState = result == null ? Constants.TaskExecState.FAIL : result.isSuccess() ? Constants.TaskExecState.SUCCESS : Constants.TaskExecState.FAIL;
        Constants.TaskFiredWay firedWay = triggerGroup.equals(Constants.TASK_GROUP_MANUAL) ? Constants.TaskFiredWay.MANUAL : triggerGroup.equals(Constants.TASK_GROUP_TMP) ? Constants.TaskFiredWay.TMP : triggerGroup.equals(Constants.TASK_GROUP_LINKAGE) ? Constants.TaskFiredWay.LINKAGE : Constants.TaskFiredWay.SCHEDULE;
        try {
            String sql = "INSERT INTO BS_TASK_HISTORY(SCHED_NAME,INSTANCE_ID,FIRE_ID, TASK_NAME, TASK_GROUP, FIRED_TIME,FIRED_WAY, COMPLETE_TIME, EXPEND_TIME, REFIRED, EXEC_STATE, LOG) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            try (
                    Connection connection = DBConnectionManager.getInstance().getConnection(LocalDataSourceJobStore.TX_DATA_SOURCE_PREFIX + schedulerName);
                    PreparedStatement preparedStatement = connection.prepareStatement(sql)
            ) {
                preparedStatement.setString(1, schedulerName);
                preparedStatement.setString(2, schedulerInstanceId);
                preparedStatement.setString(3, context.getFireInstanceId());
                preparedStatement.setString(4, taskJobName);
                preparedStatement.setString(5, taskJobGroup);
                preparedStatement.setLong(6, context.getFireTime().getTime());
                preparedStatement.setString(7, firedWay.toString());
                preparedStatement.setLong(8, currentTime.getTime());
                preparedStatement.setLong(9, context.getJobRunTime());
                preparedStatement.setInt(10, context.getRefireCount());
                preparedStatement.setString(11, execState.toString());
                preparedStatement.setString(12, taskLogger.getLog());

                preparedStatement.execute();
            } catch (Exception e) {
                logger.error(e);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean vetoTaskExecution(TaskExecutionContext context, Scheduler scheduler) {
        TaskExecutionLogger taskLogger = context.getLogger();
        int minExecInterval = 3000;
        if (context.getPreviousFireTime() != null && context.getFireTime().getTime() - context.getPreviousFireTime().getTime() <= minExecInterval) {
            taskLogger.warning("任务最近执行时间：" + DateFormatUtils.format(context.getPreviousFireTime(), "yyyy-MM-dd HH:mm:ss") + "，任务执行间隔不能低于" + minExecInterval + "ms，请调整任务配置");
            return true;
        }
        return false;
    }
}