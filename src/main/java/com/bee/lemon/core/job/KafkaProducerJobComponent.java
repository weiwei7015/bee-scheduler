package com.bee.lemon.core.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author weiwei 用于发送Kafka消息
 */
public class KafkaProducerJobComponent extends JobComponent {
    @Override
    public String getName() {
        return "KafkaProducerJob";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getAuthor() {
        return "vivi";
    }

    @Override
    public String getDescription() {
        return "用于发送Kafka消息";
    }

    @Override
    public String getParamTemplate() {
        StringBuilder t = new StringBuilder();
        t.append("{\r");
        t.append("	\"brokerList\":\"\",\r");
        t.append("	\"messages\":[\r");
        t.append("		{\"topic\":\"\",\"content\":{}},\r");
        t.append("		{\"topic\":\"\",\"content\":\"\"},\r");
        t.append("		{\"topic\":\"\",\"content\":[{},{}]}\r");
        t.append("	]\r");
        t.append("}");
        return t.toString();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("KafkaProducerJob.execute()");
        JSONObject taskParam = getTaskParam(context);
        String brokerList = taskParam.getString("brokerList");
        JSONArray messageArray = taskParam.getJSONArray("messages");

        Properties props = new Properties();
        props.put("client.id", "Scheduler");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "0");
        props.put("metadata.broker.list", brokerList);
        Producer<String, String> producer = new Producer<String, String>(new ProducerConfig(props));

        try {
            List<KeyedMessage<String, String>> messages = new ArrayList<>();
            for (int i = 0; i < messageArray.size(); i++) {
                JSONObject msg = messageArray.getJSONObject(i);
                messages.add(new KeyedMessage<String, String>(msg.getString("topic"), msg.getString("content")));
            }
            producer.send(messages);
        } finally {
            producer.close();
        }

    }

}
