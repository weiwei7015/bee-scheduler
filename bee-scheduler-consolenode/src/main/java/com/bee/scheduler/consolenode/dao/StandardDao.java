package com.bee.scheduler.consolenode.dao;

import com.bee.scheduler.consolenode.entity.User;
import com.bee.scheduler.consolenode.model.ClusterSchedulerNode;
import com.bee.scheduler.consolenode.model.ExecutedTask;
import com.bee.scheduler.consolenode.model.Pageable;
import com.bee.scheduler.consolenode.model.TaskDetail;

import java.util.List;

public interface StandardDao {
    /**
     * ================= User =====================
     */
    User getUserByAccount$Pwd(String account, String pwd);

    void updateUserByAccount(User entity);

    /**
     * ================= Cluster =====================
     */
    List<ClusterSchedulerNode> getClusterSchedulerNodes(String schedulerName);

    /**
     * ================= Task =====================
     */
    Pageable<TaskDetail> queryTask(String schedulerName, List<String> taskNameList, List<String> taskGroupList, List<String> taskStateList, Integer pageNum, Integer pageSize);

    Pageable<String> queryTaskGroups(String schedulerName, String kw, Integer page, Integer pageSize);

    Pageable<String> queryTaskNames(String schedulerName, String kw, Integer page, Integer pageSize);

    /**
     * ================= TaskHistory =====================
     */
    ExecutedTask getTaskHistory(String fireId);

    Pageable<ExecutedTask> queryTaskHistory(String schedulerName, List<String> fireIdList, List<String> taskNameList, List<String> taskGroupList, List<String> execStateList, List<String> firedWayList, List<String> instanceIdList, Long firedTimeBefore, Long firedTimeAfter, Integer pageNum, Integer pageSize);

    Pageable<String> queryTaskHistoryGroups(String schedulerName, String kw, Integer pageNum, Integer pageSize);

    Pageable<String> queryTaskHistoryNames(String schedulerName, String kw, Integer pageNum, Integer pageSize);
}
