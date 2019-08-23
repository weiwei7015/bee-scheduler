package com.bee.scheduler.consolenode.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.quartz.TimeOfDay;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

@JsonComponent
public class JacksonComponent {

    public static class TimeOfDaySerializer extends JsonSerializer<TimeOfDay> {
        @Override
        public void serialize(TimeOfDay value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, value.getHour());
            cal.set(Calendar.MINUTE, value.getMinute());
            cal.set(Calendar.SECOND, value.getSecond());
            gen.writeNumber(cal.getTimeInMillis());
        }
    }

    public static class TimeOfDayDeserializer extends JsonDeserializer<TimeOfDay> {
        @Override
        public TimeOfDay deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return TimeOfDay.hourAndMinuteAndSecondFromDate(ctxt.readValue(p, Date.class));
        }
    }
}