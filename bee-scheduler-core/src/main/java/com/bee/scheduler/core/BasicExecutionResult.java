package com.bee.scheduler.core;

import com.alibaba.fastjson.JSONObject;

/**
 * @author weiwei
 */
public class BasicExecutionResult {
    private boolean success;
    private JSONObject data;

    public BasicExecutionResult(boolean success, JSONObject data) {
        this.success = success;
        this.data = data;
    }

    public static BasicExecutionResult success() {
        return success(new JSONObject());
    }

    public static BasicExecutionResult success(JSONObject data) {
        if (data == null) {
            data = new JSONObject();
        }
        return new BasicExecutionResult(true, data);
    }

    public static BasicExecutionResult fail() {
        return fail(new JSONObject());
    }

    public static BasicExecutionResult fail(JSONObject data) {
        if (data == null) {
            data = new JSONObject();
        }
        return new BasicExecutionResult(false, data);
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
