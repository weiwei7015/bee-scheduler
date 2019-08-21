package com.bee.scheduler.context.common;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author weiwei
 */
public enum TaskSpecialGroup {
    TMP, LINKTMP;

    public static boolean contains(String group) {
        for (TaskSpecialGroup item : TaskSpecialGroup.values()) {
            if (StringUtils.equalsIgnoreCase(item.name(), group)) {
                return true;
            }
        }
        return false;
    }

    public static String[] stringValues() {
        String[] stringValues = new String[TaskSpecialGroup.values().length];
        for (int i = 0; i < TaskSpecialGroup.values().length; i++) {
            stringValues[i] = TaskSpecialGroup.values()[i].name();
        }
        return stringValues;
    }
}
