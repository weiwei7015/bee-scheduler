package com.bee.scheduler.core.listener;

import com.bee.scheduler.core.Constants;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLog;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.utils.DBConnectionManager;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;

/**
 * @author weiwei 任务事件监听， 存储数据库
 */
public class TaskEventRecorder extends TaskListenerSupport {
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
    public void taskExecutionVetoed(TaskExecutionContext context) {
        TaskExecutionLog taskLogger = context.getLogger();
        JobExecutionContext jobExecutionContext = context.getJobExecutionContext();
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        Trigger trigger = jobExecutionContext.getTrigger();
        Scheduler scheduler = jobExecutionContext.getScheduler();

        Date currentTime = Calendar.getInstance().getTime();

        taskLogger.warning("任务[" + jobDetail.getKey() + "]已被取消执行！");

        // 记录执行历史
        Constants.TaskFiredWay firedWay = trigger.getKey().getGroup().equals(Constants.TASK_GROUP_MANUAL) ? Constants.TaskFiredWay.MANUAL : trigger.getKey().getGroup().equals(Constants.TASK_GROUP_TMP) ? Constants.TaskFiredWay.TMP : trigger.getKey().getGroup().equals(Constants.TASK_GROUP_LINKAGE) ? Constants.TaskFiredWay.LINKAGE : Constants.TaskFiredWay.SCHEDULE;

        try {
            String schedulerName = scheduler.getSchedulerName();
            String schedulerInstanceId = scheduler.getSchedulerInstanceId();

            String sql = "INSERT INTO BS_TASK_HISTORY(SCHED_NAME,INSTANCE_ID,FIRE_ID, TASK_NAME, TASK_GROUP, FIRED_TIME, FIRED_WAY, COMPLETE_TIME, EXPEND_TIME, REFIRED, EXEC_STATE, LOG) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

            Connection connection = DBConnectionManager.getInstance().getConnection(LocalDataSourceJobStore.TX_DATA_SOURCE_PREFIX + schedulerName);
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, schedulerName);
                preparedStatement.setString(2, schedulerInstanceId);
                preparedStatement.setString(3, jobExecutionContext.getFireInstanceId());
                preparedStatement.setString(4, jobDetail.getKey().getName());
                preparedStatement.setString(5, jobDetail.getKey().getGroup());
                preparedStatement.setLong(6, jobExecutionContext.getFireTime().getTime());
                preparedStatement.setString(7, firedWay.toString());
                preparedStatement.setLong(8, currentTime.getTime());
                preparedStatement.setLong(9, jobExecutionContext.getJobRunTime());
                preparedStatement.setInt(10, jobExecutionContext.getRefireCount());
                preparedStatement.setString(11, Constants.TaskExecState.VETOED.toString());
                preparedStatement.setString(12, taskLogger.getLogContent());
                preparedStatement.execute();
            } catch (Exception e) {
                logger.error(e);
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void taskWasExecuted(TaskExecutionContext context, JobExecutionException jobException) {
        TaskExecutionLog taskLogger = context.getLogger();
        JobExecutionContext jobExecutionContext = context.getJobExecutionContext();
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        Trigger trigger = jobExecutionContext.getTrigger();
        Scheduler scheduler = jobExecutionContext.getScheduler();
        Date currentTime = Calendar.getInstance().getTime();

        // 记录执行历史
        Constants.TaskExecState execState = jobException == null ? Constants.TaskExecState.SUCCESS : Constants.TaskExecState.FAIL;
        Constants.TaskFiredWay firedWay = trigger.getKey().getGroup().equals(Constants.TASK_GROUP_MANUAL) ? Constants.TaskFiredWay.MANUAL : trigger.getKey().getGroup().equals(Constants.TASK_GROUP_TMP) ? Constants.TaskFiredWay.TMP : trigger.getKey().getGroup().equals(Constants.TASK_GROUP_LINKAGE) ? Constants.TaskFiredWay.LINKAGE : Constants.TaskFiredWay.SCHEDULE;

        try {
            String schedulerName = scheduler.getSchedulerName();
            String schedulerInstanceId = scheduler.getSchedulerInstanceId();

            Connection connection = DBConnectionManager.getInstance().getConnection(LocalDataSourceJobStore.TX_DATA_SOURCE_PREFIX + schedulerName);

            String sql = "INSERT INTO BS_TASK_HISTORY(SCHED_NAME,INSTANCE_ID,FIRE_ID, TASK_NAME, TASK_GROUP, FIRED_TIME,FIRED_WAY, COMPLETE_TIME, EXPEND_TIME, REFIRED, EXEC_STATE, LOG) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";


            PreparedStatement preparedStatement = null;

            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, schedulerName);
                preparedStatement.setString(2, schedulerInstanceId);
                preparedStatement.setString(3, jobExecutionContext.getFireInstanceId());
                preparedStatement.setString(4, jobDetail.getKey().getName());
                preparedStatement.setString(5, jobDetail.getKey().getGroup());
                preparedStatement.setLong(6, jobExecutionContext.getFireTime().getTime());
                preparedStatement.setString(7, firedWay.toString());
                preparedStatement.setLong(8, currentTime.getTime());
                preparedStatement.setLong(9, jobExecutionContext.getJobRunTime());
                preparedStatement.setInt(10, jobExecutionContext.getRefireCount());
                preparedStatement.setString(11, execState.toString());
                preparedStatement.setString(12, taskLogger.getLogContent());

                preparedStatement.execute();
            } catch (Exception e) {
                logger.error(e);
            } finally {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    @Override
    public boolean vetoTaskExecution(TaskExecutionContext context) {
        TaskExecutionLog taskLogger = context.getLogger();
        JobExecutionContext jobExecutionContext = context.getJobExecutionContext();
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        Trigger trigger = jobExecutionContext.getTrigger();
        Scheduler scheduler = jobExecutionContext.getScheduler();
        Date currentTime = Calendar.getInstance().getTime();

        int minExecInterval = 3000;

        if (jobExecutionContext.getPreviousFireTime() != null && jobExecutionContext.getFireTime().getTime() - jobExecutionContext.getPreviousFireTime().getTime() <= minExecInterval) {
            taskLogger.warning("任务最近执行时间：" + DateFormatUtils.format(jobExecutionContext.getPreviousFireTime(), "yyyy-MM-dd HH:mm:ss") + "，任务执行间隔不能低于" + minExecInterval + "ms，请调整任务配置");
            return true;
        }
        return false;
    }

}