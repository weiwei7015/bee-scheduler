package com.bee.scheduler.executor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.ExecutionResult;
import com.bee.scheduler.core.ExecutionContext;
import com.bee.scheduler.core.ExecutorModule;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author weiwei 用于发送Kafka消息
 */
public class KafkaProducerTaskModule implements ExecutorModule {
    @Override
    public String getId() {
        return "KafkaProducerTaskModule";
    }

    @Override
    public String getName() {
        return "KafkaProducerTaskModule";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getAuthor() {
        return "weiwei";
    }

    @Override
    public String getDescription() {
        return "用于发送Kafka消息";
    }

    @Override
    public String getParamTemplate() {
        return "{\r" +
                "    brokerList:'',\r" +
                "    messages:[\r" +
                "        {topic:'',content:{}},\r" +
                "        {topic:'',content:''},\r" +
                "        {topic:'',content:[{},{}]}\r" +
                "    ]\r" +
                "}";
    }

    @Override
    public ExecutionResult exec(ExecutionContext context) throws Exception {
        JSONObject taskParam = context.getParam();
        Log logger = context.getLogger();

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
        logger.info("任务执行成功");
        return ExecutionResult.success();
    }
}
