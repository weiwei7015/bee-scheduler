package com.bee.scheduler.context;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author weiwei
 */
public class SimpleTest {
    public static void main(String[] args) {
        System.out.println(DateUtils.truncate(new Date(), Calendar.DATE));
    }
}
