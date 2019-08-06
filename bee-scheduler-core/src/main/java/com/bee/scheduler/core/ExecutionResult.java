package com.bee.scheduler.core;

import com.alibaba.fastjson.JSONObject;

/**
 * @author weiwei
 */
public class ExecutionResult {
    private boolean success;
    private JSONObject data;

    public ExecutionResult(boolean success, JSONObject data) {
        this.success = success;
        this.data = data;
    }

    public static ExecutionResult success() {
        return success(new JSONObject());
    }

    public static ExecutionResult success(JSONObject data) {
        if (data == null) {
            data = new JSONObject();
        }
        return new ExecutionResult(true, data);
    }

    public static ExecutionResult fail() {
        return fail(new JSONObject());
    }

    public static ExecutionResult fail(JSONObject data) {
        if (data == null) {
            data = new JSONObject();
        }
        return new ExecutionResult(false, data);
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
