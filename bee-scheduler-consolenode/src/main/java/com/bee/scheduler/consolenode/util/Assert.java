package com.bee.scheduler.consolenode.util;

/**
 * @author weiwei
 */
public class Assert {
    public static void check(boolean expression, RuntimeException exception) {
        if (!expression) {
            throw exception;
        }
    }
}
