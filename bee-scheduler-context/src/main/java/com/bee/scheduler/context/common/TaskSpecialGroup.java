package com.bee.scheduler.context.common;

import org.apache.commons.lang3.StringUtils;

/**
 * @author weiwei
 */
public enum TaskSpecialGroup {
    TMP;

    public static boolean contains(String group) {
        for (TaskSpecialGroup item : TaskSpecialGroup.values()) {
            if (StringUtils.equalsIgnoreCase(item.name(), group)) {
                return true;
            }
        }
        return false;
    }
}
