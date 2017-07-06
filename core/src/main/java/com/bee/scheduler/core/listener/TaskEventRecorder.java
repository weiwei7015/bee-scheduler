package com.bee.scheduler.core.listener;

import com.bee.scheduler.core.Constants;
import com.bee.scheduler.core.JobExecutionContextHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.quartz.listeners.JobListenerSupport;
import org.quartz.utils.DBConnectionManager;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;

/**
 * @author weiwei 任务事件监听， 存储到通知集合
 */
public class TaskEventRecorder extends JobListenerSupport {
    private Log logger = LogFactory.getLog(getClass());

    private DataSource dataSource;

    public TaskEventRecorder(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override

    public String getName() {
        return "TaskEventRecorder";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        JobDetail jobDetail = context.getJobDetail();
        JobExecutionContextHelper.appendExecLog(context, "任务[" + jobDetail.getKey() + "]将开始执行");
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        JobDetail jobDetail = context.getJobDetail();
        Trigger trigger = context.getTrigger();
        Date currentTime = Calendar.getInstance().getTime();

        JobExecutionContextHelper.appendExecLog(context, "任务[" + jobDetail.getKey() + "]正在运行，已被取消执行！");


        // 记录执行历史
        String taskExecLog = JobExecutionContextHelper.getExecLog(context);
        Constants.TaskFiredWay firedWay = trigger.getKey().getGroup().equals(Constants.TASK_GROUP_Manual) ? Constants.TaskFiredWay.MANUAL : trigger.getKey().getGroup().equals(Constants.TASK_GROUP_Tmp) ? Constants.TaskFiredWay.TMP : Constants.TaskFiredWay.SCHEDULE;


        try {
            String schedulerName = context.getScheduler().getSchedulerName();
            String schedulerInstanceId = context.getScheduler().getSchedulerInstanceId();

            Connection connection = DBConnectionManager.getInstance().getConnection(LocalDataSourceJobStore.TX_DATA_SOURCE_PREFIX + schedulerName);

            String sql = "INSERT INTO BS_TASK_HISTORY(SCHED_NAME,INSTANCE_NAME,FIRE_ID, TASK_NAME, TASK_GROUP, FIRED_TIME, FIRED_WAY, COMPLETE_TIME, EXPENDTIME, REFIRED, EXEC_STATE, LOG) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, schedulerName);
            preparedStatement.setString(2, schedulerInstanceId);
            preparedStatement.setString(3, context.getFireInstanceId());
            preparedStatement.setString(4, jobDetail.getKey().getName());
            preparedStatement.setString(5, jobDetail.getKey().getGroup());
            preparedStatement.setLong(6, context.getFireTime().getTime());
            preparedStatement.setString(7, firedWay.toString());
            preparedStatement.setLong(8, currentTime.getTime());
            preparedStatement.setLong(9, context.getJobRunTime());
            preparedStatement.setInt(10, context.getRefireCount());
            preparedStatement.setString(11, Constants.TaskExecState.VETOED.toString());
            preparedStatement.setString(12, taskExecLog);

            preparedStatement.execute();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        JobDetail jobDetail = context.getJobDetail();
        Trigger trigger = context.getTrigger();
        Date currentTime = Calendar.getInstance().getTime();

        if (jobException == null) {
            JobExecutionContextHelper.appendExecLog(context, "任务[" + jobDetail.getKey() + "]执行成功！");
        } else {
            JobExecutionContextHelper.appendExecLog(context, "任务[" + jobDetail.getKey() + "]失败！");
            JobExecutionContextHelper.appendExecLog(context, "异常详情：");
            JobExecutionContextHelper.appendExecLog(context, "摘要 -> " + jobException.getMessage());
            JobExecutionContextHelper.appendExecLog(context, "明细 -> " + jobException);

        }

        // 记录执行历史
        String taskExecLog = JobExecutionContextHelper.getExecLog(context);
        Constants.TaskExecState execState = jobException == null ? Constants.TaskExecState.SUCCESS : Constants.TaskExecState.SUCCESS;
        Constants.TaskFiredWay firedWay = trigger.getKey().getGroup().equals(Constants.TASK_GROUP_Manual) ? Constants.TaskFiredWay.MANUAL : trigger.getKey().getGroup().equals(Constants.TASK_GROUP_Tmp) ? Constants.TaskFiredWay.TMP : Constants.TaskFiredWay.SCHEDULE;

        try {
            String schedulerName = context.getScheduler().getSchedulerName();
            String schedulerInstanceId = context.getScheduler().getSchedulerInstanceId();

            Connection connection = DBConnectionManager.getInstance().getConnection(LocalDataSourceJobStore.TX_DATA_SOURCE_PREFIX + schedulerName);

            String sql = "INSERT INTO BS_TASK_HISTORY(SCHED_NAME,INSTANCE_ID,FIRE_ID, TASK_NAME, TASK_GROUP, FIRED_TIME,FIRED_WAY, COMPLETE_TIME, EXPEND_TIME, REFIRED, EXEC_STATE, LOG) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, schedulerName);
            preparedStatement.setString(2, schedulerInstanceId);
            preparedStatement.setString(3, context.getFireInstanceId());
            preparedStatement.setString(4, jobDetail.getKey().getName());
            preparedStatement.setString(5, jobDetail.getKey().getGroup());
            preparedStatement.setLong(6, context.getFireTime().getTime());
            preparedStatement.setString(7, firedWay.toString());
            preparedStatement.setLong(8, currentTime.getTime());
            preparedStatement.setLong(9, context.getJobRunTime());
            preparedStatement.setInt(10, context.getRefireCount());
            preparedStatement.setString(11, execState.toString());
            preparedStatement.setString(12, taskExecLog);

            preparedStatement.execute();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}