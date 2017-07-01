package com.bee.lemon.web.converter;

import org.quartz.TimeOfDay;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by weiwei on 2017/7/1.
 */
@Component
public class StringToTimeOfDayConverter implements Converter<String, TimeOfDay> {
    @Override
    public TimeOfDay convert(String source) {
        System.out.println("StringToTimeOfDayConverter.convert");
        return null;
    }
}
