package com.bee.scheduler.admin.model;

/**
 * @author weiwei
 */
public class TmpTaskConfig extends TaskConfig {
    private Boolean enableStartDelay;
    private Integer startDelay;

    public Boolean getEnableStartDelay() {
        return enableStartDelay;
    }

    public void setEnableStartDelay(Boolean enableStartDelay) {
        this.enableStartDelay = enableStartDelay;
    }

    public Integer getStartDelay() {
        return startDelay;
    }

    public void setStartDelay(Integer startDelay) {
        this.startDelay = startDelay;
    }
}
