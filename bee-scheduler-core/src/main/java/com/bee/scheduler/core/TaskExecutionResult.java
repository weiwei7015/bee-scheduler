package com.bee.scheduler.core;

import com.alibaba.fastjson.JSONObject;

/**
 * @author weiwei
 */
public class TaskExecutionResult {
    private boolean success;
    private JSONObject data;

    public TaskExecutionResult(boolean success, JSONObject data) {
        this.success = success;
        this.data = data;
    }

    public static TaskExecutionResult success() {
        return success(new JSONObject());
    }

    public static TaskExecutionResult success(JSONObject data) {
        if (data == null) {
            data = new JSONObject();
        }
        return new TaskExecutionResult(true, data);
    }

    public static TaskExecutionResult fail() {
        return fail(new JSONObject());
    }

    public static TaskExecutionResult fail(JSONObject data) {
        if (data == null) {
            data = new JSONObject();
        }
        return new TaskExecutionResult(false, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        if (data == null) {
            data = new JSONObject();
        }
        this.data = data;
    }
}
