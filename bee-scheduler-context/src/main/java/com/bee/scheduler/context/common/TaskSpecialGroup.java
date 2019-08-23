package com.bee.scheduler.context.common;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
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

    public static List<String> stringValues() {
        List<String> valueList = new ArrayList<>();
        for (TaskSpecialGroup value : values()) {
            valueList.add(value.name());
        }
        return valueList;
    }
}
