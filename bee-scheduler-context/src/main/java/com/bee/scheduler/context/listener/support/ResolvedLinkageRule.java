package com.bee.scheduler.context.listener.support;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author weiwei
 */
public class ResolvedLinkageRule {
    public enum Mode {Create, Trigger}

    private Mode mode;
    private String taskGroup;
    private String taskName;
    private LinkageTaskConfig linkageTaskConfig;
    private Integer delay;
    private Boolean condition;
    private String conditionEl;
    private JSONObject exports;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public LinkageTaskConfig getLinkageTaskConfig() {
        return linkageTaskConfig;
    }

    public void setLinkageTaskConfig(LinkageTaskConfig linkageTaskConfig) {
        this.linkageTaskConfig = linkageTaskConfig;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Boolean getCondition() {
        return condition;
    }

    public void setCondition(Boolean condition) {
        this.condition = condition;
    }

    public String getConditionEl() {
        return conditionEl;
    }

    public void setConditionEl(String conditionEl) {
        this.conditionEl = conditionEl;
    }

    public JSONObject getExports() {
        return exports;
    }

    public void setExports(JSONObject exports) {
        this.exports = exports;
    }


    public static class LinkageTaskConfig {
        private String taskModule;
        private JSONObject params;
        private JSONArray linkageRule;

        public String getTaskModule() {
            return taskModule;
        }

        public void setTaskModule(String taskModule) {
            this.taskModule = taskModule;
        }

        public JSONObject getParams() {
            return params;
        }

        public void setParams(JSONObject params) {
            this.params = params;
        }

        public JSONArray getLinkageRule() {
            return linkageRule;
        }

        public void setLinkageRule(JSONArray linkageRule) {
            this.linkageRule = linkageRule;
        }
    }

}

