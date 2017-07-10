package com.bee.scheduler.admin.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.quartz.DateBuilder;
import org.quartz.TimeOfDay;

import java.util.Date;

/**
 * @author weiwei
 */
public class TmpTaskConfig extends TaskConfig {
    private Integer startDelay;

    public Integer getStartDelay() {
        return startDelay;
    }

    public void setStartDelay(Integer startDelay) {
        this.startDelay = startDelay;
    }
}
